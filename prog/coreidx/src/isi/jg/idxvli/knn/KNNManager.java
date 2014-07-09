package isi.jg.idxvli.knn;

import isi.jg.idxvli.*;

/**
 * Une interface pour effectuer le calcul de la distance entre les documents. 
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public interface KNNManager {

    /**
     * Prépare une structure de calcul de KNN.
     * @param _verbose pour le debuging (true)
     * @param _glue indexation de référence
     * @param minocc minimum d'occurences pour être dans la présélection.
     * @param maxlevel maximum d'occurences  en 0/00 du corpus pour être dans a présélection.
     * @param formulaIDF inverse document frequency formula.
     * @param formulaTF terme frequency formula.
     */
    public void initialize(IdxStructure _glue, int minocc, int maxlevel, boolean _verbose, int formulaIDF, int formulaTF);

    /** Chercher les N premiers voisins du document d, sans formattage.
     * @param doc document
     * @param N nombre de voisins
     * @return réponse
     */
    public int[][] getKNNForDoc(int doc, int N);

    public KNNResult KNNForDoc(int doc, int N);

    /** Chercher les N premiers voisins du texte request, sans formattage.
     * @param request texte de référence
     * @param N nombre de voisins
     * @return réponse
     */
    public KNNResult getKNN1(String request, int N);

    /** Chercher les N premiers voisins du texte request, sans formattage, avec une liste prédéfinie de document.
     * @param topic liste de doc
     * @param request texte de référence
     * @param N nombre de voisins
     * @return réponse
     */
    public KNNResult getKNNinTopic(int[] topic, String request, int N);

       
    public float[] getRawKNN(String request);
   public int[][] getKNN(String request, int N);

    /** Chercher la pondération des documents
     * @param request texte de référence
     * @return réponse
     */
    public float[] getSimilarity(String request);

    /** visualiser le résultat d'une réponse knn
     * @param res Résultat d'une requête KNN (getKNN)
     */
    public void showKNN(int[][] res);

    public void showKNNWithName(int[][] res);

    /** Chercher les N premiers voisins du texte request
     * @param request texte de référence
     * @param N nombre de voisins
     * @return XML format
     */
    public String searchforKNN(String request, int N);

    /** formatage XML d'une ligne de réponse
     * @param fn nom du fichier
     * @param doc document
     * @param score niveau de similarité
     * @return XML format
     */
    public String XMLrefFNWithScore(String fn, int doc, int score);
}
