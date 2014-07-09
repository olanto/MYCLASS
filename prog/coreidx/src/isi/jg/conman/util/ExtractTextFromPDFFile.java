/**
 * Copyright (c) 2003-2006, www.pdfbox.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdfbox; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.pdfbox.org
 *
 * 
 *  * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
package isi.jg.conman.util;

import java.io.*;
import java.io.Writer;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;
import org.pdfbox.util.PDFTextStripper;

/**
 * This is the main program that simply parses the pdf document and transforms it
 * into text.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.14 $
 */
public class ExtractTextFromPDFFile {

    /**
     * This is the default encoding of the text to be output.
     */
    public static final String DEFAULT_ENCODING =
            null;
    //"ISO-8859-1";
    //"ISO-8859-6"; //arabic
    //"US-ASCII";
    //"UTF-8";
    //"UTF-16";
    //"UTF-16BE";
    //"UTF-16LE";
    /**
     * private constructor.
     */
    private ExtractTextFromPDFFile() {
    //static class
    }

    /**
     * Infamous main method.
     *
     * @param args Command line arguments, should be one and a reference to a file.
     *
     * @throws Exception If there is an error parsing the document.
     */
    public static void main(String[] args) throws Exception {
        boolean toHTML = false;
        boolean sort = true;
        String password = "";
        String encoding = "UTF8";
        String pdfFile = "C:/JG/AjavaProject/PDFBox/test1.pdf";
        String textFile = "C:/JG/AjavaProject/PDFBox/test1.text";
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;



        Writer output = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);

            System.out.println(pdfFile + " - " + document.toString());
            System.out.println(" ->" + textFile);

            if (document.isEncrypted()) {
                System.out.println("You do not have permission to extract text from :" + pdfFile);
                return;
            }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            output = new OutputStreamWriter(buf, encoding);
            System.out.println("open output:" + encoding);

            PDFTextStripper stripper = null;
            if (toHTML) {
                stripper = new PDFText2HTML();
            } else {
                stripper = new PDFTextStripper();
                System.out.println("open stripper");
            }
            stripper.setSortByPosition(sort);
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.writeText(document, output);

            String s = new String(buf.toString(encoding));

            System.out.println(s);

        } finally {
            if (output != null) {
                output.close();
            }
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * This will print the usage requirements and exit.
     */
    private static void usage() {
        System.err.println("Usage: java org.pdfbox.ExtractText [OPTIONS] <PDF file> [Text File]\n" +
                "  -password  <password>        Password to decrypt document\n" +
                "  -encoding  <output encoding> (ISO-8859-1,UTF-16BE,UTF-16LE,...)\n" +
                "  -console                     Send text to console instead of file\n" +
                "  -html                        Output in HTML format instead of raw text\n" +
                "  -sort                        Sort the text before writing\n" +
                "  -startPage <number>          The first page to start extraction(1 based)\n" +
                "  -endPage <number>            The last page to extract(inclusive)\n" +
                "  <PDF file>                   The PDF document to use\n" +
                "  [Text File]                  The file to write the text to\n");
        System.exit(1);
    }
}
