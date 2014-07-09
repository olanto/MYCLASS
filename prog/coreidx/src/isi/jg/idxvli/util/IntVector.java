package isi.jg.idxvli.util;

import java.util.*;

/**
 *  Comportements d'un vecteur de Int.
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
 *  Comportements d'un vecteur de Int
 *
 */
public interface IntVector {

    /**  crée un vecteur 2^_maxSize par défaut à l'endroit indiqué par le path, (maximum=2^31) */
    public IntVector create(String _path, String _file, int _maxSize);

    /**  ouvre un vecteur à l'endroit indiqué par le path */
    public IntVector open(String _path, String _file);

    /**  ferme un gestionnaire de vecteur (et sauve les modifications*/
    public void close();

    /** mets à jour la position pos avec la valeur val */
    public void set(int pos, int val);

    /**  cherche la valeur à la position pos  */
    public int get(int pos);

    /**  retourne la taille du vecteur */
    public int length();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne des statistiques */
    public String getStatistic();
}
