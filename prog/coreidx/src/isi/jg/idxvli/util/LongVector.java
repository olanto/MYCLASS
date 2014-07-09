package isi.jg.idxvli.util;

import java.util.*;

/**
 *  Comportements d'un vecteur de Long.
 *
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
 *  Comportements d'un vecteur de Long.
 *
 */
public interface LongVector {

    /**  cr�e un vecteur 2^_maxSize par d�faut � l'endroit indiqu� par le path, (maximum=2^31) */
    public LongVector create(String _path, String _file, int _maxSize);

    /**  ouvre un vecteur � l'endroit indiqu� par le path */
    public LongVector open(String _path, String _file);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets � jour la position pos avec la valeur val */
    public void set(int pos, long val);

    /**  cherche la valeur � la position pos  */
    public long get(int pos);

    /**  retourne la taille du vecteur */
    public int length();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne des statistiques */
    public String getStatistic();
}
