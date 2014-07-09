package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;

/**
 *  gestionaire de mots avec deux hash tables.
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
public class StringTable_Classic implements StringRepository {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "StringTable_Classic 2.1";
    /* variables d'un gestionnaire du dictionaire -------------------------------------- */
    /** definit la version */
    String VERSION;
    /** definit le nom générique des fichiers */
    String GENERIC_NAME;
    /** definit le path pour l'ensemble des fichiers dépendant de ce Dictionnaire */
    String pathName;
    /** definit le fichier */
    String idxName;
    /** dictionnaire de mots (mot->indice)*/
    private Hashtable<String, Integer> words;
    /** dictionnaire inverse de mots (indice->mot)*/
    private Hashtable<Integer, String> wordsinv;
    /** nbr de mots actuellement dans le dictionnaire */
    private int count = 0;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public StringTable_Classic() {
    }

    /**  crée une word table (la taille et la longueur max ne sont pas utilisés) par défaut à l'endroit indiqué par le path */
    public final StringRepository create(String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new StringTable_Classic(_path, _idxName, "ext", _maxSize));
    }

    /**  ouvre un gestionnaire de mots  à l'endroit indiqué par le _path */
    public final StringRepository open(String _path, String _idxName) {
        return (new StringTable_Classic(_path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- StringTable is closed now ");
    }

    /** créer une nouvelle instance de StringTable à partir des données existantes*/
    private StringTable_Classic(String _pathName, String _idxName) {  // recharge un gestionnaire
        pathName = _pathName;
        idxName = _idxName;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de StringTable*/
    private StringTable_Classic(String _pathName, String _idxName, String _generic_name, int _maxSize) {
        createStringTable(_pathName, _idxName, _generic_name, _maxSize);
    }

    private final void createStringTable(String _pathName, String _idxName, String _generic_name, int _maxSize) {  // recharge un gestionnaire
        pathName = _pathName;
        idxName = _idxName;
        GENERIC_NAME = _generic_name;
        VERSION = SOFT_VERSION;
        initFirstTime(_maxSize);
        saveMasterFile();
    }

    private final void initFirstTime(int _maxSize) { // n'utiliser que la première fois, à la création
        count = 0;
        words = new Hashtable<String, Integer>();
        wordsinv = new Hashtable<Integer, String>();
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + idxName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // écrire les flags
            p.writeObject(GENERIC_NAME);
            p.writeInt(count);
            p.writeObject(words);
            p.writeObject(wordsinv);
            System.out.println("save Master String Table File for: " + pathName + "/" + idxName + " id: " + GENERIC_NAME);
            p.flush();
            ostream.close();
        } catch (IOException e) {
            System.err.println("IO error in StringTable.saveMasterFile");
            e.printStackTrace();
        }
    }

    private final void loadMasterFile() {
        try {
            FileInputStream istream = new FileInputStream(pathName + "/" + idxName);
            ObjectInputStream p = new ObjectInputStream(istream);
            VERSION = (String) p.readObject();
            GENERIC_NAME = (String) p.readObject();
            count = p.readInt();
            words = ((Hashtable<String, Integer>) p.readObject());  // le casting est nécessaire et génére un warning à la compilation
            wordsinv = (Hashtable<Integer, String>) p.readObject(); // le casting est nécessaire et génére un warning à la compilation
            System.out.println("load Master String Table File for: " + pathName + "/" + idxName + " id: " + GENERIC_NAME);
            istream.close();
        } catch (Exception e) {
            System.err.println("IO error file StringTable.loadMasterFile");
            e.printStackTrace();
        }
    }

    private final void printMasterFile() {
        msg("--- String Table parameters, Current version: " + SOFT_VERSION);
        msg("pathName: " + pathName);
        msg("idxName: " + idxName);
        msg("GENERIC_NAME: " + GENERIC_NAME);
        msg("count: " + count);
    }

    /**  ajoute un terme au gestionnaire retourne le numéro du terme, retourne EMPTY s'il y a une erreur */
    public final int put(String w) {
        if ((words.get(w)) != null) {
            return words.get(w);
        }  // existe déjâ
        //System.out.println("put:"+w);
        words.put(w, count);
        wordsinv.put(count, w);
        count++;
        return count - 1;
    }

    /**  cherche le numéro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String w) {
        Integer n = words.get(w);
        if (n == null) {
            return EMPTY;
        }
        return n.intValue();
    }

    /**  cherche le terme associé à un numéro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        if (i < 0 || i > count) {
            return NOTINTHIS;
        }
        return wordsinv.get(i);
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "String Table statistics -> " +
                "/n count: " + count;
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        return count;
    }

    public final void modify(int i, String newValue) {
        error("not implemented");
    }
}
