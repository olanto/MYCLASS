package isi.jg.idxvli.server;

import static isi.jg.util.Messages.*;
import java.io.*;

/**
 * Classe stockant les  résultats d'une requête.
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
public class QLResultAndRank implements Serializable {

    /* les documents résultats */
    public int[] result;
    /* les poids des résultats */
    public float[] rank;
    /* les poids des résultats */
    public String[] docName;
    /* la durée d'exécution */
    public long duration;  // en ms

    /** crée un résultat
     * @param result id des documents
     * @param rank poids des documents
     * @param duration durée
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
