package org.olanto.cat;

import org.olanto.cat.util.RandomizeDoc;
import org.olanto.idxvli.extra.DocBag;
import org.olanto.idxvli.DoParse;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import static org.olanto.idxvli.IdxConstant.*;

/** classe pour générer un réseau de neurone par apprentissage et pour le tester.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * toute autre utilisation est sujette à autorisation
 * <hr>
 * <b>principe de fonctionnement</b>
 * basé sur l'apprentissage et sur les réseaux de neurones
 *
 */
public class NNOneNFiltered {

    private static Experiment collectResult;

    /**
     * @param INMEMORY the INMEMORY to set
     */
    public static void setINMEMORY(boolean _INMEMORY) {
        INMEMORY = _INMEMORY;
    }

    /**
     * @param NB_PROC the NB_PROC to set
     */
    public static void setNB_PROC(int _NB_PROC) {
        NB_PROC = _NB_PROC;
    }

    /**
     * @return the collectResult
     */
    public static Experiment getCollectResult() {
        return collectResult;
    }

    /**
     * @param collectResult the collectResult to set
     */
    public static void setCollectResult(Experiment _collectResult) {
        collectResult = _collectResult;
    }

    static class theThread extends Thread {

        float[] cumul;     // the result of a evaluation of net neuron
        public final static int THREADFAIL = 1;
        public final static int THREADPASS = 0;
        int _status;
        int id;
        int start;
        int stop;

        public int status() {
            return _status;
        }

        public theThread(int _id, int _start, int _stop) {
            _status = THREADFAIL;
            id = _id;
            stop = _stop;
            start = _start;
            id = _id;
        }

        public void run() {
            int count = 0;
            cumul = new float[maxgroup]; // init structure

            for (int i = start + id; i < stop; i += NB_PROC) {
                count++;
                int d = RandomizeDoc.rand[i];
                if (testDocOK(d)) {
                    int[] g = ActiveGroup.getGroup(d);
                    computeWinnow(cumul, d);
                    correctWinnow(cumul, g, d);
                }
            }

            //System.out.print("Thread " + id + "count: " + count + "\n");
            //System.out.print("Thread " + id + ": End with success\n");
            System.out.print(" - " + id);
            _status = THREADPASS;
            stop();
            System.out.print("Thread " + id + ": Didn't expect to get here!\n");
            _status = THREADFAIL;
        }
    }
    private static final String detailFolderName = "detail";
    /** signature
     */
    private static String filterName;
    private static String s = null;
    /** niveau 1 de catégorisation
     */
    private static int NB_PROC = 4;
    /** niveau 1 de catégorisation
     */
    public static final int CAT_SECTION = 1;
    /** niveau 2 de catégorisation
     */
    public static final int CAT_CLASS = 3;
    /** niveau 3 de catégorisation
     */
    public static final int CAT_SUBCLASS = 4;
    /** niveau 4 de catégorisation
     */
    public static final int CAT_MAINGROUP = 7;
    /** pondération unitaire des termes
     */
    public static final int SDF_ONE = 0;
    /** pondération racine carrée de la fréquence du terme dans le document
     */
    public static final int SDF_SQUARE = 1;
    /** pondération fréquence du terme dans le document
     */
    public static final int SDF_N = 2;
    /** pondération log de la fréquence du terme dans le document
     */
    public static final int SDF_LN = 3;
    /** pondération puissance de la fréquence du terme dans le document
     */
    public static final int SDF_POWER = 4;
    /** puissance associée ? SDF_POWER */
    private static double POWER = 0.5;
    /** initialisation forcée */
    private static int FORCE_INIT = 0;
    private static int method = 0; // method for sdf
    static float[] sdf = new float[DocBag.MAXOCCINDOC];  // weighting of feature

    /* learn on multi-class */
    /** apprentissage uniquement sur la catégorie principale du document
     */
    public static final boolean LEARNMULTIGROUP = true;
    /** apprentissage sur les catégories secondaires du document
     */
    public static final boolean LEARNMONOGROUP = false;
    static boolean learntype = true;
    /* normalised feature weigthing*/
    /** normalisation des pondérations
     */
    public static final boolean NORMALISED = true;
    /** pas de normalisation des pondérations
     */
    public static final boolean UNNORMALISED = false;
    static boolean normalisedFeature = true;
    /* flag for system */
    /** mode montrant les résultats intermédiares de l'apprentissage
     */
    private static boolean VERBOSE = false; /* show training result ... */

    /** chargement des documents en mémoire (pour accélére l'apprentissage)
     */
    private static boolean INMEMORY = true; /* keep DocBag in memory alway for Fast */

    /** montrer les détails des tests
     */
    private static final int nfirst = 3;  // test on ...

    /* filter feature */
    private static IdxStructure glue;  // global variable
    //static IdxIndexer Indexer; // global variable
    private static final byte MINBYTE = -127;  // limit for compact implementation
    private static final byte MAXBYTE = +127;
    /** seuil minimum pour le minimum d'occurence d'un terme dans le corpus selectionné
     */
    public static int GLOBALMINOCC = 2; // inferior limit for minocc
    private static int maxtrain = 0;  // define 0..maxtrain -> the set of training documents
    private static int lastdoc;      // define maxtrain..lastdoc -> the set of testing documents
    private static int lasttesttraindoc; // define 0..lasttesttraindoc -> the subset of training documents
    private static int lasttestdoc;      // define lasttesttraindoc..lasttestdoc -> the subset of testing documents
    private static int nbfeatures;  // nb feature used
    private static int lastword;
    static byte[][] nnc;     // neuron net positive Winnow compact
    static float[] alfaPn;     // puissance de alfa pour ositive Winnow compact
    static float[] alfaNn;     // puissance de alfa pour ositive Winnow compact
    static int maxgroup = 0; // nb of group
    static int maxused = 0;  // nb of active feature
    private static byte[] wordusetrain;  // set of words used in the training docs
    static byte[] worduse;       // set of words used as feature (after filtering)
    static int[] wordAtIdx;      // map for compacting words indexind
    static int[] wordFreq;       // map for compacting words freq
    private static int[] wordOcctrain;   // occ for training doc

//    static int[] docbag;          // active docbag (for train, correct, and evaluation)
    private static int[][] alldocbag;    //  docbag if in memmory
    private static float[] cumulShared;     // the result of a evaluation of net neuron
    private static float add = 1.06f,  qlevel = 1000,  deltamin,  deltamax,  qlevelminusdelta,  qlevelplusdelta;
    private static int minocc,  maxocc;
    private static long avg;  // modif 30.1.2006
    private static NNBottomGroup BootGroup;
    static NNLocalGroupFiltered ActiveGroup;
    static int categorylevel;
    static String prefix;
    private static int docbagAvgLength;
    private static int docbagMinLength;
    private static int docbagMaxLength;

