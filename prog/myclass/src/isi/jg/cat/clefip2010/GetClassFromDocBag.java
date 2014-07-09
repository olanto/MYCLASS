/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isi.jg.cat.clefip2010;

import isi.jg.idxvli.IdxStructure;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;
import static isi.jg.util.Messages.*;

/**
 *
 * @author jg
 */
public class GetClassFromDocBag {

    static String patentName;
    private static Pattern p = Pattern.compile("\\s");  // les fins de mots
    private static IdxStructure id;

    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();
        Classify.init();

        String dir = "Z:/CLEFIP10/FINALTASK/relevant.txt";
        String out = "Z:/CLEFIP10/FINALTASK/ssft_CE6800_run4_CLS.txt";
        readTopic(dir, out);
    }

    private static void readTopic(String fname, String out) {
        //msg("load topic");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname));
            BufferedReader in = new BufferedReader(isr);
            OutputStreamWriter result = new OutputStreamWriter(new FileOutputStream(out));
            String w = in.readLine();
            while (w != null) {
                String docname = w;
                 // msg(docname);
                int doc = id.getIntForDocument(docname);
                int[] docbag = id.getBagOfDoc(doc);
                String res = Classify.adviseSimple(docbag, 4, 616);
                //msg(docname + res);
                String[] seg = p.split(res);
                for (int i = 1; i < seg.length; i++) {
                    result.write(docname + " Q0 " + seg[i] + " " + (i) + " " + (1001 - i) + "\n");
                }
                w = in.readLine();
            }
            result.close();
        } catch (Exception e) {
            error("IO error in readList", e);
        }
    }
}
