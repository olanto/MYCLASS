package isi.jg.deploy.frende;

import java.rmi.*;
import java.io.*;
import isi.jg.conman.server.*;
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
public class reclassifyCorpus {

    public static void main(String[] args) {


            msg("init clssifier ...");
            Classify.init();
                 classifyAmflf("Y:/CLEFIP/CORPUS/CLEFIP-ATABSCML-FRENDE.mflf",
                         "Y:/CLEFIP/catalog/clefip_reclass.txt"
                         ,"UTF-8"
                         );
            msg("end ...");
    }

    private static void classifyAmflf(String f,String fo, String IDX_MFL_ENCODING) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f), IDX_MFL_ENCODING);
            BufferedReader fin = new BufferedReader(isr, 1000000);
            OutputStreamWriter result=new OutputStreamWriter(new FileOutputStream(fo), IDX_MFL_ENCODING);
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
            int count=0;
            while (w != null) {  // repeat for all documents in this file
                if (count%1000==0){msg("count:"+count);}
                count++;
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
                String classnew=Classify.adviseSimple(news.toString(),4);
                result.write(fshort+classnew+"\n");
                //msg (fshort+classnew);
            }  // while
            fin.close();
        } catch (IOException e) {
            error("IO error", e);
        }
    }
}
