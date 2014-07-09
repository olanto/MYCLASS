package isi.jg.idxvli.util;

import java.util.*;

/**
 *  Comportements d'un vecteur de int[fixedArraySize].
 *
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
 *  Comportements d'un vecteur de int[fixedArraySize].
 *
 */
public interface IntArrayVector {

    /**  crée un vecteur 2^_maxSize par défaut à l'endroit indiqué par le path, (maximum=2^31), objet=int[fixedArraySize] */
    public IntArrayVector create(String _path, String _file, int _maxSize, int _fixedArraySize);

    /**  ouvre un vecteur à l'endroit indiqué par le path */
    public IntArrayVector open(String _path, String _file);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets à jour la position pos avec la valeur val */
    public void set(int pos, int[] val);

    /** élimine le vecteur a la position pos */
    public void clear(int pos);

    /**  cherche la valeur à la position pos  */
    public int[] get(int pos);

    /**  cherche la valeur à la position pos, la ième valeur   */
    public int get(int pos, int i);

    /**  retourne la taille du vecteur */
    public int length();

    /**  imprime des statistiques */
    public void printStatistic();
}
