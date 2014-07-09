package isi.jg.idxvli.knn;

import isi.jg.idxvli.*;

/**
 * Une interface pour effectuer le calcul de la distance entre les documents. 
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public interface KNNManager {

    /**
     * Pr�pare une structure de calcul de KNN.
     * @param _verbose pour le debuging (true)
     * @param _glue indexation de r�f�rence
     * @param minocc minimum d'occurences pour �tre dans la pr�s�lection.
     * @param maxlevel maximum d'occurences  en 0/00 du corpus pour �tre dans a pr�s�lection.
     * @param formulaIDF inverse document frequency formula.
     * @param formulaTF terme frequency formula.
     */
    public void initialize(IdxStructure _glue, int minocc, int maxlevel, boolean _verbose, int formulaIDF, int formulaTF);

    /** Chercher les N premiers voisins du document d, sans formattage.
     * @param doc document
     * @param N nombre de voisins
     * @return r�ponse
     */
    public int[][] getKNNForDoc(int doc, int N);

    public KNNResult KNNForDoc(int doc, int N);

    /** Chercher les N premiers voisins du texte request, sans formattage.
     * @param request texte de r�f�rence
     * @param N nombre de voisins
     * @return r�ponse
     */
    public KNNResult getKNN1(String request, int N);

    /** Chercher les N premiers voisins du texte request, sans formattage, avec une liste pr�d�finie de document.
     * @param topic liste de doc
     * @param request texte de r�f�rence
     * @param N nombre de voisins
     * @return r�ponse
     */
    public KNNResult getKNNinTopic(int[] topic, String request, int N);

       
    public float[] getRawKNN(String request);
   public int[][] getKNN(String request, int N);

    /** Chercher la pond�ration des documents
     * @param request texte de r�f�rence
     * @return r�ponse
     */
    public float[] getSimilarity(String request);

    /** visualiser le r�sultat d'une r�ponse knn
     * @param res R�sultat d'une requ�te KNN (getKNN)
     */
    public void showKNN(int[][] res);

    public void showKNNWithName(int[][] res);

    /** Chercher les N premiers voisins du texte request
     * @param request texte de r�f�rence
     * @param N nombre de voisins
     * @return XML format
     */
    public String searchforKNN(String request, int N);

    /** formatage XML d'une ligne de r�ponse
     * @param fn nom du fichier
     * @param doc document
     * @param score niveau de similarit�
     * @return XML format
     */
    public String XMLrefFNWithScore(String fn, int doc, int score);
}
