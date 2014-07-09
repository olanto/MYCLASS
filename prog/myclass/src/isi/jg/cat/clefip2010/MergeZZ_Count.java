package isi.jg.cat.clefip2010;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import java.io.*;
import java.util.regex.Pattern;

/**
 * TOTAL 4,40 countrealnext:1002428
STOP: read and rewrite - 233000 ms
BUILD SUCCESSFUL (total time: 4 minutes 4 seconds)

TOTAL countrealnext:2753028
STOP: read and rewrite - 2393656 ms
BUILD SUCCESSFUL (total time: 39 minutes 59 seconds)
 *
 *
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * permet de g�n�rer une collection avec 2 dimensions
 */
public class MergeZZ_Count {

    static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    private static Pattern p = Pattern.compile("\\s");  // les fins de mots
    static int count=0;
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationForRewrite());
        id.Statistic.global();
        t1 = new Timer("read and rewrite");
        merge("C:/TEMPOMFLF/merge_frende_24_20.mflf", "UTF-8");
 //       merge("C:/TEMPORES/rewrite_frende_24_20.mflf", "C:/TEMPOMFLF/merge_frende_24_20.mflf", "UTF-8");
     System.out.println(count);   t1.stop();
    }

    private static void merge(String f,String IDX_MFL_ENCODING) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f), IDX_MFL_ENCODING);
            BufferedReader fin = new BufferedReader(isr, 1000000);
            msg(f + ":open in: " + IDX_MFL_ENCODING);
            String w = null;
            StringBuilder news = new StringBuilder("");
            try {  // first doc align
                w = fin.readLine();
                while (w != null && !w.startsWith("#####")) {
                    w = fin.readLine();
                }
            } catch (Exception e) {
                System.err.println("IO error during read file:" + w);
                e.printStackTrace();
            }
            while (w != null) {  // repeat for all documents in this file
                String fshort = w.substring(5, w.length() - 5); // skip ###### 
                news.setLength(0); count++;
                msg(fshort);
                try {
                    w = fin.readLine();
                    while (w != null && !w.startsWith("#####")) {
                        news.append(w);
                        news.append(" ");
                        w = fin.readLine();
                    }
                } catch (Exception e) {
                    System.err.println("IO error during read  file:" + w);
                    e.printStackTrace();
                }
             


        }  // while
        fin.close();
    }
    catch



        (IOException e) {
            error("IO error", e);
    }
}

}


