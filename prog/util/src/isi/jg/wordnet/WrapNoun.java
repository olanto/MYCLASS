package isi.jg.wordnet;

import java.io.*;
import static isi.jg.util.Messages.*;

/**
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 *
 * Test de l'indexeur, mode query
 */
public class WrapNoun {

    private static OutputStreamWriter out;

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {


        String filename = "C:/AAA/WN/NOUN.DAT";
        try {
            out = new OutputStreamWriter(new FileOutputStream(filename + ".dic"));
            readNativeWN(filename);
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void write(String id, String form, String hyper) {
        try {
            out.write(id + " " + form + " " + hyper + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void readNativeWN(String fname) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String w = in.readLine();
            msg("read:" + w);
            while (w != null) {
                String id = "WSD" + w.substring(0, 8) + "-" + w.substring(12, 13);
                int lastchar = w.indexOf(" ", 17);
                String form = "";
                if (lastchar == -1) {
                    msg("no forms term in:" + w);
                } else {
                    form = w.substring(17, lastchar);
                }
                String hyper = "NHT";
                int firsthyper = w.indexOf("@ ", lastchar);
                if (firsthyper == -1) {
                    msg("no hyper term in:" + w);
                } else {
                    hyper = "WSD" + w.substring(firsthyper + 2, firsthyper + 10) + "-" + w.substring(firsthyper + 11, firsthyper + 12);
                }


                write(id, form, hyper);
                //msg(id + " " + form + " " + hyper);
                w = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
