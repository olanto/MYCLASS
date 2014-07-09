package isi.jg.conman.util;

import static isi.jg.util.Messages.*;

import java.io.*;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;
import org.pdfbox.util.PDFTextStripper;

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
public class PDF2TXT {

    public static String pdf2txt(byte[] pdfbuf, String pdfFile) {
        boolean toHTML = false;
        boolean sort = true;
        String password = "";

        String encoding = "UTF-8";
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;



        Writer output = null;
        PDDocument document = null;
        try {

            ByteArrayInputStream in = new ByteArrayInputStream(pdfbuf);
            document = PDDocument.load(in);
            
            System.out.println(pdfFile+" - "+document.toString());

            if (document.isEncrypted()) {
                System.out.println("You do not have permission to extract text from :" + pdfFile);
                document.close();
                return null;
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            output = new OutputStreamWriter(buf, encoding);
            //System.out.println("open output:"+encoding);

            PDFTextStripper stripper = null;
            if (toHTML) {
                stripper = new PDFText2HTML();
            } else {
                stripper = new PDFTextStripper();
            //System.out.println("open stripper");
            }
            stripper.setSortByPosition(sort);
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.writeText(document, output);

            String s = new String(buf.toString(encoding));

            if (!isText(s.substring(0, Math.min(2000, s.length())))) {
                System.out.println("(rejected) file :" + pdfFile + " = " + s.substring(0, Math.min(100, s.length())));
                output.close();
                document.close();
                return null;
            }

            output.close();
            document.close();
            return s;

        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                output.close();
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static boolean isText(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                count++;
            }
        }
        if (count * 100 / (s.length()+1) > 6) {
            return true;
        } // au moins x % de blancs 8 semble assez raisonable
        else {
            return false;
        }
    }
}
