package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;

/**
 * gestionaire de mots chargé en mémoire.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *  <pre>
 *  concurrence 
 *  get est // pour les lecteurs (pas de pbr?)
 *  les autres doivent être protégé par un écrivain (externe)
 *  </pre>
 */
public class StringTable_HomeHash_InMemory implements StringRepository {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "StringTable_HomeHash_InMemory 2.1";
    /* variables d'un gestionnaire du dictionaire -------------------------------------- */
    /** definit la version */
    String VERSION;
    /** definit le nom générique des fichiers */
    String GENERIC_NAME;
    /** definit le path pour l'ensemble des fichiers dépendant de ce Dictionnaire */
    String pathName;
    /** definit le fichier */
    String idxName;
    /** defini la taille maximum du dictionaire 2^maxSize, (maximum=2^31) */
    private int maxSize = 10;
    /** defini un complément pour des opérations sur les int */
    private int comp32 = 32 - maxSize;
    /** defini la table de mot */
    private String[] T = new String[(int) Math.pow(2, maxSize)];
    /** defini la table des indirections entre les hash et les id */
    private int[] indirect = new int[(int) Math.pow(2, maxSize)];
    /** defini la taille utilisable du dictionaire de mots */
    private int utilSize = (int) Math.pow(2, maxSize) - 1;
    /** nbr de mots actuellement dans le dictionnaire */
    private int count = 0;
    /** nbr de collisions enregistrées */
    private long collision = 0;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public StringTable_HomeHash_InMemory() {
    }

    /**  crée une word table de la taille 2^_maxSize par défaut à l'endroit indiqué par le path */
    public final StringRepository create(String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new StringTable_HomeHash_InMemory(_path, _idxName, "ext", _maxSize));
    }

    /**  ouvre un gestionnaire de mots  à l'endroit indiqué par le _path */
    public final StringRepository open(String _path, String _idxName) {
        return (new StringTable_HomeHash_InMemory(_path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- StringTable is closed now ");
    }

    /** créer une nouvelle instance de StringTable à partir des données existantes*/
    private StringTable_HomeHash_InMemory(String _pathName, String _idxName) {  // recharge un gestionnaire
        pathName = _pathName;
        idxName = _idxName;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de StringTable*/
    private StringTable_HomeHash_InMemory(String _pathName, String _idxName, String _generic_name, int _maxSize) {
        createStringTable(_pathName, _idxName, _generic_name, _maxSize);
    }

    private final void createStringTable(String _pathName, String _idxName, String _generic_name, int _maxSize) {  // recharge un gestionnaire
        pathName = _pathName;
        idxName = _idxName;
        GENERIC_NAME = _generic_name;
        maxSize = _maxSize;
        VERSION = SOFT_VERSION;
        initFirstTime();
        saveMasterFile();
    }

    private final void initFirstTime() { // n'utiliser que la première fois, à la création
        comp32 = 32 - maxSize;
        count = 0;
        collision = 0;
        T = new String[(int) Math.pow(2, maxSize)];
        indirect = new int[(int) Math.pow(2, maxSize)];
        for (int i = 0; i < (int) Math.pow(2, maxSize); i++) {
            indirect[i] = EMPTY;
        }
        utilSize = (int) Math.pow(2, maxSize) - 1;
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + idxName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // écrire les flags
            p.writeObject(GENERIC_NAME);
            p.writeInt(maxSize);
            p.writeInt(comp32);
            p.writeInt(count);
            p.writeInt(utilSize);
            p.writeLong(collision);
            p.writeObject(T);
            p.writeObject(indirect);
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
            maxSize = p.readInt();
            comp32 = p.readInt();
            count = p.readInt();
            utilSize = p.readInt();
            collision = p.readLong();
            T = (String[]) p.readObject();
            indirect = (int[]) p.readObject();
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
        msg("maxSize: " + maxSize);
        msg("comp32: " + comp32);
        msg("count: " + count);
        msg("utilSize: " + utilSize);
        msg("collision: " + collision);
    }

    /**  ajoute un terme au gestionnaire retourne le numéro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja 
     */
    public final int put(String w) {
        //  System.out.println(w);
        if (count >= utilSize) { // on doit garder un trou pour le get()
            error("*** error StringTable is full");
            return EMPTY;
        }
        int h = hash(w);
        // System.out.println(w+" h init:"+h);
        while (indirect[h] != -1 && !w.equals(T[indirect[h]])) { // cherche un trou
            h = ((h + 1) << comp32) >>> comp32;
            collision++;  // est pas tout à fait juste si plusieurs occurences similaires
        }
        if (indirect[h] != -1) {
            return indirect[h];
        }   // existe déjà  bug 11.12.2005!
        //  on a trouvé un trou
        indirect[h] = count;
        T[indirect[h]] = w;
        count++;
        return indirect[h];
    }

    /**  cherche le numéro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String w) {
        int h = hash(w);
        //System.out.println(w+" h init:"+h);
        while (indirect[h] != -1 && !w.equals(T[indirect[h]])) { // cherche un trou ou égal
            h = ((++h) << comp32) >>> comp32;
        //collision++;  // est pas tout à fait juste si plusieurs occurences similaires
        }
        if (indirect[h] == -1) {
            return EMPTY;
        } // pas trouvé
        else {
            return indirect[h];
        }
    }

    /**  cherche le terme associé à un numéro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        if (i < 0 || i > count) {
            return NOTINTHIS;
        }
        return T[i];
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "String Table statistics :" + pathName + "/" + idxName + " id: " + GENERIC_NAME +
                "\n  Current version: " + SOFT_VERSION +
                "\n  utilSize: " + utilSize + " count: " + count + " collision: " + collision;
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        //msg("count from :"+count);
        return count;
    }

    private final int hash(String s) {  // ok
        return (s.hashCode() << comp32) >>> comp32;
    }

    public final void modify(int i, String newValue) {
        T[i] = newValue;
    }
}
