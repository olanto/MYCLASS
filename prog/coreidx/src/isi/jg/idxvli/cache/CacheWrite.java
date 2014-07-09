package isi.jg.idxvli.cache;

/**
 * comportement d'un cache pour la mise � jour.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * permet de g�rer les informations sur le remplissage partielle du cache
 */
public interface CacheWrite extends CacheIdx {

    /**
     * retourne la taille r�ellement utilis�e dans le vecteur pour le terme i dans le cache
     * @param i terme
     * @return la taille r�ellement utilis�e
     */
    public int getCountOf(int i);

    /**
     * assigne la taille r�ellement utilis�e dans le vecteur pour le terme i dans le cache
     * @param i terme
     * @param value la taille r�ellement utilis�e
     */
    public void setCountOf(int i, int value);

    /**
     * incr�mente la taille r�ellement utilis�e dans le vecteur pour le terme i dans le cache
     * @param i terme
     */
    public void incCountOf(int i);

    /**r�initialise toutes les taille r�ellement utilis�e dans le cache =0
     */
    public void resetAll();
}
