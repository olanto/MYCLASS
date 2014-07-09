/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.jg.deploy.wipo2013;

import eob.SomeConstant;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 *
 * @author x
 */
public class CleanCatalog {

    static Pattern sep = Pattern.compile("[\\t]");  // les tab

    public static void main(String[] args) {
        try {
            //  procesADir("h:/PATDB/XML/docdb");
            String drive = SomeConstant.ROOTDIR;
            String set = SomeConstant.GROUP;
            //      sourceXML = drive + "/EOB/ARTS-DATA/XML/FULLTEXT/RES/";
            String catEN = "h:/PATDB/catalog/EN.cat.val";
            String catFR = "h:/PATDB/catalog/FR.cat.val";
            String rdn = ".rdn";
            cleanSymbols(catFR);
            cleanSymbols(catEN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void addSymb(Vector<String> symb, String s) {
        for (int i = 0; i < symb.size(); i++) {
            if (symb.get(i).equals(s)) { //déjà la
                return;
            }
        }
        symb.add(s);
    }

    public static void cleanSymbols(String fileName) {

        try {
            Random gen = new Random(352345);
            System.out.println("-------------- StatFirstSymbols :" + fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            BufferedReader insource = new BufferedReader(isr);
            OutputStreamWriter outcat = new OutputStreamWriter(new FileOutputStream(fileName + ".clean"), "UTF-8");
            String w = insource.readLine();
            int countpat = 0;
            while (w != null) {
                String[] patsymbs = sep.split(w);
                if (patsymbs.length < 2) {
                    System.out.println("error for :" + w);
                } else {
                    countpat++;
                    Vector<String> symb = new Vector<>();
                    for (int i = 1; i < patsymbs.length; i++) {
                        addSymb(symb, patsymbs[i]);
                    }

                   // randomize
                    int shuffle = symb.size();
                    if (shuffle != 1) {
                        String[] ps = new String[shuffle];
                        for (int i = 0; i < shuffle; i++) {
                            ps[i] = symb.get(i);
                        }
                        for (int i = 1; i < 5 * shuffle; i++) {
                            int a = gen.nextInt(shuffle);
                            int b = gen.nextInt(shuffle);
                            String tempo = ps[b];
                            ps[b] = ps[a];
                            ps[a] = tempo;
                        }
                        for (int i = 0; i < shuffle; i++) {
                            symb.set(i,ps[i]);
                        }
                    }



                    outcat.append(patsymbs[0] + "\t");
                    for (int i = 0; i < symb.size(); i++) {
                        outcat.append(symb.get(i) + "\t");
                    }
                    outcat.append("\n");
                }
                w = insource.readLine();
            }
            isr.close();
            outcat.close();
            System.out.println("countpat :" + countpat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
