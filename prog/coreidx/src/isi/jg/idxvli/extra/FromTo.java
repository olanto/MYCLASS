package isi.jg.idxvli.extra;

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
 */
public class FromTo {

    /* la position début*/
    public int from;
    /* la position fin */
    public int to;

    /** crée un intervalle
     * @param from debut
     * @param to fin
     */
    public FromTo(int from, int to) {
        this.from = from;
        this.to = to;
    }
}
