package isi.jg.idxvli.doc;

import java.util.*;
import java.io.*;
import isi.jg.idxvli.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.doc.KeepProperties.*;
import java.util.concurrent.locks.*;

/**
 * gestionnaire de documents.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * gestionnaire de documents.
 *
 * *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - écrivain en exclusion avec tous
 *  doit être le point d'accès pour toutes les structures utilisées !
 *  </pre>
 *
 */
public class Documents1 implements DocumentManager {

    private StringRepository documentName;
    private IntVector documentSize;
    private LongVector documentDate;
    private BitVector documentInvalid;
    private BitVector documentIndexed;
    private PropertiesManager documentProperties;
    private IdxMode updatingMode = IdxMode.INCREMENTAL;
    private LanguageMode keepLanguage;
    private CollectionMode keepCollection;

    /** créer une nouvelle instance de repository pour effectuer les create, open*/
    public Documents1() {
    }

    /**  crée un gestionnaire de documents (la taille et la longueur) à l'endroit indiqué par le path */
    public final DocumentManager create(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new Documents1(_ManagerImplementation, _keepLanguage, _keepCollection,
                _path, _idxName, _maxSize, _lengthString));
    }

    /**  ouvre un gestionnaire de documents  à l'endroit indiqué par le _path */
    public final DocumentManager open(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            IdxMode _updatingMode, String _path, String _idxName) {
        return (new Documents1(_ManagerImplementation, _keepLanguage, _keepCollection,
                _updatingMode, _path, _idxName));
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        DETLOG.info("mode close:" + updatingMode.name());
        if (updatingMode == IdxMode.DIFFERENTIAL) {
            DETLOG.info("Delete All Non Indexed Doc");
            DeleteAllNonIndexedDoc();
        }
        PutInvalidDocLikeAPropertie();
        documentName.close();
        documentSize.close();
        documentDate.close();
        documentInvalid.close();
        documentProperties.close();
        DETLOG.info("--- DocumentManager is closed now ");
    }

    /** créer une nouvelle instance de DocumentManager à partir des données existantes*/
    private Documents1(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            IdxMode _updatingMode, String _pathName, String _idxName) {  // recharge un gestionnaire
        updatingMode = _updatingMode;
        keepLanguage = _keepLanguage;
        keepCollection = _keepCollection;
        switch (_ManagerImplementation) {
            case DIRECT:
                documentName = (new StringTable_Direct_InMemory()).open(_pathName, _idxName + "_name");
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case FAST:
                documentName = (new StringTable_HomeHash_InMemory()).open(_pathName, _idxName + "_name");
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case BIG:
                documentName = (new StringTable_HomeHash_OnDisk_DIO()).open(_pathName, _idxName + "_name");
                // doit être remplacé par une implementation sur disque
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case XL:
                documentName = (new StringTable_OnDisk_WithCache_MapIO_XL()).open(_pathName, _idxName + "_name");
                // doit être remplacé par une implementation sur disque
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
            case XXL:
                documentName = (new StringTable_OnDisk_WithCache_XXL()).open(_pathName, _idxName + "_name");
                // doit être remplacé par une implementation sur disque
                documentSize = (new IntVector_InMemory()).open(_pathName, _idxName + "_size");
                documentDate = (new LongVector_InMemory()).open(_pathName, _idxName + "_date");
                documentInvalid = (new BitVector_InMemoryZIP()).open(_pathName, _idxName + "_invalid");
                break;
        }
        if (updatingMode == IdxMode.DIFFERENTIAL) {
            DETLOG.info("open differential vector");
            documentIndexed = (new BitVector_Volatile()).create(_pathName, _idxName + "_indexed", documentInvalid.length());
        }
        if (updatingMode == IdxMode.QUERY && MODE_CONTINUE == ContinueMode.ALT) {
            DETLOG.info("open documentProperties R");
            documentProperties = (new Properties1()).open(_ManagerImplementation, _pathName, _idxName + "_properties", readWriteMode.r);
        } else {
            DETLOG.info("open documentProperties RW");
            documentProperties = (new Properties1()).open(_ManagerImplementation, _pathName, _idxName + "_properties", readWriteMode.rw);
        }
    //  }

    }

    /** créer une nouvelle instance de Document Manager*/
    private Documents1(implementationMode _ManagerImplementation, LanguageMode _keepLanguage, CollectionMode _keepCollection,
            String _pathName, String _idxName, int _maxSize, int _lengthString) {
        keepLanguage = _keepLanguage;
        keepCollection = _keepCollection;
        switch (_ManagerImplementation) {
            case DIRECT:
                documentName = (new StringTable_Direct_InMemory()).create(_pathName, _idxName + "_name", _maxSize, -1);
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case FAST:
                documentName = (new StringTable_HomeHash_InMemory()).create(_pathName, _idxName + "_name", _maxSize , -1); 
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case BIG:
                documentName = (new StringTable_HomeHash_OnDisk_DIO()).create(_pathName, _idxName + "_name", _maxSize , _lengthString);
                // doit être remplacé par une implementation sur disque
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
            case XL:
                documentName = (new StringTable_OnDisk_WithCache_MapIO_XL()).create(_pathName, _idxName + "_name", _maxSize , _lengthString);
                // doit être remplacé par une implementation sur disque
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
             case XXL:
                documentName = (new StringTable_OnDisk_WithCache_XXL()).create(_pathName, _idxName + "_name", _maxSize , _lengthString);
                // doit être remplacé par une implementation sur disque
                documentSize = (new IntVector_InMemory()).create(_pathName, _idxName + "_size", _maxSize);
                documentDate = (new LongVector_InMemory()).create(_pathName, _idxName + "_date", _maxSize);
                documentInvalid = (new BitVector_InMemoryZIP()).create(_pathName, _idxName + "_invalid", (int) Math.pow(2, _maxSize));
                break;
        }
        documentProperties = (new Properties1()).create(_ManagerImplementation, _pathName, _idxName + "_properties",
                DOC_PROPERTIES_MAXBIT, DOC_PROPERTIES_MAX_LENGHT, _maxSize);
    //  }
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        DETLOG.info(
                "\n------------------------------------------------------------" +
                "\n- DOCS TABLE STAT                                         -" +
                "\n------------------------------------------------------------" +
                "\nmode:" + (updatingMode.name()) +
                "\n\nREFERENCE STRUCTURE:" +
                "\n" + documentName.getStatistic() +
                "\n\nSIZE STRUCTURE:" +
                documentSize.getStatistic() +
                "\n\nDATE STRUCTURE:" +
                documentDate.getStatistic() +
                "\n\nINVALID STATUS STRUCTURE:" +
                documentInvalid.getStatistic() +
                "\n\nPROPERTIES STRUCTURE:" +
                documentProperties.getStatistic());
    }
    /** opération sur documentName verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentNameRW = new ReentrantReadWriteLock();
    private final Lock documentNameR = documentNameRW.readLock();
    private final Lock documentNameW = documentNameRW.writeLock();

    /**  ajoute un document au gestionnaire retourne le numéro du docuemnt*/
    public final int put(String d) {
        documentNameW.lock();
        try {
            //msg("add this:"+d);
            int id = documentName.put(d);
            if (updatingMode == IdxMode.DIFFERENTIAL) { // marque ce document car il existe dans le corpus à indexer
                documentIndexed.set(id, true);
            }
            if (keepLanguage == LanguageMode.YES) {
                keepLanguageOfDoc(d, id, documentProperties);
            }
            if (keepCollection == CollectionMode.YES) {
                keepCollectionOfDoc(d, id, documentProperties);
            }
            return id;
        } finally {
            documentNameW.unlock();
        }
    }

    /**  cherche le numéro du document, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String d) {
        documentNameR.lock();
        try {
            return documentName.get(d);
        } finally {
            documentNameR.unlock();
        }
    }

    /**  cherche le document associé à un numéro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        documentNameR.lock();
        try {
            return documentName.get(i);
        } finally {
            documentNameR.unlock();
        }
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        documentNameR.lock();
        try {
            return documentName.getCount();
        } finally {
            documentNameR.unlock();
        }
    }
    /** opération sur documentDate verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentDateRW = new ReentrantReadWriteLock();
    private final Lock documentDateR = documentDateRW.readLock();
    private final Lock documentDateW = documentDateRW.writeLock();

    /**  enregistre la date pour le document i */
    public final void setDate(int i, long date) {
        documentDateW.lock();
        try {
            documentDate.set(i, date);
        } finally {
            documentDateW.unlock();
        }
    }

    /**  demande  la date pour le document i */
    public final long getDate(int i) {
        documentDateR.lock();
        try {
            return documentDate.get(i);
        } finally {
            documentDateR.unlock();
        }
    }
    /** opération sur documentSize verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentSizeRW = new ReentrantReadWriteLock();
    private final Lock documentSizeR = documentSizeRW.readLock();
    private final Lock documentSizeW = documentSizeRW.writeLock();

    /**  enregistre la taille pour le document i */
    public final void setSize(int i, int size) {
        documentSizeW.lock();
        try {
            documentSize.set(i, size);
        } finally {
            documentSizeW.unlock();
        }
    }

    /**  demande  la taille pour le document i */
    public final int getSize(int i) {
        documentSizeR.lock();
        try {
            return documentSize.get(i);
        } finally {
            documentSizeR.unlock();
        }
    }
    /** opération sur documentProperties verrous ------------------------------------------*/
    private final ReentrantReadWriteLock documentPropertiesRW = new ReentrantReadWriteLock();
    private final Lock documentPropertiesR = documentPropertiesRW.readLock();
    private final Lock documentPropertiesW = documentPropertiesRW.writeLock();

    /**
     * enregistre la propiété pour le document i
     * @param i numéro du document
     * @param properties propirété
     */
    public void setPropertie(int i, String properties) {
        documentPropertiesW.lock();
        try {
            documentProperties.put(properties, i);
        } finally {
            documentPropertiesW.unlock();
        }
    }

    /**
     * élimine la propiété pour le document i
     * @param i numéro du document
     * @param properties propirété
     */
    public void clearPropertie(int i, String properties) {
        documentPropertiesW.lock();
        try {
            documentProperties.clear(properties, i);
        } finally {
            documentPropertiesW.unlock();
        }
    }

    /**
     * Retourne la taille pour le document i
     * @param i numéro du document
     * @param properties propriété
     * @return la valeur de cette propiété
     */
    public boolean getPropertie(int i, String properties) {
        documentPropertiesR.lock();
        try {
            return documentProperties.get(properties, i);
        } finally {
            documentPropertiesR.unlock();
        }
    }

    /**
     * propage l'invalidité des documents dans les propriétés
     */
    public void propagateInvalididy() {
        PutInvalidDocLikeAPropertie();  // pas de protection spéciale pour la concurrence
    }

    /** le status d'invalidité est normalisé comme une propriété */
    private final void PutInvalidDocLikeAPropertie() {
        //msg("===================== PutInvalidDocLikeAPropertie");
        if (documentProperties != null) {
            documentProperties.put(INVALID_NAME, documentInvalid.get());
        }
    }

    /**  cherche toutes les documents possédant une propriétés*/
    public final SetOfBits satisfyThisProperty(String properties) {
        //msg("===================== satisfyThisProperty");
        if (documentProperties != null) {
            //msg("===================== get:"+properties);
            return documentProperties.get(properties);
        }
        return null;
    }

    /**
     *  récupère le dictionnaire de propriétés
     * @return liste des propriétés actives
     */
    public List<String> getDictionnary() {
        return documentProperties.getDictionnary();
    }

    /**
     *  récupère le dictionnaire de propriétés ayant un certain préfix (COLECT., LANG.)
     * @param prefix préfixe des propriétés
     * @return liste des propriétés actives
     */
    public List<String> getDictionnary(String prefix) {
        return documentProperties.getDictionnary(prefix);
    }

    /** opération pour indexer ------------------------------------------*/
    /**  verifie si le document est à indexer (nom différent, date différente,
     * si il répond vrai, alors il faut indexer le document (car il a peut-être invalidé un document déja existant)
    , pas protégé le processus appelant doit rendre exclusif les appels
     */
    public final boolean IndexThisDocument(String fname, long date) {
        int id = get(fname);  // cherche si le document est déjà indexé
        if (id == StringRepository.EMPTY) {
            return true;
        } // un nouveau document
        long fdate = getDate(id);
        if (fdate != date) { // une mise à jour
            msg("IndexThisDocument maj:" + fname + " fdate " + fdate + " newdate " + date);
            invalid(id);
            return true;
        }
        if (updatingMode == IdxMode.DIFFERENTIAL) { // marque ce document car il existe dans le corpus à indexer
            documentIndexed.set(id, true);
        }

        return false; // cas le document est identique,
    }

    /**  rend invalide le document i 
    , pas protégé le processus appelant doit rendre exclusif les appels
     */
    public final void invalid(int i) {
        documentDate.set(i, INVALID_DATE); // marque la date avec une valeur impossible
        String fname = get(i); // récupère l'ancien nom'
        documentName.modify(i, INVALID_NAME + fname);  // ajoute une préfixe impossible
        documentInvalid.set(i, true);
    }

    /**  test si le document i  est valide 
    , pas protégé le processus appelant doit rendre exclusif les appels
     */
    public final boolean isValid(int i) {
        return !documentInvalid.get(i);
    }

    /**  le nombre de document valides
    , pas protégé le processus appelant doit rendre exclusif les appels
     */
    public final int countValid() {
        int size = documentName.getCount();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (isValid(i)) {
                count++;
            }
        }
        return count;
    }

    /** rend invalide tous les documents qui ne sont pas indexé 
    , pas protégé le processus appelant doit rendre exclusif les appels
     */
    private final void DeleteAllNonIndexedDoc() {
        int size = documentName.getCount();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (!documentIndexed.get(i) && isValid(i)) {
                invalid(i);
                count++;
            }
        }
        DETLOG.info("Nbr of Non-Indexed documents:" + count);
    }
}


