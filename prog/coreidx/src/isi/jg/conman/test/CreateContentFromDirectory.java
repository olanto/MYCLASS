package isi.jg.conman.test;

import isi.jg.conman.*;
import isi.jg.util.Timer;
import java.io.*;
import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;

/**
 * *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Test de l'indexeur, création d'un nouvel index
 */
public class CreateContentFromDirectory {

    private static ContentStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new ContentStructure("NEW", new ConfigurationContentManager());

        //id.getFromDirectory("c:/jdk1.5/docs/guide");
        id.getFromDirectory("C:/Documents and Settings/jacques.guyot/Bureau/Quote");


//       for (int i=0;i<3;i++){
//            msg(id.getStringContent(i));
//        }



        id.Statistic.global();
        id.close();
        t1.stop();

    }
}
