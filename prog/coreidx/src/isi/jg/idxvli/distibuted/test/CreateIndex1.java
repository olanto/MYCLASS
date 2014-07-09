package isi.jg.idxvli.distibuted.test;

import isi.jg.idxvli.server.test.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.idxvli.server.*;

/** Test de l'indexeur, création d'un nouvel index
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
public class CreateIndex1 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new IdxStructure("NEW", new ConfigurationNative1());


        //id.indexdir("C:/D");// indexation du dossier spécifié
        id.indexdir("D:/BIG");// indexation du dossier spécifié
        //id.indexdir("C:/jdk1.5/docs");// indexation du dossier spécifié
        //id.indexdir("E:/WIPO/EN");// indexation du dossier spécifié
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global();
        id.close();
        t1.stop();

    }
}
