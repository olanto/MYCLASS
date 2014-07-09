package isi.jg.idxvli;

import java.io.*;
import java.util.*;

/**
 * Une classe pour g�rer des listes de termes.
 * Les listes de stop words sont g�r�es ainsi
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class SetOfWords {

    Hashtable<String, Integer> words = new Hashtable<String, Integer>();
    int count = 0;

    /**
     * cr�ation d'une liste charg�e depuis un fichier.
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

    /** v�rifie si un terme appartient � la liste.
     * @param w terme � v�rifier
     * @return vrai si dans la liste
     */
    public final boolean check(String w) {
        //System.out.println("test dont index:"+w+" . "+words.get(w));
        if (words.get(w) == null) {
            return false;
        }
        return true;
    }

    /** v�rifie si un terme appartient � la liste.
     * @param w terme � v�rifier
     * @return vrai si dans la liste
     */
    public final boolean check(StringBuffer w) {
        return check(w.toString());
    }
}
