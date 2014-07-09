/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.idxvli.server.test;

import java.rmi.*;
import isi.jg.idxvli.server.*;
import static isi.jg.util.Messages.*;

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
 */
public class TestClientStop {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://localhost/VLI");
            // Remote r = Naming.lookup("rmi://SVRFI31/10.212.2.131/TestRMI");

            System.out.println("access to serveur");

            if (r instanceof IndexService) {
                IndexService is = ((IndexService) r);
                String s = is.getInformation();
                System.out.println("chaîne renvoyée = " + s);
                System.out.println("client send a stop signal ... could take time");
                is.quit();



                msg("end ...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
