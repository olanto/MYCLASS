package isi.jg.idxvli;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.text.*;
import isi.jg.util.Timer;
import isi.jg.idxvli.util.*;
import isi.jg.idxvli.ql.*;
import isi.jg.idxvli.doc.*;
import isi.jg.idxvli.word.*;
import isi.jg.idxvli.extra.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.SetOperation.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.idxvli.cache.*;
import isi.jg.idxvli.extra.DocBag;
import isi.jg.idxvli.extra.DocSeq;
import isi.jg.idxvli.extra.DocPosChar;
import isi.jg.idxvli.server.*;
import isi.jg.idxvli.propertie.*;
import isi.jg.conman.server.*;

 /*
 *La cr�ation d'un index peut ressembler au code suivant:
 *
 *<pre>
 *
 *
 *
 * to do
 *
 * - mettre le mode readOnly dans tous les objets lisant sur les disques ...
 *
 *  probleme par rapport � l'ancienne version avec le cat�goriseur.
 *
 * - le mode sans les positions ne garde que les documents (pas le nombre d'occurence) donc le cat�goriseur
 *   ne peut pas fonctionner si l'on n'index pas les positions aussi (il faut faire quelque chose !!)
 * - il n'y a pas de mode 2 passe pour le cat�goriseur, donc il travaille sur tous les mots (il faut aussi faire
 *   quelque chose).
 *
 */
         
