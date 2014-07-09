package isi.jg.idxvli.cache;

import static isi.jg.util.Messages.*;

/**
 * Classe stockant les  bornes des positions d'un vecteur.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Remarque cette classe permet par exemple de rapporté la position et la longueur dans un vecteur
 * pour pouvoir traiter les informations dans ce vecteur au lieu d'effectuer une copie.
 */
public class PosLength {

    /* la position */
    public int pos;
    /* la longueur */
    public int length;

    /** crée une borne
     * @param pos position
     * @param length longueur
     */
    public PosLength(int pos, int length) {
        this.pos = pos;
        this.length = length;
    }
}
