package isi.jg.idxvli.cache;

/**
 *  Comportement du coordinateur des caches pour l'indexation.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 * Comportement du coordinateur des caches pour l'indexation.
 *
 */
public interface CacheCoordinator {

    /** d�marre le timing pour les mesures de performance
     */
    public void startTimer();

    /** incr�mente le total des termes index�s
     */
    public void incTotalIdx();

    /** retourne la taille total des caches coordons�s
     */
    public int cacheCurrentSize();

    /** retourne le nombre de termes actifs dans les caches
     */
    public int allocate();

    /** indique si les caches sont � nettoyer
     */
    public boolean cacheOverFlow();

    /** lib�re partiellement les caches
     */
    public void freecache();

    /** lib�re compl�tement les caches
     */
    public void freecacheFull();
}
