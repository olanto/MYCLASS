 /*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.server;

import java.rmi.*;
import isi.jg.conman.*;
import isi.jg.conman.server.*;
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
 */
public class GetContentService {

    public static ContentService getServiceCM(String serviceName) {

        ContentService is;

        try {
            System.out.println("try to connect server " + serviceName);
            Remote r = Naming.lookup(serviceName);
            System.out.println("access to serveur");
            if (r instanceof ContentService) {
                is = ((ContentService) r);
                String s = is.getInformation();
                System.out.println("Test ... return = " + s);
                msg(serviceName + " service open ...");
                return is;
            } else {
                msg(serviceName + " exist but not a right type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ContentService runServiceCM(ContentInit client, String serviceName) {

        ContentService contentobj;
        try {
            System.out.println("initialisation de ContentService_BASIC ...");

            contentobj = new ContentService_BASIC();
            contentobj.getAndInit(client, "INCREMENTAL");

            System.out.println("Enregistrement du serveur");

            //      String name="rmi://"+java.net.InetAddress.getLocalHost()+"/IDX";
            String name = "rmi://localhost/CM";
            System.out.println("name:" + name);
            Naming.rebind(name, contentobj);
            System.out.println("Server content manager started");
            return contentobj;
        } catch (Exception e) {
            error("Server content manager", e);
        }
        return null;

    }

    public static IndexService getServiceVLI(String serviceName) {

        IndexService is;

        try {

            System.out.println("try to connect server " + serviceName);
            Remote r = Naming.lookup(serviceName);
            System.out.println("access to serveur");
            if (r instanceof IndexService) {
                is = ((IndexService) r);
                String s = is.getInformation();
                System.out.println("Test ... return = " + s);
                msg(serviceName + " service open ...");
                return is;
            } else {
                msg(serviceName + " exist but not a right type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
