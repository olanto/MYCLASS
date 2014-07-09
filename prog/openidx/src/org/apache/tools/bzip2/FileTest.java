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
public class FileTest {
    
    /** Creates a new instance of FileTest */
    public FileTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // test compression
//        String[] arg=new String [3];
//        arg[0]="c";
//        arg[1]="c:/test.txt";
//        arg[2]="c:/test.txt.bz";
        // test decompression
        getFromDirectory("p:/clef2008");
        
        
    }
    
        static void getFromDirectory(String pathName){
        File f = new File(pathName);
        if (f.isFile()) {
                if (pathName.endsWith(".bz2")){  // un bzip2
          String[] arg=new String [2];
        arg[0]=pathName;
        arg[1]=pathName.substring(0,pathName.length()-8)+".txt";         
        Test.main(arg);
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

    
}
