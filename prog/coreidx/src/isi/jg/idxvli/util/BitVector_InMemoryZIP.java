package isi.jg.idxvli.util;

import java.util.*;
import java.util.zip.*;

import java.io.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

/**
 *  Comportements d'un vecteur de bit chargé entièrement en mémoire et zippé.
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
public class BitVector_InMemoryZIP implements BitVector {
    /* constantes d'un gestionnaire  -------------------------------------- */

    static final String SOFT_VERSION = "BitVector_InMemoryZIP 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
    private String fileName;
    private int[] b;
    private int size = 0;
    private static final int[] mask = {1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8, 1 << 9,
        1 << 10, 1 << 11, 1 << 12, 1 << 13, 1 << 14, 1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19,
        1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27, 1 << 28, 1 << 29,
        1 << 30, 1 << 31
    };
    private static final boolean nowrapboolean = true;  // no wrap pour le ZIP
    private static Deflater def = new Deflater(9, nowrapboolean);
    private static Inflater inf = new Inflater(nowrapboolean);

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public BitVector_InMemoryZIP() {
    }

    /**  crée un vecteur de taille 2^_maxSize à l'endroit indiqué par le path */
    public final BitVector create(String _pathName, String _fileName, int _maxSize) {
        return (new BitVector_InMemoryZIP(_pathName, _fileName, _maxSize));
    }

    /**  ouvre un vecteur  à l'endroit indiqué par le _path */
    public final BitVector open(String _pathName, String _fileName) {
        return (new BitVector_InMemoryZIP(_pathName, _fileName));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** créer une nouvelle instance de WordTable à partir des données existantes*/
    private BitVector_InMemoryZIP(String _pathName, String _fileName) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de WordTable*/
    private BitVector_InMemoryZIP(String _pathName, String _fileName, int _maxSize) {
        createBitVector_InMemoryZIP(_pathName, _fileName, _maxSize);
    }

    private final void createBitVector_InMemoryZIP(String _pathName, String _fileName, int _maxSize) {
        pathName = _pathName;
        fileName = _fileName;
        size = _maxSize;
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        if ((size % 32) != 0) {
            error_fatal("BitVector_InMemory size must be a multiple of 32");
        }
        b = new int[size / 32];
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // écrire les flags
            p.writeInt(size);
            byte[] bb = compress(b);
            // showVector(bb);
            p.writeObject(bb);
            System.out.println("save Bit Vector: " + pathName + "/" + fileName);
            p.flush();
            ostream.close();
        } catch (IOException e) {
            error("IO error in BitVector_InMemoryz-.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            byte[] bb = (byte[]) p.readObject();
            // showVector(bb);
            b = decompress(bb, size / 32 + 1);
            msg("load Bit Vector: " + pathName + "/" + fileName);
            istream.close();
        } catch (Exception e) {
            error("IO error file BitVector_InMemorZIP.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Bit Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
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
        return "BitVector_InMemoryZIP: " + pathName + "/" + fileName + "statistics -> " +
                "\n  size: " + size;
    }
}
