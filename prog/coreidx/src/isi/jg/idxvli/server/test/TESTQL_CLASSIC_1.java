package isi.jg.idxvli.server.test;

import isi.jg.idxvli.*;
import isi.jg.idxvli.server.*;
import isi.jg.util.Timer;
import java.io.*;
import isi.jg.idxvli.ql.*;
import static isi.jg.util.Messages.*;
import isi.jg.util.TimerNano;

/**
 * *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 * Test de l'indexeur, mode query
 */
public class TESTQL_CLASSIC_1 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("INCREMENTAL", new ConfigurationNative());
        id.Statistic.global();
        //id.checkIntegrityOfW("nana",true);
        //testref();


//        test("select");
//        test("hashtable");
//        test("hashtable AND interface");
//        test("NEAR(garbage,collection)");
//        test("NEXT(create,view)");


        // test avec BILANG ... book
        test("nana");
        test("kopeks");
        test("russie");
        test("oui");
        test("lune");
//        test("kopeks AND russie");
//        test("nana AND anna");
//        test("charbon AND mine");
//        test("charbon mine");
//        test("(charbon) (mine)");
//        test("charbon ~(noir mine)");
//        test("nana OR lune");
//        test("nana | lune");
//        test("nana MINUS anna");
//        test("nana - anna");
//        test("NEAR(belle,femme)");
//        test("~(belle,femme)");
//        test("~(belle femme)");
////        test("NEXT(pommes,terre)");
//        test("QUOTATION(\"un jeune homme qui venait achever son\")");
//        test("\"un jeune homme qui venait achever son\"");
////        test("QUOTATION(\"un jeune homme qui venait\")");
////        test("QUOTATION(\"un jeune homme qui\")");
////        test("pari");
////        test("fasdjfasfdj");
//        test("chabron ~(noir AND mine) (nana OR lune) \"un jeune homme\" 2006 MINUS anna");
//        testplus("charbon AND mine");
//        testplus("charbon mine");
//        testplus("(charbon) (mine)");
//        testplus("charbon ~(noir mine)");
//        testplus("nana OR lune");
//        testplus("nana | lune");
//        testplus("nana MINUS anna");
//        testplus("nana - anna");
//        testplus("NEAR(belle,femme)");
//        testplus("~(belle,femme)");
//        testplus("~(belle femme)");


        t1.stop();
        id.close();
    }

    public static void test(String s) {
        TimerNano t1 = new TimerNano("parse:" + s, false);
        int start = 0;
        int size = 5;
        QLResultNice nice = id.evalQLNice(null, s, start, size);
        t1.stop(false);
        if (nice.result.length == 0) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + nice.result.length);
            for (int i = start; i < Math.min(start + size, nice.result.length); i++) {
                msg("  doc " + nice.result[i] + " : " + nice.docname[i]);
            }
        }
        msg("oups:" + nice.alternative);
        msg("\n-----------------------------------------------------------------------------\n");
    //showVector(res);
    }

    public static void testplus(String s) {
        TimerNano t1 = new TimerNano("parse:" + s, false);
        int start = 5;
        int size = 5;
        QLResultNice nice = id.evalQLNice(null, s, start, size);
        t1.stop(false);
        if (nice.result.length == 0) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + nice.result.length);
            for (int i = start; i < Math.min(start + size, nice.result.length); i++) {
                msg("  doc " + nice.result[i] + " : " + nice.docname[i]);
            }
        }
        msg("oups:" + nice.alternative);
        msg("\n-----------------------------------------------------------------------------\n");
    //showVector(res);


    }

    private static void testref() {
        String s = readFromFile("C:/JG/gigaversion/data/REFTEST.TXT");

        {
            TimerNano t1 = new TimerNano("ref 1-----------:", false);
            id.Query.parseQuery("MULTI", s, "6");
            t1.stop(false);
        }

//        {TimerNano t1=new TimerNano("ref 2-----------:",false);
//        id.Query.parseQuery("MULTI",s,"6");
//        t1.stop(false);
//        }

    //System.out.println(id.Query.parseQuery("MULTI",s,"6"));
    }

    private static String readFromFile(String fname) {
        StringBuffer b = new StringBuffer("");
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String w = in.readLine();
            while (w != null) {
                //System.out.println(w);
                b.append(w);
                w = in.readLine();
            }
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        return b.toString();
    }
}
