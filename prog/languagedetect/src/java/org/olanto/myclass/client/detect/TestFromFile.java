/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.myclass.client.detect;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *
 * @author olanto.org J. Guyot
 */
public class TestFromFile {


    static DectectCallWS client;

    public static void main(String[] args) {
        // prepare call to WebService
      //  DectectCallWS.BASE_URI = "http://localhost:8080/olanto/detect";
       DectectCallWS.BASE_URI = "http://192.168.1.32:8080/languagedetect/detect";
       client = new DectectCallWS();
        System.out.println("WS REST Url :" + DectectCallWS.BASE_URI);

      file2classify("C:/MYCLASSREST/FRENCHTEST/frenchSample/FR.cat.val.final50max50.sample10000.mflf");
       // file2classify("C:/MYCLASSREST/FRENCHTEST/frenchSample/FR.cat.val.final50max50.sample1000.mflf");

    }

    public static void file2classify(String fname) {
        System.out.println("load  from:" + fname);
        String w = "";
        int count=0;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            w = in.readLine();  // assume patent are on 2 lines
            while (w != null) {
                count++;

                String patnumer = w;
                if (!w.startsWith("#####")) {
                    System.out.println("error in file" + w);
                }
                w = in.readLine(); // patent text
                w= w.substring(0, Math.min(w.length(), 3000));
               // String submit = paramXML.replace("$$$$$", w);
              // String response = client.postXml(submit);
                  String response = client.getLanguage(w);
  //              System.out.println("\n\npost call Response:\n\n" + response);

                String p1 = extract(response, "<rank>1</rank><category>", "</category>");
                String p2 = extract(response, "<rank>2</rank><category>", "</category>");
                String p3 = extract(response, "<rank>3</rank><category>", "</category>");
                System.out.println(count+"\t"+patnumer + "\t" + p1 + "\t" + p2 + "\t" + p3 + "\t");

                w = in.readLine();

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("IO error in file2classify " + w);
        }
    }

    public static String extract(String response, String start, String stop) {
        int posstart = response.indexOf(start, 0);
        if (posstart == -1) {
            return "ERROR";
        }
        int posstop = response.indexOf(stop, posstart);
        if (posstop == -1) {
            return "ERROR";
        }
        return response.substring(posstart + start.length(), posstop);
    }
}
