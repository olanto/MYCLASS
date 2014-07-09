package isi.jg.idxvli.knn;

import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.idxvli.cache.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.idxvli.extra.DocBag;

/**
 * Une classe pour effectuer le calcul de la distance entre les documents (complète).
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
 *
 * ATTENTION CETTE VERSION NECESSITE INDEXATION AVEC LES POSITIONS!! UN PROBLEME POUR LA LECTURE DES OCCURENCES
 *
 *
 */
public class Okapi implements KNNManager {

    private static IdxStructure glue;
    private static int lastdoc,  lastword;
    private static CacheRead_Opti indexread;
    private static boolean[] KNNFilter; // word to be tested in knn
    private static int KNNused = 0; // # in KNNFilter
    private static int sizeKNN = 0;
    private static int formulaTF = 1;
    private static int loadMemory = 0;
    //   final int maxocc=1000;
    /** ponderation d'un mot j en fonction de sa frequence dans le corpus*/
    private static float[] wt;
    /** ponderation d'un mot j en fonction de sa frequence dans un document*/
    private static float[][] wtd;
    /** ponderation d'un document en fonction de ces mots*/
    private static float[] wd;
    /** index des document en fonction de ces mots*/
    private static int[][] wi;
    private static float[] cumul;
    static boolean verbose;

    /** crée une classe pour les recheches KNN*/
    public Okapi() {
    }

    /**
     * Prépare une structure de calcul de KNN.
     * @param _formulaTF formule de la fréquence des termes
     * @param _verbose pour le debuging (true)
     * @param _glue indexation de référence
     * @param minocc minimum d'occurences pour être dans la présélection.
     * @param maxlevel maximum d'occurences  en 0/00 du corpus pour être dans a présélection.
     * @param formulaIDF inverse document frequency formula.
     */
    public final void initialize(IdxStructure _glue, int minocc, int maxlevel, boolean _verbose,
            int formulaIDF, int _formulaTF) {
        Timer t1 = null;
        glue = _glue;
        lastdoc = glue.lastUpdatedDoc; // ??? plus possible ???  il faut un initialisation incrémentale
        lastword = glue.lastUpdatedWord;  // ??? plus possible ???  il faut un initialisation incrémentale
        indexread = glue.indexread;
        verbose = _verbose;
        formulaTF = _formulaTF;
        cumul = new float[lastdoc];
        buildFilterKNN(minocc, maxlevel);
        if (verbose) {
            System.out.println("#word=" + lastword + "  #knn word=" + KNNused);
        }
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
        computeWeightsTF(formulaTF);
        if (verbose) {
            System.out.println("Load in memory:" + loadMemory * 8 / 1024 + "+" + lastword / 1024 + "*const? [Kbytes] constant>8");
        }
        if (verbose) {
            t1.stop();
        }

    }

    /** Chercher les N premiers voisins du document d, sans formattage. N'est pas implémenté dans cette classe (voir TFxIDF_ONE)
     * utilise boostedTopNDoc qui nécessite des réglages selon la collection.
     * @param doc document
     * @param N nombre de voisins
     * @return réponse
     */
    public final int[][] getKNNForDoc(int doc, int N) {
        error("not implemented");
        return null;
    }

    public final synchronized float[] getSimilarity(String request) {
        error("not implemented");
        return null;
    }

    public final synchronized void showKNNWithName(int[][] res) {
        error("not implemented");
    }

    /** Chercher les N premiers voisins du texte request, sans formattage.
     * @param doc id doc
     * @param N nombre de voisins
     * @return réponse
     */
    public final KNNResult KNNForDoc(int doc, int N) {
        error("not implemented");
        return null;
    }

    public final KNNResult getKNNinTopic(int[] topic, String request, int N) {
        error("not implemented");
        return null;
    }

