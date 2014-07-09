package isi.jg.cat.clefip2010;

import isi.jg.idxvli.IdxStructure;
import java.io.*;
import java.util.regex.Pattern;
import static isi.jg.util.Messages.*;

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
public class CreateReleavanceFile {

    private static Pattern p = Pattern.compile("\\s");  // les fins de mots
private static IdxStructure id;

    public static void main(String[] args) {

       id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();

        msg("init  ...");
        changeformat("Z:/CLEFIP10/CAT/Clefip2010-IPC-TEST.cat",
                "Z:/CLEFIP10/TASK/relevantnum.txt",
                 "Z:/CLEFIP10/TASK/relevant.txt"
 );
        msg("end ...");
    }

    private static void changeformat(String f, String fo, String fo2) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader fin = new BufferedReader(isr, 1000000);
            OutputStreamWriter result = new OutputStreamWriter(new FileOutputStream(fo));
            OutputStreamWriter result2 = new OutputStreamWriter(new FileOutputStream(fo2));
            msg(f + ":open in: " );
            String w= fin.readLine();
                while (w != null && !w.equals("")) {
                    //msg(w);
                    String[] seg=p.split(w);
                    int docnum=id.getIntForDocument(seg[0]);
                    for (int i=1;i<seg.length;i++){
                    result.write(docnum+" 0 "+seg[i]+" 1\n");
                   
                    }
                   result2.write(seg[0]+"\n");
                   w = fin.readLine();
                }



        fin.close();
        result.close();
        result2.close();
    }
    catch




        (IOException e) {
            error("IO error", e);
    }
}

}
