package isi.jg.idxvli.server;

import static isi.jg.util.Messages.*;
import java.io.*;

/**
 * Classe stockant les  r�sultats d'une requ�te.
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
public class QLResultAndRank implements Serializable {

    /* les documents r�sultats */
    public int[] result;
    /* les poids des r�sultats */
    public float[] rank;
    /* les poids des r�sultats */
    public String[] docName;
    /* la dur�e d'ex�cution */
    public long duration;  // en ms

    /** cr�e un r�sultat
     * @param result id des documents
     * @param rank poids des documents
     * @param duration dur�e
     */
    public QLResultAndRank(int[] result, float[] rank, long duration) {
        this.result = result;
        this.rank = rank;
        this.duration = duration;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(result);
        out.writeObject(rank);
        out.writeObject(docName);
        out.writeLong(duration);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        result = (int[]) in.readObject();
       rank = (float[]) in.readObject();
       docName = (String[]) in.readObject();
        duration = in.readLong();
    }
}
