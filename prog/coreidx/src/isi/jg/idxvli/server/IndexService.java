package isi.jg.idxvli.server;

import java.rmi.*;
import java.util.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.knn.*;
import isi.jg.conman.server.*;
import isi.jg.idxvli.doc.*;

/* -server -Xmx1400m -Djava.rmi.server.codebase="file:///c:/JG/VLI_RW/dist/VLI_RW.jar"  -Djava.security.policy="c:/JG/VLI_RW/rmi.policy" */

/* default permissions granted to all domains
grant codeBase "file://c:/JG/VLI_RW/*" {
permission java.security.AllPermission;
};
grant { 
// JG modifier pour le rmi
permission java.net.SocketPermission "*:1099-", "accept, connect, listen, resolve";
permission java.security.AllPermission;
};
 */
/**
 *  Facade pour les services des index manager en modeRMI (ces services peuvent
 * �tre augment�s en publiant des m�thodes de IdxStructure)
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public interface IndexService extends Remote {

     /**
     * Pour demander des informations sur le serveur (pour v�rifier la connexion)
     * @return nom du serveur ...
     * @throws java.rmi.RemoteException
     */
    public String getInformation() throws RemoteException;

 /**
 * Pour demander la construction de nouvelles structures pour l'indexeur 
 * (dans le cas d'une r�initialisation compl�te)
 * @param client configuration de la structure
 * @throws java.rmi.RemoteException
 */
    public void startANewOne(IdxInit client) throws RemoteException;

     /**
     * Pour demander l'ouverture d'un indexeur existant
     * @param client configuration de la structure
     * @param mode (QUERY,INCREMENTAL,DIFFERENTIAL)
     * @throws java.rmi.RemoteException
     */
    public void getAndInit(IdxInit client, String mode, boolean OpenCM) throws RemoteException;

    /**
     * pour forcer l'�criture des caches
     * @throws java.rmi.RemoteException
     */
    public void flush() throws RemoteException;

     /**
     * pour rendre visible les nouvelles indexations
     * @throws java.rmi.RemoteException
     */
    public void showFullIndex() throws RemoteException;

     /**
     * Pour obtenir les statisques (nb doc, %compression, ...) de l'indexeur
     * @return r�sultat des stat
     * @throws java.rmi.RemoteException
     */
     public String getStatistic() throws RemoteException;

    /**
     * pour stopper le serveur
     * @throws java.rmi.RemoteException
     */
     public void quit() throws RemoteException;

    /**
     * pour indexer un r�pertoire 
     * @param path chemin
     * @throws java.rmi.RemoteException
     */
     public void indexdir(String path) throws RemoteException;

    /**
     * pour indexer un contenu
     * @param path nom du document
     * @param content contenu � indexer
     * @throws java.rmi.RemoteException
     */
     public void indexThisContent(String path, String content) throws RemoteException;

    /**
     * pour indexer un contenu
     * @param path nom du document
     * @param content contenu � indexer
     * @param lang langue du document
     * @throws java.rmi.RemoteException
     */
     public void indexThisContent(String path, String content, String lang) throws RemoteException;

     /**
     * pour indexer un contenu
     * @param path nom du document
     * @param content contenu � indexer
     * @param lang langue du document
    * @param collection �tiquettes des collections
     * @throws java.rmi.RemoteException
     */
     public void indexThisContent(String path, String content, String lang, String[] collection) throws RemoteException;

    /**
     * �valuation d'une requ�te
     * @param request requ�te
     * @return r�sultat
     * @throws java.rmi.RemoteException
     */
     public QLResult evalQL(String request) throws RemoteException;
    
      /**
     * �valuation d'une requ�te
     * @param request requ�te
     * @return r�sultat �tendu avec le ranking
     * @throws java.rmi.RemoteException
     */
     public QLResultAndRank evalQLMore(String request) throws RemoteException;

     /**
     * �valuation d'une requ�te
     * @param request requ�te
     * @param start depuis
     * @param size taille du r�sultat 
     * @return r�sultat �tendu avec snippet
     * @throws java.rmi.RemoteException
     */
     public QLResultNice evalQLNice(String request, int start, int size) throws RemoteException;

     /**
     * �valuation d'une requ�te
     * @param request requ�te
     * @param properties propri�t�s � v�rifier
     * @param start depuis
     * @param size jusqua 
     * @return r�sultat �tendu avec snippet
     * @throws java.rmi.RemoteException
     */
     public QLResultNice evalQLNice(String request, String properties, int start, int size) throws RemoteException;

    /**
     * �valuation d'une requ�te
     * @param request requ�te
     * @param properties propri�t�s � v�rifier
     * @param profile profil � v�rifier
     * @param start depuis
     * @param size jusqua 
     * @return r�sultat �tendu avec snippet
     * @throws java.rmi.RemoteException
     */
     public QLResultNice evalQLNice(String request, String properties, String profile, int start, int size) throws RemoteException;

    /**
     * �valuation d'une requ�te
     * @param request requ�te
     * @param lang propri�t�s � v�rifier
     * @return r�sultat
     * @throws java.rmi.RemoteException
     */
     public QLResult evalQL(String lang, String request) throws RemoteException;

    /**
     * pour chercher les noms des documents
     * @param docList liste d'identifiant
     * @return noms
     * @throws java.rmi.RemoteException
     */
     public String[] getDocName(int[] docList) throws RemoteException;

    /**
     * �tablir la validit� d'un document
     * @param doc
     * @return validit�
     * @throws java.rmi.RemoteException
     */
     public boolean docIsValid(int doc) throws RemoteException;

    /**
     * cherche le contenu d'un document
     * @param docId
     * @return contenu
     * @throws java.rmi.RemoteException
     */
     public String getDoc(int docId) throws RemoteException;

    /**
     * cherche l'id d'un document
     * @param docName nom
     * @return id
     * @throws java.rmi.RemoteException
     */public int getDocId(String docName) throws RemoteException;

    /** Chercher les N premiers voisins du document d, sans formattage.
     * @param doc document
     * @param N nombre de voisins
     * @return r�ponse
     */
    public KNNResult KNNForDoc(int doc, int N) throws RemoteException;

    /** Cherche l'encodage d'un document
     * @return encodage
     */
    public String getDOC_ENCODING() throws RemoteException;

    /**
     * export le vocabulaire de l'index
     * @param min borne inf�rieure
     * @param max borne sup�rieure
     * @throws java.rmi.RemoteException
     */public void exportEntry(long min, long max) throws RemoteException;

    /**
     *  r�cup�re le dictionnaire de propri�t�s
     * @return liste des propri�t�s actives
     */
    public PropertiesList getDictionnary() throws RemoteException;

    /**
     *  r�cup�re le dictionnaire de propri�t�s ayant un certain pr�fix (COLECT., LANG.)
     * @param prefix pr�fixe des propri�t�s
     * @return liste des propri�t�s actives
     */
    public PropertiesList getDictionnary(String prefix) throws RemoteException;

    /**
     * d�termine la langue (actuellement Markov)
     * @param txt texte
     * @return langue
     * @throws java.rmi.RemoteException
     */
    public String getLanguage(String txt) throws RemoteException;

    /**
     * d�termine les propri�t�s d'un document
     * @param txt texte
     * @return propri�t�s
     * @throws java.rmi.RemoteException
     */
    public String[] getCollection(String txt) throws RemoteException;
}

