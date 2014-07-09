/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.idxvli.distibuted.test;

import isi.jg.idxvli.server.test.*;
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
public class SendStopToAll {

    public static void main(String[] args) {

        int nbidx = 4;
         SendClose[] sc=new SendClose[nbidx+1];

         for (int i = 1; i <= nbidx; i++) {
             sc[i]=new SendClose(i);
             sc[i].start();
         }
         }
    }
class SendClose extends Thread {

    int name;

    SendClose(int name) {
        this.name = name;
    }

    public void run() {
        try {

            System.out.println("connect to serveur "+name);

            Remote r = Naming.lookup("rmi://localhost/VLI"+name);
            // Remote r = Naming.lookup("rmi://SVRFI31/10.212.2.131/TestRMI");

            System.out.println("access to serveur "+name);

            if (r instanceof IndexService) {
                IndexService is = ((IndexService) r);
                String s = is.getInformation();
                System.out.println("info from "+name+" = " + s);
                System.out.println("client send a stop signal to  "+name+" ... could take time");
                is.quit();
              msg("end ... "+name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
}