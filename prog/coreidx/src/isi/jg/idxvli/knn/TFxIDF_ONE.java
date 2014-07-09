package isi.jg.idxvli.knn;

import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.util.Messages.*;
import isi.jg.idxvli.cache.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.util.TimerNano;
import isi.jg.idxvli.extra.DocBag;

/**
 * Une classe pour effectuer le calcul de la distance entre les documents (optimisée). Cette classe est une version simplifiée de TFxIDF
 * - avec wtd=1
 * - on supprime le filtre, car on a minocc déja filtrant
 * 
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 *  implémente les cosinus ...
 */
public class TFxIDF_ONE implements KNNManager {

    private static IdxStructure glue;
    private static int lastdoc,  lastword;
    private static CacheRead_Opti indexread;
    /** ponderation d'un mot j en fonction de sa frequence dans le corpus*/
    private static float[] wt;
    /** ponderation d'un mot j en fonction de sa frequence dans un document*/
    // static float[][] wtd; toujours = 1
    /** ponderation d'un document en fonction de ces mots*/
    private static float[] wd;
    private static float[] cumul;
    private static int[] topdoc;
    private static boolean verbose;
    private static boolean[] KNNFilter;

    /** crée une classe pour les recheches KNN*/
    public TFxIDF_ONE() {
    }

    /**
     * Prépare une structure de calcul de KNN.
     * On précalcul les poids IDF et TF.
     * On présélectionne les termes
     * @param _glue indexation de référence
     * @param minocc minimum d'occurences pour être dans la présélection. (ce paramètre est purement formel, pour des raisons de compatibilité)
     * @param maxlevel maximum d'occurences  en 0/00 du corpus pour être dans a présélection. (ce paramètre est purement formel, pour des raisons de compatibilité)
     * @param _verbose montre les détails
     * @param formulaIDF inverse document frequency formula.
     *       1 -> ln(1+N/f(t));
     *       2 -> ln(N/f(t)-1)
     * @param formulaTF toujours à 1. (ce paramètre est purement formel, pour des raisons de compatibilité)
     */
    public final void initialize(IdxStructure _glue, int minocc, int maxlevel, boolean _verbose, int formulaIDF, int formulaTF) {
        Timer t1 = null;
        glue = _glue;
        lastdoc = glue.lastUpdatedDoc; // ??? plus possible ???  il faut un initialisation incrémentale

        lastword = glue.lastUpdatedWord; // ??? plus possible ???   il faut un initialisation incrémentale

        indexread = glue.indexread;  ///  attention !!! si modif

        verbose = _verbose;
        cumul = new float[lastdoc];
        topdoc = new int[lastdoc];
        buildFilterKNN(minocc, maxlevel);
        //
        if (verbose) {
            t1 = new Timer("computeWeightsIDF");
        }
        computeWeightsIDF(formulaIDF);
        if (verbose) {
            t1.stop();
        }
        if (verbose) {
            t1 = new Timer("computeWeightsTF");
        }
        computeWeightsTF();
        if (verbose) {
            System.out.println("Load in memory:" + 4 * (lastdoc * 3 + lastword) / 1024 + " [Kbytes]");
        }
        if (verbose) {
            t1.stop();
        }

    }

    /** Chercher les N premiers voisins du document d, sans formattage.
     * utilise boostedTopNDoc qui nécessite des réglages selon la collection.
     * @param doc document
     * @param N nombre de voisins
     * @return réponse
     */
    public final int[][] getKNNForDoc(int doc, int N) {
        //Timer t1=new Timer("getKNNForDoc:");
        int[] docbag = glue.getBagOfDoc(doc);
        computeKNN(docbag);
        //Timer t2=new Timer("topNDoc:");
        int[][] knndoc = boostedTopNDoc(N);
        //t2.stop();
        //t1.stop();
        return knndoc;
    }

    public final KNNResult KNNForDoc(int doc, int N) {
        TimerNano time = new TimerNano("knn", true);
        int[][] res = getKNNForDoc(doc, N);
        int[][] tranpos = new int[2][res.length];
        for (int i = 0; i < res.length; i++) {
            tranpos[0][i] = res[i][0];
            tranpos[1][i] = res[i][1];
        }
        return new KNNResult(tranpos[0], tranpos[1], time.stop(true));
    }

    /** Chercher les N premiers voisins du texte request, sans formattage.
     * Cette méthode est synchronisée car elle utilise cumul qui est static, donc le
     * calcul du KNN doit être protégé.
     * @param request texte de référence
     * @param N nombre de voisins
     * @return réponse
     */
    public final KNNResult getKNN1(String request, int N) {
        TimerNano time = new TimerNano("knn", true);
        int[][] res = getKNN(request, N);
        int[][] tranpos = new int[2][res.length];
        for (int i = 0; i < res.length; i++) {
            tranpos[0][i] = res[i][0];
            tranpos[1][i] = res[i][1];
        }
        return new KNNResult(tranpos[0], tranpos[1], time.stop(true));
    }

