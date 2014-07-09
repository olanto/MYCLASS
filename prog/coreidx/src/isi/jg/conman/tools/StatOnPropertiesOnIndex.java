/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.tools;

import java.rmi.*;
import java.util.*;
import isi.jg.idxvli.server.*;
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
public class StatOnPropertiesOnIndex {

    static final boolean verboseTitle = false;
    static IndexService id;

    public static void main(String[] args) {

        inventoryOf("rmi://localhost/VLI");
    }

    public static void inventoryOf(String VLIService) {
        msg("");
        msg("");
        msg("---------- " + VLIService);
        id = getServiceVLI(VLIService);
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
            msg("---- all properties PROFIL:");
            prop = id.getDictionnary("PROFIL.");
            showVector(prop.result);
            msg("statistic:" + id.getStatistic());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
}
