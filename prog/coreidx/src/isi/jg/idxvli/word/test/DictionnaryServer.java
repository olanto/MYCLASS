/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.idxvli.word.test;

import java.rmi.*;
import static isi.jg.util.Messages.*;
import isi.jg.idxvli.word.*;

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
public class DictionnaryServer {

    public static void main(String[] args) {


        try {
            System.out.println("initialisation de l'indexeur ...");

            DictionnaryService_BASIC idxobj = new DictionnaryService_BASIC();

            System.out.println("Enregistrement du serveur");

            String name = "rmi://jg4/TERMDICT";
            System.out.println("name:" + name);
            Naming.rebind(name, idxobj);
            System.out.println("Serveur lancé");

        } catch (Exception e) {
            error("Serveur Idx", e);
        }


    }
}
