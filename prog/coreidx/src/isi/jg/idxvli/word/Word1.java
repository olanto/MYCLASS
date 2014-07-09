package isi.jg.idxvli.word;

import isi.jg.idxvli.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import java.util.concurrent.locks.*;

/**
 *  gestionnaire de dictionnaire.
 *
 * <p>author: Jacques Guyot
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * <p>
 * *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - �crivain en exclusion avec tous
 *  doit �tre le point d'acc�s pour toutes les structures utilis�es !
 *  </pre>
 *   */
public class Word1 implements WordManager {

    private StringRepository dictionnary;
    private readWriteMode RW = readWriteMode.rw;

    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public Word1() {
    }

    /**  cr�e un gestionnaire de documents (la taille et la longueur) � l'endroit indiqu� par le path */
    public final WordManager create(implementationMode _ManagerImplementation,
            String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new Word1(_ManagerImplementation,
                _path, _idxName, _maxSize, _lengthString));
    }

    /**  ouvre un gestionnaire de documents  � l'endroit indiqu� par le _path */
    public final WordManager open(implementationMode _ManagerImplementation,
            readWriteMode _RW, String _path, String _idxName) {
        return (new Word1(_ManagerImplementation,
                _RW, _path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        dictionnary.close();
        msg("--- WordManager is closed now ");
    }

    /** cr�er une nouvelle instance de WordManager � partir des donn�es existantes*/
    private Word1(implementationMode _ManagerImplementation,
            readWriteMode _RW, String _pathName, String _idxName) {  // recharge un gestionnaire
        RW = _RW;
        switch (_ManagerImplementation) {
            case FAST:
                dictionnary = (new StringTable_HomeHash_InMemory()).open(_pathName, _idxName + "_dict");
                break;
            case BIG:
                dictionnary = (new StringTable_OnDisk_WithCache_MapIO()).open(_pathName, _idxName + "_dict");
                break;
            case XL:
                dictionnary = (new StringTable_OnDisk_WithCache_MapIO_XL()).open(_pathName, _idxName + "_dict");
                break;
            case XXL:
                dictionnary = (new StringTable_OnDisk_WithCache_XXL()).open(_pathName, _idxName + "_dict");
                break;
        }
    }

    /** cr�er une nouvelle instance de Word Manager*/
    private Word1(implementationMode _ManagerImplementation,
            String _pathName, String _idxName, int _maxSize, int _lengthString) {
        switch (_ManagerImplementation) {
            case FAST:
                dictionnary = (new StringTable_HomeHash_InMemory()).create(_pathName, _idxName + "_dict", _maxSize, -1); 
                break;
            case BIG:
                dictionnary = (new StringTable_OnDisk_WithCache_MapIO()).create(_pathName, _idxName + "_dict", _maxSize, _lengthString); 
                break;
            case XL:
                dictionnary = (new StringTable_OnDisk_WithCache_MapIO_XL()).create(_pathName, _idxName + "_dict", _maxSize, _lengthString); 
                break;
            case XXL:
                dictionnary = (new StringTable_OnDisk_WithCache_XXL()).create(_pathName, _idxName + "_dict", _maxSize, _lengthString); 
                break;
        }
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg("------------------------------------------------------------");
        msg("- WORDS TABLE STAT                                         -");
        msg("------------------------------------------------------------");
        msg("DICTIONNARY:");
        dictionnary.printStatistic();
        msg("");
    }
    /** op�ration sur documentName verrous ------------------------------------------*/
    private final ReentrantReadWriteLock dictionnaryRW = new ReentrantReadWriteLock();
    private final Lock dictionnaryR = dictionnaryRW.readLock();
    private final Lock dictionnaryW = dictionnaryRW.writeLock();

    /**  ajoute un document au gestionnaire retourne le num�ro du docuemnt*/
    public final int put(String d) {
        dictionnaryW.lock();
        try {
            //msg("add this:"+d);
            int id = dictionnary.put(d);
            return id;
        } finally {
            dictionnaryW.unlock();
        }
    }

    /**  cherche le num�ro du document, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String d) {
        dictionnaryR.lock();
        try {
            return dictionnary.get(d);
        } finally {
            dictionnaryR.unlock();
        }
    }

    /**  cherche le document associ� � un num�ro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        dictionnaryR.lock();
        try {
            return dictionnary.get(i);
        } finally {
            dictionnaryR.unlock();
        }
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        dictionnaryR.lock();
        try {
            return dictionnary.getCount();
        } finally {
            dictionnaryR.unlock();
        }
    }
}


