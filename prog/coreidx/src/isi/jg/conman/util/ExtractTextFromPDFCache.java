/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.util;

import java.rmi.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.conman.server.GetContentService.*;
import static isi.jg.conman.util.UtilitiesOnHTML.*;
import isi.jg.idxvli.util.SetOfBits;
import java.util.regex.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.*;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

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
public class ExtractTextFromPDFCache {

    static final boolean verboseTitle = false;

    public static void main(String[] args) {
        ContentService cmCollect = getServiceCM("rmi://localhost/CM_COLLECT");
        //ContentService cmClean=getServiceCM("rmi://localhost/CM_CLEAN");
        String title = null;
        int notitle = 0;
        try {
//            msg(cmClean.getRefName("0"));
//            msg(cmClean.getTitle("0"));
//            msg(cmClean.getCleanText("0"));
            byte[] b = cmCollect.getBin("http://cui.unige.ch/isi/reports/fnz-ht-05.pdf");
            msg(new String(b));

            msg("-------");
            msg(PDF2TXT.pdf2txt(b, "http://cui.unige.ch/isi/reports/fnz-ht-05.pdf"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
