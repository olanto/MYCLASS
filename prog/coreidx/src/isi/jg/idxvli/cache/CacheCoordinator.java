package isi.jg.idxvli.cache;

/**
 *  Comportement du coordinateur des caches pour l'indexation.
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
 * Comportement du coordinateur des caches pour l'indexation.
 *
 */
public interface CacheCoordinator {

    /** démarre le timing pour les mesures de performance
     */
    public void startTimer();

    /** incrémente le total des termes indexés
     */
    public void incTotalIdx();

    /** retourne la taille total des caches coordonsés
     */
    public int cacheCurrentSize();

    /** retourne le nombre de termes actifs dans les caches
     */
    public int allocate();

    /** indique si les caches sont à nettoyer
     */
    public boolean cacheOverFlow();

    /** libère partiellement les caches
     */
    public void freecache();

    /** libère complètement les caches
     */
    public void freecacheFull();
}
