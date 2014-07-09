/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.idxvli.distibuted.test;

import isi.jg.idxvli.IdxInit;
import java.rmi.*;
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
public class RunServerWrite1 {

    public static void main(String[] args) {

        int i = 1;

        IdxInit conf = new ConfigurationNative1();
            try {

                System.out.println("initialisation de l'indexeur " + i + " ...");

                IndexService_BASIC idxobj = new IndexService_BASIC();
                idxobj.getAndInit(conf, "INCREMENTAL",false);

                System.out.println("Enregistrement du serveur " + i);

                //      String name="rmi://"+java.net.InetAddress.getLocalHost()+"/IDX";
                String name = "rmi://localhost/VLI" + i;
                System.out.println("name:" + name);
                Naming.rebind(name, idxobj);
                System.out.println("Serveur lancé " + i);


            } catch (Exception e) {
                error("Serveur Idx " + i, e);
            }
 
    }
}
