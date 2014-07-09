package isi.jg.idxvli.cache;

/**
 * Comportement d'un cache d'index.
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
 * Comportement d'un cache d'index.
 * Les indices des termes sont ceux utilisés par l'implémentation du cache
 * (donc par forcemment ceux du dictionnaire de termes)
 */
public interface CacheIdx {

    /**
     * retourne une référence sur le cache du terme i. On travaille donc directement dans le cache!
     * @param i terme
     * @return veteur original
     */
    public int[] getReferenceOn(int i);

    /**
     * retourne une copie du cache du terme i
     * @param i terme
     * @return veteur
     */
    public int[] getCopyOf(int i);

    /**
     * retourne la valeur du cache du terme i à la position j
     * @param i terme
     * @param j position
     * @return valeur
     */
    public int v(int i, int j);

    /**
     * retourne le vecteur des valeurs du cache du terme i de from sur la longueur donnée
     * @param i terme
     * @param from position
     * @param length longeur
     * @return vecteur des valeurs
     */
    public int[] getv(int i, int from, int length);

    /**
     * assigne une valeur au cache du terme i à la position j
     * @param i terme
     * @param j position
     * @param value valeur
     */
    public void setv(int i, int j, int value);

    /**
     * incrémente la valeur du cache du terme i à la position j
     * @param i terme
     * @param j position
     */
    public void incv(int i, int j);

    /**
     * retourne la taille total du cache
     * @return la taille total du cache
     */
    public int getCurrentSize();

    /**
     * détermine si le terme i est pas dans le cache
     * @param i terme
     * @return true=le terme i est pas dans le cache
     */
    public boolean isNotLoaded(int i);

    /**
     * détermine si le terme i est  dans le cache
     * @param i terme
     * @return true=le terme i est  dans le cache
     */
    public boolean isLoaded(int i);

    /**
     * retourne la taille du vecteur i du cache
     * @param i terme
     * @return  taille du vecteur i
     */
    public int length(int i);

    /**
     * inscrit un nouveau terme dans le cache avec une taille initiale
     * @param i terme
     * @param initialSize taille initiale
     */
    public void newVector(int i, int initialSize);

    /**
     * inscrit un nouveau terme dans le cache avec un vecteur initial
     * @param i terme
     * @param reg vecteur initial
     */
    public void registerVector(int i, int[] reg);

    /**
     * libère le terme i dans le cache
     * @param i terme
     */
    public void releaseVector(int i);

    /**
     * assigne une nouvelle taille au cache du terme i
     * @param i terme
     * @param newSize nouvelle taille
     */
    public void resize(int i, int newSize);
}
