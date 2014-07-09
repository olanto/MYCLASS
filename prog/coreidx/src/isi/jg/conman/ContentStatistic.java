package isi.jg.conman;

import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;
import static isi.jg.conman.ContentConstant.*;

/**
 * Une classe pour collecter des informations statistiques sur l'indexeur.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class ContentStatistic {

    ContentStructure glue;

    ContentStatistic(ContentStructure id) {
        glue = id;
    }

    /** Affiche dans la console des statistiques sur l'indexeur.
     */
    public void global() {
        msg("STATISTICS global:");
        msg("documax: " + DOC_MAX + ", docNnow: " + (glue.lastdoc) + ", used: " + ((glue.lastdoc * 100) / DOC_MAX) + "%");
        if (glue.lastdoc != 0) {
            msg("docNow: " + (glue.lastdoc) + " docValid: " + (glue.docstable.countValid() + ", valid: " + (glue.docstable.countValid() * 100) / glue.lastdoc) + "%");
        }
        contentSize();
    }

    public String getGlobal() {
        return "Kdoc: " + DOC_MAX / 1024 + "/" + (glue.lastdoc / 1024) + " " + ((glue.lastdoc * 100) / Math.max(DOC_MAX, 1)) + "%";
    }

    public void document() {
        glue.docstable.printStatistic();
    }

    public void content() {
        glue.IO.printContentStatistic();
    }

    public void contentSize() {
        msg("STATISTICS content size:");
        msg("docNow: " + glue.lastdoc);
        long real = 0;
        long stored = 0;
        for (int i = 0; i < glue.lastdoc; i++) {
            real += glue.IO.realSizeOfContent(i);
            stored += glue.IO.storeSizeOfContent(i);
        }

        msg("realSize: " + (real / MEGA) +
                " [Mb], storedSize: " + (stored / MEGA) +
                " Compression: " + (stored * 100) / (real + 1) + "%");
    }
}
