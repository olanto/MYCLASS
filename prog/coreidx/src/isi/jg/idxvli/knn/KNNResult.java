package isi.jg.idxvli.knn;

import static isi.jg.util.Messages.*;
import java.io.*;

/**
 * Classe stockant les  r�sultats d'une requ�te KNN.
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
public class KNNResult implements Serializable {

    /* les documents r�sultats */
    public int[] result;
    /* les scores des r�sultats */
    public int[] score;
    /* la dur�e d'ex�cution */
    public long duration;

    /** cr�e un r�sultat
     * @param result id des documents
     * @param duration dur�e
     */
    public KNNResult(int[] result, int[] score, long duration) {
        this.result = result;
        this.score = score;
        this.duration = duration;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(result);
        out.writeObject(score);
        out.writeLong(duration);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        result = (int[]) in.readObject();
        score = (int[]) in.readObject();
        duration = in.readLong();
    }
}
