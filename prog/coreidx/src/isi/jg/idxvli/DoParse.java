package isi.jg.idxvli;

import isi.jg.idxvli.extra.DocBagInteractive;
import isi.jg.idxvli.extra.Stemmer;
import isi.jg.idxvli.ref.IdxReference;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.TreeSet;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

/**
 * Une classe pour effectuer le parsing des documents.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *Une classe pour effectuer le parsing des documents.<br>
 *
 * On peut changer le tokenizer en modifiant l'initialisation de WORD_DEF
 * et associant le bon code.<br>
 *
 * On peut penser que chaque client à un méthode next() et normalise () et définir un type
 * associé à un case pour sélectionner le bon next et normalise (à faire)
 *
 */
public class DoParse {

    static final int EOF = -1;
    // attention les variables sont statiques pour accélerer mais si des accès concurrents sont possibles
    // alors il faut protéger le parsing
    static SetOfWords DontIndexThis;
    static int nbToken = 0;
    public static IdxStructure glue;
    /** variable contenant le symbole courant*/
    public static StringBuilder cw = new StringBuilder();
    /** la source de caractère à parser */
    public static Reader in;
    /** indique si la fin du fichier est atteinte */
    public static boolean EOFflag = true;
    /** posistion dans la source de caractère */
    public static int poschar = 0;
    public static boolean acceptedToken = true; // pour le Tokeniser

    public static boolean conceptFound = false; // pour le Tokeniser attention ne supporte pas la concurrence

    public static boolean wordFound = false; // pour le Tokeniser attention ne supporte pas la concurrence

    public static boolean wordExternal = false; // pour le Tokeniser attention ne supporte pas la concurrence