    public final KNNResult getKNNinTopic(int[] topic, String request, int N) {
        error("not implemented");
        return null;
    }

    public final synchronized float[] getRawKNN(String request) {
        Timer t1 = null;
        if (verbose) {
            t1 = new Timer("parsing:");
        }
        DoParse a = new DoParse(request, glue.dontIndexThis);
        int[] requestDB = a.getDocBag(glue); // get the docBag of the request
        //id.showVector(requestDB);

        if (verbose) {
            t1.stop();
        }

        if (verbose) {
            t1 = new Timer("computing KNN:");
        }
        computeKNN(requestDB);
        return cumul;

    }

    public final synchronized int[][] getKNN(String request, int N) {
        Timer t1 = null;
        if (verbose) {
            t1 = new Timer("parsing:");
        }
        DoParse a = new DoParse(request, glue.dontIndexThis);
        int[] requestDB = a.getDocBag(glue); // get the docBag of the request
        //id.showVector(requestDB);

        if (verbose) {
            t1.stop();
        }

        if (verbose) {
            t1 = new Timer("computing KNN:");
        }
        computeKNN(requestDB);
        if (verbose) {
            t1.stop();
        }

        if (verbose) {
            t1 = new Timer("sorting KNN:");
        }
        int[][] knndoc = topNDoc(N);
        //id.showVector(knndoc);
        if (verbose) {
            t1.stop();
        }
        return knndoc;
    }

    public final synchronized float[] getSimilarity(String request) {
        Timer t1 = null;
        if (verbose) {
            t1 = new Timer("parsing:");
        }
        DoParse a = new DoParse(request, glue.dontIndexThis);
        int[] requestDB = a.getDocBag(glue); // get the docBag of the request
        //id.showVector(requestDB);

        if (verbose) {
            t1.stop();
        }

        if (verbose) {
            t1 = new Timer("computing KNN:");
        }
        computeKNN(requestDB);
        if (verbose) {
            t1.stop();
        }

        return cumul;
    }

