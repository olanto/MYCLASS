package isi.jg.idxvli.doc;

import static isi.jg.util.Messages.*;
import java.io.*;

/**
 * Classe stockant une liste de propri�ts.
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
public class PropertiesList implements Serializable {

    /* les propri�t�s r�sultats */
    public String[] result;

    /** cr�e une propertie List
     * @param result id des documents
     */
    public PropertiesList(String[] result) {
        this.result = result;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(result);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        result = (String[]) in.readObject();
    }
}