    public final KNNResult getKNN1(String request, int N) {
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

    /** visualiser le résultat d'une réponse knn
     * @param res Résultat d'une requête KNN (getKNN)
     */
    public final void showKNN(int[][] res) {
        for (int i = 0; i < res.length; i++) {
            System.out.println(i + ":" + res[i][0] + "/" + res[i][1]);
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

        return "<P_small><URL target=\"document\" href=\"" + cleanValue(fn) + "\">" + cleanValue(fn) + "</URL>(" + score + ")</P_small>";
    }

    private final void computeKNN(int[] docbag) {
        //Timer t1=new Timer("Start compute dynamic semantic dist for document :"+d);
        cumul = new float[lastdoc];  // reset cumul of weights
        float wdb = 0; // weigh for the doc bag

        for (int i = 0; i < docbag.length; i++) {  //pour chaque mot du docbag
            int wordinDB = docbag[i] / DocBag.MAXOCCINDOC;
            int weightinDB = docbag[i] % DocBag.MAXOCCINDOC;
            float wtdid = 1;
            //if(verbose)System.out.println(glue.getTermOfW(wordinDB)+"("+weightinDB+")");
            if (KNNFilter[wordinDB]) {
                int lastwi = wi[wordinDB].length; // # doc possédant le mot i
                for (int j = 0; j < lastwi; j++) { // pour chaque document commun de ce mot i
                    cumul[wi[wordinDB][j]] += wtd[wordinDB][j] * weightinDB ;
                    //wdb += weightinDB * weightinDB;
                }
            }
        }
    //t1.stop();
    }

    private final int topDoc() {
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

    private final int[][] topNDoc(int n) { // must be optimise !!!
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

    private final void buildFilterKNN(int minocc, int maxlevel) {  // maxlevel est signifiant en o/oo  - on filtre les mots trop et peu fréquents
        KNNFilter = new boolean[lastword];
        KNNused = 0;
        for (int i = 0; i < lastword; i++) {
            if ((glue.getOccOfW(i) >= minocc) &&
                    (((glue.getOccOfW(i) * 1000) / lastdoc) <= maxlevel)) {
                KNNFilter[i] = true;
                KNNused++;
            }
        }
    }

    private final void computeWeightsIDF(int formula) { // Inverse Document Frequency
        wt = new float[lastword];
        for (int i = 0; i < lastword; i++) {
            if (KNNFilter[i]) {
                if (formula == 1) { //  ln(1+N/f(t))
                    wt[i] = (float) Math.log(1 + (double) lastdoc / (double) glue.getOccOfW(i));
                }
                if (formula == 2) { //  ln(N/f(t)-1)
                    wt[i] = (float) Math.log((double) lastdoc / (double) glue.getOccOfW(i) - 1);
                }
            }
        }
    }

    private final void computeWeightsTF(int formula) { // BM25 pour tous les cas
        wtd = new float[lastword][];
        wd = new float[lastdoc];
        wi = new int[lastword][];
        
         double nbd=(double) glue.lastUpdatedDoc;
        
                float lmoy=1000;
        if (glue.cntpos!=0)  lmoy = glue.cntpos / glue.lastUpdatedDoc; // longueur moyenne des documents

        //!!!!!!
        for (int i = 1; i < lastdoc; i++) {
            wd[i] = glue.docstable.getSize(i); // longueur du document
        }

 
        for (int i = 0; i < lastword; i++) {
            if (KNNFilter[i]) { // filter first
                int lastwi = glue.getOccOfW(i);
                //System.out.println("word:"+i+", #docs:"+lastwi);
                loadMemory += lastwi;
                wtd[i] = new float[lastwi];  // weighs
                wi[i] = new int[lastwi];     // documents
                glue.indexread.lockForFull(i);  // load index of j can be optimise (no need of position)
                // zone protégée
                int[] occ = glue.indexread.getvDoc(i, lastwi, lastwi);// les occurences de i
                double nbdt=(double)lastwi;
               for (int j = 0; j < lastwi; j++) {
                  wi[i][j] = indexread.vDoc(i, j); // copy index
                  double nbtd = (float) occ[j]; // copy index
                double ld = (float) wd[wi[i][j]]; // long du doc
                double num=nbtd*Math.log((nbd-nbdt+0.5)/(nbdt+0.5));
                double den=2*(0.25+0.75*(ld/lmoy))+nbtd;
                 double okapi = num/den;
                wtd[i][j] =(float) okapi;  //new 
                }
                // fin de zone
                glue.indexread.unlock(i); // keep memory small! now we have a small copy in wi[][]
            }
        }
    }
}