    /** visualiser le résultat d'une réponse knn
     * @param res Résultat d'une requête KNN (getKNN)
     */
    public final void showKNN(int[][] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println(i + ":" + res[i][0] + "/" + res[i][1]);
        }
    }

    /** visualiser le résultat d'une réponse knn
     * @param res Résultat d'une requête KNN (getKNN)
     */
    public final void showKNNWithName(int[][] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println(i + ":" + glue.getFileNameForDocument(res[i][0]) + "/" + res[i][1]);
        }
    }

    /** Chercher les N premiers voisins du texte request
     * @param request texte de référence
     * @param N nombre de voisins
     * @return XML format
     */
    public final String searchforKNN(String request, int N) {
        int[][] knndoc = getKNN(request, N);
        String r = "";
        for (int i = 0; i < knndoc.length; i++) {
            if (knndoc[i][0] != NOT_FOUND) {
                r = r + XMLrefFNWithScore(glue.getFileNameForDocument(knndoc[i][0]), knndoc[i][0], knndoc[i][1]);
            } else {
                break;
            } // finish document null

        }

        if (r.equals("")) {
            return "<paragraphe>no documents for this condition</paragraphe>";
        }
        return r;
    }

    /** formatage XML d'une ligne de réponse
     * @param fn nom du fichier
     * @param doc document
     * @param score niveau de similarité
     * @return XML format
     */
    public final String XMLrefFNWithScore(String fn, int doc, int score) {  // inspiré de celle de IdxQuery

        //      return "<P_small><URL target=\"document\" href=\"" + cleanValue(fn) + "\">" + cleanValue(fn) + "</URL>(" + score + ")</P_small>";
        return fn + " (" + score + ")\n";
    }

    private final static void computeKNN(int[] docbag) {
        //Timer t1=new Timer("Start compute dynamic semantic dist for document :"+d);
        cumul = new float[lastdoc];  // reset cumul of weights

        float wdb = 0; // weigh for the doc bag

        for (int i = 0; i < docbag.length; i++) {  //pour chaque mot du docbag

            int wordinDB = docbag[i] / DocBag.MAXOCCINDOC;
            if (KNNFilter[wordinDB]) {  // pas filtré
                //System.out.println("knn word "+wordinDB+" - "+glue.getStringforW(wordinDB));

                int weightinDB = docbag[i] % DocBag.MAXOCCINDOC;
                glue.indexread.lockForBasic(wordinDB);  // protège l'utilisation du mot

                int[] doc = glue.indexread.getReferenceOnDoc(wordinDB);

                //    int lastwi = glue.indexread.getNbDoc(wordinDB); // # doc possédant le mot i
                int lastwi = doc.length;
                //System.out.println("# doc "+lastwi);


                for (int j = 0; j < lastwi; j++) { // pour chaque document commun de ce mot i
                    //  cumul[indexread.vDoc(wordinDB, j)] += wt[wordinDB];

                    cumul[doc[j]] += wt[wordinDB];
                    wdb += 1;
                }
                glue.indexread.unlock(wordinDB);
            }
        }
        wdb = (float) Math.sqrt(wdb);
        if (wdb != 0) {
            for (int i = 1; i < lastdoc; i++) { // for each document no

                cumul[i] = cumul[i] / (wd[i] * wdb); //   normalise pas avec le poids du document source

            }
        }
    //t1.stop();
    }

    private final static int topDoc() {
        float max = 0;
        int imax = 0;
        for (int i = 1; i < lastdoc; i++) { // for each document

            if (max < cumul[i]) {
                max = cumul[i];
                imax = i;
            }
        }
        if (max == 0) {
            return NOT_FOUND;
        }
        return imax;
    }

    private final static int[][] topNDoc(int n) { // must be optimise !!!

        int[][] res = new int[n][2];
        for (int j = 0; j < n; j++) {
            res[j][0] = NOT_FOUND;
        } // init res

        for (int j = 0; j < n; j++) {
            float max = 0;
            int imax = 0;
            for (int i = 1; i < lastdoc; i++) { // for each document

                if (max < cumul[i]) {
                    int k = NOT_FOUND;
                    for (k = 0; k < j; k++) {
                        if (res[k][0] == i) {
                            break;
                        }
                    }
                    if ((k != NOT_FOUND) && (res[k][0] != i)) {
                        max = cumul[i];
                        imax = i;
                    }
                }
            }
            if (max == 0) {
                return res;
            } // partial result

            res[j][0] = imax; // new one

            res[j][1] = -(int) (10 * Math.log(cumul[imax])); // score in 10000

        }
        return res;
    }

    private final static int[][] boostedTopNDoc(int n) { // must be optimise !!!

        final float offset = 0.0005f; // must be set for each collection !!

        int[][] res = new int[n][2];
        for (int j = 0; j < n; j++) {
            res[j][0] = NOT_FOUND;
        } // init res

        int nonzero = 0;
        for (int i = 1; i < lastdoc; i++) { // for each document

            if (cumul[i] > offset) {
                topdoc[nonzero] = i;
                nonzero++;
            }
        }
        System.out.println("nonzero:" + nonzero);
        for (int j = 0; j < n; j++) {
            float max = 0;
            int imax = 0;
            for (int i = 0; i < nonzero; i++) { // for each document

                if (max < cumul[topdoc[i]]) {
                    int k = NOT_FOUND;
                    for (k = 0; k < j; k++) {
                        if (res[k][0] == topdoc[i]) {
                            break;
                        }
                    }
                    if ((k != NOT_FOUND) && (res[k][0] != topdoc[i])) {
                        max = cumul[topdoc[i]];
                        imax = topdoc[i];
                    }
                }
            }
            if (max == 0) {
                return res;
            } // partial result

            res[j][0] = imax; // new one

            res[j][1] = -(int) (10 * Math.log(cumul[imax])); // score in 10000

        }
        return res;
    }

    private final static void computeWeightsIDF(int formula) { // Inverse Document Frequency

        wt = new float[lastword];
        for (int i = 0; i < lastword; i++) {
            //  if(KNNFilter[i]){
            if (formula == 1) { //  ln(1+N/f(t))

                wt[i] = (float) Math.log(1 + (double) lastdoc / (double) glue.getOccOfW(i));
            }
            if (formula == 2) { //  ln(N/f(t)-1)

                wt[i] = (float) Math.log((double) lastdoc / (double) glue.getOccOfW(i) - 1);
            }
        // }
        }
    }

    private final void buildFilterKNN(int minocc, int maxlevel) {  // maxlevel est signifiant en o/oo  - on filtre les mots trop et peu fréquents

        KNNFilter = new boolean[lastword];
        int KNNused = 0;
        for (int i = 0; i < lastword; i++) {
            if ((glue.getOccOfW(i) >= minocc) &&
                    (((glue.getOccOfW(i) * 1000) / lastdoc) <= maxlevel)) {
                KNNFilter[i] = true;
                KNNused++;
            }
        }
        if (verbose) {
            System.out.println("#word=" + lastword + "  #knn word=" + KNNused);
        }
    }

    private final static void computeWeightsTF() { // Term Frequency

        wd = new float[lastdoc];
        for (int i = 1; i < lastdoc; i++) {
            wd[i] = glue.docstable.getSize(i);
        }
        for (int i = 1; i < lastdoc; i++) {
            wd[i] = (float) Math.sqrt(wd[i]); // finish the distance calculus

        }
    }
}
