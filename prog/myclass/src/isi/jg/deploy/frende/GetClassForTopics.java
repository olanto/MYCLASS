/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package isi.jg.deploy.frende;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import static org.olanto.util.Messages.*;
/**
 *
 * @author jg
 */
public class GetClassForTopics {
    
    static String patentName;

       public static void main(String[] args)   {
             Classify.init();

        String dir="Y:/CLEFIP/Task/Main-XLarge";
        ScanDir(dir);
    }

    private static void getClass(String toBeClassify) {
         String txt=readTopic(toBeClassify);
        msg(patentName+Classify.adviseSimple(txt,4));
    }

       public static void ScanDir(String path) {
           String EXT=".xml";
        File f = new File(path);
        if (f.isFile()) {
            //System.out.println("path:"+f);
            if (path.endsWith(EXT)) {
                String[] res=f.getName().split("_");
                patentName=res[1];
                getClass(path);
            }
        } else {
            String[] lf = f.list();
            int ilf = Array.getLength(lf);
            for (int i = 0; i < ilf; i++) {
                ScanDir(path + "/" + lf[i]);
            }
        }
    }

    
    private static String readTopic(String fname) {
        //msg("load topic");
        String result = "";
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                result += w;
                w = in.readLine();
            }
        } catch (Exception e) {
            error("IO error in readList", e);
        }
        return result;
    }
}
