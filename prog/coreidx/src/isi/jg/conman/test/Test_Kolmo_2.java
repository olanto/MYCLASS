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
public class Test_Kolmo_2 {

    private static ContentStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new ContentStructure("QUERY", new ConfigurationContentManager());
        boolean bzip2 = false;


        msg("doc 0 = " + id.getFileNameForDocument(0));
        msg("doc 1 = " + id.getFileNameForDocument(1));

        msg("dist 0 0 =" + id.distOfKolmogorov(0, 0, bzip2));
        msg("dist 0 1 =" + id.distOfKolmogorov(0, 1, bzip2));
        msg("dist 1 0 =" + id.distOfKolmogorov(1, 0, bzip2));
        msg("dist 1 1 =" + id.distOfKolmogorov(1, 1, bzip2));

        t1 = new Timer("global time");
        // init diagonale
        msg("---- init diagonal ----");
        double[] kd = new double[id.lastdoc];
        for (int k = 0; k < id.lastdoc; k++) {
            String sk = id.getStringContent(k, 0, 10000);
            kd[k] = id.kdlength(sk, bzip2);
        }

        for (int k = 0; k < id.lastdoc; k++) {
            String sk = id.getStringContent(k, 0, 10000);
            for (int i = k; i < id.lastdoc; i++) {
                String si = id.getStringContent(i, 0, 10000);
                double dkolmo = id.distOfKolmogorov(kd[k], kd[i], sk, si, bzip2);
            //msg("dist\t"+k+"\t"+i+"\t"+dkolmo+"\t"+id.getFileNameForDocument(i));
            }
            msg(" finish:" + k);
        }
        t1.stop();

        id.Statistic.global();






    }
}