/**
 * Une classe pour construire une structure d'indexation.
 *</pre>
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class IdxStructure {

    /** ordre dans le vecteur d'indexation pour les DOCuments*/
    public static final int DOC = 0;
    /** ordre dans le vecteur d'indexation pour les OCCurrences*/
    public static final int OCC = 1;
    /** ordre dans le vecteur d'indexation pour les indices du VECteur de positions*/
    public static final int VEC = 2;
    /** ce qui est chercher n'est pas trouv� (valeur de retour)*/
    public static final int notFind = -1;
    /** structure contenant les mots non-indexables*/
    public SetOfWords dontIndexThis;
    /** dictionnaire de mots (mot->indice) et (indice->mot)*/
    public WordManager wordstable;
    /** dictionnaire de mots (indice->liste des mots sans lemmatisation ayant la m�me racine)*/
    public TreeSet<String>[] stemList;
    /** structure de l'index pour l'indexation*/
    public CacheWrite indexdoc;
    /** structure de l'index pour les positions pour l'indexation*/
    public CacheWrite indexpos;
    /** permet la translation des num�ros des mots vers les num�ros d'un cache restreint*/
    public CacheTranslate idxtrans;
    /** structure de l'index pour les positions pour la consultation*/
    public CacheRead_Opti indexread;
    /** coordinateur des caches lors de l'indexation*/
    public CacheCoordinator indexCoord;
    /** coordinateur des caches lors de l'indexation*/
    public QLManager executor;
    /** position de l'index des mots et des freq d'un document
     * <br> rdnbag[i]->information sur l'indexation du i-�me documement
     */
    protected long[] rdnbag;
    /** position de l'index des mots en s�quence d'un document
     * <br> rdnseq[i]->s�equence des mots  du i-�me documement
     */
    protected long[] rdnseq;
    /** position de l'index des position des mots en s�quence d'un document
     * <br> rdnposchar[i]->s�equence des positions des mots  du i-�me documement
     */
    protected long[] rdnposchar;
    /** dictionnaire de documents (document->indice) (indice->document)*/
    public DocumentManager docstable;
    /** la structure comprend un indexer*/
    public IdxIndexer Indexer;
    /** la structure comprend un module d'interrogation*/
    public IdxQuery Query;
    /** la structure comprend un module de statistique*/
    public IdxStatistic Statistic;
    /** la structure comprend un module de gestion des IO*/
    public IdxIO IO;
    /** indice du dernier mot index� mais pas encore en structure WRITE*/
    public int lastRecordedWord = 0;
    /** indice du dernier mot index� en structure READ*/
    public int lastUpdatedWord = 0;
    /** indice du dernier document index� mais pas encore en structure WRITE*/
    public int lastRecordedDoc = 0;
    /** indice du dernier document index�. en structure READ*/
    public int lastUpdatedDoc = 0;
    /** indice du dernier document index� � classifier (pour une indexation en deux passes. */
    public int nbdoctoclassify = 0;
    /** nbre total de positions index�e*/
    public long cntpos = 0;
    /** flag indiquant qu'il n'y pas de document invalide (permet de sauter les filtres */
    public static boolean ZERO_INVALID_DOC = false;
    /** correcteur ortografic */
    public static OupsQuery oups;
    /** marqueur pour les propri�t�s (langue, collection) */
    public static MarkerManager marker;

    /** une liste de documents  (pour QLTwice) attention cette impl�mentation ne supporte pas la concurrence.
     Il s'agit d'un test pour la recherche*/
    public static int[] resQ1;
    
    
    /** Cr�e une structure d'indexation
     */
    public IdxStructure() {
    }

    /** Cr�e une structure d'indexation . Permet de d�composer l'initalisation.
     * @param _mode (QUERY,INCREMENTAL,DIFFERENTIAL,)
     */
    public IdxStructure(String _mode) {
        MODE_IDX = IdxMode.valueOf(_mode);
    }

    /**
     * cr�ation d'une structure d'indexation. Permet de d�composer l'initalisation.
     * @param _mode (NEW_INDEXATION)
     * @param name  (pas utilis�)
     *
     */
    public IdxStructure(String _mode, String name) {
        if (!_mode.equals("NEW")) {
            error_fatal("must be in mode NEW_INDEXATION");
        }
        MODE_IDX = IdxMode.valueOf(_mode);
    }

    /** Cr�e une structure d'indexation et initialise la.
     * @param _mode (NEW,QUERY,INCREMENTAL,DIFFERENTIAL,)
     * @param client la configuration client
     */
    public IdxStructure(String _mode, IdxInit client) {
        MODE_IDX = IdxMode.valueOf(_mode);
        createComponent(client);
        loadIndexDoc();
    }

    /* service IO */
    /** ferme tout les managers (doc,term,objsto) pour lib�rer la m�moire pour le classifieur !
     */
    public final void closeAllManager() {
        IO.closeAllManager();
    }

    /** r�cu�re le vecteur de s�quence des termes d'un document
     * @param d document
     * @return s�quence
     */
    public final int[] getSeqOfDoc(int d) {
        return IO.loadSeq(d);
    }

    /** sauve un �l�ment du cache (uniquement pour le management des caches)
     * @param i indice du cache
     */
    public final void saveVectorW(int i) {
        IO.saveVectorW(i);
    }

    /** r�cu�re le vecteur des positions des termes d'un document
     * @param d document
     * @return s�quence
     */
    public final int[] getPosCharOfDoc(int d) {
        return IO.loadPosChar(d);
    }

    /**
     * r�cu�re la liste des termes ayant le meme lemme
     * @return la liste des termes
     * @param w lemme recherch�
     */
    public final String getStemList(int w) {
        String res = "";
        for (Iterator<String> i = stemList[w].iterator(); i.hasNext();) {
            res += i.next() + " ";
        }
        return res;
    }

    /** r�cu�re le sac des termes d'un document
     * @param d document
     * @return sac de termes
     */
    public final int[] getBagOfDoc(int d) {
        return IO.loadBag(d);
    }

    /** charge un index.
     */
    public final void loadIndexDoc() {
        IO.loadindexdoc();
    }

         /** Index le contenu du directoire en ne comptant que les mots sans enregistrer les documents.
     * @param path directoire � indexer
     */
    public final void indexdirOnlyCount(String path) {
    DO_DOCBAG = false;
    DO_DOCRECORD = false;
    NO_IDX_ONLY_COUNT=true; // implique IDX_SAVE_POSITION=false; MODE_RANKING=RankingMode.NO;
    IDX_SAVE_POSITION=false;
    MODE_RANKING=RankingMode.NO;
    MODIFY_IDX=true;
    WORD_TWOPASS = false;  // permet d'ajouter des nouveaux mots

    int save_lastdoc =lastRecordedDoc;
    Indexer.indexdir(path);
    nbdoctoclassify=lastRecordedDoc;  // save estimate doc to classify
    lastRecordedDoc= save_lastdoc;   // restore
    }
     /** Index le contenu du directoire en ne comptant que les mots sans enregistrer les documents.
     * @param path directoire � indexer
     */
    public final void indexdirBuildDocBag(String path) {
    DO_DOCBAG = true;
    DO_DOCRECORD = true;
    NO_IDX_ONLY_COUNT=true;
    MODIFY_IDX=false;
    WORD_TWOPASS = true;  // ne pas ajouter des nouveaux mots
    Indexer.indexdir(path);
    }
    /** Index le contenu du directoire indiqu�.
     * @param path directoire � indexer
     */
    public final void indexdir(String path) {
        Indexer.indexdir(path);
    }

    /** Index ce contenu  avec cet identifiant.
     * @param fname identifiant
     * @param content contenu � indexer
     */
    public final void indexThisContent(String fname, String content) {
        Indexer.IndexThisContent(fname, content);
    }

    /**
     * sauvegarder les donn�es encore dans les caches de l'index
     */
    public final void flushIndexDoc() {
        indexCoord.freecacheFull();
    }

    /**
     * permettre de voir l'index en entier
     * doit �tre ex�cuter en mode exclusif
     */
    public final void showFullIndex() {
        indexCoord.freecacheFull();   // sauvegarder les donn�es encore dans les caches de l'index
        this.docstable.propagateInvalididy();
        this.lastUpdatedWord = this.lastRecordedWord; // avance les compteurs jusqu'� la fin
        this.lastUpdatedDoc = this.lastRecordedDoc;
        // actuellement on jette le cache courant, ceci n'est pas le plus mauvais car une gestion plus fine
        // demande que chaque vecteur soit anaylis� pour savoir si il faut le recharger. pour finir les vecteurs les plus courants
        // ont une forte probalilit� d'�tre modifi�s
        InitQueryCache(); // pour finir on r�initialise compl�tement
    // pour le KNN, il est difficile de ne pas les r�initialiser
    // ??? todo ???
    // initialiser le KNN

    }

    /** sauvegarder les donn�es encore dans les caches et ferme l'un index
     */
    public final void close() {
        indexCoord.freecacheFull();
        IO.saveindexdoc();
    }

    /** r�cup�re le nom de la racine de la structure d'indexation
     * @return dossier racine
     */
    public final String getIdxRootName() {
        return COMMON_ROOT;
    }

    /** r�cup�re le nom de l'index actuellement actif
     * @return sac nom de fichier
     */
    public final String getIdxName() {
        return IdxConstant.currentf;
    }

    /** nombre de documents dans lesquels apparait le terme j
     * @param j i�me terme
     * @return nbr de documents
     */
    public final int getOccOfW(int j) { // Count in how many documents word j appears
        return IO.getOccOfW(j);
    }

    /** nombre de documents dans lesquels apparait le terme w
     * @param w  terme
     * @return nbr de documents
     */
    public final int getOccOfW(String w) { // Count in how many documents word j appears
        int j = this.getIntForW(w);
        if (j == notFind) {
            return 0;
        }
        return IO.getOccOfW(j);
    }

    /** nombres d'occurence dans tous le corpus du terme j
     * @param j i�me terme
     * @return nbr occurence
     */
    public final int getOccCorpusOfW(int j) { // Count  how occurences of  word j in all corpus
        indexread.lockForFull(j);
        // zone prot�g�e pour n
        int nbocc = 0;
        int count = indexread.getNbDoc(j);
        for (int i = 0; i < count; i++) {
            nbocc += indexread.vDoc(j, count * OCC + i);
        }
        //fin de zone
        indexread.unlock(j);
        return nbocc;
    }

    /** retourne le vecteur des positions du terme i pour le document n
     * ! Doit-�tre utilis� sur un terme prot�g�
     * @param i terme
     * @param n document
     * @return vecteur de positions
     */
    public final int[] getWposition(int i, int n) { // load position for word i , n th documents
        // msg("word: "+i+" n: "+n);
        int tl = indexread.getNbDoc(i);
        return indexread.getvPos(i, indexread.vDoc(i, tl * VEC + n), indexread.vDoc(i, tl * OCC + n));
    }

    /** retourne les bornes des positions du terme i pour le document n.
     * ! Doit-�tre utilis� sur un terme prot�g�
     * @param i terme
     * @param n document
     * @return vecteur de positions
     */
    public final PosLength getWPosLength(int i, int n) {
        //  msg("word: "+i+" n: "+n);
        int tl = indexread.getNbDoc(i);
        return new PosLength(indexread.vDoc(i, tl * VEC + n), indexread.vDoc(i, tl * OCC + n));
    }

    /** charge le vecteur (utilis� uniquement par le gestionnaire de cache).
     * @param n terme
     */
    public void loadVectorWforBasic(int n) {
        IO.loadVectorWforBasic(n);
    }

    /** charge le vecteur (utilis� uniquement par le gestionnaire de cache).
     * @param n terme
     */
    public void loadVectorWforFull(int n) {
        IO.loadVectorWforFull(n);
    }

    /** longeur d'un document.
     * @param d document
     * @return nbr de termes index�s
     */
    public int getLengthOfD(int d) {
        return docstable.getSize(d);
    }

    /** indice d'un terme (le texte est normalis� Lower ...), les indices ne sont pris que dans l'intervalle de lecture
     * @param w texte
     * @return indice du terme (-1 = pas trouv�)
     */
    public int getIntForW(String w) {
        String word;
        int n;
        word = WORD_DEFINITION.normaliseWord(this, w);
        n = wordstable.get(word);
        if (n != -1 && n < lastUpdatedWord) {
            //ne pas charger son vecteur!! checkVectorWforBasic(n.intValue());
            return n;
        } else {
            return notFind;
        }
    }

    /** indice d'un terme (le terme est d�j� normalis�), les indices ne sont pris que dans l'intervalle de lecture
     * @param w texte
     * @return indice du terme (-1 = pas trouv�)
     */
    public int getIntForRawW(String w) {
        int n;
        n = wordstable.get(w);
        if (n != -1 && n < lastUpdatedWord) {
            //ne pas charger son vecteur!! checkVectorWforBasic(n.intValue());
            return n;
        } else {
            return notFind;
        }
    }

    /** nom du fichier du document i
     * @param d i�me document
     * @return nom de fichier
     */
    public String getFileNameForDocument(int d) {
        return docstable.get(d);
    }

    /** num�ro du document ayant le libell� s
     * @param s nom du fichier
     * @return num�ro du document (-1 = pas trouv�)
     */
    public int getIntForDocument(String s) {
        return docstable.get(s);
    }

    /** nom du terme  i
     * @return nom du terme
     * @param i i�me terme
     */
    public final String getStringforW(int i) {
        return wordstable.get(i);
    }

    /**
     *  r�cup�re le dictionnaire de propri�t�s
     * @return liste des propri�t�s actives
     */
    public List<String> getDictionnary() {
        return docstable.getDictionnary();
    }

    /**
     *  r�cup�re le dictionnaire de propri�t�s ayant un certain pr�fix (COLECT., LANG.)
     * @param prefix pr�fixe des propri�t�s
     * @return liste des propri�t�s actives
     */
    public List<String> getDictionnary(String prefix) {
        return docstable.getDictionnary(prefix);
    }

    /**
     * retourne la liste des documents valides correspondants � la requ�te, (null) si erreur.
     * @param request requ�te
     * @return la liste des documents valides
     */
    public final int[] executeRequest(String request) {
        return executor.get(request, this);
    }

        /**
     * retourne la liste des documents valides correspondants � la requ�te, (null) si erreur.
     * @param request requ�te
      * @return la liste des documents valides
     */
    public final QLResultAndRank executeRequestMore(String request) {
        return executor.getMore(request, this);
    }

    /**
     * v�rifie l'int�girit� d'un terme
     * @param verbose d�tail du check
     * @param w terme � v�rifier
     */
    public final void checkIntegrityOfW(String w, boolean verbose) {
        if (verbose) {
            msg("check integrity for:" + w);
        }

        int n1 = getIntForW(w);
        indexread.lockForFull(n1);
        // zone prot�g�e
        int r1[] = indexread.getReferenceOnDoc(n1);
        int il1 = indexread.getNbDoc(n1);
        if (verbose) {
            msg("indexdoc:" + il1 + " #n1:" + n1);
        //showVector(r1);
        }
        for (int i = 0; i < il1; i++) {
            //msg(" doc:"+r1[i]+", n1:"+n1+", i:"+i);
            int[] pos = getWposition(n1, i);
            //msg ("doc:"+r1[i]);showVector(pos);
            int prevVal = -1;
            for (int j = 0; j < pos.length; j++) {
                if (pos[j] <= prevVal) {
                    msg("integrity failure:" + w + " doc:" + r1[i] + " pos:" + pos[j] + "<=:" + prevVal);
                }
                prevVal = pos[j];
            }

        }
        //fin de zone
        indexread.unlock(n1);
    }

    /**
     * v�rifie l'int�girit� de tout l'index
     * @param verbose d�tail de la v�rification
     */
    public final void checkIntegrityOfRND(boolean verbose) {
        Timer t1 = new Timer("check integrity lastRecordedWord=" + lastRecordedWord);
        for (int i = 0; i < lastRecordedWord; i++) {
            checkIntegrityOfW(getStringforW(i), verbose);
        }
        t1.stop();
    }

    /**
     * initialise un lemmatiseur dans une langue donn�e
     * @param lang langue de stemming
     */
    public void initStemmer(String lang) {
        WORD_USE_STEMMER = true;
        WORD_STEMMING_LANG = lang;
        Stemmer.init(WORD_STEMMING_LANG);
    }

    /**
     * indique si les caches d�passent la taille IDXSIZETOFREE & allocate
     * @return true=d�passe les capacit�s
     */
    public boolean cacheOverFlow() {
        return (indexdoc.getCurrentSize() + indexpos.getCurrentSize() > INDEXING_CACHE_SIZE) || (IDX_CACHE_COUNT <= idxtrans.allocate());
    }

    /**
     * indique la taille total des caches
     * @return la taille
     */
    public int cacheCurrentSize() {
        return (indexdoc.getCurrentSize() + indexpos.getCurrentSize());
    }

    public QLResultNice evalQLNice(ContentService cs, String request, int start, int size) {
        return executor.get(this, cs, request, start, size);
    }

    public QLResultNice evalQLNice(ContentService cs, String request1,
                                   String request2, int start, int size1, int size2) {
        return executor.get(this, cs, request1, request2, start, size1, size2);
    }
   
    
    public QLResultNice evalQLNice(ContentService cs, String request, String properties, int start, int size) {
        return executor.get(this, cs, request, properties, start, size);
    }

    public QLResultNice evalQLNice(ContentService cs, String request, String properties, String profile, int start, int size) {
        return executor.get(this, cs, request, properties, profile, start, size);
    }

    public void exportEntry(long min, long max) {
        long count = 0;
        try {
            msg("exported in :" + ORG_FILE + " min occ:" + min + " max occ:" + max);
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(ORG_FILE), "UTF-8");
            OutputStreamWriter outfreq = new OutputStreamWriter(new FileOutputStream(ORG_FILE+".freq"), "UTF-8");
            int n, occofW, occCourpusofW;
            for (int i = 0; i < this.lastRecordedWord; i++) {
                String entry = getStringforW(i);
                occofW = getOccOfW(i);
                if (occofW > min & occofW <= max) {
                    out.write(entry + "\n");
                    outfreq.write(entry + "\t"+occofW+ "\n");
                    count++;
                }
            }
            msg("total exported:" + count);
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("IO error ExportEntry");
        }
    }

    public String getLanguage(String txt) {
        if (IDX_MARKER) {
            return marker.getLanguage(txt);
        }
        return null; // pas actif;
    }

    public String[] getCollection(String txt) {
        if (IDX_MARKER) {
            return marker.getCollectionFromName(txt);
        }
        return null; // pas actif;
    }

      /**
     * cr�e un composant d'indexation sans les fichiers d'index
     */
    public void initComponent(IdxInit client) {
        // initialise les constantes et la configuration
        client.InitPermanent();
        client.InitConfiguration();
//        IdxConstant.openLogger();
//        IdxConstant.show();

        // create stemmer if used
        if (WORD_USE_STEMMER) {
            Stemmer.init(WORD_STEMMING_LANG);
        }
         dontIndexThis = new SetOfWords(IDX_DONTINDEXTHIS);
         msg("#stop words:"+dontIndexThis.words.size());
    }
  /**
     * cr�e un composant d'indexation avec une racine pour les fichiers d'index
     */
    public void createComponent(IdxInit client) {
        // initialise les constantes et la configuration
        client.InitPermanent();
        client.InitConfiguration();
        IdxConstant.openLogger();
        IdxConstant.show();

        // create stemmer if used
        if (WORD_USE_STEMMER) {
            Stemmer.init(WORD_STEMMING_LANG);
        }
        // create ortografic if used
        if (ORTOGRAFIC) {
            oups = new OupsQuery(this, DICT_FILE, PHONET_FILE, ORG_FILE);
        }
        // create ortografic if used
        if (IDX_MARKER) {
            marker = new Marker(LANGUAGE_TRAINING, COLLECTION_DOMAIN);
        }
        // create reference
        if (MODE_IDX == IdxMode.NEW || MODE_IDX == IdxMode.INCREMENTAL || MODE_IDX == IdxMode.DIFFERENTIAL) {
            Indexer = new IdxIndexer(this);
        }
        if (MODE_IDX == IdxMode.QUERY) {
            Query = new IdxQuery(this);
        }
        Statistic = new IdxStatistic(this);
        IO = new IdxIO(this);
        dontIndexThis = new SetOfWords(IDX_DONTINDEXTHIS);

        if (MODE_IDX == IdxMode.NEW || MODE_IDX == IdxMode.INCREMENTAL || MODE_IDX == IdxMode.DIFFERENTIAL || MODE_CONTINUE == ContinueMode.MIX) {
            InitIndexer();
        }
        if (MODE_IDX == IdxMode.QUERY || MODE_CONTINUE == ContinueMode.MIX) {
            InitQuery();
        }
    }

    void InitQuery() {
        InitQueryCache();
        executor = new QL_Basic();
    }

    void InitQueryCache() {
        msg("init mode:" + MODE_IDX.name());
        // init cacheread
        if (CACHE_IMPLEMENTATION_READ == implementationMode.FAST) {
            indexread = new CacheRead_Opti(this, QUERY_CACHE_COUNT, QUERY_CACHE_SIZE);
        } // idem � big
        else {
            indexread = new CacheRead_Opti(this, QUERY_CACHE_COUNT, QUERY_CACHE_SIZE);
        }  //

    }

    void InitIndexer() {
        msg("wordmax:" + WORD_MAX);
        msg("init mode:" + MODE_IDX.name());
        switch (CACHE_IMPLEMENTATION_INDEXING) {
            case FAST:
                indexdoc = new CacheIdx_ExtGC(WORD_MAX);
                indexpos = new CacheIdx_ExtGC(WORD_MAX);
                idxtrans = new CacheTranslate_OneOne(WORD_MAX);
                IDX_CACHE_COUNT = WORD_MAX;
                break;
            case BIG:
                indexdoc = new CacheIdx_ExtGC(IDX_CACHE_COUNT + IDX_RESERVE);
                indexpos = new CacheIdx_ExtGC(IDX_CACHE_COUNT + IDX_RESERVE);
                idxtrans = new CacheTranslate_Subset(IDX_CACHE_COUNT + IDX_RESERVE);
                break;
        }
        indexCoord = new CacheCoordinator_Basic(this, indexdoc, indexpos, idxtrans);
        if (IDX_WITHDOCBAG) { // seulement si l'on veut faire de la classification
            DocBag.init(this);
            rdnbag = new long[DOC_MAX];
        }
        if (IDX_MORE_INFO) { // seulement si l'on veut des info suppl�mentaires sur les doc
            //glue.doclength = new int[glue.maxdoc];
            DocSeq.init();
            rdnseq = new long[DOC_MAX];
            DocPosChar.init();
            rdnposchar = new long[DOC_MAX];
        }
        if (STEM_KEEP_LIST && WORD_USE_STEMMER) { // seulement si l'on veut des info suppl�mentaires sur la lemmatisation
            stemList = new TreeSet[WORD_MAX];  // g�n�re un warning � la compilation voir "Generics in the Java Programming Language" de Gilad Bracha" July 5, 2004
        }
        if (IDX_WITHDOCBAG) {
            for (int j = 0; j < DOC_MAX; j++) {
                rdnbag[j] = 0;
            }
        }
        indexCoord.startTimer(); // initial time for global statistic

    }
}
