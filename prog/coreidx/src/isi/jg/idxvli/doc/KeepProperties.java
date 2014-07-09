package isi.jg.idxvli.doc;

import isi.jg.idxvli.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 * classe pour la manipulation et l'extraction des propriétés.
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * classe pour la manipulation et l'extraction des propriétés. 
 * Cette classe est modifiée en fonction des besoins du client et de la structure des documents
 *
 */
public class KeepProperties {

    /**
     * détermine la propriété de language d'un document.
     * @param fname nom du fichier
     * @param idDoc document
     * @param PM gestionnaire de propriété
     */
    public static void keepLanguageOfDoc(String fname, int idDoc, PropertiesManager PM) {
        //msg("keepLanguageOfDoc:"+fname);
        if (fname.indexOf("_EN.") != -1) {
            PM.put("_EN", idDoc);
            return;
        }
        if (fname.indexOf("_FR.") != -1) {
            PM.put("_FR", idDoc);
            return;
        }
        if (fname.indexOf("_FR2.") != -1) {
            PM.put("_FR2", idDoc);
            return;
        }
        if (fname.indexOf("_DE.") != -1) {
            PM.put("_DE", idDoc);
            return;
        }
        if (fname.indexOf("_DU.") != -1) {
            PM.put("_DU", idDoc);
            return;
        }
        if (fname.indexOf("_IT.") != -1) {
            PM.put("_IT", idDoc);
            return;
        }
        if (fname.indexOf("_PT.") != -1) {
            PM.put("_PT", idDoc);
            return;
        }
        if (fname.indexOf("_ES.") != -1) {
            PM.put("_ES", idDoc);
            return;
        }
        if (fname.indexOf("_SW.") != -1) {
            PM.put("_SW", idDoc);
            return;
        }
        if (fname.indexOf("_RU.") != -1) {
            PM.put("_RU", idDoc);
            return;
        }
        if (fname.indexOf("_CH.") != -1) {
            PM.put("_CH", idDoc);
            return;
        }
        if (fname.indexOf("_AR.") != -1) {
            PM.put("_AR", idDoc);
            return;
        }
        if (fname.indexOf("_ZH.") != -1) {
            PM.put("_ZH", idDoc);
            return;
        }
        PM.put("_OTHER", idDoc);
    }

    /**
     * détermine la ou les collections d'un document.
     * @param fname nom du fichier
     * @param idDoc document
     * @param PM gestionnaire de propriété
     */
    public static void keepCollectionOfDoc(String fname, int idDoc, PropertiesManager PM) {
        //msg("keepLanguageOfDoc:"+fname);
        int i = fname.indexOf("/");
        while (i != -1) {
            //msg(fname.substring(0,i));
            PM.put(fname.substring(0, i), idDoc);
            i = fname.indexOf("/", i + 1);
        }
    }
}


