package isi.jg.onto;

import static isi.jg.util.Messages.*;
import static java.util.Arrays.*;
import isi.jg.idxvli.*;
import java.io.*;

/**
 * Classe stockant les  Concepts associ�s � un terme.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class Concepts implements Serializable {

    /* les concepts */
    public int[] c;

    /** cr�e une liste
     * @param c une liste externe
     */
    public Concepts(int[] c) {
        this.c = c;
    }

    /** cr�e une liste vide
     */
    public Concepts() {
        this.c = null;
    }

    public static final Concepts getConcept(IdxStructure id, ManyOntologyManager ontologyLib, String lang, String word) {
        LexicManager currentLexic = ontologyLib.get(lang);
        String stemOfWord = DoParse.stem(id, lang, word);
        Concepts concepts = currentLexic.get(stemOfWord);
        return concepts;
    }

    public void add(int id) {
        if (c == null) {
            c = new int[1];
            c[0] = id;
        } else {
            if (binarySearch(c, id) < 0) { // pas dans la liste
                int[] copy = new int[c.length + 1];
                System.arraycopy(c, 0, copy, 0, c.length);
                copy[c.length] = id;
                sort(copy);
                c = copy;
            }
        }
    }

    public void add(int[] id) {
        for (int i = 0; i < id.length; i++) {
            add(id[i]);
        }
    }

    public int length() {
        if (c == null) {
            return 0;
        } else {
            return c.length;
        }
    }

    public boolean testIn(int id) {
        if (c == null) {
            return false;
        } else {
            if (binarySearch(c, id) < 0) { // pas dans la liste
                return false;
            } else {
                return true;
            }
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(c);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        c = (int[]) in.readObject();
    }
}
