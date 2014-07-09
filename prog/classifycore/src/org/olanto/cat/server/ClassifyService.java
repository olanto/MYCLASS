package org.olanto.cat.server;

import java.rmi.*;

/**
 *  Facade pour les services de classification en modeRMI (ces services peuvent
 * ?tre augment�s en publiant des m�thodes MNN)
 * <p>
 * <b>JG-2010
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2010 & Simple-Shift
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2010</b>
 *<p>
 */
public interface ClassifyService extends Remote {

    /**
     * Pour demander des informations sur le serveur (pour v�rifier la connexion)
     * @return nom du serveur ...
     * @throws java.rmi.RemoteException
     */
     public String getInformation() throws RemoteException;

     /**
     * Pour demander l'initialisation du serveur
     */
    public void init() throws RemoteException;

    /**
     * Pour obtenir n premi?res cat�gories
     * @param p le texte ? classifier
     * @param nbAdvise nombre de pr�dictions
     * @return les cat�gories pr�dites
     * @throws java.rmi.RemoteException
     */
  public  ClassifyResult advise(String p, int nbAdvise) throws RemoteException ;

}