    /** Crée un analyseur lexical. sur la source (Reader) avec la liste de mots
     * à ne pas indexer SOF. Se place sur la première occurence de la source
     * @param s chaîne de caractères à parser
     * @param SOF la liste de mots à ne pas indexer
     */
    public DoParse(String s, SetOfWords SOF) {
        //msg("doparse:"+s);
        try {

            in = new StringReader(s);
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        poschar = 0;
        acceptedToken = false;
        EOFflag = false;
        WORD_DEFINITION.next(this);
        DontIndexThis = SOF;
    }

    /**
     * Crée un analyseur lexical. sur la source (Reader) avec la liste de mots
     * à ne pas indexer SOF. Se place sur la première occurence de la source
     * @param rdr la source à analyser
     * @param SOF la liste de mots à ne pas indexer
     */
    public DoParse(Reader rdr, SetOfWords SOF) {
        try {

            in = new BufferedReader(rdr);
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        poschar = 0;
        acceptedToken = false;
        EOFflag = false;
        WORD_DEFINITION.next(this);
        DontIndexThis = SOF;
    }

    /**
     * cette méthode est utilisée pour établir le coût minimum du parsing.Les indices ne sont pris que dans l'intervalle total
    String word="";
     * pas de minuscule, pas de normalisation, ...
     * pour l'utiliser, il faut la renommer start !
     * @param glue l'indexeur de référence
     */
    protected final void start_quick(IdxStructure glue) {
        String word = "";
        int n;
        int pa = 1;  /* les positions partent de 1 */
        while (!EOFflag) {  // WIPO break length
            //System.out.println(id.lastdoc+","+DocBag.differentWords+","+id.lastword+","+cw);

            if (cw.length() >= WORD_MINLENGTH) {  //&& !DontIndexThis.check(cw)  ){
                // ici word est NORMALISE

                word = (cw.toString());
                //   one pass, on ajoute des termes au fur et a mesure ...
                n = glue.wordstable.get(word);
                if (n == -1) { // new word

                    n = glue.wordstable.put(word);
                // System.out.print(word+",");
                //n = id.wordstable.get(word);
                }
                glue.Indexer.addref(n, glue.lastRecordedDoc, pa, poschar);
                pa++;
            }
            WORD_DEFINITION.next(this);
        }
        nbToken = pa;  // memorise la nombre de termes reconnus

    }

    /**
     * commence le parsing, et indexe. n'est pas synchronisée! Les indices ne sont pris que dans l'intervalle total
     * @param id l'indexeur de référence
     */
    protected final void start(IdxStructure id) {
        glue = id;
        String word = "", wordWithoutStem = "";
        int n;
        int pa = 1;  /* les positions partent de 1 */
        int cntword = 0;
        int ratiolimit = WORD_NFIRSTOFDOC * 8 / 3;  // from experiment !!
        //       while (!EOFflag) {
        //       while (!EOFflag&&DocBag.differentWords<=id.NfirstWordOfDoc) {  // WIPO break length with docBag

        while (!EOFflag && cntword <= ratiolimit) {  // WIPO break length
            //System.out.println(id.lastdoc+","+DocBag.differentWords+","+id.lastword+","+cw);

            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            if (word.length() >= WORD_MINLENGTH && !DontIndexThis.check(word)) {
                acceptedToken = true;
                // System.out.println("doparse:"+word);
                //                if (WORD_USE_STEMMER){
                //                    wordWithoutStem=word;
                //                    word=Stemmer.stemmingOfW(word);}
                cntword++;
                // ici word est NORMALISE
                if (WORD_TWOPASS) {// two pass, on ajoute plus de terme, ils sont sélectionnés dans la passe 1, pour la classification 

                    n = id.wordstable.get(word);
                    if (n != -1) { // existing word

                        id.Indexer.addref(n, id.lastRecordedDoc, pa, poschar);
                        pa++;
                    }
                } else {// one pass, on ajoute des termes au fur et a mesure ...

                    n = id.wordstable.get(word);
                    //msg("get"+n+","+word);
                    if (n == -1) { // new word

                        n = id.wordstable.put(word);
                        //msg("put"+n+","+word);
                        // System.out.print(word+",");
                        //n = id.wordstable.get(word);
                        if (STEM_KEEP_LIST && WORD_USE_STEMMER) { // ouvre une liste pour enregistrer les mots origines

                            id.stemList[n] = new TreeSet<String>();
                        }
                    }
                    id.Indexer.addref(n, id.lastRecordedDoc, pa, poschar);
                    if (STEM_KEEP_LIST && WORD_USE_STEMMER) { // sauver les termes sans lemmatisation

                        id.stemList[n].add(wordWithoutStem);
                    }
                    pa++;
                }
            } else {
                acceptedToken = false;
            }
            //TimerNano t=new TimerNano("next",false);
            WORD_DEFINITION.next(this);
        //t.stop(false);
        }
        nbToken = pa;  // memorise la nombre de termes reconnus

        id.lastRecordedWord = id.wordstable.getCount(); // met a jour le dernier mot connu

    }

    /** construit un doc bag a partir de l'entrée du parseur. Les indices ne sont pris que dans l'intervalle de lecture
     * la méthode est protégée pour séquentialiser les parsing concurrent
     * @param id indexeur de référence
     * @return vecteur au format DocBag
     *
     */
    synchronized public final int[] getDocBag(IdxStructure id) {
        String word;
        int n;
        DocBagInteractive dbi = new DocBagInteractive(id);

        //System.out.println("getDocBag eof:"+EOFflag);

        //       while (!EOFflag) {
        while (!EOFflag && dbi.getDifferentWords() <= WORD_NFIRSTOFDOC) {  // WIPO break length
            //System.out.println(cw);

            if (cw.length() >= WORD_MINLENGTH) {
                word = WORD_DEFINITION.normaliseWord(id, cw.toString());
                if (!DontIndexThis.check(word)) {
                    acceptedToken = true;
                    if (WORD_USE_STEMMER) {
                        word = Stemmer.stemmingOfW(word);
                    }
                    // ici word est NORMALISE
                    n = id.wordstable.get(word);
                    if (n != -1 && n < id.lastUpdatedWord) { // existing word
                        //System.out.println("in doc bag:"+word);

                        dbi.addWord(n); // for winnow ...  // could be optimize for interactive purpose

                    }
                } else {
                    acceptedToken = false;
                }
            } else {
                acceptedToken = false;
            }
            WORD_DEFINITION.next(this);
        }
        return dbi.compact();
    }

    /** construit un doc bag a partir de l'entrée du pareur. Les indices ne sont pris que dans l'intervalle de lecture
     * la méthode est protégée pour séquentialiser les parsing concurrent
     * @param id indexeur de référence
     * @return vecteur au format DocBag
     *
     */
    synchronized public final int[] getConceptualDocBag(IdxStructure id) {
        String word;
        int n;
        DocBagInteractive dbi = new DocBagInteractive(id);

        //System.out.println("getDocBag eof:"+EOFflag);

        while (!EOFflag) {
            String wordNoStem = cw.toString();
            //System.out.println("conceptualDB:"+cw+".");
            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            //System.out.println("after normal:"+word+".");
            if (!DontIndexThis.check(word)) {
                //msg(word+","+DontIndexThis.check(word));
                acceptedToken = true;
                // ici word est NORMALISE
                n = id.wordstable.get(word);
                if (n != -1 && n < id.lastUpdatedWord) { // existing word
                    //System.out.println("in doc bag:"+word);

                    if (conceptFound && !wordFound) {
                        dbi.addWord(n);
                        System.out.println("in doc bag:" + word);
                    }
                    if (!conceptFound && wordFound) {
                        if (IDX_CONCEPTUAL) { //conceptuel

                            n = id.wordstable.get(stem(id, "_EN", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_FR", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_IT", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_DU", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_DE", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_ES", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_FI", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                            n = id.wordstable.get(stem(id, "_SW", wordNoStem));
                            if (n != -1 && n < id.lastUpdatedWord) {
                                dbi.addWord(n);
                            }
                        } else {
                            dbi.addWord(n);  // native seulement
                        //System.out.println("in doc bag:"+word);

                        }
                    }//System.out.println("in doc bag:"+word);}

                }
            } else {
                if (!ACTUAL_LANGUAGE.equals("_RU") && conceptFound) {
                    acceptedToken = false;
                } else {
                    acceptedToken = true;
                }
            }
            WORD_DEFINITION.next(this);
        }
        return dbi.compact();
    }

    /** construit une requête a partir de l'entrée du parseur. Les indices ne sont pris que dans l'intervalle de lecture
     * la méthode est protégée pour séquentialiser les parsing concurrent
     * @param id indexeur de référence
     * @return une requete QL
     *
     */
    synchronized public final String buildConceptualRequest(IdxStructure id) {
        String word;
        int n;
        String request = "(";
        boolean firstAND = true;
        boolean firstOR = true;
        while (!EOFflag) {
            //System.out.println(cw);
            String wordNoStem = cw.toString();
            word = WORD_DEFINITION.normaliseWord(id, cw.toString());
            if (!DontIndexThis.check(word)) {
                acceptedToken = true;
                // ici word est NORMALISE

                n = id.wordstable.get(word);
                //msg(word+" "+n+" "+firstOR+" "+conceptFound+" "+wordFound);
                if (n != -1 && n < id.lastUpdatedWord) { // existing word

                    if (wordFound) {
                        firstOR = true;
                    }
                    if (!firstAND && wordFound && !ACTUAL_LANGUAGE.equals("_RU")) {
                        request += ") AND (";
                    }
                    if (!firstAND && wordFound && ACTUAL_LANGUAGE.equals("_RU")) {// cas du russe

                        request += ") AND ( \"" + word + "\"";
                    }
                    if (firstAND && wordFound && ACTUAL_LANGUAGE.equals("_RU")) {// cas du russe

                        request += "\"" + word + "\"";
                    }
                    if (!firstOR && conceptFound && !wordFound) {
                        request += " OR \"" + word + "\"";
                    }
                    if (firstOR && !conceptFound && wordFound) {
                        request += "\"" +
                                stem(id, "_EN", wordNoStem) + "\" OR \"" + stem(id, "_FR", wordNoStem) + "\" OR \"" + stem(id, "_IT", wordNoStem) + "\" OR \"" + stem(id, "_DU", wordNoStem) + "\" OR \"" + stem(id, "_DE", wordNoStem) + "\" OR \"" + stem(id, "_ES", wordNoStem) + "\" OR \"" + stem(id, "_FI", wordNoStem) + "\" OR \"" + stem(id, "_SW", wordNoStem) + "\"";
                        firstOR = false;
                    } // cas d'un mot non reconnu

                    if (firstOR && conceptFound && !wordFound) {
                        request += "\"" + word + "\"";
                        firstOR = false;
                    }
                    firstAND = false;
                }
            } else {
                if (!ACTUAL_LANGUAGE.equals("_RU") && conceptFound) {
                    acceptedToken = false;
                } else {
                    acceptedToken = true;
                }

            }
            WORD_DEFINITION.next(this);
        }
        request += ")";
        return request;
    }

    synchronized public static String stem(IdxStructure id, String lang, String word) {
        if (lang.equals("_EN")) {
            Stemmer.init("english");
        }
        if (lang.equals("_FR")) {
            Stemmer.init("french");
        }
        if (lang.equals("_IT")) {
            Stemmer.init("italian");
        }
        if (lang.equals("_DU")) {
            Stemmer.init("dutch");
        }
        if (lang.equals("_DE")) {
            Stemmer.init("german");
        }
        if (lang.equals("_ES")) {
            Stemmer.init("spanish");
        }
        if (lang.equals("_FI")) {
            Stemmer.init("finnish");
        }
        if (lang.equals("_SW")) {
            Stemmer.init("swedish");
        }
        String res = WORD_DEFINITION.normaliseWord(id, word);
        // recharge le stemmer courant
        if (ACTUAL_LANGUAGE.equals("_EN")) {
            Stemmer.init("english");
        }
        if (ACTUAL_LANGUAGE.equals("_FR")) {
            Stemmer.init("french");
        }
        if (ACTUAL_LANGUAGE.equals("_IT")) {
            Stemmer.init("italian");
        }
        if (ACTUAL_LANGUAGE.equals("_DU")) {
            Stemmer.init("dutch");
        }
        if (ACTUAL_LANGUAGE.equals("_DE")) {
            Stemmer.init("german");
        }
        if (ACTUAL_LANGUAGE.equals("_ES")) {
            Stemmer.init("spanish");
        }
        if (ACTUAL_LANGUAGE.equals("_FI")) {
            Stemmer.init("finnish");
        }
        if (ACTUAL_LANGUAGE.equals("_SW")) {
            Stemmer.init("swedish");
        }
        if (ACTUAL_LANGUAGE.equals("_RU")) {
            Stemmer.init("russian");
        }
        return res;
    }

    /** utilise ce parseur pour le compte d'un référenceur (construire la liste des mots). Les indices ne sont pris que dans l'intervalle de lecture
     * la méthode est protégée pour séquentialiser les parsing concurrent
     * @param id indexeur de référence
     * @param s le référenceur faisant la demande
     *
     */
    synchronized public final void scanString(IdxStructure id, IdxReference s) {  // utilisé par le référenceur

        String word;
        int n;
        int idxW;

        while (!EOFflag) {
            //System.out.println(cw);
            idxW = IdxReference.NotIndexed;
            {
                s.word[s.lastscan] = (cw.toString()); // save the value

                if (cw.length() >= WORD_MINLENGTH && !DontIndexThis.check(cw.toString())) {
                    word = WORD_DEFINITION.normaliseWord(id, cw.toString());
                    if (WORD_USE_STEMMER) {
                        word = Stemmer.stemmingOfW(word);
                    }
                    //System.out.println("scan:"+word);
                    n = id.wordstable.get(word);
                    if (n != -1 && n < id.lastUpdatedWord) {
                        idxW = n;
                    } // get the ref}

                }
                s.idxW[s.lastscan] = idxW;
                s.lastscan++;
            }
            if (s.lastscan >= IdxReference.MaxIndexedW) {
                System.out.print("WARNING TRY to overpass ScannedString.MaxIndexedW");
                break;
            }
            WORD_DEFINITION.next(this);
        }  // while

    }

    /** utilise ce parseur pour récupérer une liste d'id de termes (construire la liste des mots). Les indices ne sont pris que dans l'intervalle de lecture
     * la méthode est protégée pour séquentialiser les parsing concurrent
     * @param id indexeur de référence
     *
     */
    synchronized public final int[] scanString(IdxStructure id) {  // utilisé par QL

        String word;
        int lastscan = 0,  n;
        int[] idxW = new int[MAX_CITATION]; // max de terme dans une citation

        while (!EOFflag) {
            //System.out.println(cw);
            if (cw.length() >= WORD_MINLENGTH && !DontIndexThis.check(cw.toString())) {
                word = WORD_DEFINITION.normaliseWord(id, cw.toString());
                if (WORD_USE_STEMMER) {
                    word = Stemmer.stemmingOfW(word);
                }
                //System.out.println("scan:"+word);
                n = id.wordstable.get(word);
                if (n != -1 && n < id.lastUpdatedWord) {
                    if (lastscan < MAX_CITATION) {
                        idxW[lastscan] = n;
                        lastscan++;
                    } // get the ref}

                }
            }
            WORD_DEFINITION.next(this);
        }  // while

        int[] res = new int[lastscan];
        System.arraycopy(idxW, 0, res, 0, lastscan);
        return res;
    }

        /** utilise ce parseur pour récupérer une liste d'id de termes (construire la liste des mots). Les indices ne sont pris que dans l'intervalle de lecture
     * la méthode est protégée pour séquentialiser les parsing concurrent
     * @param id indexeur de référence
     *
     */
    synchronized public final String[] tokenizeString(IdxStructure id) {  // utilisé par QL
        int  MAXWORD=1000;
        String word;
        int lastscan = 0,  n;
        String[] idxW = new String[MAXWORD]; // max de terme dans une phrase

        while (!EOFflag) {
            //System.out.println(cw);
           word = WORD_DEFINITION.normaliseWord(id, cw.toString());
           if (word.length() >= WORD_MINLENGTH && !DontIndexThis.check(word)) {
                if (WORD_USE_STEMMER) {
                    word = Stemmer.stemmingOfW(word);
                }
                //System.out.println("scan:"+word);
                    if (lastscan < MAXWORD) {
                        idxW[lastscan] = word;
                        lastscan++;

                }
            }
            WORD_DEFINITION.next(this);
        }  // while

        String[] res = new String[lastscan];
        System.arraycopy(idxW, 0, res, 0, lastscan);
        return res;
    }
}
