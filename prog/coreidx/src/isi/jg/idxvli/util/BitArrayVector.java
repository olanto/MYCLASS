package isi.jg.idxvli.util;

import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;

/**
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize].
 * cr�e une structure de bit qui sera vue comme un tableau.
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
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize].
 */
public interface BitArrayVector {

    /**
     * cr�e un un tableau de bit[2^maxSize][fixedArraySize] � l'endroit indiqu� par le path et file
     * @param _implementation cette classe poss�de plusieurs impl�mentations (FAST,BIG)
     * @param _path dossier contenant les fichiers
     * @param _file nom racine des fichiers
     * @param _maxSize nbre de vecteurs du tableaux = 2^_maxSize
     * @param _fixedArraySize nbre taille fixe des vecteurs
     * @return un tableau de bit[2^maxSize][fixedArraySize]
     */
    public BitArrayVector create(implementationMode _implementation, String _path, String _file, int _maxSize, int _fixedArraySize);

    /**
     * ouvre un vecteur � l'endroit indiqu� par le path
     * @param _ManagerImplementation 
     * @param _path 
     * @param _file 
     * @param _RW 
     * @return un gestionnaire de vecteurs
     */
    public BitArrayVector open(implementationMode _ManagerImplementation, String _path, String _file, readWriteMode _RW);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /**
     * mets � jour la position i avec la valeur val du vecteur pos
     * @param pos 
     * @param i 
     * @param val 
     */
    public void set(int pos, int i, boolean val);

    /**
     * mets � jour la position i avec le vecteur complet
     * @param pos 
     * @param v 
     */
    public void set(int pos, SetOfBits v);

    /**
     * cherche la valeur � la position pos, la i�me valeur
     * @param pos 
     * @param i 
     * @return valeur
     */
    public boolean get(int pos, int i);

    /**
     * cherche le vecteur complet � la position pos
     * @param pos 
     * @return vecteur
     */
    public SetOfBits get(int pos);

    /**
     * retourne la taille du vecteur
     * @return la taille du vecteur
     */
    public int length();

    /**
     * retourne la taille du vecteur � la position pos
     * @param pos 
     * @return la taille du vecteur � la position pos
     */
    public int length(int pos);

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne des statistiques */
    public String getStatistic();
}
