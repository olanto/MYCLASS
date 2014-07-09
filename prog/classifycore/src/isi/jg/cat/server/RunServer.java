package isi.jg.cat.server;

import java.rmi.*;
import static isi.jg.util.Messages.*;

/**
 *
 * <p>
 * <b>JG-2010
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2010
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2010</b>
 *<p>
 *
 * -Xmx1000m -Djava.rmi.server.codebase="file:///c:/JG/prog/classifycore/dist/classifycore.jar"  -Djava.security.policy="c:/ JG/prog/classifycore /rmi.policy"
 */
public class RunServer {

    public static void main(String[] args) {


        try {
            System.out.println("initialisation du classifier ...");

            ClassifyService_BASIC classifyobj = new ClassifyService_BASIC();
            classifyobj.init();
            System.out.println("Enregistrement du serveur");
            String name = "rmi://localhost/C1";
            System.out.println("name:" + name);
            Naming.rebind(name, classifyobj);
            System.out.println("Server classifier manager started");
        } catch (Exception e) {
            error("Server classifier manager", e);
        }


    }
}
