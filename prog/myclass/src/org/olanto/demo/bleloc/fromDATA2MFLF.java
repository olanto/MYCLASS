/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.demo.bleloc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simple
 */
public class fromDATA2MFLF {

    public static void main(String[] args) {
        convert("C:/SIMPLE_CLASS/sample/bleloc/classdata.txt");
    }

    public static void convert(String filename) {
        InputStreamReader data = null;
        BufferedWriter outmflf = null;
        BufferedWriter outcat = null;
        try {
            data = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader in = new BufferedReader(data);
            outmflf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename + ".mflf"), "UTF-8"));
            outcat = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename + ".cat"), "UTF-8"));

            String w = in.readLine();

            int count = 0;
            while (w != null) {
                String[] part = w.split("\t");
                System.out.println(part[0] + "|" + part[1] + "|" + part[2] + "|" + part[3] + "|");
                String header = "#####" + part[0] + "-" + part[1] + "#####";
                String cat = String.format("%7s", part[0]).replace(' ', '-');
                String catinfo = part[0] + "-" + part[1] + "\t" + cat;
                System.out.println(catinfo);
                outmflf.append(header + "\n");

    
                String content = crossValue(part);
               // content+="\n"+includeValue(part);
//                String content includeValue(part);
                //             String content=simpleValue(part);
                outmflf.append(content + "\n");
                outcat.append(catinfo + "\n");
                w = in.readLine();
            }
            outmflf.close();
            outcat.close();
        } catch (IOException ex) {
            Logger.getLogger(fromDATA2MFLF.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                data.close();
            } catch (IOException ex) {
                Logger.getLogger(fromDATA2MFLF.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                outmflf.close();
            } catch (IOException ex) {
                Logger.getLogger(fromDATA2MFLF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    static String includeValue(String[] part) {
        int[] val = new int[6];
        for (int i = 2; i < 6; i++) {
            val[i] = Integer.parseInt(part[i]);
        }
        String result = "";
        for (int i = 0; i <= val[2]; i += 5) {
            result += "r100v" + i + " ";
        }
        for (int i = 0; i <= val[3]; i += 5) {
            result += "r101v" + i + " ";
        }
        for (int i = 0; i <= val[4]; i += 5) {
            result += "r102v" + i + " ";
        }
        for (int i = 0; i <= val[5]; i += 5) {
            result += "r103v" + i + " ";
        }

        return result;
    }

    static String crossValue(String[] part) {
        int[] val = new int[6];
        for (int i = 2; i < 6; i++) {
            val[i] = Integer.parseInt(part[i]);
        }
        String result = "";
        for (int k = 2; k < 6; k++) {
            for (int i = 0; i <= val[k]; i += 5) {
                for (int j = k + 1; j < 6; j++) {
                    for (int n = 0; n <= val[j]; n += 5) {
                        result += "r" + (98 + k) + "v" + i + "r" + (98 + j) + "v" + n + " ";
                    }
                }
            }
        }


        return result;
    }

    static String simpleValue(String[] part) {
        return "r100v" + part[2] + " "
                + "r101v" + part[3] + " "
                + "r102v" + part[4] + " "
                + "r103v" + part[5] + "\n";
    }
}
