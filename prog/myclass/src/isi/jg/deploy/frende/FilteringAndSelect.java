package isi.jg.deploy.frende;

import java.io.*;
import static org.olanto.util.Messages.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class FilteringAndSelect {

    static String goal = "A24";
    static float level = 1300;
    static int minLength = 1;

    public static void main(String[] args) {


        msg("init clssifier ...");
        Classify.init();
        classifyAmflf(SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/clef/clef_EN.mflf",
                SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/clef/clef_select_" + goal + ".txt", "ISO-8859-1");
        msg("end ...");
    }

    private static void classifyAmflf(String f, String fo, String IDX_MFL_ENCODING) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f), IDX_MFL_ENCODING);
            BufferedReader fin = new BufferedReader(isr, 1000000);
            OutputStreamWriter result = new OutputStreamWriter(new FileOutputStream(fo), IDX_MFL_ENCODING);
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
                news.setLength(0);
                try {
                    w = fin.readLine();
                    while (w != null && !w.startsWith("#####")) {
                        news.append(w);
                        news.append("\n");
                        w = fin.readLine();
                    }
                } catch (Exception e) {
                    System.err.println("IO error during read  file:" + w);
                    e.printStackTrace();
                }
                msg (fshort+""+Classify.advise3(news.toString()));
                if (Classify.filtering(news.toString(), goal, level)) {
                    if (news.length() > minLength) {
                        try {
                            result.write("-----------" + fshort + "\tlength:" + news.length() + "\t" + Classify.advise3(news.toString()) + "\n" + news.toString() + "\n");
                        } catch (IOException ex) {
                            Logger.getLogger(FilteringAndSelect.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        System.out.println(fshort + "\tlength:" + news.length() + "\t" + Classify.advise3(news.toString()));
                    }
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
