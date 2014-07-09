package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;

/**
 * gestionaire de mots chargé en mémoire // ici les mots ne sont que des nombres (pour le document manager avec le content manager
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * <p>
 *  <pre>
 *  concurrence 
 *  get est // pour les lecteurs (pas de pbr?)
 *  les autres doivent être protégé par un écrivain (externe)
 *  </pre>
 */
public class StringTable_Direct_InMemory implements StringRepository {
    /* constantes d'un gestionnaire du dictionaire -------------------------------------- */

    static final String SOFT_VERSION = "StringTable_Direct_InMemory 2.1";
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
    /** defini la table de mot */
    private int[] T = new int[(int) Math.pow(2, maxSize)];
    /** defini la table des indirections entre les hash et les id */
    private int[] inverse = new int[(int) Math.pow(2, maxSize)];
    /** nbr de mots actuellement dans le dictionnaire */
    private int count = 0;
    /** defini la taille utilisable du dictionaire de mots */
    private int utilSize = (int) Math.pow(2, maxSize) - 1;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public StringTable_Direct_InMemory() {
    }

    /**  crée une word table de la taille 2^_maxSize par défaut à l'endroit indiqué par le path */
    public final StringRepository create(String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new StringTable_Direct_InMemory(_path, _idxName, "ext", _maxSize));
    }

    /**  ouvre un gestionnaire de mots  à l'endroit indiqué par le _path */
    public final StringRepository open(String _path, String _idxName) {
        return (new StringTable_Direct_InMemory(_path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        saveMasterFile();
        msg("--- StringTable is closed now ");
    }

    /** créer une nouvelle instance de StringTable à partir des données existantes*/
    private StringTable_Direct_InMemory(String _pathName, String _idxName) {  // recharge un gestionnaire
        pathName = _pathName;
        idxName = _idxName;
        loadMasterFile();
    //printMasterFile();
    }

    /** créer une nouvelle instance de StringTable*/
    private StringTable_Direct_InMemory(String _pathName, String _idxName, String _generic_name, int _maxSize) {
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
        count = 0;
        T = new int[(int) Math.pow(2, maxSize)];
        inverse = new int[(int) Math.pow(2, maxSize)];
        for (int i = 0; i < (int) Math.pow(2, maxSize); i++) {
            inverse[i] = EMPTY;
        }
        utilSize = (int) Math.pow(2, maxSize);
    }

    private final void saveMasterFile() {  // sauver les informations persistante du gestionnaire
        try {
            FileOutputStream ostream = new FileOutputStream(pathName + "/" + idxName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(VERSION); // écrire les flags
            p.writeObject(GENERIC_NAME);
            p.writeInt(maxSize);
            p.writeInt(count);
            p.writeInt(utilSize);
            p.writeObject(T);
            p.writeObject(inverse);
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
            count = p.readInt();
            utilSize = p.readInt();
            T = (int[]) p.readObject();
            inverse = (int[]) p.readObject();
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
        msg("utilSize: " + utilSize);
        msg("count: " + count);
    }

    final int getint(String w) {// les noms sont des nombres !
        try {
            return Integer.parseInt(w);
        } catch (NumberFormatException ex) {
            error("conversion doc_name must be a number!" + w, ex);
            return EMPTY;
        }
    }

    /**  ajoute un terme au gestionnaire retourne le numéro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja 
     */
    public final int put(String w) {
        //  System.out.println(w);
        int nw = getint(w);
        if (nw == EMPTY) {
            return EMPTY;
        } // erreur de conversion
        if (inverse[nw] != EMPTY) {
            return inverse[nw];
        } // existe déja
        // nouvelle entrée
        if (count >= utilSize) { // 
            error("*** error StringTable is full");
            return EMPTY;
        }

        T[count] = nw;
        inverse[nw] = count;
        count++;
        return inverse[nw];
    }

    /**  cherche le numéro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String w) {
        int nw = getint(w);
        if (nw == EMPTY) {
            return EMPTY;
        } // erreur de conversion
        return inverse[nw];
    }

    /**  cherche le terme associé à un numéro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        if (i < 0 || i > utilSize) {
            return NOTINTHIS;
        }
        return "" + T[i];
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "String Table statistics :" + pathName + "/" + idxName + " id: " + GENERIC_NAME +
                "\n  Current version: " + SOFT_VERSION +
                "\n  count: " + count;
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        //msg("count from :"+count);
        return count;
    }

    public final void modify(int i, String newValue) {  // utilisé pour invalider un document
        inverse[T[i]] = EMPTY;
        T[i] = EMPTY;
    }
}
