package isi.jg.idxvli.util;

import java.util.*;

/**
 *  Comportements d'un vecteur de bit.
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
 *  Comportements d'un vecteur de bit.
 */
public interface BitVector {

    /**  cr�e un vecteur _maxSize par d�faut � l'endroit indiqu� par le path, (maximum=2^31), 
     *   _maxSize doit �tre un multiple de 32
     */
    public BitVector create(String _path, String _file, int _maxSize);

    /**  ouvre un vecteur � l'endroit indiqu� par le path */
    public BitVector open(String _path, String _file);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets � jour la position pos avec la valeur val */
    public void set(int pos, boolean val);

    /**  cherche la valeur � la position pos  */
    public boolean get(int pos);

    /**  cherche le vecteur entier  */
    public SetOfBits get();

    /**  retourne la taille du vecteur */
    public int length();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne des statistiques */
    public String getStatistic();
}
