/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isi.jg.deploy.gamma;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class countDieze {

    public static void main(String[] args) {
        InputStreamReader isr = null;
        Hashtable<String,String> h=new Hashtable<String,String>(2000000);
        try {
           // isr = new InputStreamReader(new FileInputStream("Z:/IPCCAT/French/French-IATABSDESC.mflf"), "UTF-8");
            isr = new InputStreamReader(new FileInputStream("Z:/IPCCAT/en-IATABSDESC.mflf"), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String w;
            w = in.readLine();
            int countstart = 0;
            int countend = 0;
            int small = 0;
            int currentline = 1;
            int size = 0;
            while (w != null) {
                //System.out.println("in news:"+w);
                if (w.startsWith("#####")) {
                    //System.out.println(size);
//                    if (size < 1000) {
//                        small++;
//                        System.out.println(size + " " + w + "-" + currentline);
//                    }
//                    size = 0;
                    String name=w.substring(5, w.length() - 5);
                    h.put(name,"ok");
                    countstart++;
                    //if (countstart!=h.size())System.out.println(  w );
                    
                } 
//                else {
//                    size += w.length();
//                }
               if (countstart % 1000 == 0) {
                    System.out.println(countstart + "-" + countend + " - "+ h.size());
                }
                w = in.readLine();
                currentline++;
            }
            System.out.println(countstart + "-" + countend + "-" +small+ " - "+ h.size());
        } catch (Exception ex) {
            Logger.getLogger(countDieze.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(countDieze.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
