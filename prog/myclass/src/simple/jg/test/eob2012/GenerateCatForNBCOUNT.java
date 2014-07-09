/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.jg.test.eob2012;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 *
 * @author x
 */
public class GenerateCatForNBCOUNT {

    static Pattern sep = Pattern.compile("[\\t]");  // les tabs
    static String drive = SomeConstant.ROOTDIR;
    static String set = SomeConstant.GROUP;
    static String[] resSetTest, resSetTrain;

    public static void main(String[] args) {
        generate(drive, set, 1);
    }

    public static void generate(String drive, String set, int pcttrain) {
        try {
            //  procesADir("h:/PATDB/XML/docdb");
            //      sourceXML = drive + "/EOB/ARTS-DATA/XML/FULLTEXT/RES/";
            String targetTest = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTest" + pcttrain + ".cat";
            String targetTrain = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrain" + pcttrain + ".cat";
            String targetTestNB = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTestNB" + pcttrain + ".cat";
            String targetTrainNB = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrainNB" + pcttrain + ".cat";
            String targetTestCOUNT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTestCOUNT" + pcttrain + ".cat";
            String targetTrainCOUNT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrainCOUNT" + pcttrain + ".cat";

            resSetTrain = loadResult(targetTrain);
            resSetTest = loadResult(targetTest);


            BuiltTTNB(targetTestNB, targetTrainNB);
            BuiltTTCOUNT(targetTestCOUNT, targetTrainCOUNT, pcttrain);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String[] loadResult(String fileName) {
        Vector<String> pat = new Vector<String>();

        try {
            System.out.println("open :" + fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            BufferedReader insource = new BufferedReader(isr);
            String w = insource.readLine();
            while (w != null) {
                pat.add(w);
                w = insource.readLine();
            }
            isr.close();
            String[] ps = new String[pat.size()];
            for (int i = 0; i < pat.size(); i++) {
                ps[i] = pat.get(i);
            }
            Arrays.sort(ps);
            return ps;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void BuiltTTNB(String targetTest, String targetTrain) {
        try {

            System.out.println("create files for :" + targetTrain);
            OutputStreamWriter outcat = new OutputStreamWriter(new FileOutputStream(targetTrain), "UTF-8");
            for (int i = 0; i < resSetTrain.length; i++) {
                outcat.append(NBClass(resSetTrain[i]) + "\n");
            }
            outcat.close();

            outcat = new OutputStreamWriter(new FileOutputStream(targetTest), "UTF-8");
            for (int i = 0; i < resSetTest.length; i++) {
                outcat.append(NBClass(resSetTest[i]) + "\n");
            }
            outcat.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void BuiltTTCOUNT(String targetTest, String targetTrain, int pcttrain) {
        try {

            System.out.println("create files for :" + targetTrain);
            OutputStreamWriter outcat = new OutputStreamWriter(new FileOutputStream(targetTrain), "UTF-8");
            for (int i = 0; i < resSetTrain.length; i++) {
                outcat.append(countClass(resSetTrain[i]) + "\n");
            }
            outcat.close();

            outcat = new OutputStreamWriter(new FileOutputStream(targetTest), "UTF-8");
            for (int i = 0; i < resSetTest.length; i++) {
                outcat.append(countClass(resSetTest[i]) + "\n");
            }
            outcat.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String NBClass(String s) {
        String[] cats = sep.split(s);
        int nb = (cats.length - 1);
        if (nb == 1) {
            return cats[0] + "\t" + fillFix("MONO");
        }
        return cats[0] + "\t" + fillFix("MULTI");
    }

    static String countClass(String s) {
        String[] cats = sep.split(s);
        int nb = (cats.length - 1);

        return cats[0] + "\t" + fillFix("" + nb);
    }

    static String fillFix(String s) {
        String res = s;
        for (int i = res.length(); i < 14; i++) {
            s += "-";
        }
        return s;
    }
}
