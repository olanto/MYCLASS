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
public class Test_Kolmo {

    private static ContentStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new ContentStructure("QUERY", new ConfigurationContentManager());
        boolean bzip2 = false;

        for (int i = 0; i < id.lastdoc; i++) {
            msg("id=" + i + " - " + id.getFileNameForDocument(i));
        }


        msg("doc 0 = " + id.getFileNameForDocument(0));
        msg("doc 1 = " + id.getFileNameForDocument(1));

        msg("dist 0 0 =" + id.distOfKolmogorov(0, 0, bzip2));
        msg("dist 0 1 =" + id.distOfKolmogorov(0, 1, bzip2));
        msg("dist 1 0 =" + id.distOfKolmogorov(1, 0, bzip2));
        msg("dist 1 1 =" + id.distOfKolmogorov(1, 1, bzip2));

        t1 = new Timer("global time");
        int k = 500;
        for (int i = 0; i < id.lastdoc; i++) {
            msg("dist\t" + k + "\t" + i + "\t" + id.distOfKolmogorov(k, i, bzip2) + "\t" + id.getFileNameForDocument(i));
        }
        t1.stop();

        id.Statistic.global();






    }
}
