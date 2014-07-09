package isi.jg.idxvli.cache;

/**
 * comportement d'un cache pour la mise à jour.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * permet de gérer les informations sur le remplissage partielle du cache
 */
public interface CacheWrite extends CacheIdx {

    /**
     * retourne la taille réellement utilisée dans le vecteur pour le terme i dans le cache
     * @param i terme
     * @return la taille réellement utilisée
     */
    public int getCountOf(int i);

    /**
     * assigne la taille réellement utilisée dans le vecteur pour le terme i dans le cache
     * @param i terme
     * @param value la taille réellement utilisée
     */
    public void setCountOf(int i, int value);

    /**
     * incrémente la taille réellement utilisée dans le vecteur pour le terme i dans le cache
     * @param i terme
     */
    public void incCountOf(int i);

    /**réinitialise toutes les taille réellement utilisée dans le cache =0
     */
    public void resetAll();
}
