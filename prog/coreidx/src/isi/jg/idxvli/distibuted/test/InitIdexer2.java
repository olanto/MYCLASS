/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.idxvli.distibuted.test;

import static isi.jg.util.Messages.*;
import isi.jg.idxvli.server.*;

/**
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 * –Djava.security.policy=policy.all 
 */
public class InitIdexer2 {

    public static void main(String[] args) {

         try {
            System.out.println("initialisation de l'indexeur 2...");

            IndexService_BASIC idxobj = new IndexService_BASIC();
            idxobj.startANewOne(new ConfigurationNative2());
        } catch (Exception e) {
            error("Serveur Idx 2", e);
        }


    }

 
}
