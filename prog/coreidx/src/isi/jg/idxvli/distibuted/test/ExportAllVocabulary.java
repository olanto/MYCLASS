package isi.jg.idxvli.distibuted.test;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
 *
 * Test de l'indexeur, mode query
 */
public class ExportAllVocabulary {

    // classe chargée de stresser les query
    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY");
        // création de la racine de l'indexation
        id.createComponent(new ConfigurationNative1());

        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();

        id.Statistic.global();

        String filename = "C:/bigvocabulary.txt";
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename));
            int last = id.lastUpdatedWord;

            for (int i = 0; i < last; i++) {
                out.write(id.getStringforW(i) + "\n");
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            error("io",ex);
        }
        t1.stop();
    }
}
    