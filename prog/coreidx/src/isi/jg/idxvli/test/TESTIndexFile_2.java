package isi.jg.idxvli.test;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import java.io.*;
import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;

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
 *
 * Test de l'indexeur, mode incrémental et différentiel
 */
public class TESTIndexFile_2 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {
        maj1();

    }

    private static void maj1() {
        id = new IdxStructure(
                //        "INCREMENTAL", // mode
                "DIFFERENTIAL" // mode
                );

        // création de la racine de l'indexation
        id.createComponent(new Configuration());
        // charge l'index
        id.loadIndexDoc();
        // indexation du dossier spécifié
        //id.indexdir("C:/jdk1.5_old/docs/relnotes");
        id.indexdir("C:/OMC_corpus/BISD/S01");
        id.flushIndexDoc();
        id.Statistic.global();
        id.close();
        t1.stop();

        System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
    }
}
