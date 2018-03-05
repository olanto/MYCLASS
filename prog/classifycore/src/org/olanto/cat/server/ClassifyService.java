/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myLCASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.cat.server;

import java.rmi.*;

/**
 *  Facade pour les services de classification en modeRMI (ces services peuvent
 * ?tre augmentés en publiant des méthodes MNN)
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

