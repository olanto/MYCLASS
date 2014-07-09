package isi.jg.onto;

import static isi.jg.util.Messages.*;
import static java.util.Arrays.*;
import java.io.*;

/**
 * Classe stockant les  termes associés à un concept.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class Terms implements Serializable {

    /* les termes */
    public String[] t;

    /** crée une liste
     * @param t une liste externe
     */
    public Terms(String[] t) {
        this.t = t;
    }

    /** crée une liste vide
     */
    public Terms() {
        this.t = null;
    }

    public String dump() {
        if (t == null) {
            return null;
        }
        String s = "";
        for (int i = 0; i < t.length; i++) {
            s += t[i] + " / ";
        }
        return s;
    }

    public void add(String id) {
        if (t == null) {
            t = new String[1];
            t[0] = id;
        } else {
            if (binarySearch(t, id) < 0) { // pas dans la liste
                String[] copy = new String[t.length + 1];
                System.arraycopy(t, 0, copy, 0, t.length);
                copy[t.length] = id;
                sort(copy);
                t = copy;
            }
        }
    }

    public boolean testIn(String id) {
        if (t == null) {
            return false;
        } else {
            if (binarySearch(t, id) < 0) { // pas dans la liste
                return false;
            } else {
                return true;
            }
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(t);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        t = (String[]) in.readObject();
    }
}
