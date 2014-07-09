package isi.jg.idxvli.test;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import java.io.*;
import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.util.TimerNano;
import isi.jg.idxvli.ql.QueryOperator;

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
public class TESTQueryMode {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY");
        // création de la racine de l'indexation
        id.createComponent(new Configuration());

        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();

        id.Statistic.global();
        //id.Statistic.topIndexByLength(10);
        //id.checkIntegrityOfRND(true);

        System.out.println(id.getStringforW(1));
        System.out.println(id.getStringforW(10));
        System.out.println(id.getStringforW(20));
        System.out.println("" + id.getIntForW("bleu"));
        System.out.println("" + id.getIntForW("vodka"));
        System.out.println("" + id.getIntForW("package"));
        System.out.println("" + id.getIntForW("class"));
        System.out.println(id.getFileNameForDocument(1));
//        id.checkVectorWforBasic(2025);
//        id.checkVectorWforFull(2025);
//        
//        int[]  r=new int[2];
//        r[0]=3;r[1]=6;
//        for (int i=0;i<8;i++)
//        System.out.println(i+","+id.Indexer.getValue(r,0,2,i));

        test("NEAR", "under", "application");
        test("NEAR", "under", "application");


        test("AND", "center", "align");
        test("NEAR", "center", "align");

//////          test("AND", "fish", "shark"); 
//////          test("AND", "shark", "fish"); 
////// 
//////          test("NEAR", "fish", "shark"); 
//////          test("NEAR", "shark", "fish"); 
//////
//////          test("NEXT", "fish", "shark"); 
//////          test("NEXT", "shark", "fish"); 


//        test("SINGLE", "bleu", ""); 
//          test("NEXT", "shark", "asafoetida"); 
//          test("NEXT", "asafoetida", "shark"); 
////          test("NEAR", "shark", "shark"); 
//          test("NEXT", "fish", "shark"); 
//          test("NEXT", "shark", "fish"); 
// 
//          test("NEXT", "fish", "shark"); 
//          test("NEXT", "shark", "fish");
//          
//          test("NEXT", "fish", "cake"); 
//          test("NEXT", "cake", "fish"); 
//          test("NEXT", "vodka", "cake"); 
//          test("NEXT", "cake", "vodka"); 
//          test("NEXT", "butter", "shark"); 
//          test("NEXT", "shark", "butter"); 
//          test("NEXT", "chocolate", "cake"); 
//          test("NEXT", "cake", "chocolate"); 

        QueryOperator.getDocforWseq3(id, "taste", "and", "season");
        QueryOperator.getDocforWseq3(id, "taste", "and", "season");



        String s = readFromFile("C:/JG/gigaversion/data/REFTEST.TXT");

        id.Query.parseQuery("MULTI", s, "6");


        System.out.println(id.Query.parseQuery("MULTI", s, "6"));

//          test("NEXT", "apple", "pie"); 
//        System.out.println(id.Query.parseQuery("SINGLE", "trois", "")); 
//        System.out.println(id.Query.parseQuery("SINGLE", "966.45", "")); 
        //  System.out.println(id.Query.parseQuery("AND", "poisson", "plat")); 
        //  System.out.println(id.Query.parseQuery("NEAR", "poisson", "plat")); 
        // System.out.println(id.Query.parseQuery("NEXT", "plat", "poisson")); 
        TimerNano t2 = new TimerNano("empty", false);
        t2.stop(false);


        t1.stop();
    }

    private static void test(String command, String w1, String w2) {
        TimerNano t1 = new TimerNano(command + " " + w1 + " " + w2, false);
        System.out.println((id.Query.parseQuery(command, w1, w2)).length());
        t1.stop(false);
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
