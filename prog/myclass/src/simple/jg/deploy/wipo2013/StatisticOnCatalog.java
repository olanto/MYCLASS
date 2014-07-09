/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.jg.deploy.wipo2013;

import eob.PatentEOB;
import eob.SomeConstant;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 *
 * @author x
 */
public class StatisticOnCatalog {

    static Pattern sep = Pattern.compile("[\\t]");  // les tab
    static HashMap<String,Integer> finalTraining=new HashMap();
    static HashMap<String,Integer> inCatalog=new HashMap();

    public static void main(String[] args) {
        try {
            //  procesADir("h:/PATDB/XML/docdb");
            String drive = SomeConstant.ROOTDIR;
            String set = SomeConstant.GROUP;
            //      sourceXML = drive + "/EOB/ARTS-DATA/XML/FULLTEXT/RES/";
            String catEN = "h:/PATDB/catalog/EN.cat.val.clean";
            String catFR = "h:/PATDB/catalog/FR.cat.val.clean";
           // StatNBSymbols(catFR);
           StatNBSymbols(catEN);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    
    public static void StatNBSymbols(String fileName) {

        try {
            System.out.println("-------------- StatNBSymbols :" + fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            BufferedReader insource = new BufferedReader(isr);
            String w = insource.readLine();
            int countpat = 0;
            int countsymb = 0;
            while (w != null) {
                countpat++;
                String[] patsymbs = sep.split(w);
                countsymb += patsymbs.length-1 ; // premier id du patent 
                finalTraining.put(patsymbs[1],0);
                for (int i=1;i<patsymbs.length;i++){
                    Integer count=inCatalog.get(patsymbs[i]);
                    if (count==null){
                        inCatalog.put(patsymbs[i], 1); // première fois
                    } else {
                       // System.out.println("add 1");
                        count++;
                        inCatalog.put(patsymbs[i], count);
                    }
                    
                }
                w = insource.readLine();
            }
            isr.close();
            System.out.println("countpat :" + countpat + ", countsymb :" + countsymb + ", moy:"
                    + ((float) countsymb / (float) countpat));
            System.out.println("nb cat in training :" + finalTraining.size());
            System.out.println("nb cat in catalog :" + inCatalog.size());
            statSymbols();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void statSymbols() {
         
  
        try {
            System.out.println("-------------- Stat Symbols :");
             for (Iterator<String> iter = finalTraining.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                int count = inCatalog.get(key);
                System.out.println(key + "\t" + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
