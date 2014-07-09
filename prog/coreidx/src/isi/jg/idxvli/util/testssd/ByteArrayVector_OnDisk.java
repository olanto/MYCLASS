package isi.jg.idxvli.util.testssd;

import isi.jg.idxvli.util.*;
import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *  Comportements d'un vecteur de byte[fixedArraySize] géré sur disque.
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
public class ByteArrayVector_OnDisk implements ByteArrayVector {

    static final int minInit = 128;  // nbr de blocs pour l'initialisation
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */
    static final String SOFT_VERSION = "ByteArrayVector_InMemory 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers dépendant de cet ObjectStore */
    private String pathName;
    /** definit les fichiers dépendant de cet ObjectStore */
    private String fileName;
    private byte[][] v;
    private int size = 0;
    private int fixedArraySize;
    private int fixedDiskSize;
    private int maxUsedlength = 0;
    private RandomAccessFile rdoc;
    private readWriteMode RW = readWriteMode.rw;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public ByteArrayVector_OnDisk() {
    }

    /**  crée un vecteur de taille 2^_maxSize à l'endroit indiqué par le path */
    public final ByteArrayVector create(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new ByteArrayVector_OnDisk(_pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  à l'endroit indiqué par le _path */
    public final ByteArrayVector open(String _pathName, String _fileName, readWriteMode _RW) {
        return (new ByteArrayVector_OnDisk(_pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        try {
            rdoc.close();
        } catch (Exception e) {
            error("IO error in close()", e);
        }
        msg("--- vector is closed now:" + fileName);
    }

    /** créer une nouvelle instance de WordTable à partir des données existantes*/
    private ByteArrayVector_OnDisk(String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de WordTable*/
    private ByteArrayVector_OnDisk(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createByteArrayVector_OnDisk(_pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createByteArrayVector_OnDisk(String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        fixedDiskSize = fixedArraySize + 4;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        try {
            rdoc = new RandomAccessFile(pathName + "/" + fileName + ".rnd", RW.name());
            initFirstTime();
            saveMasterFile();
        } catch (Exception e) {
            error("IO error in createByteArrayVector_OnDisk()", e);
        }
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        // alloue la totalité a l'initialisation
        byte[] b = new byte[minInit * fixedDiskSize];
        for (int i = 0; i < size / minInit; i++) {
            writeBytes(b, (long) i * (long) b.length, rdoc); // marque vide
        }
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            if (RW == readWriteMode.rw) {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // écrire les flags
                p.writeInt(size);
                p.writeInt(fixedArraySize);
                p.writeInt(fixedDiskSize);
                p.writeInt(maxUsedlength);
                System.out.println("save Int Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } else {
                msg("UnSave Byte Vector: " + pathName + "/" + fileName);
            }
        } catch (IOException e) {
            error("IO error in ByteArrayVector_OnDisk.saveMasterFile", e);
        }


    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            fixedDiskSize = p.readInt();
            maxUsedlength = p.readInt();
            System.out.println("load Long Vector: " + pathName + "/" + fileName);
            istream.close();
            rdoc = new RandomAccessFile(pathName + "/" + fileName + ".rnd", RW.name());
        } catch (Exception e) {
            error("IO error file IntVector_InMemory.loadMasterFile", e);
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
            long posOnDisk = (long) pos * (long) fixedDiskSize;
            writeInt(val.length, posOnDisk, rdoc);
            writeBytes(val, posOnDisk + 4, rdoc);
        } else {
            error("int array is too big length:" + val.length + " limit:" + fixedArraySize);
        }
    }

    /**  cherche la valeur à la position pos  */
    public final byte[] get(int pos) {
        long posOnDisk = (long) pos * (long) (fixedDiskSize);
        int length=readInt(posOnDisk, rdoc);
        return readBytes(32, posOnDisk + 4, rdoc);
    }

    /**  cherche la valeur à la position pos, la ième valeur   */
    public final byte get(int pos, int i) {
        byte[] b = get(pos);
        if (b == null) {
            error("get for " + pos + " is null in " + pathName + "/" + fileName);
            return 0;
        }
        if (i >= b.length) {
            error("get for " + pos + "," + i + " is too big in" + pathName + "/" + fileName);
            return 0;
        }
        return b[i];
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille maximum des vecteurs stocké*/
    public final int maxUsedlength() {
        return maxUsedlength;
    }

    /**  retourne la taille du vecteur à la position pos*/
    public final int length(int pos) {
        // peut devenir une methode de l'interface
        long posOnDisk = (long) pos * (long) (fixedDiskSize);
        return readInt(posOnDisk, rdoc);
    }

    private final int countNotEmpty() {
        // ceci peut être couteux à lire sur le disque !!!!!!!
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (length(i) != 0) {
                count++;
            }
        }
        return count;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("ByteArrayVector " + pathName + "/" + fileName + "statistics -> ");
        msg(" size: " + size);
        //msg(" not Empty: " + countNotEmpty()); // ceci peut être couteux à lire sur le disque !!!!!!!
       msg(" not Empty: not activated"); // ceci peut être couteux à lire sur le disque !!!!!!!
        msg("fixedArraySize: " + fixedArraySize);
        msg("maxUsedlength: " + maxUsedlength);
    }

    public final void clear(int pos) {
        byte[] b = new byte[fixedDiskSize];  // tout à 0
        writeBytes(b, (long) pos * (long) b.length, rdoc); // marque vide
    }
}
