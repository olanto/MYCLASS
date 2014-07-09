/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.tools;

import java.rmi.*;
import java.util.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.conman.server.GetContentService.*;
import static isi.jg.conman.util.UtilitiesOnHTML.*;
import isi.jg.idxvli.util.SetOfBits;
import java.util.regex.*;
import isi.jg.idxvli.doc.*;

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
public class StatOnPropertiesOnContent {

    static final boolean verboseTitle = false;
    static ContentService id;

    public static void main(String[] args) {

        inventoryOf("rmi://localhost/CM_COLLECT");
        inventoryOf("rmi://localhost/CM_CLEAN");
    }

    public static void inventoryOf(String CMService) {
        msg("");
        msg("");
        msg("---------- " + CMService);
        id = getServiceCM(CMService);
        try {
            msg("---- all properties:");
            PropertiesList prop = id.getDictionnary();
            showVector(prop.result);
            msg("---- all properties TYPE:");
            prop = id.getDictionnary("TYPE.");
            showVector(prop.result);
            msg("---- all properties LANG:");
            prop = id.getDictionnary("LANG.");
            showVector(prop.result);
            msg("---- all properties COLLECTION:");
            prop = id.getDictionnary("COLLECTION.");
            showVector(prop.result);
            msg("#doc:" + id.getSize());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        msg("----- Type ---------------------------");
        countSet("TYPE.STRING");
        countSet("TYPE.PDF");
        countSet("TYPE.ERROR");
        countSet("TYPE.INDEXABLE");
        countSet("TYPE.CONVERTABLE");
        countSet("TYPE.CLEAN_TEXT");
        msg("----- collected ---------------------------");
        countSet("STATE.INDEXED");
        countSet("STATE.CONVERTED");
        countSet("STATE.ERROR");
        msg("----- Web Robot ---------------------------");
        countSet("PROTOCOLE.URL");
        countSet("STATE.NOTLOAD");
        countSet("STATE.LOAD");
        countSet("STATE.ERRORLOAD");
    }

    static void countSet(String setName) {
        try {
            int lastdoc = id.getSize();
            int count = 0;
            SetOfBits sob = id.satisfyThisProperty(setName);
            if (sob == null) {
                msg("no property for " + setName + " :" + count);
                return;
            }

            for (int i = 0; i < lastdoc; i++) {
                if (sob.get(i)) {
                    count++;
                }
            }
            msg("count for " + setName + " :" + count);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