    NNOneNFiltered() {
    }
    ;

    /** initialisation du réseau. Le réseau est initialisé avec un groupe de document pour l'apprentissage et pour les tests.
     * L'initialisation se fait une fois et plusieurs apprentissages et tests consécutifs sont possibles.
     *
     * @param _BootGroup les groupes de documents d'apprentissage et de test.
     * @param _glue la structure d'index permettant de décoder les noms des documents et les termes indexés.
     * @param _normalised normalisation ({NORMALISED} ou {UNNORMALISED}).
     * @param _method méthode de pondération des neurones (SDF_ONE, SDF_SQUARE, SDF_N, SDF_LN).
     *
     * NORMALISED et SDF_SQUARE donne les meilleurs résultats (dans les corpus testés)
     */
    public static void init(String _s, NNBottomGroup _BootGroup, IdxStructure _glue, boolean _normalised, int _method) {
        s = _s;
        glue = _glue;
        BootGroup = _BootGroup;
        lastdoc = glue.lastRecordedDoc;
        lastword = glue.lastRecordedWord;
        normalisedFeature = _normalised;
        method = _method;
        if (INMEMORY) {
            alldocbag = new int[lastdoc][];
            for (int d = 1; d < lastdoc; d++) {
                alldocbag[d] = glue.getBagOfDoc(d);
            }
        }

        initSdf(method);
        avgLength();
        System.out.println("GLOBALMINOCC: " + GLOBALMINOCC + " , MAX features:" + maxused);
    }

    static final void initSdf(int method) {
        if (method == SDF_ONE) {
            for (int i = 0; i < sdf.length; i++) {
                sdf[i] = 1;
            }
        } else if (method == SDF_N) {
            for (int i = 0; i < sdf.length; i++) {
                sdf[i] = i;
            }
        } else if (method == SDF_LN) {
            for (int i = 0; i < sdf.length; i++) {
                sdf[i] = (float) Math.log(i + 1);
            }
        } else if (method == SDF_SQUARE) {
            for (int i = 0; i < sdf.length; i++) {
                sdf[i] = (float) Math.sqrt(i);
            }
        } else if (method == SDF_POWER) {
            for (int i = 0; i < sdf.length; i++) {
                sdf[i] = (float) Math.pow(i, POWER);
            }
        } else {
            System.out.println("ERROR in initSdf:" + method);
        }
    }

    static final void computeWinnow(float[] cumul, int d) {
        computeWinnowPosCompact(cumul, d);
        normalisedFeature(cumul, d);
    }

    static final void correctWinnow(float[] cumul, int[] group, int d) {
        correctWinnowPosCompact(cumul, group, d);
    }

    static final void initWinnow() {
        worduse = new byte[maxused];
        for (int j = 0; j < lastword; j++) {
            int iiii = wordAtIdx[j];
            if (iiii != NOT_FOUND) {
                worduse[iiii] = wordusetrain[j]; // init from training doc

                if (worduse[iiii] == 0x4) {
                    if (wordOcctrain[j] >= minocc && wordOcctrain[j] <= maxocc) {
                        ; // ok

                    } else {
                        worduse[iiii] = 0x1;
                    }
                }
            }
        }
        initWinnowPosCompact();
        cumulShared = new float[maxgroup]; // init structure


    }

