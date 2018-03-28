/**
 * ********
 * Copyright © 2010-2014 Olanto Foundation Geneva
 *
 * This file is part of myCAT.
 *
 * myCAT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.demo.langdetect_word;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.olanto.util.Timer;

public class EU_corpusWordBuild {      // is an application, not an applet !

    static Timer t1 = new Timer("global time");
    static final String mflfFile="C:\\MYCLASS_MODEL\\sample\\langdetect\\corpus_dgt2014.mflf 100000";
   static final String catFile="C:\\MYCLASS_MODEL\\sample\\langdetectngram\\corpus_5dgt2014.cat";

   /**
    * 
lang	#sentences	#sentences >50
BG	2291508	100000
CS	4133468	100000
DA	3673367	100000
DE	3702172	100000
EL	3520392	100000
EN	5897003	100000
ES	3672342	100000
ET	4157404	100000
FI	3635751	100000
FR	4173704	100000
GA	44309	32011
HU	4289015	100000
IT	3710917	100000
LT	4215587	100000
LV	4221833	100000
MT	2592797	100000
NL	3648794	100000
PL	4161042	100000
PT	4093752	100000
RO	2251151	100000
SH	276502	100000
SK	4216071	100000
SL	4184398	100000
SV	3739451	100000
    */
   
   
    public static void main(String[] args) {

           indexThis("C1", "C:/CORPUS/DGT2014/DGT2014.24x", Integer.MAX_VALUE,50,100000, "UTF-8");
        //indexThis("C1", "C:/CORPUS/DGT2014/DGT2014.24x", 100000, 50, 100000,"UTF-8");

    }

    public static void indexThis(String name, String fileso, int limit, int minlen, int maxcollect, String txt_encoding) {
        LangMap.init("BG " + "CS " + "DA " + "DE " + "EL " + "EN " + "ES " + "ET " + "FI " + "FR " + "GA " + "HU " + "IT " + "LT " + "LV " + "MT " + "NL " + "PL " + "PT " + "RO " + "SH " + "SK " + "SL " + "SV");

        int[] stat = new int[24];
        int[] currentID = new int[24];
        System.out.println("------------- index corpus: " + fileso);
        int totread = 0;
        try {
            InputStreamReader isrso = new InputStreamReader(new FileInputStream(fileso), txt_encoding);
            OutputStreamWriter outmflf = new OutputStreamWriter(new FileOutputStream(mflfFile), "UTF-8");
            OutputStreamWriter outcat = new OutputStreamWriter(new FileOutputStream(catFile), "UTF-8");

            BufferedReader so = new BufferedReader(isrso);
            String wso = so.readLine();
            while (wso != null && totread < limit) {
                totread++;
                if (totread % 100000 == 0) {
                    System.out.println(totread);
                }
                String[] x24 = wso.split("\t");
                if (x24.length == 24) {
                    for (int i = 0; i < 24; i++) {
                        if (!x24[i].equals(".")) {
                            stat[i]++;
                            if (x24[i].length() > minlen && currentID[i]<maxcollect) { 
                                currentID[i]++;
                                outcat.append(LangMap.getlang(i)+currentID[i]+"\t"+LangMap.getlang(i)+"\n");
                                outmflf.append("#####"+LangMap.getlang(i)+currentID[i]+"#####\n");
                                outmflf.append(x24[i]+".\n");
                            }
                        }
                    }
                } else {
                    System.out.println("  erronr X24 " + wso);
                }
                wso = so.readLine();
            }
            so.close();outcat.close();outmflf.close();
            System.out.println("   read entries: " + totread);
            System.out.println("lang" + "\t" + "#sentences" + "\t" + "#sentences >" + minlen);
            for (int i = 0; i < 24; i++) {
                System.out.println(LangMap.getlang(i) + "\t" + stat[i] + "\t" + currentID[i]);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
