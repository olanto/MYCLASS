package isi.jg.idxvli;

import java.io.*;
import java.util.*;

/**
 * Une classe pour gérer des listes de termes.
 * Les listes de stop words sont gérées ainsi
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class SetOfWords {

    Hashtable<String, Integer> words = new Hashtable<String, Integer>();
    int count = 0;

    /**
     * création d'une liste chargée depuis un fichier.
     * Chaque ligne du fichier est un terme de la liste
     * 
     * @param fname nom du fichier
     */
    public SetOfWords(String fname) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                //System.out.println("dont index:"+w);
                words.put(w, count);
                w = in.readLine();
            }
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
    }

    /** vérifie si un terme appartient à la liste.
     * @param w terme à vérifier
     * @return vrai si dans la liste
     */
    public final boolean check(String w) {
        //System.out.println("test dont index:"+w+" . "+words.get(w));
        if (words.get(w) == null) {
            return false;
        }
        return true;
    }

    /** vérifie si un terme appartient à la liste.
     * @param w terme à vérifier
     * @return vrai si dans la liste
     */
    public final boolean check(StringBuffer w) {
        return check(w.toString());
    }
}
