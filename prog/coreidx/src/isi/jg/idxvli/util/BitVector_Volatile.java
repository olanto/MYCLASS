package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;

/**
 *  Comportements d'un vecteur de bit chargé entièrement en mémoire (sans sauvegarde).
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
public class BitVector_Volatile implements BitVector {
    /* constantes d'un gestionnaire  -------------------------------------- */
//    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
//    private String pathName;
//    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
//    private String fileName;
    private int[] b;
    private int size = 0;
    private static final int[] mask = {1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8, 1 << 9,
        1 << 10, 1 << 11, 1 << 12, 1 << 13, 1 << 14, 1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19,
        1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27, 1 << 28, 1 << 29,
        1 << 30, 1 << 31
    };

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public BitVector_Volatile() {
    }

    /**  crée un vecteur de taille 2^_maxSize (path et filename sont pas utilisés! */
    public final BitVector create(String _pathName, String _fileName, int _maxSize) {
        return (new BitVector_Volatile(_pathName, _fileName, _maxSize));
    }

    /**  ouvre un vecteur  à l'endroit indiqué par le _path, not implemented */
    public final BitVector open(String _pathName, String _fileName) {
        error("not implemented");
        return null;
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        error("not implemented");
    }

    /** créer une nouvelle instance de WordTable*/
    private BitVector_Volatile(String _pathName, String _fileName, int _maxSize) {
        createBitVector_Volatile(_pathName, _fileName, _maxSize);
    }

    private final void createBitVector_Volatile(String _pathName, String _fileName, int _maxSize) {
        size = _maxSize;
        initFirstTime();
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        if ((size % 32) != 0) {
            error_fatal("BitVector_InMemory size must be a multiple of 32");
        }
        b = new int[size / 32];
    }

    /** mets à jour la position pos avec la valeur val */
    public final void set(int pos, boolean val) {
        if (val) {
            b[pos / 32] |= mask[pos % 32];
        } // set bit
        else {
            b[pos / 32] &= ~mask[pos % 32];
        }
    }

    /**  cherche la valeur à la position pos  */
    public final boolean get(int pos) {
        return (b[pos >> 5] & mask[pos % 32]) == mask[pos % 32]; // >>5 divise par 32 
    }

    /**  cherche le vecteur entier  */
    public final SetOfBits get() {
        return new SetOfBits(b);
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "BitVector_Volatile: " + "statistics -> " +
                "\n  size: " + size;
    }
}
