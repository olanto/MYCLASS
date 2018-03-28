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
import java.io.InputStreamReader;
import org.olanto.util.Timer;

/**
   read entries: 5897003
0	2291508
1	4133468
2	3673367
3	3702172
4	3520392
5	5897003
6	3672342
7	4157404
8	3635751
9	4173704
10	44309
11	4289015
12	3710917
13	4215587
14	4221833
15	2592797
16	3648794
17	4161042
18	4093752
19	2251151
20	276502
21	4216071
22	4184398
23	3739451

 */
public class Statistic_DGT_2014_X241 {      // is an application, not an applet !

    static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        indexThis("C1", "C:/CORPUS/DGT2014/DGT2014.24x", Integer.MAX_VALUE,50, "UTF-8");
     //   indexThis("C1", "C:/CORPUS/DGT2014/DGT2014.24x", 100000, 50, "UTF-8");

    }

    public static void indexThis(String name, String fileso, int limit, int minlen, String txt_encoding) {
        LangMap.init("BG "+"CS "+"DA "+"DE "+"EL "+"EN "+"ES "+"ET "+"FI "+"FR "+"GA "+"HU "+"IT "+"LT "+"LV "+"MT "+"NL "+"PL "+"PT "+"RO "+"SH "+"SK "+"SL "+"SV");
        
        int[] stat = new int[24];
        int[] statminlen = new int[24];
        System.out.println("------------- index corpus: " + fileso);
        int totread = 0;
        try {
            InputStreamReader isrso = new InputStreamReader(new FileInputStream(fileso), txt_encoding);
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
                            if(x24[i].length()>minlen)statminlen[i]++;
                        }
                    }
                } else {
                    System.out.println("  erronr X24 " + wso);
                }
                wso = so.readLine();
            }
            so.close();
            System.out.println("   read entries: " + totread);
            System.out.println("lang" + "\t" + "#sentences" + "\t" + "#sentences >"+ minlen );
            for (int i = 0; i < 24; i++) {
                System.out.println(LangMap.getlang(i) + "\t" + stat[i] +"\t" + statminlen[i]);
            }

            
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
