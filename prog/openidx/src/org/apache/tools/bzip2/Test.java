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
public class Test {
    private static final int BUFFER_SIZE = 1024*1024; // 1 Méga
    private static final String BZIP2_HEADER = "BZ";
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        if (args.length == 0)
            printUsage();
        else {
            boolean bCompressionMode = (args[0].equals("c"));
            if (bCompressionMode) {
                info("*** Compression Mode");
                compress(args[1], args[2]);
            } else {
                //info("Decompression Mode");
                decompress(args[0], args[1], BZIP2_HEADER.length());
               //decompressInternal(args[0], args[1], BZIP2_HEADER.length());
               // decompressAndShow(args[0], args[1], BZIP2_HEADER.length());
            }
        }
        //info("Total time = "+ (System.currentTimeMillis() - start) +" ms");
    }
    
    private static void printUsage() {
        info("Usage: Test [c] input_file output_file");
        info(" c   : Compression mode");
    }
    
    /**
     * @param string
     * @param i
     */
    private static void compress(String fileName, String outFileName) {
        BufferedOutputStream out = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
            out = new BufferedOutputStream(new FileOutputStream(outFileName));
            out.write(BZIP2_HEADER.getBytes());
            if (in != null) {
                CBZip2OutputStream doc = null;
                doc = new CBZip2OutputStream(out);
                byte[] buffer = new byte[BUFFER_SIZE];
                int nbBytes = 0;
                nbBytes = in.read(buffer);
                
                while (nbBytes > 0) {
                    doc.write(buffer, 0, nbBytes);
                    nbBytes = in.read(buffer);
                }
                doc.flush();
                doc.close();
                in.close();
                out.close();
            }
        } catch (Exception e) {
            error(e.getMessage());
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    error(e1.getMessage());
                }
            }
            (new File(outFileName)).delete();
        }
    }
 
    public static void decompressAndShow(String fileName, String outFileName, int skip) {
        long nb=-1;
                long start = System.currentTimeMillis();
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
            //info("Skipping " + in.read(new byte[skip]) +" bytes.");
            if (in != null) {
                CBZip2InputStream doc = new CBZip2InputStream(in);
                byte[] buffer = new byte[BUFFER_SIZE];
                int nbBytes = 0;
                nbBytes = doc.read(buffer);
                while (nbBytes > 0) {
                    nb++;
                   // info(""+nb);
                    info(new String(buffer));
                     nbBytes = doc.read(buffer);
                     if (nb==0){
                         break;
                     }
                }
                in.close();
            }
        } catch (FileNotFoundException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    /**
     * un test de rapidité
     */
    public static void decompressInternal(String fileName, String outFileName, int skip) {
        long nb=-1;
                long start = System.currentTimeMillis();
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
            info("Skipping " + in.read(new byte[skip]) +" bytes.");
            if (in != null) {
                CBZip2InputStream doc = new CBZip2InputStream(in);
                byte[] buffer = new byte[BUFFER_SIZE];
                int nbBytes = 0;
                nbBytes = doc.read(buffer);
                while (nbBytes > 0) {
                    nb++;
                   // info(""+nb);
                     nbBytes = doc.read(buffer);
                     if (nb%10==0){
                         info("time = "+ (System.currentTimeMillis() - start) +" ms");
                         start = System.currentTimeMillis();
                     }
                }
                in.close();
            }
        } catch (FileNotFoundException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

      public static void decompress(String fileName, String outFileName, int skip) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
           info("Skipping " + in.read(new byte[skip]) +" bytes.");
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
            error(e.getMessage());
        } catch (IOException e) {
            error(e.getMessage());
        }
    }
  
    
    private static void error(String msg) {
        System.err.println("[ERROR] "+msg);
    }
    
    private static void info(String msg) {
        System.err.println("[INFO ] "+msg);
    }
}
