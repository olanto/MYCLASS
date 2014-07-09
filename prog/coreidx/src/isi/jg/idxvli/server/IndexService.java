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
 * être augmentés en publiant des méthodes de IdxStructure)
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
public interface IndexService extends Remote {

     /**
     * Pour demander des informations sur le serveur (pour vérifier la connexion)
     * @return nom du serveur ...
     * @throws java.rmi.RemoteException
     */
    public String getInformation() throws RemoteException;

 /**
 * Pour demander la construction de nouvelles structures pour l'indexeur 
 * (dans le cas d'une réinitialisation complète)
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
     * pour forcer l'écriture des caches
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
     * @return résultat des stat
     * @throws java.rmi.RemoteException
     */
     public String getStatistic() throws RemoteException;

    /**
     * pour stopper le serveur
     * @throws java.rmi.RemoteException
     */
     public void quit() throws RemoteException;

    /**
     * pour indexer un répertoire 
     * @param path chemin
     * @throws java.rmi.RemoteException
     */
     public void indexdir(String path) throws RemoteException;

    /**
     * pour indexer un contenu
     * @param path nom du document
     * @param content contenu à indexer
     * @throws java.rmi.RemoteException
     */
     public void indexThisContent(String path, String content) throws RemoteException;

    /**
     * pour indexer un contenu
     * @param path nom du document
     * @param content contenu à indexer
     * @param lang langue du document
     * @throws java.rmi.RemoteException
     */
     public void indexThisContent(String path, String content, String lang) throws RemoteException;

     /**
     * pour indexer un contenu
     * @param path nom du document
     * @param content contenu à indexer
     * @param lang langue du document
    * @param collection étiquettes des collections
     * @throws java.rmi.RemoteException
     */
     public void indexThisContent(String path, String content, String lang, String[] collection) throws RemoteException;

    /**
     * évaluation d'une requête
     * @param request requête
     * @return résultat
     * @throws java.rmi.RemoteException
     */
     public QLResult evalQL(String request) throws RemoteException;
    
      /**
     * évaluation d'une requête
     * @param request requête
     * @return résultat étendu avec le ranking
     * @throws java.rmi.RemoteException
     */
     public QLResultAndRank evalQLMore(String request) throws RemoteException;

     /**
     * évaluation d'une requête
     * @param request requête
     * @param start depuis
     * @param size taille du résultat 
     * @return résultat étendu avec snippet
     * @throws java.rmi.RemoteException
     */
     public QLResultNice evalQLNice(String request, int start, int size) throws RemoteException;

     /**
     * évaluation d'une requête
     * @param request requête
     * @param properties propriétés à vérifier
     * @param start depuis
     * @param size jusqua 
     * @return résultat étendu avec snippet
     * @throws java.rmi.RemoteException
     */
     public QLResultNice evalQLNice(String request, String properties, int start, int size) throws RemoteException;

    /**
     * évaluation d'une requête
     * @param request requête
     * @param properties propriétés à vérifier
     * @param profile profil à vérifier
     * @param start depuis
     * @param size jusqua 
     * @return résultat étendu avec snippet
     * @throws java.rmi.RemoteException
     */
     public QLResultNice evalQLNice(String request, String properties, String profile, int start, int size) throws RemoteException;

    /**
     * évaluation d'une requête
     * @param request requête
     * @param lang propriétés à vérifier
     * @return résultat
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
     * établir la validité d'un document
     * @param doc
     * @return validité
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
     * @return réponse
     */
    public KNNResult KNNForDoc(int doc, int N) throws RemoteException;

    /** Cherche l'encodage d'un document
     * @return encodage
     */
    public String getDOC_ENCODING() throws RemoteException;

    /**
     * export le vocabulaire de l'index
     * @param min borne inférieure
     * @param max borne supérieure
     * @throws java.rmi.RemoteException
     */public void exportEntry(long min, long max) throws RemoteException;

    /**
     *  récupère le dictionnaire de propriétés
     * @return liste des propriétés actives
     */
    public PropertiesList getDictionnary() throws RemoteException;

    /**
     *  récupère le dictionnaire de propriétés ayant un certain préfix (COLECT., LANG.)
     * @param prefix préfixe des propriétés
     * @return liste des propriétés actives
     */
    public PropertiesList getDictionnary(String prefix) throws RemoteException;

    /**
     * détermine la langue (actuellement Markov)
     * @param txt texte
     * @return langue
     * @throws java.rmi.RemoteException
     */
    public String getLanguage(String txt) throws RemoteException;

    /**
     * détermine les propriétés d'un document
     * @param txt texte
     * @return propriétés
     * @throws java.rmi.RemoteException
     */
    public String[] getCollection(String txt) throws RemoteException;
}

