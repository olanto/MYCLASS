package isi.jg.wordnet;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import static isi.jg.util.Messages.*;

/**
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 *
 * Test de l'indexeur, mode query
 */
public class WordNetDict {

    private static Hashtable<String, String> id2gen = new Hashtable<String, String>();
    private static Hashtable<String, String> id2form = new Hashtable<String, String>();
    private static Pattern p = Pattern.compile("[\\s\\.+~*,\"\\(\\)]");  // les fins de mots

    /**
     * application de test
     * @param args sans
     */
    public WordNetDict() {
        readNativeWN("C:/AAA/WN/ADJ.DAT.dic");
        readNativeWN("C:/AAA/WN/NOUN.DAT.dic");
     }

    public static void main(String[] args) {  // only for test

        WordNetDict wn = new WordNetDict();
      String wsd="WSD10749669-n"; 
       msg(wsd+"-f->"+wn.getForm(wsd));
       msg(wsd+"-g->"+wn.getGen(wsd)+"-f->"+wn.getForm(wn.getGen(wsd)));
       wsd="WSD02726367-a"; 
       msg(wsd+"-f->"+wn.getForm(wsd));
       msg(wsd+"-g->"+wn.getGen(wsd)+"-f->"+wn.getForm(wn.getGen(wsd)));

    }

    public String getForm(String wsd){
        return id2form.get(wsd);
    }
    public String getGen(String wsd){
        return id2gen.get(wsd);
    }
    
    private static void readNativeWN(String fname) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String w = in.readLine();
            //msg("read:" + w);
            while (w != null) {
                String[] words = p.split(w);
                id2gen.put(words[0], words[2]);
                id2form.put(words[0], words[1]);
                w = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
