package isi.jg.conman.server;

import java.rmi.*;
import isi.jg.conman.*;
import isi.jg.idxvli.util.*;
import isi.jg.idxvli.doc.*;

/**
 *  Facade pour les services des contents manager en modeRMI (ces services peuvent
 * être augmentés en publiant des méthodes de ContentStructure)
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public interface ContentService extends Remote {

    /**
     * Pour demander des informations sur le serveur (pour vérifier la connexion)
     * @return nom du serveur ...
     * @throws java.rmi.RemoteException
     */
     public String getInformation() throws RemoteException;
/**
 * Pour demander la construction de nouvelles structures pour le conteneur 
 * (dans le cas d'une réinitialisation complète)
 * @param client configuration de la structure
 * @throws java.rmi.RemoteException
 */
    public void startANewOne(ContentInit client) throws RemoteException;

    /**
     * Pour demander l'ouverture d'un conteneur existant
     * @param client configuration de la structure
     * @param mode (QUERY,INCREMENTAL,DIFFERENTIAL)
     * @throws java.rmi.RemoteException
     */
    public void getAndInit(ContentInit client, String mode) throws RemoteException;

    /**
     * Pour obtenir les statisques (nb doc, %compression, ...) du conteneur
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
     * pour ajouter un nom de document sans son contene (voir crawler)
     * @param url identifiant du document
     * @throws java.rmi.RemoteException
     */
    public void addURL(String url) throws RemoteException;

    /**
     * pour ajouter un document dans le conteneur
     * @param docName identifiant du document
     * @param content contenu du document (textuel)
     * @param lang langue du document
     * @param collection attribut du document
     * @throws java.rmi.RemoteException
     */
    public void addContent(String docName, String content, String lang, String collection) throws RemoteException;

    /**
     *  pour ajouter/remplacer le contenu d'un document déjà connu
     * @param docID id interne du document
     * @param content contenu du document (textuel)
     * @param type (html,java,txt, ...)
     * @throws java.rmi.RemoteException
     */public void saveContent(int docID, String content, String type) throws RemoteException;

    /**
     * pour ajouter/remplacer le contenu d'un document déjà connu
     * @param docID id interne du document
     * @param content contenu du document (binary)
     * @param type (pdf,doc,ppt, ...)
     * @throws java.rmi.RemoteException
     */
     public void saveContent(int docID, byte[] content, String type) throws RemoteException;

     /**
     * pour ajouter un ensemble de documents depuis un répertoire
     * @param path chemin du répertoire
     * @throws java.rmi.RemoteException
     */
     public void getFromDirectory(String path) throws RemoteException;

        /**
         * pour récupérer le nom du document associé à un id
         * @param doc id du document
         * @return nom du document
         * @throws java.rmi.RemoteException
         */
     public String getDocName(int doc) throws RemoteException;

    /**
         * pour savoir si le document est valide (pas supprimé)
         * @param doc id du document
         * @return false=pas valide true=valide
         * @throws java.rmi.RemoteException
         */
     public boolean docIsValid(int doc) throws RemoteException;

    /**
     * pour récupérer le contenu du document associé à un id
     * @param docId id
     * @return contenu (textuel)
     * @throws java.rmi.RemoteException
     */
     public String getDoc(int docId) throws RemoteException;

    /**
     * pour récupérer le contenu du document associé à un nom de document
     * @param docName nom de document
     * @return contenu (textuel)
     * @throws java.rmi.RemoteException
     */
     public String getDoc(String docName) throws RemoteException;

    /**
     * pour récupérer le contenu du document associé à un id
     * @param docId id
     * @return contenu (binary)
     * @throws java.rmi.RemoteException
     */public byte[] getBin(int docId) throws RemoteException;

    /**
     * pour récupérer le contenu du document associé à un nom de document
     * @param docName nom de document
     * @return contenu (binary)
     * @throws java.rmi.RemoteException
     */
    public byte[] getBin(String docName) throws RemoteException;

    /**
     * pour extraire une section du contenu d'un document associé à un id
     * @param docId id
     * @param from début de la section
     * @param to fin de la section
     * @return extraction (textuelle)
     * @throws java.rmi.RemoteException
     */
    public String getPartOfDoc(int docId, int from, int to) throws RemoteException;

    /**
     * pour récupérer l'id document associé à un nom de document
     * @param docName nom de document
     * @return id
     * @throws java.rmi.RemoteException
     */
    public int getDocId(String docName) throws RemoteException;

     /**
     * pour récupérer le nombre de documents du conteneur
     * @return id
     * @throws java.rmi.RemoteException
     */
    public int getSize() throws RemoteException;

  
    /**
     * pour tester si un document est complet (valide avec un contenu et pas traité)
     * @param doc id
     * @return false=pas traitable true=traitable
     * @throws java.rmi.RemoteException
     */
    public boolean isIndexable(int doc) throws RemoteException;

     /**
     * pour indiquer qu'un document est traité
     * @param doc id
     * @throws java.rmi.RemoteException
     */
    public void setIndexed(int doc) throws RemoteException;

    /**
     * pour récupérer l'ensemble des documents satisfaisant 
     * une propriété (lang, collection, state, etc)
     * @param properties nom de la propiété
     * @return vector de bits
     * @throws java.rmi.RemoteException
     */
    public SetOfBits satisfyThisProperty(String properties) throws RemoteException;

    /**
     * pour associer une propriété à un document
     * @param docID id
     * @param properties nom de la propiété
     * @throws java.rmi.RemoteException
     */
    public void setDocumentPropertie(int docID, String properties) throws RemoteException;

     /**
     * pour enlever une propriété à un document
     * @param docID id
     * @param properties nom de la propiété
     * @throws java.rmi.RemoteException
     */
    public void clearDocumentPropertie(int docID, String properties) throws RemoteException;

    /**
     *  récupère le dictionnaire de propriétés actives du conteneur
     * @return liste des propriétés actives
     */
    public PropertiesList getDictionnary() throws RemoteException;

    /**
     *  pour récupèrer le dictionnaire de propriétés pour un groupe de propriété (COLECT., LANG.)
     * @param prefix préfixe du groupe des propriétés
     * @return liste des propriétés actives
     */
    public PropertiesList getDictionnary(String prefix) throws RemoteException;

    /**
     * pour ajouter un contenu structuré dans le conteneur (voir mode CLEAN)
     * @param docName nom du document
     * @param refName référence externe du document (url, path)
     * @param title titre du document
     * @param cleantxt texte "nettoyé" du document
     * @throws java.rmi.RemoteException
     */
    public void setRefDoc(String docName, String refName, String title, String cleantxt) throws RemoteException;

    /**
     * pour récupérer la référence externe d'un contenu structuré
     * @param docName id
     * @return la référence externe
     * @throws java.rmi.RemoteException
     */
    public String getRefName(String docName) throws RemoteException;

    /**
     * pour récupérer le titre d'un contenu structuré
     * @param docName id
     * @return le titre
     * @throws java.rmi.RemoteException
     */
    public String getTitle(String docName) throws RemoteException;

     /**
     * pour récupérer le titre d'un contenu structuré
     * @param docName id
     * @return le titre
     * @throws java.rmi.RemoteException
     */
    public String getCleanText(String docName) throws RemoteException;

     /**
     * pour récupérer le texte d'un contenu structuré
     * @param docName id
     * @return le texte
     * @throws java.rmi.RemoteException
     */
    public String getCleanText(String docName, int from, int to) throws RemoteException;
}

