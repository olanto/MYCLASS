package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;

/**
 * Comportements d'un tableau de bit[2^maxSize][fixedArraySize] Zippé.
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
public class BitArrayVector_ZIP implements BitArrayVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "BitArrayVector_ZIP 2.1";
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
    private ByteArrayVector vZip;
    private readWriteMode RW = readWriteMode.rw;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public BitArrayVector_ZIP() {
    }

    /**  crée un vecteur de taille 2^_maxSize à l'endroit indiqué par le path */
    public final BitArrayVector create(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        return (new BitArrayVector_ZIP(_ManagerImplementation, _pathName, _fileName, _maxSize, _fixedArraySize));
    }

    /**  ouvre un vecteur  à l'endroit indiqué par le _path */
    public final BitArrayVector open(implementationMode _ManagerImplementation, String _pathName, String _fileName, readWriteMode _RW) {
        return (new BitArrayVector_ZIP(_ManagerImplementation, _pathName, _fileName, _RW));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        vZip.close();
        msg("--- vector is closed now:" + fileName);
    }

    /** créer une nouvelle instance de WordTable à partir des données existantes*/
    private BitArrayVector_ZIP(implementationMode _ManagerImplementation, String _pathName, String _fileName, readWriteMode _RW) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        RW = _RW;
        loadMasterFile();
        switch (_ManagerImplementation) {
            case FAST:
                vZip = new ByteArrayVector_InMemory().open(_pathName, _fileName + "_zip", RW);
                break;
            case BIG:
                vZip = new ByteArrayVector_OnDisk().open(_pathName, _fileName + "_zip", RW);
                break;
        }
    //printMasterFile();
    }

    /** créer une nouvelle instance de WordTable*/
    private BitArrayVector_ZIP(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        createBitArrayVector_ZIP(_ManagerImplementation, _pathName, _fileName, _maxSize, _fixedArraySize);
    }

    private final void createBitArrayVector_ZIP(implementationMode _ManagerImplementation, String _pathName, String _fileName, int _maxSize, int _fixedArraySize) {
        pathName = _pathName;
        fileName = _fileName;
        fixedArraySize = _fixedArraySize;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        switch (_ManagerImplementation) {
            case FAST:
                vZip = new ByteArrayVector_InMemory().create(pathName, fileName + "_zip", _maxSize, fixedArraySize);
                break;
            case BIG:
                vZip = new ByteArrayVector_OnDisk().create(pathName, fileName + "_zip", _maxSize, fixedArraySize / HOPE_COMPRESSION);
                break;
        }
        initFirstTime();
        saveMasterFile();
        vZip.close();
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        SetOfBits wBA = new SetOfBits(fixedArraySize);  // crée un vecteur vide
        byte[] empty = wBA.getZip();
        for (int i = 0; i < size; i++) {
            vZip.set(i, empty);
        } //initialise tous les vecteurs à vide
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        if (RW == readWriteMode.rw) {
            try {
                FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
                ObjectOutputStream p = new ObjectOutputStream(ostream);
                p.writeObject(VERSION); // écrire les flags
                p.writeInt(size);
                p.writeInt(fixedArraySize);
                msg("save Bit Vector: " + pathName + "/" + fileName);
                p.flush();
                ostream.close();
            } catch (IOException e) {
                error("IO error in BitArrayVector_ZIP.saveMasterFile", e);
            }
        } else {
            msg("UnSave Bit Vector: " + pathName + "/" + fileName);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            fixedArraySize = p.readInt();
            System.out.println("load Long Vector: " + pathName + "/" + fileName);
            istream.close();
        } catch (Exception e) {
            error("IO error file BitArrayVector_ZIP.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Bit Array Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
        msg("fixedArraySize: " + fixedArraySize);
    }

    /** mets à jour la position pos avec la valeur val, la ième valeur  */
    public final void set(int pos, int i, boolean val) {
        if (RW == readWriteMode.rw) {
            SetOfBits wBA = new SetOfBits(vZip.get(pos), fixedArraySize);  // crée un vecteur depuis son zip
            wBA.set(i, val);
            vZip.set(pos, wBA.getZip());
        } else {
            error("BitArrayVector_ZIP.set is not allowed, mode must be in rw");
        }
    }

    /** mets à jour la position pos avec le vecteur complet*/
    public final void set(int pos, SetOfBits v) {
        vZip.set(pos, v.getZip());
    }

    /**  cherche la valeur à la position pos, la ième valeur   */
    public final boolean get(int pos, int i) {
        SetOfBits wBA = new SetOfBits(vZip.get(pos), fixedArraySize);  // crée un vecteur depuis son zip
        return wBA.get(i);
    }

    /**  cherche le vecteur complet à la position pos  */
    public final SetOfBits get(int pos) {
        return new SetOfBits(vZip.get(pos), fixedArraySize);
    }

    /**  retourne la taille du vecteur */
    public final int length() {
        return size;
    }

    /**  retourne la taille du vecteur à la position pos*/
    public final int length(int pos) {
        return fixedArraySize;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "Bit Array Vector statistics -> " + pathName + "/" + fileName +
                "\n  size: " + size +
                "\n  fixedArraySize: " + fixedArraySize +
                "\n  Only for BIG, hopeCompression: " + HOPE_COMPRESSION + ", maxUsedLength: " + vZip.maxUsedlength() +
                "\n  actualCompression: " + (fixedArraySize / vZip.maxUsedlength());
    }
}
