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
public class CollectHTMLAndClean {

    static final boolean verboseTitle = false;

    public static void main(String[] args) {
        ContentService cmCollect = getServiceCM("rmi://localhost/CM_COLLECT");
        ContentService cmClean = getServiceCM("rmi://localhost/CM_CLEAN");
        String title = null;
        int notitle = 0;
        try {
            int lastdoc = cmCollect.getSize();
            msg("#doc:" + lastdoc);
            SetOfBits indexable = cmCollect.satisfyThisProperty("TYPE.INDEXABLE");
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                if (indexable.get(i)) {  // on doit donc pouvoir trouver un titre si html
                    String txt = cmCollect.getDoc(i);
                    if (txt.length() != 0) {
                        if (txt.startsWith("%PDF")) {  // PDF
                            msg("skip unconverted PDF:" + cmCollect.getDocName(i));
                        } else if (txt.substring(0, Math.min(500, txt.length())).contains("charset=windows-1251")) {  // RUSSE
                            msg("skip unconverted 1251:" + cmCollect.getDocName(i));
                        } else {  // on peut indexer
                            if (verboseTitle) {
                                msg("process doc:" + i + " length:" + txt.length());
                            }
                            title = getTitle(txt);
                            if (title == null) {
                                notitle++;
                                msg("no title for:" + cmCollect.getDocName(i));
                                title = "no title";
                            }
                            title = clean(title);
                            String refname = cmCollect.getDocName(i);
                            String cleantxt = html2txt(cmCollect.getDoc(i));
                            if (verboseTitle) {
                                msg("assign title :" + title + " for doc" + cmCollect.getDocName(i));
                            }
                            cmClean.setRefDoc("" + i, refname, title, cleantxt);
                        }
                    }
                }
            }
            msg("notitle:" + notitle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
