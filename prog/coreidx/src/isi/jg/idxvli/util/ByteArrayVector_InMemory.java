package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *  Comportements d'un vecteur de byte[fixedArraySize] chargé entièrement en mémoire.
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
public class ByteArrayVector_InMemory implements ByteArrayVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "ByteArrayVector_InMemory 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
    private String fileName;
    private byte[][] v;
    private int size = 0;
    private int fixedArraySize;
    private int maxUsedlength = 0;
    private readWriteMode RW = readWriteMode.rw;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public ByteArrayVector_InMemory() {
    }

    /**  crée un vecteur de taille 2^_maxSize à l'endroit indiqué par le path */
    public final ByteArrayVector create(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new ByteArrayVector_InMemory(_pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  à l'endroit indiqué par le _path */
    public final ByteArrayVector open(String _pathName, String _fileName, readWriteMode _RW) {
        return (new ByteArrayVector_InMemory(_pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** créer une nouvelle instance de WordTable à partir des données existantes*/
    private ByteArrayVector_InMemory(String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de WordTable*/
    private ByteArrayVector_InMemory(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createByteArrayVector_InMemory(_pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createByteArrayVector_InMemory(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        v = new byte[size][];
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        if (RW == readWriteMode.rw) {
            try {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // écrire les flags
                p.writeInt(size);
                p.writeInt(fixedArraySize);
                p.writeInt(maxUsedlength);
                p.writeObject(v);
                System.out.println("save Byte Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } catch (IOException e) {
                error("IO error in ByteArrayVector_InMemory.saveMasterFile", e);
            }
        } else {
            msg("UnSave Byte Vector: " + pathName + "/" + fileName);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            maxUsedlength = p.readInt();
            v = (byte[][]) p.readObject();
            System.out.println("load Byte Arrays Vector: " + pathName + "/" + fileName);
            //printMasterFile();
            istream.close();
        } catch (Exception e) {
            error("IO error file ByteArrayVector_InMemory.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Byte Array Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
        msg("maxUsedlength: " + maxUsedlength);
    }

    /** mets à jour la position pos avec la valeur val */
    public final void set(int pos, byte[] val) {
        // pas de test sur le mode RW, pour accélérer
        if (val.length <= fixedArraySize) {
            if (val.length > maxUsedlength) {
                maxUsedlength = val.length;
            }
            //msg ("set pos:"+pos);
            v[pos] = val;
        } else {
            error("int array is too big length:" + val.length + " limit:" + fixedArraySize);
        }
    }

    /**  cherche la valeur à la position pos  */
    public final byte[] get(int pos) {
        return v[pos];
    }

    /**  cherche la valeur à la position pos, la ième valeur   */
    public final byte get(int pos, int i) {
        return v[pos][i];
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille du vecteur à la position pos*/
    public final int length(int pos) {
        if (v[pos] != null) {
            return v[pos].length;
        }
        return 0;
    }

    /**  retourne la taille maximum des vecteurs stocké*/
    public final int maxUsedlength() {
        return maxUsedlength;
    }

    private final int countNotEmpty() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (v[i] != null) {
                count++;
            }
        }
        return count;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("Byte Array Vector statistics -> " + pathName + "/" + fileName);
        msg(" size: " + size);
        msg(" not Empty: " + countNotEmpty());
        msg("fixedArraySize: " + fixedArraySize);
        msg("maxUsedlength: " + maxUsedlength);
    }

    public final void clear(int pos) {
        v[pos] = null;
    }
}
