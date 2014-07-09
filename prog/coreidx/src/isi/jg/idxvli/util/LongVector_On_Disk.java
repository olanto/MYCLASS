package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import isi.jg.idxvli.mapio.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *  Comportements d'un vecteur de Long bufferisé avec des IO Map.
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
 */
public class LongVector_On_Disk implements LongVector {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "LongVector_On_Disk 2.1";
    /* variables du gestionnaire  -------------------------------------- */
    /** definit la version */
    private String VERSION;
    /** definit le path pour l'ensemble des fichiers dépendant de ce gestionnaire */
    private String pathName;
    /** definit le path pour l'ensemble des fichiers dépendant de ce gestionnaire */
    private String fileName;
    private DirectIOFile v;
    private int size = 0;
    private int slice2n = 20;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public LongVector_On_Disk() {
    }

    /**  crée un vecteur de taille 2^_maxSize à l'endroit indiqué par le path */
    public final LongVector create(String _pathName, String _fileName, int _maxSize) {
        return (new LongVector_On_Disk(_pathName, _fileName, _maxSize));
    }

    /**  ouvre un vecteur  à l'endroit indiqué par le _path */
    public final LongVector open(String _pathName, String _fileName) {
        return (new LongVector_On_Disk(_pathName, _fileName));
    }

    /**  ferme un vecteur  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- vector is closed now:" + fileName);
    }

    /** créer une nouvelle instance de WordTable à partir des données existantes*/
    private LongVector_On_Disk(String _pathName, String _fileName) {  // recharge un gestionnaire
        pathName = _pathName;
        fileName = _fileName;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de WordTable*/
    private LongVector_On_Disk(String _pathName, String _fileName, int _maxSize) {
        createLongVector_InMemory(_pathName, _fileName, _maxSize);
    }

    private final void createLongVector_InMemory(String _pathName, String _fileName, int _maxSize) {
        pathName = _pathName;
        fileName = _fileName;
        size = (int) Math.pow(2, _maxSize);
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        try {
            v = new MappedFile();
            v.open(pathName + "/" + fileName + "_dio", MappingMode.NOMAP, readWriteMode.rw, slice2n, size * 8);
//         v.seek(size*4-4);
//         v.writeInt(0); // marque la dernière place
        } catch (IOException e) {
            error("IO error in LongVector_On_Disk.initFirstTime", e);
        }
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // écrire les flags
            p.writeInt(size);
            System.out.println("save Int Vector: " + pathName + "/" + fileName);
            p.flush();
            ostream.close();
            v.close();
        } catch (IOException e) {
            error("IO error in LongVector_On_Disk.saveMasterFile", e);
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            size = p.readInt();
            System.out.println("load Int Vector: " + pathName + "/" + fileName);
            istream.close();
            v = new MappedFile();
            v.open(pathName + "/" + fileName + "_dio", MappingMode.NOMAP, readWriteMode.rw, slice2n, size * 8);
        } catch (Exception e) {
            error("IO error file LongVector_On_Disk.loadMasterFile", e);
        }
    }

    private final void printMasterFile() {
        msg("--- Int Vector parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("fileName: " + fileName);
        msg("size: " + size);
    }

    /** mets à jour la position pos avec la valeur val */
    public final void set(int pos, long val) {
        try {
            v.seek(pos << 3); // pos*8
            v.writeLong(val);
        } catch (Exception e) {
            error("IO error file LongVector_On_Disk.set pos:" + pos + " val:" + val, e);
        }
    }

    /**  cherche la valeur à la position pos  */
    public final long get(int pos) {
        try {
            v.seek(pos << 3); // pos*8
            return v.readLong();
        } catch (Exception e) {
            error("IO error file LongVector_On_Disk.get pos:" + pos, e);
        }
        return -1;
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
        return "LongVector_On_Disk: " + pathName + "/" + fileName + "statistics -> " +
                "\n  size: " + size;
    }
}
