/*
 * FileTest.java
 *
 * Created on 16. janvier 2006, 17:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.apache.tools.bzip2;
import java.io.*;

/**
 *
 * @author Jacques.Guyot
 */
public class bz2totxt {
    
    private static final int BUFFER_SIZE = 1024*1024; // 1 Méga
    private static final String BZIP2_HEADER = "BZ";
    
    public static void main(String[] args) {
        getFromDirectory("c:/tempo");
    }
    
    static void getFromDirectory(String pathName){
        File f = new File(pathName);
        if (f.isFile()) {
            if (pathName.endsWith(".bz2")){  // un bzip2
                String[] arg=new String [2];
                decompress(pathName,pathName.substring(0,pathName.length()-8)+".txt",2 );
            } else {
                //pas un bzip2
            }
        } else {
            System.out.println("indexdir:"+pathName);
            String[] lf = f.list();
            int ilf = lf.length;
            for (int i = 0; i < ilf; i++)
                getFromDirectory(pathName + "/" + lf[i]);
        }
    }
    
    public static void decompress(String fileName, String outFileName, int skip) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
            //System.out.println("Skipping " + in.read(new byte[skip]) +" bytes.");
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFileName));
            if (in != null) {
                CBZip2InputStream doc = new CBZip2InputStream(in);
                byte[] buffer = new byte[BUFFER_SIZE];
                int nbBytes = 0;
                nbBytes = doc.read(buffer);
                while (nbBytes > 0) {
                    out.write(buffer, 0, nbBytes);
                    nbBytes = doc.read(buffer);
                }
                in.close();
                out.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
