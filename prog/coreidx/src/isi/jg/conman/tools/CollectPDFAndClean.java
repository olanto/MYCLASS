/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.tools;

import isi.jg.conman.util.*;
import java.rmi.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.conman.server.GetContentService.*;
import static isi.jg.conman.util.UtilitiesOnHTML.*;
import isi.jg.idxvli.util.SetOfBits;
import java.util.regex.*;

/**
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
public class CollectPDFAndClean {

    static final boolean verboseTitle = true;

    public static void main(String[] args) {
        ContentService cmCollect = getServiceCM("rmi://localhost/CM_COLLECT");
        ContentService cmClean = getServiceCM("rmi://localhost/CM_CLEAN");
        try {
            int lastdoc = cmCollect.getSize();
            msg("#doc:" + lastdoc);
            SetOfBits convertable = cmCollect.satisfyThisProperty("TYPE.CONVERTABLE");
            SetOfBits pdftype = cmCollect.satisfyThisProperty("TYPE.PDF");
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                if (convertable.get(i) && pdftype.get(i)) {  // convertible & pfd
                    byte[] b = cmCollect.getBin(i);
                    String refname = cmCollect.getDocName(i);
                    String txt = PDF2TXT.pdf2txt(b, refname);
                    if (txt != null && txt.length() != 0) {
                        // on peut indexer 
                        String cleantxt = html2txt(txt);
                        String title = cleantxt.substring(0, Math.min(60, cleantxt.length()));

                        if (verboseTitle) {
                            msg("assign title :" + title + " for doc" + refname);
                        }
                        cmClean.setRefDoc("" + i, refname, title, cleantxt);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
