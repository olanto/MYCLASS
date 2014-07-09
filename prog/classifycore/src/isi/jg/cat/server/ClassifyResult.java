package isi.jg.cat.server;

import java.io.*;

/**
 * Classe stockant les  résultats d'une classification.
 * <p>
 * <b>JG-2010
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2010 & Simple-Shift
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2010</b>
 *<p>
 */
public class ClassifyResult implements Serializable {

    /* les codes des résultats */
    private String[] category;

    public ClassifyResult(String[] category) {
        this.category = category;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(getCategory());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        category = (String[]) in.readObject();
    }

    /**
     * @return the category
     */
    public String[] getCategory() {
        return category;
    }

    /**
     * only for debug
     */
    public void show() {
        if (category != null) {
            for (int i = 0; i < category.length; i++) {
                System.out.println(i + " -> " + category[i]);
            }
        } else {
            System.out.println("category is null!");
        }
    }
}
