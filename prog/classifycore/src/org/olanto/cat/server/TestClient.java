package org.olanto.cat.server;

import java.rmi.*;

/**
 *
 * <p>
 * <b>JG-2010
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2010
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nï¿½cessite une autorisation explicite de son auteur
 * JG-2010</b>
 *<p>
 */
public class TestClient {

    public static void main(String[] args) {


        try {

            System.out.println("connect to server");

            Remote r = Naming.lookup("rmi://localhost/C1");

            System.out.println("access to server");

            if (r instanceof ClassifyService) {
                ClassifyService is = ((ClassifyService) r);
                String s = is.getInformation();
                System.out.println("info = " + s);
    
                // test de classification

                is.advise("information database query (more text ...) .", 3).show();

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
