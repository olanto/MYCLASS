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
public class fromDATA2MFLF11raspi {

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
                String header = "#####" + part[0] + "-" + part[1]+ "-" + part[2] + "#####";
                String cat = String.format("%7s", part[1]).replace(' ', '-');
                String catinfo = part[0] + "-" + part[1] + "-" + part[2] + "\t" + cat;
                System.out.println(catinfo);
                outmflf.append(header + "\n");


               // String content = cross3Value(part);
                String content = crossValue(part);
                //             content+="\n"+includeValue(part);  // finalement n'ajoute rien ...
                //              String content=includeValue(part);
                 //                          String content=simpleValue(part);
                outmflf.append(content + "\n");
                outcat.append(catinfo + "\n");
                w = in.readLine();
            }
            outmflf.close();
            outcat.close();
        } catch (IOException ex) {
            Logger.getLogger(fromDATA2MFLF11raspi.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                data.close();
            } catch (IOException ex) {
                Logger.getLogger(fromDATA2MFLF11raspi.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                outmflf.close();
            } catch (IOException ex) {
                Logger.getLogger(fromDATA2MFLF11raspi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    static String includeValue(String[] part) {
        int[] val = new int[14];
        for (int i = 3; i < 14; i++) {
            val[i] = Integer.parseInt(part[i]);
        }
        String result = "";
        for (int i = 0; i <= val[3]; i += 5) {
            result += "r100v" + i + " ";
        }
        for (int i = 0; i <= val[4]; i += 5) {
            result += "r101v" + i + " ";
        }
        for (int i = 0; i <= val[5]; i += 5) {
            result += "r102v" + i + " ";
        }
        for (int i = 0; i <= val[6]; i += 5) {
            result += "r103v" + i + " ";
        }
        for (int i = 0; i <= val[7]; i += 5) {
            result += "r104v" + i + " ";
        }
        for (int i = 0; i <= val[8]; i += 5) {
            result += "r105v" + i + " ";
        }
        for (int i = 0; i <= val[9]; i += 5) {
            result += "r106v" + i + " ";
        }
        for (int i = 0; i <= val[10]; i += 5) {
            result += "r107v" + i + " ";
        }
        for (int i = 0; i <= val[11]; i += 5) {
            result += "r108v" + i + " ";
        }
       for (int i = 0; i <= val[12]; i += 5) {
            result += "r109v" + i + " ";
        }
       for (int i = 0; i <= val[13]; i += 5) {
            result += "r110v" + i + " ";
        }

        return result;
    }

    static String crossValue(String[] part) {
        int[] val = new int[14];
        for (int i = 3; i < 14; i++) {
            val[i] = Integer.parseInt(part[i]);
        }
        int step = 3;
        StringBuilder result = new StringBuilder("");
        for (int k = 3; k < 14; k++) {
            for (int i = 0; i <= val[k]; i += step) {
                for (int j = k + 1; j < 14; j++) {
                    for (int n = 0; n <= val[j]; n += step) {
                        result.append("r" + (97 + k) + "v" + i + "r" + (97 + j) + "v" + n + " ");
                    }
                }
            }
        }
        return result.toString();
    }
    

    static String cross3Value(String[] part) {
        int[] val = new int[14];
        for (int i = 3; i < 14; i++) {
            val[i] = Integer.parseInt(part[i]);
        }
        int step = 5;
        StringBuilder result = new StringBuilder("");
        for (int k = 3; k < 14; k++) {
            for (int i = 0; i <= val[k]; i += step) {
                for (int j = k + 1; j < 14; j++) {
                    for (int n = 0; n <= val[j]; n += step) {
                         for (int p = j + 1; p < 14; p++) {
                            for (int q = 0; q <= val[p]; q += step) {
                                result.append("r" + (97 + k) + "v" + i + "r" + (97 + j) + "v" + n + "r" + (97 + p) + "v" + q + " ");
                            }
                        }
                    }
                }
            }
        }



        return result.toString();
    }

    static String simpleValue(String[] part) {
        return "r100v" + part[3] + " "
                + "r101v" + part[4] + " "
                + "r102v" + part[5] + " "
                + "r103v" + part[6] + " "
                + "r104v" + part[7] + " "
                + "r105v" + part[8] + " "
                + "r106v" + part[9] + " "
                + "r107v" + part[10] + " "
                + "r108v" + part[11] + " "
                + "r109v" + part[12] + " "
                + "r110v" + part[13] +
                "\n";
    }
}
