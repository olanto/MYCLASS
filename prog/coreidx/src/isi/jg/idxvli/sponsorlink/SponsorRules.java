package isi.jg.idxvli.sponsorlink;

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
 * pour stoker un système de règle pour les liens sponsorisé
 */
public class SponsorRules {

    Hashtable<String, String[]> sponsors;
    String sponsorfilename;
    private static Pattern p;
    private static String SEPARATOR = ";";

    /** Creates a new instance of Rule */
    public SponsorRules(String fname) {
        sponsorfilename = fname;
        sponsors = new Hashtable<String, String[]>();
        p = Pattern.compile(SEPARATOR);
        msg("init sponsor rules from file : " + fname);
        loadRules();
    }

    private void loadRules() {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(sponsorfilename), "ISO-8859-1");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            int count = 0;
            while (w != null) {
                count++;
                msg("line " + count + " : " + w);
                if (!w.startsWith("//")) {// pas un commentaire
                    String[] items = p.split(w);
                    if (items.length >= 2) {
                        String[] links = new String[items.length - 1];
                        for (int j = 0; j < links.length; j++) {
                            links[j] = items[j + 1];
                        }
                        sponsors.put(items[0], links);  // on doit ajouter du code pour vérifier les doublons !!!!!!
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
        String[] links = sponsors.get(s);
        if (links != null) {
            return links;
        }
        return null;
    }

    public static void main(String[] args) { // pour les tests
        SponsorRules ar = new SponsorRules("C:/JG/VLI_RW/data/urlsponsor.txt");
        showVector(ar.eval("java"));
        showVector(ar.eval("informatique"));
        showVector(ar.eval("médecine"));
        showVector(ar.eval("kjlkjlk"));

    }
}




