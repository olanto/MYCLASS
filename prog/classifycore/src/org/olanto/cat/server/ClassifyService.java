package org.olanto.cat.server;

import java.rmi.*;

/**
 *  Facade pour les services de classification en modeRMI (ces services peuvent
 * ?tre augmentés en publiant des méthodes MNN)
 * <p>
 * <b>JG-2010
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2010 & Simple-Shift
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2010</b>
 *<p>
 */
public interface ClassifyService extends Remote {

    /**
     * Pour demander des informations sur le serveur (pour vérifier la connexion)
     * @return nom du serveur ...
     * @throws java.rmi.RemoteException
     */
     public String getInformation() throws RemoteException;

     /**
     * Pour demander l'initialisation du serveur
     */
    public void init() throws RemoteException;

    /**
     * Pour obtenir n premi?res catégories
     * @param p le texte ? classifier
     * @param nbAdvise nombre de prédictions
     * @return les catégories prédites
     * @throws java.rmi.RemoteException
     */
  public  ClassifyResult advise(String p, int nbAdvise) throws RemoteException ;

}

