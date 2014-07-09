/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.tools;

import java.rmi.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.conman.server.GetContentService.*;

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
public class ResetIndexed {

    public static void main(String[] args) {

        ContentService is = getServiceCM("rmi://localhost/CM_COLLECT");

        try {
            int lastdoc = is.getSize();
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                is.clearDocumentPropertie(i, "STATE.INDEXED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