    /* positive winnow and balanced implementation */
    static final void computeWinnowPosCompact(float[] cumul, int d) {

        for (int i = 0; i < maxgroup; i++) {
            cumul[i] = 0;
        } //equivaltent to cumul = new float[maxgroup];

        int[] docbag = alldocbag[d];
        for (int i = 0; i < docbag.length; i++) {
            int iiii = wordAtIdx[docbag[i] / DocBag.MAXOCCINDOC];  // with indirection
            if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard
                float wsdf = sdf[docbag[i] % DocBag.MAXOCCINDOC]; // eval feature weight
                // if  (DISCARDPROCESS) worduse[iiii]=0x8;
                for (int j = 0; j < maxgroup; j++) {
                    if (nnc[iiii][j] < 0)// negative
                    {
                        cumul[j] += alfaNn[-nnc[iiii][j]] * wsdf;
                    } else // positive
                    {
                        cumul[j] += alfaPn[nnc[iiii][j]] * wsdf;
                    }
                }
            }
        }
    }

    static final void computeWinnowPosCompactOnVector(float[] cumul, int[] docbag) {
        cumul = new float[maxgroup];
        // showVector(docbag);
        for (int i = 0; i < docbag.length; i++) {
            int iiii = wordAtIdx[docbag[i] / DocBag.MAXOCCINDOC];  // with indirection
            if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard
                float wsdf = sdf[docbag[i] % DocBag.MAXOCCINDOC]; // eval feature weight
                // if  (DISCARDPROCESS) worduse[iiii]=0x8;
                for (int j = 0; j < maxgroup; j++) {
                    if (nnc[iiii][j] < 0)// negative
                    {
                        cumul[j] += alfaNn[-nnc[iiii][j]] * wsdf;
                    } else // positive
                    {
                        cumul[j] += alfaPn[nnc[iiii][j]] * wsdf;
                    }
                }
            }
        }
    //showVector(cumul);
    //showVector(nnc[wordAtIdx[docbag[0]/DocBag.MAXOCCINDOC]]);
    }

    static final void correctWinnowPosCompact(float[] cumul, int[] group, int d) {
        int[] docbag = alldocbag[d];
        //showVector(cumul);
        for (int j = 0; j < maxgroup; j++) {
            if (cumul[j] > qlevelminusdelta) { // predict in the group
                if (!inGroup(group, j)) {// minimize if not ingroup
                    for (int i = 0; i < docbag.length; i++) {
                        int iiii = wordAtIdx[docbag[i] / DocBag.MAXOCCINDOC];  // with indirection
                        if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard
                            if (nnc[iiii][j] > MINBYTE) {
                                nnc[iiii][j]--;
                            }
                        // if  (DISCARDPROCESS) wordnmistake[iiii]++;
                        }
                    }
                }
            }
            if (cumul[j] < qlevelplusdelta) { // not in the group
                if (inGroup(group, j)) {// reinforce
                    for (int i = 0; i < docbag.length; i++) {
                        int iiii = wordAtIdx[docbag[i] / DocBag.MAXOCCINDOC];  // with indirection
                        if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard
                            if (nnc[iiii][j] < MAXBYTE) {
                                nnc[iiii][j]++;
                            }
                        // if  (DISCARDPROCESS) wordpmistake[iiii]++;
                        }
                    }
                }
            }
        }
    }

    static void initWinnowPosCompact() {
        nnc = new byte[maxused][maxgroup];
        for (int j = 0; j < maxused; j++) {
            for (int i = 0; i < maxgroup; i++) {
                nnc[j][i] = (byte) FORCE_INIT;
            }
        }

    }

    static final void normalisedFeature(float[] cumul, int d) {
        if (normalisedFeature) {
            int[] docbag = alldocbag[d];
            float normalised = 0;
            for (int i = 0; i < docbag.length; i++) {  // compute normalisation for this doc
                int iiii = wordAtIdx[docbag[i] / DocBag.MAXOCCINDOC];  // with indirection
                if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard
                    normalised += sdf[docbag[i] % DocBag.MAXOCCINDOC];
                }
            }
            for (int j = 0; j < maxgroup; j++) {  // normalised
                cumul[j] /= normalised;
            }
        }
    }

    static boolean testDocOK(int d) { // selecting doc for training

        int g = ActiveGroup.getMainGroup(d);
        return (g != NOT_FOUND) && ActiveGroup.groupSize(d) >= 1; //==1 monogroup

    }

    static boolean testDocOK2(int d) { // selecting doc for testing

        int g = ActiveGroup.getMainGroup(d);
        return (g != NOT_FOUND) && ActiveGroup.groupSize(d) >= 1; //==1 monogroup

    }

    static void featureWinnow(String title) {
        // showVector(docbag);
        int totused = 0, totopen = 0, totdisc = 0, totfilt = 0;
        for (int i = 0; i < maxused; i++) {
            if (worduse[i] == 0x8) {
                totused++;
            }
            if (worduse[i] == 0x4) {
                totopen++;
            }
            if (worduse[i] == 0x2) {
                totdisc++;
            }
            if (worduse[i] == 0x1) {
                totfilt++;
            }
        }
        System.out.println(title + " used:" + totused + ", open:" + totopen + ", discarded:" + totdisc + ", filtred:" + totfilt);
        nbfeatures = totopen;
    }

    static boolean inGroup(int[] group, int g) {
        int max;
        if (learntype) {
            max = group.length; // learn multi

        } else {
            max = 1; // learn main only

        }
        for (int i = 0; i < max; i++) {
            if (group[i] == g) {
                return true;
            }
        }
        return false;
    }

    static void avgLength() {
        Timer t1 = new Timer("avgLength()");
        int minlength = 999999999, maxlength = -1;
        int nbdoc = 0, totdoc = 0;
        int[] doclength = new int[lastdoc];
        wordusetrain = new byte[lastword];  // 8=used in train 4=used training 2=discard 1=TF is too low

        wordOcctrain = new int[lastword];  // occ in train Set

        for (int id = 1; id < lastdoc; id++) { // use train vocabulary
            //  for(int id=maxtrain;id<lastdoc;id++){ // use test vocabulary

            if (BootGroup.doctype[id] == NNBottomGroup.TRAINDOC) { // only training doc

                int[] docbag = alldocbag[id];
                for (int n = 0; n < docbag.length; n++) {
                    //System.out.println(id+","+n+","+docbag[n]+","+DocBag.MAXOCCINDOC);
                    wordusetrain[docbag[n] / DocBag.MAXOCCINDOC] = 0x4;
                    wordOcctrain[docbag[n] / DocBag.MAXOCCINDOC]++;
                }
                nbdoc++;
                totdoc += docbag.length;
                doclength[id] = docbag.length;
                if (docbag.length < minlength) {
                    minlength = docbag.length;
                }
                if (docbag.length > maxlength) {
                    maxlength = docbag.length;
                }
            }
        }
        avg = totdoc / nbdoc;
        System.out.println("#doc:" + nbdoc + ", avg:" + avg + ", min:" + minlength + ", max:" + maxlength);
        docbagAvgLength = (int) avg;
        docbagMinLength = minlength;
        docbagMaxLength = maxlength;

        maxused = 0;
        wordAtIdx = new int[lastword];
        for (int j = 0; j < lastword; j++) {// translate

            if (wordOcctrain[j] >= GLOBALMINOCC) {
                wordAtIdx[j] = maxused;
                maxused++;
            } else {
                wordAtIdx[j] = NOT_FOUND;
            }
        }

        wordFreq = new int[maxused];
        for (int j = 0; j < lastword; j++) {// compute freq

            if (wordAtIdx[j] != NOT_FOUND) {
                wordFreq[wordAtIdx[j]] = glue.getOccOfW(j); // document appearance

            }
        }

        float startwith;
        if (normalisedFeature) {
            //     startwith=qlevel; } // normalisation dans le calcul
            startwith = qlevel;
        } // normalisation dans le calcul
        else {
            startwith = qlevel / (float) avg;
        }  // moyenne des longueurs des doc

        if (VERBOSE) {
            System.out.println("startwith:" + startwith);
        }
        alfaPn = new float[MAXBYTE + 1];
        alfaNn = new float[MAXBYTE + 1];
        alfaPn[0] = startwith;
        alfaNn[0] = startwith;
        for (int i = 0; i < MAXBYTE; i++) {  // init power of alfa

            alfaPn[i + 1] = alfaPn[i] * add;
            alfaNn[i + 1] = alfaNn[i] / add;  // min is not used symetric promotion and demotion

        }
        t1.stop();

    }

    /** apprentissage du réseau de neurone.
     * Le réseau est spécifié par son niveau de classification et par un éventuel sous ensemble ? classifier.
     * @param _categorylevel niveau de classification.
     * @param _prefix sous ensemble ? classifier.
     * @param repeatK facteur de répétition de l'apprentissage (4, par exemple)
     * @param _qlevel valeur d'initialisation des neurones (1000, par exemple).
     * @param _add facteur de promotion (1.06 par exemple).
     * @param _minocc nombre minimum d'occurences du terme dans le corpus pour ?tre sélectionné (3, par exemple).
     * @param _maxocc nombre maximum d'occurences du terme dans le corpus pour ?tre sélectionné (1000, par exemple).
     * @param _deltamin fronti?re épaisse (en dessous du seuil) (300, par exemple).
     * @param _deltamax fronti?re épaisse (en dessus du seuil) (300, par exemple).
     * @param verbosetrain montre les détails du groupe et de la répartition des documents de test et d'apprentissage.
     * @param testtrain effectuer le test avec les documents d'apprentissage
     * @param trainpart pourcentage pris pour l'apprentissage, le reste est pour le test
     *
     * NORMALISED et SDF_SQUARE donne les meilleurs résultats (dans les corpus testés)
     */
    public static void TrainWinnow(String[] filter, int _categorylevel, String _prefix, int repeatK, float _qlevel, float _add, int _minocc, int _maxocc,
            float _deltamin, float _deltamax, boolean verbosetrain,
            boolean testtrain, int trainpart) {
        // init global
        filterName="";
        
        for (int i = 0; i < filter.length; i++) {
            filterName += filter[i];
        }
        prefix = _prefix;
        categorylevel = _categorylevel;
        System.out.println(categorylevel + "." + prefix);
        if (RandomizeDoc.lasttraindoc == 0) {  // ne le faire qu'une fois pour préserver l'ordre
            System.out.println("randomize");
            RandomizeDoc.init(glue, BootGroup, prefix);
        }
        if (testtrain) { // on utilise une partie du train set pour les tests

            lasttesttraindoc = (trainpart * RandomizeDoc.lasttraindoc) / 100;  // trainpart %

            lasttestdoc = RandomizeDoc.lasttraindoc;
            maxtrain = lasttesttraindoc;

        } else { // on utilise le catalogue de test pour les tests

            lasttesttraindoc = RandomizeDoc.lasttraindoc;  // 100 %

            maxtrain = RandomizeDoc.lasttraindoc;
            lasttestdoc = RandomizeDoc.lasttestdoc;
        }

        System.out.println("Train 0.." + lasttesttraindoc + " Test .." + lasttestdoc);


        ActiveGroup = new NNLocalGroupFiltered(maxtrain, filter, BootGroup, glue, categorylevel, verbosetrain);


        maxgroup = ActiveGroup.maxgroup;
        System.out.println("Active group:" + maxgroup);

        if (verbosetrain) {
            showRepartitionTX();
        }
        add = _add;


        if (_minocc >= GLOBALMINOCC) {
            minocc = _minocc;
        } else {
            System.out.println("minocc>=GLOBALMINOCC initialise to:" + GLOBALMINOCC);
            minocc = GLOBALMINOCC;
        }
        maxocc = _maxocc;
        qlevel = _qlevel;
        deltamin = _deltamin;
        deltamax = _deltamax;
        qlevelminusdelta = qlevel - deltamin;
        qlevelplusdelta = qlevel + deltamax;

        Timer t1 = new Timer("TrainWinnow");
        //MemoryEconomizer.usedMemory("before initWinnow");
        initWinnow();
        //MemoryEconomizer.usedMemory("After initWinnow");
        featureWinnow("filter ");
        if (VERBOSE) {
            featureWinnow("filter ");
        //
        }
        if (collectResult != null) {
            collectResult.maxgroup = maxgroup;
            collectResult.maxtrain = maxtrain;
            collectResult.lastdoc = lastdoc;
            collectResult.nbfeatures = nbfeatures;
            collectResult.docbagAvgLength = docbagAvgLength;
            collectResult.docbagMinLength = docbagMinLength;
            collectResult.docbagMaxLength = docbagMaxLength;
            collectResult.maxUsed = maxused;
        }

        long start = System.currentTimeMillis();
        int maxepoch = 1;
        int epochsize = maxtrain / maxepoch;
        for (int cycle = 0; cycle < 1; cycle++) {
            for (int epoch = 0; epoch < maxepoch; epoch++) {
                int epochstart = epoch * epochsize;
                int epochstop = epochstart + epochsize;
                for (int k = 0; k < repeatK; k++) {
//                    for (int id = epochstart; id < epochstop; id++) {
//                        int d = RandomizeDoc.rand[id];
//                        if (testDocOK(d)) {
//                            int[] g = ActiveGroup.getGroup(d);
//                            computeWinnow(d);
//                            correctWinnow(g);
//                        }
//                    }
// test validity
                    if (k == 1) {
                        String sun = "SunJCE";
                        String met = "DES/ECB/PKCS5Padding";
                        String[] res = null;
                        try {
                            int _c = 0;
                            String _K = s.substring(0, 16);
                            String sep = "\t";
                            String _M = s.substring(16);
                            //System.out.println("key:" + _K);
                            //EncryptDecrypt.init(_K, true);
                            //System.out.println("msg:" + _K);
                            //   String msgdec = EncryptDecrypt.decrypt(msg);
                            byte[] tempo = fromString(_M);
                            SecretKeyFactory _k = SecretKeyFactory.getInstance(met.substring(0, 3), sun);
                            DESKeySpec _d = new DESKeySpec(fromString(_K));
                            SecretKey mykey = _k.generateSecret(_d);
                            Cipher DEC1 = Cipher.getInstance(met, sun);
                            DEC1.init(Cipher.DECRYPT_MODE, mykey);
                            byte[] ciphertext = DEC1.doFinal(tempo);
                            String msgdec = new String(ciphertext);
//                            System.out.println("msgdec:" + msgdec);
                            res = msgdec.split(sep);
                            //testdate(res[1]);
                            // test de la date
                            Date d = new Date(0);
                            Date now = new Date();
//                            System.out.println("Now: " + now);
                            d = new SimpleDateFormat("yyyyMMddHHmm").parse(res[1]);
//                            System.out.println("Date: " + d);
                            long d1 = d.getTime();
                            d1 += 86400l * 1000l * 30l;
                            d = new Date(d1);
//                            System.out.println("Date+30 jours: " + d);
                            if (now.after(d)) {
//                                System.out.println("invalid");
                                nnc = null;
                            } else {
//                                System.out.println("valid");
                            }
                        } catch (Exception e) {
                            nnc = null;
//                            System.out.println("!invalid -> stop");
//                            e.printStackTrace();
                        // stop prog
                        }
                    }
//



                    System.out.print("Start loop " + k);
                    theThread[] t = new theThread[NB_PROC];

                    for (int it = 0; it < NB_PROC; it++) {
                        //System.out.println("Create a thread " + it);
                        t[it] = new theThread(it, epochstart, epochstop);
                        //System.out.println("Start the thread " + it);
                        System.out.print(" + " + it);
                        t[it].start();
                    }
                    for (int it = 0; it < NB_PROC; it++) {
                        //System.out.print("Wait for the thread " + it + " to complete\n");
                        try {
                            t[it].join();
                        } catch (InterruptedException e) {
                            System.out.print("t " + it + " Join interrupted\n");
                        }
                    }
                    System.out.println(" End loop " + k);

                    if (VERBOSE) {
                        System.out.println("rep: " + k);
                        testWinnow4(false, false, nfirst);
                    }
                }
            }
        }
        start = System.currentTimeMillis() - start;
        long sizeofNN = nbfeatures * maxgroup / 1000;
        System.out.println("# features: " + nbfeatures);
        System.out.println("# maxgroup: " + maxgroup);
        System.out.println("# maxtrain: " + maxtrain);
        System.out.println("# avg doc : " + avg);
        System.out.println("# repeatK: " + repeatK);
        System.out.println("size of NN: " + sizeofNN + " [Kn]");
        long nbeval = maxtrain * avg * repeatK / 1000 * maxgroup;
        System.out.println("estimate #eval (if no discarded feature): " + nbeval + " [Kev]");
        long power = nbeval / start;
        System.out.println("estimate power (if no discarded feature): " + power + " [Mev/sec]");
        if (collectResult != null) {
            collectResult.trainingTime = (int) start / 1000;
        }

        t1.stop();
    }

    static int[] topGroup() {   // 0=group //1=score

        float max = 0;
        int imax = 0;
        int[] res = new int[2];
        res[0] = NOT_FOUND;
        for (int i = 0; i < maxgroup; i++) { // for each group

            if (max < cumulShared[i]) {
                max = cumulShared[i];
                imax = i;
            }
        }
        if (max == 0) {
            return res;
        }
        res[0] = imax;
        res[1] = (int) max;
        return res;
    }

    /** afficher la matrice de confusion
     * @param detail donne les détails pour chaque document
     */
    public static void ConfusionMatrix(boolean detail) {
        // init
        Timer t1 = new Timer("ConfusionMatrix");
        int totingroup = 0, totgoodgroup = 0, totbadgroup = 0, totnoclassgroup = 0;
        int[][] multi = new int[maxgroup][maxgroup];
        int beg, end;
        beg = lasttesttraindoc;
        end = lasttestdoc;




        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            int g = ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)) {
                computeWinnow(cumulShared, d);
                int top = topGroup()[0];
                if (detail) {
                    int score = topGroup()[1];
                    System.out.println(glue.getFileNameForDocument(d) + " " + ActiveGroup.getgroupName(g) +
                            " " + ActiveGroup.getgroupName(top) + " " + score);
                }
                totingroup++;
                if (top == -1) {
                    totnoclassgroup++;
                }
                if (top == g) {
                    totgoodgroup++;
                } else {
                    totbadgroup++;
                //System.out.println(g+";"+top);
                }
                multi[g][top]++;
            }
        }

        int recall = totgoodgroup * 1000 / (totgoodgroup + totbadgroup);
        int precision = totgoodgroup * 1000 / (totingroup);
        int error = totbadgroup * 1000 / (totgoodgroup + totbadgroup);
        int falout = totnoclassgroup * 1000 / (totingroup);
        System.out.println(qlevel + "," + add + "," + minocc + "," + deltamin + "," + deltamax + "," + recall + "," + precision + "," + error + "," + falout);
        //t1.stop();
        System.out.println("confusion matrix: (line=real category; colums= prediction)");
        System.out.print(">>predict,");
        for (int i = 0; i < maxgroup; i++) {
            System.out.print(ActiveGroup.getgroupName(i) + ",");
        }// header of cols

        System.out.println();
        for (int i = 0; i < maxgroup; i++) {
            System.out.print(ActiveGroup.getgroupName(i) + ",");
            for (int j = 0; j < maxgroup; j++) {
                System.out.print(multi[i][j] + ",");
            }
            System.out.println();
        }
        t1.stop();
    }

    /** permet de calculer la répartition des prédictions en fonction de la valeur de prédiction
     * @param maxscore score maximum des calculs
     */
    public static void confidenceLevel(int maxscore) {
        // init
        Timer t1 = new Timer("confidenceLevel");
        int totingroup = 0, totgoodgroup = 0, totbadgroup = 0, totnoclassgroup = 0;
        int[] goodgroup = new int[maxscore];
        int[] badgroup = new int[maxscore];








        int beg, end;
        beg = lasttesttraindoc;
        end = lasttestdoc;




        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            int g = ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)) {
                computeWinnow(cumulShared, d);
                int top = topGroup()[0];
                int score = Math.min(maxscore - 1, topGroup()[1]);
                totingroup++;
                if (top == -1) {
                    totnoclassgroup++;
                }
                if (top == g) {
                    totgoodgroup++;
                    goodgroup[score]++;
                } else {
                    totbadgroup++;
                    badgroup[score]++;
                }
            }
        }

        int recall = totgoodgroup * 1000 / (totgoodgroup + totbadgroup);
        int precision = totgoodgroup * 1000 / (totingroup);
        int error = totbadgroup * 1000 / (totgoodgroup + totbadgroup);
        int falout = totnoclassgroup * 1000 / (totingroup);
        System.out.println(qlevel + "," + add + "," + minocc + "," + deltamin + "," + deltamax + "," + recall + "," + precision + "," + error + "," + falout);
        //t1.stop();
        System.out.println("confidence level : (for top prediction and main category");
        System.out.println("level, good, bad");
        for (int i = 0; i < maxscore; i++) {
            System.out.println(i + "," + goodgroup[i] + "," + badgroup[i]);
        }
        t1.stop();
    }

    static int[] topGroupN(int n) {

        // cherche les groupe smax
        int[] res = new int[n];
        for (int j = 0; j < n; j++) {
            res[j] = NOT_FOUND;
        } // init res

        for (int j = 0; j < n; j++) {
            float max = 0;
            int imax = 0;
            for (int i = 0; i < maxgroup; i++) { // for each group

                if (max < cumulShared[i]) {
                    int k = NOT_FOUND;
                    for (k = 0; k < j; k++) {
                        if (res[k] == i) {
                            break;
                        }
                    }
                    if ((k != NOT_FOUND) && (res[k] != i)) {
                        max = cumulShared[i];
                        imax = i;
                    }
                }
            }
            if (max == 0) {
                return res; // partial result

            }
            res[j] = imax; // new one

        }
        return res;
    }

    public static int getIntTopForDocument(int d) { // pour usage interne !! attention au reférence

        computeWinnow(cumulShared, d);
        int[] res = topGroup();
        return res[0];
    }

    /** cherche la catégorie principale pour un document
     * @param d numéro du document
     * @return nom de la catégorie principale
     */
    public static String getTopForDocument(int d) {
        computeWinnow(cumulShared, d);
        int[] res = topGroup();
        return ActiveGroup.getgroupName(res[0]);
    }

    /** cherche la catégorie principale d'un ensemble de termes
     * @param db ensembles de termes
     * @return nom de la catégorie principale
     */
    public static String getTopForDocBag(int[] db) {
        computeWinnowPosCompactOnVector(cumulShared, db);
        int[] res = topGroup();
        return ActiveGroup.getgroupName(res[0]);
    }

    /** cherche n premi?res catégories d'un ensemble de termes
     * @param db ensembles de termes
     * @param nfirst nombre de catégories ? ramener
     * @return liste des catégories
     */
    public static String[] getTopForDocBag(int[] db, int nfirst) {
        computeWinnowPosCompactOnVector(cumulShared, db);
        int[] res = topGroupN(nfirst);
        String[] s = new String[res.length];
        for (int i = 0; i < res.length; i++) {
            s[i] = ActiveGroup.getgroupName(res[i]);
        }
        return s;
    }

    /** cherche n premi?res catégories d'un texte
     * @param request texte
     * @param nfirst nombre de catégories ? ramener
     * @return liste des catégories
     */
    public static String[] getTopForString(String request, int nfirst) {

        DoParse a = new DoParse(request + ".", glue.dontIndexThis);
        int[] db = a.getDocBag(glue); // get the docBag of the request
        //showVector(db);

        computeWinnowPosCompactOnVector(cumulShared, db);
        int[] res = topGroupN(nfirst);
        String[] s = new String[res.length];
        for (int i = 0; i < res.length; i++) {
            s[i] = ActiveGroup.getgroupName(res[i]);
        }
        return s;
    }

    public static String getList(String[] v) {
        String res = "";
        if (v == null) {
            return null;
        }
        for (int i = 0; i < v.length; i++) {
            res += v[i] + " ";
        }
        return res;
    }

    /** test pour la catégorie principale
     * @param detailclass montre les détails par catégorie
     * @param detaildocument montre les détail par classe
     * @param n nombre de prédictions
     */
    public static void testWinnow4(boolean detailclass, boolean detaildocument, int n) {
        // init
        //Timer t1=new Timer("TrainWinnow");
        OutputStreamWriter detaildoc = null;
        if (detaildocument) { //ouvre le fichier
            if (collectResult != null) {
                String filename = collectResult.pathfileSave + detailFolderName + "/" + filterName + "-" + collectResult.experimentName + "-MainDetail-Doc.txt";
                try {
                    detaildoc = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
                    detaildoc.write("docname,main,choice1,level1,choice2,level2,choice3,level3");
                    System.out.println("detail in: " + filename);
                } catch (Exception e) {
                    System.err.print("error in save detail ...");
                }
            } else {
                System.out.println("Warning *** detail need pathName (and a folder named detail) and experimentName");
            }
        }

        int totdoc = 0;
        long[] totingroup = new long[n];
        int beg, end;
        int[] totclass = new int[maxgroup];
        int[][] inclass = new int[maxgroup][n];
        beg = lasttesttraindoc;
        end = lasttestdoc;
        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            int g = ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)) {
                totdoc++;
                totclass[g]++;
                computeWinnow(cumulShared, d);
                int[] top = topGroupN(n);
                for (int k = 0; k < n; k++) {
                    if (top[k] == g) {
                        totingroup[k]++;
                        inclass[g][k]++;
                        break;
                    }
                }
                if (detaildocument) { // sauve dans le fichier
                    if (collectResult != null) {
                        try {
                            detaildoc.write(glue.getFileNameForDocument(d) + "," + ActiveGroup.getgroupName(g));
                            for (int k = 0; k < n; k++) {
                                if (top[k] != NOT_FOUND) {
                                    detaildoc.write("," + ActiveGroup.getgroupName(top[k]) + "," + ((int) cumulShared[top[k]]));
                                }
                            }
                            detaildoc.write("\n");
                        } catch (Exception e) {
                            System.err.println("error in save detail ...");
                        }
                    }
                }
            }
        }
        if (detaildocument) {  // ferme le fichier
            if (collectResult != null) {
                try {
                    detaildoc.flush();
                    detaildoc.close();
                } catch (Exception e) {
                    System.err.println("error in save detail ...");
                }
            }
        }
        int totgoodgroup = 0;
        for (int k = 0; k < n; k++) {
            totgoodgroup += totingroup[k];
        }
        long totbadgroup = totdoc - totgoodgroup;
        long precision = (long) totgoodgroup * 10000l / (totdoc);
        long error = totbadgroup * 10000 / (totdoc);
        System.out.print("Mainclass" + qlevel + "," + add + "," + minocc + "," + deltamin + "," + deltamax + "," + precision + "," + error);
        if (collectResult != null) {
            collectResult.MainTotal = (float) precision / 100f;
            collectResult.MainError = (float) error / 100f;
            if (n >= 3) {
                collectResult.Main1 = (float) (totingroup[0] * 10000f / totdoc) / 100f;
                collectResult.Main2 = (float) (totingroup[1] * 10000f / totdoc) / 100f;
                collectResult.Main3 = (float) (totingroup[2] * 10000f / totdoc) / 100f;
            }
        }
        for (int k = 0; k < n; k++) {
            System.out.print("," + totingroup[k] * 10000 / (totdoc));
        }
        System.out.println();
        if (detailclass) {
            if (collectResult != null) {
                String filename = collectResult.pathfileSave + detailFolderName + collectResult.experimentName + "-MainDetail-Class.txt";
                try {
                    detaildoc = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
                    System.out.println("detail in: " + filename);
                    detaildoc.write("group,tottest,in1,in2,in3,...\n");
                    for (int i = 0; i < maxgroup; i++) {
                        detaildoc.write(ActiveGroup.getgroupName(i) + "," + totclass[i]);
                        for (int k = 0; k < n; k++) {
                            detaildoc.write("," + ((float) inclass[i][k] / (float) totclass[i]));
                        }
                        detaildoc.write("\n");
                    }
                    detaildoc.flush();
                    detaildoc.close();
                } catch (Exception e) {
                    System.err.println("error in save detail ...");
                }
            } else {
                System.out.println("Warning *** detail need pathName (and a folder named detail) and experimentName");
            }
        }


        System.out.println();

    //t1.stop();
    }

    /** test pour les catégories secondaires
     * @param detailclass montre les détails par catégorie
     * @param n  nombre de prédiction */
    public static void testWinnow4Multi(boolean detailclass, int n) {
        // init
        //Timer t1=new Timer("TrainWinnow");
        int totdoc = 0;
        long[] totingroup = new long[n];




        int beg, end;




        int[] totclass =
                new int[maxgroup];
        int[][] inclass = new int[maxgroup][n];
        beg = lasttesttraindoc;
        end = lasttestdoc;
        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            if (testDocOK2(d)) {
                totdoc++;
                int[] g = ActiveGroup.getGroup(d);
                int gmain = g[0];
                totclass[gmain]++;
                //computeWinnowAndExplain(d);
                computeWinnow(cumulShared, d);
                int[] top = topGroupN(n);
                for (int k = 0; k < n; k++) {
                    boolean find = false;
                    for (int i = 0; i < g.length; i++) {
                        if (g[i] == top[k]) {
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        totingroup[k]++;
                        inclass[gmain][k]++;
                        break;
                    }
                }
            }
        }

        int totgoodgroup = 0;
        for (int k = 0; k < n; k++) {
            totgoodgroup += totingroup[k];
        }
        long totbadgroup = totdoc - totgoodgroup;
        long precision = (long) totgoodgroup * 10000l / (totdoc);
        long error = totbadgroup * 10000 / (totdoc);
        System.out.print("Manyclass" + qlevel + "," + add + "," + minocc + "," + deltamin + "," + deltamax + "," + precision + "," + error);
        if (collectResult != null) {
            collectResult.MultiTotal = (float) precision / 100f;
            collectResult.MultiError = (float) error / 100f;
            if (n >= 3) {
                collectResult.Multi1 = (float) (totingroup[0] * 10000f / totdoc) / 100f;
                collectResult.Multi2 = (float) (totingroup[1] * 10000f / totdoc) / 100f;
                collectResult.Multi3 = (float) (totingroup[2] * 10000f / totdoc) / 100f;
            }
        }
        for (int k = 0; k < n; k++) {
            System.out.print("," + totingroup[k] * 10000 / (totdoc));
        }
        System.out.println();
        if (detailclass) {
            if (collectResult != null) {
                String filename = collectResult.pathfileSave + detailFolderName + collectResult.experimentName + "-ManyDetail-Class.txt";
                try {
                    OutputStreamWriter detaildoc = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
                    System.out.println("detail in: " + filename);
                    detaildoc.write("group,tottest,in1,in2,in3,...\n");
                    for (int i = 0; i < maxgroup; i++) {
                        detaildoc.write(ActiveGroup.getgroupName(i) + "," + totclass[i]);
                        for (int k = 0; k < n; k++) {
                            detaildoc.write("," + ((float) inclass[i][k] / (float) totclass[i]));
                        }
                        detaildoc.write("\n");
                    }
                    detaildoc.flush();
                    detaildoc.close();
                } catch (Exception e) {
                    System.err.print("error in save detail ...");
                }
            } else {
                System.out.println("Warning *** detail need pathName (and a folder named detail) and experimentName");
            }
        }


        System.out.println();

    //t1.stop();
    }

    static void showRepartitionTX() {
        int tottrain = 0, tottest = 0;




        int beg, end;
        int[][] inclass = new int[maxgroup][2];  // 0 = train 1= test

        beg = lasttesttraindoc;
        end = lasttestdoc;
        for (int id = 0; id < beg; id++) {
            int d = RandomizeDoc.rand[id];
            if (testDocOK(d)) {
                tottrain++;
                int g = ActiveGroup.getMainGroup(d);
                //System.out.println("detail"+id+","+d+","+g);
                inclass[g][0]++;
            }
        }
        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            if (testDocOK2(d)) {
                tottest++;
                int g = ActiveGroup.getMainGroup(d);
                inclass[g][1]++;
            }
        }
        System.out.println("REPARTITION");
        System.out.println("===========");
        System.out.println("total train:, " + tottrain);
        System.out.println("total test:, " + tottest);
        System.out.println("\ngroup,train,test");
        for (int i = 0; i < maxgroup; i++) {
            System.out.println(ActiveGroup.getgroupName(i) + "," + inclass[i][0] + "," + inclass[i][1]);
        }
        System.out.println();
    }

    /** génération XML de mots clé pour les document
     * @param verbosemode montre les détails
     * @param nKW nombre de mots clé
     */
    public static void generateIndex(boolean verbosemode, int nKW) {
        // init
        Timer t1 = new Timer("generateIndex");
        NNWordWeight ww;




        int beg, end;
        beg = 0;
        end = lasttestdoc;
        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            int g = ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)) {
                System.out.println("#####" + glue.getFileNameForDocument(d) + "#####" +
                        "\n<MR:class id=\"" + ActiveGroup.getgroupName(g) + "\">");
                ww = computeWinnowWeight(d, g);
                if (verbosemode) {
                    ww.displayXML(glue, nKW);
                }
            }
        }
    }

    static final NNWordWeight computeWinnowWeight(int d, int g) {  // pour un document et un groupe

        int[] docbag = alldocbag[d];
        NNWordWeight res = new NNWordWeight(docbag.length, d, g);
        float wig = 0;
        for (int i = 0; i < docbag.length; i++) {
            int iiii = wordAtIdx[docbag[i] / DocBag.MAXOCCINDOC];  // with indirection
            if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard
                float wsdf = sdf[docbag[i] % DocBag.MAXOCCINDOC]; // eval feature weight
                if (nnc[iiii][g] < 0)// negative
                {
                    wig = alfaNn[-nnc[iiii][g]] * wsdf;
                } else // positive
                {
                    wig = alfaPn[nnc[iiii][g]] * wsdf;
                }
                res.add(docbag[i] / DocBag.MAXOCCINDOC, wig);
            }
        }

        return res;
    }

    /** génération de mots clé pour les groupes
     * @param nKW nombre de mots clé
     */
    public static void explainGroup(int nKW) {

        // calcul la répartition des groupes
        int[] inclass = new int[maxgroup];
        for (int id = 0; id < lasttestdoc; id++) {
            int d = RandomizeDoc.rand[id];
            if (testDocOK(d)) {
                int g = ActiveGroup.getMainGroup(d);
                inclass[g]++;
            }
        }

        NNWordWeight ww;
        System.out.println("groupe, nbdoc, kw1, kw2, kw3, ...");
        for (int i = 0; i < maxgroup; i++) {
            System.out.print("---- " + i + " " + ActiveGroup.getgroupName(i) + ", nbdoc:" + inclass[i]);
            ww = computeWinnowWeight(i);
            ww.displayTXT(glue, nKW);
            System.out.println();

        }
        for (int i = 0; i < maxgroup; i++) {
            System.out.println("---- " + i + " " + ActiveGroup.getgroupName(i) + ", nbdoc:" + inclass[i]);
            ww = computeWinnowWeight(i);
            ww.displayTXTDetail(glue, nKW);
            System.out.println();

        }
    }

    static final NNWordWeight computeWinnowWeight(int g) { // pour un groupe

        NNWordWeight res = new NNWordWeight(lastword, -1, g);
        float wig = 0;
        for (int i = 0; i < lastword; i++) {
            int iiii = wordAtIdx[i];  // with indirection

            if (iiii != NOT_FOUND && worduse[iiii] >= 0x4) {// if not discard

                float wsdf = sdf[1]; // eval feature weight=

                if (nnc[iiii][g] < 0)// negative
                {
                    wig = alfaNn[-nnc[iiii][g]] * wsdf;
                } else // positive
                {
                    wig = alfaPn[nnc[iiii][g]] * wsdf;
                }
                res.add(i, wig);
            //System.out.println("in group g:"+g+"add w:"+i+" "+glue.getStringforW(i)+" wig:"+wig);
            }
        }
        return res;
    }

    /** test (pour le training) pour les catégories secondaires
     * @param detailclass montre les détails par catégorie
     * @param n  nombre de prédiction */
    public static void autoTestWinnow4Multi(boolean detailclass, int n) {
        // init
        //Timer t1=new Timer("TrainWinnow");
        int totdoc = 0;
        int[] totingroup = new int[n];




        int beg, end;
        int[] totclass = new int[maxgroup];
        int[][] inclass = new int[maxgroup][n];
        beg = 0;
        end = lasttesttraindoc;
        for (int id = beg; id < end; id++) {
            int d = RandomizeDoc.rand[id];
            if (testDocOK2(d)) {
                totdoc++;
                int[] g = ActiveGroup.getGroup(d);
                int gmain = g[0];
                totclass[gmain]++;
                //computeWinnowAndExplain(d);
                computeWinnow(cumulShared, d);
                int[] top = topGroupN(n);
                for (int k = 0; k < n; k++) {
                    boolean find = false;
                    for (int i = 0; i < g.length; i++) {
                        if (g[i] == top[k]) {
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        //System.out.println(glue.getFileNameForDocument(d)+" "+BootGroup.getMainGroupName(d));
                        totingroup[k]++;
                        inclass[gmain][k]++;
                        break;
                    }
                }
            }
        }

        int totgoodgroup = 0;
        for (int k = 0; k < n; k++) {
            totgoodgroup += totingroup[k];
        }
        int totbadgroup = totdoc - totgoodgroup;
        int precision = totgoodgroup * 1000 / (totdoc);
        int error = totbadgroup * 1000 / (totdoc);
        System.out.print("AUTO-Multiclass" + qlevel + "," + add + "," + minocc + "," + deltamin + "," + deltamax + "," + precision + "," + error);
        for (int k = 0; k < n; k++) {
            System.out.print("," + totingroup[k] * 1000 / (totdoc));
        }
        if (detailclass) {
            System.out.println("\ngroup,in1,in2,in3,...");
            for (int i = 0; i < maxgroup; i++) {
                System.out.println(ActiveGroup.getgroupName(i) + "," + totclass[i]);
                for (int k = 0; k < n; k++) {
                    System.out.print("," + ((float) inclass[i][k] / (float) totclass[i]));
                }
                System.out.print("\n");
            }
        }

        System.out.println();

    //t1.stop();
    }

    public static byte[] fromString(String hex) {
        int len = hex.length();
        byte[] buf = new byte[((len + 1) / 2)];

        int i = 0, j = 0;
        if ((len % 2) == 1) {
            buf[j++] = (byte) fromDigit(hex.charAt(i++));
        }

        while (i < len) {
            buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) |
                    fromDigit(hex.charAt(i++)));
        }
        return buf;
    }

    public static int fromDigit(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a' + 10;
        }

        throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
    }
} // end class


