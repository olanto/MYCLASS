/*
 * Created on 6 mai 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.tools.bzip2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author mario
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Test_String_OnlyCount {
    static byte[] buffer;
    static String s;
    
    public static void main(String[] args) {
                    s="ceci est texte à compresser, ceci est texte à compresser, ceci est texte à compresser, ceci est texte à compresser, ";
            for (int i=0;i<5;i++)s=s+s;
             buffer= s.getBytes();
        System.out.println("length to be compressed = "+buffer.length);
        long start = System.currentTimeMillis();
        for (int i=0;i<100;i++){           
            compress();           
        }
        System.out.println("Total time = "+ (System.currentTimeMillis() - start) +" ms");
    }
    
    
    /**
     * @param string
     * @param i
     */
    private static void compress() {
        try {
             long start = System.currentTimeMillis();
            
            CBZip2OnlyCount doc = new CBZip2OnlyCount();
            int res=doc.getLength(buffer, 0, buffer.length);
            System.out.println("length:"+res);
            
            System.out.println("Total time = "+ (System.currentTimeMillis() - start) +" ms");
            
        } catch (Exception e) {
            System.out.println("error in compress");
        }
    }
    
    
}
