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
public class Test_String {
    private static final int BUFFER_SIZE = 1024*1024; // 1 Méga
    private static final String BZIP2_HEADER = "BZ";
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
                compress("c:/ajeter.rar");
          System.out.println("Total time = "+ (System.currentTimeMillis() - start) +" ms");
    }
    
    
    /**
     * @param string
     * @param i
     */
    private static void compress(String outFileName) {
        BufferedOutputStream out = null;
        try {
            String s="ceci est texte à compresser, ceci est texte à compresser, ceci est texte à compresser, ceci est texte à compresser, ";
            byte[] buffer = s.getBytes();
            out = new BufferedOutputStream(new FileOutputStream(outFileName));
            out.write(BZIP2_HEADER.getBytes());
      
                CBZip2OutputStream doc = null;
                doc = new CBZip2OutputStream(out);

                    doc.write(buffer, 0, buffer.length);
                    System.out.println("compress:"+doc.bytesOut);
                    System.out.println("bsLive:"+doc.bsLive);

                doc.flush();
                System.out.println("compress:"+doc.bytesOut);
                doc.close();
                System.out.println("compress:"+doc.bytesOut);
                out.close();
           
        } catch (Exception e) {
            System.out.println("error in compress");
        }
    }
 
 
}
