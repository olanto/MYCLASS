package isi.jg.idxvli.propertie;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;
import java.util.regex.*;

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
 *
 * pour stoker un système de règle pour les collections
 */
public class CollectionRules {

    ArrayList<String[]> collections;
    String collectionfilename;
    private static Pattern p;
    private static String SEPARATOR = "\\s";

    /** Creates a new instance of Rule */
    public CollectionRules(String fname) {
        collectionfilename = fname;
        collections = new ArrayList<String[]>();
        p = Pattern.compile(SEPARATOR);
        msg("init collection rules from file : " + fname);
        loadRules();
    }

    private void loadRules() {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(collectionfilename), "ISO-8859-1");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            int count = 0;
            while (w != null) {
                count++;
                msg("line " + count + " : " + w);
                if (!w.startsWith("//")) {// pas un commentaire
                    String[] items = p.split(w);
                    if (items.length >= 2) {
                        collections.add(items);
                    } else {
                        msg("ERROR in line " + count + " : " + w);
                    }
                }
                w = in.readLine();
            }
        } catch (Exception e) {
            error("IO error in loadRules", e);
        }
    }

    public String[] eval(String s) {
        //msg(s);
        for (int i = 0; i < collections.size(); i++) {
            String[] items = collections.get(i);
            if (s.contains(items[0])) {
                //msg("rule :"+items[0]);
                String[] res = new String[items.length - 1];
                for (int j = 0; j < res.length; j++) {
                    res[j] = items[j + 1];
                }
                return res;
            }
        }
        return null;
    }

    public static void main(String[] args) { // pour les tests
        CollectionRules ar = new CollectionRules("C:/JG/VLI_RW/data/urlcollection.txt");
        showVector(ar.eval("http://cui.unige.ch/index.html"));
        showVector(ar.eval("http://cui.unige.ch/isi/index.html/#local"));
        showVector(ar.eval("http://matis.unige.ch/icon.gif"));
        showVector(ar.eval("http://localhost/"));

    }
}




