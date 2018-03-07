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
import org.olanto.idxvli.DoParse;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;

public class EU_corpusNgramBuild {      // is an application, not an applet !

    static Timer t1 = new Timer("global time");
    static final String mflfFile = "C:\\MYCLASS_MODEL\\sample\\langdetect\\corpus_dgt2014.mflf 100000";
    static final String mfldNgram = "C:\\MYCLASS_MODEL\\sample\\langdetectngram\\corpus_5dgt2014_Ngram.mflf";
    private static IdxStructure id;  // only to have a parser ...

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationForCat());
        Word2Ngram(5, "UTF-8");

    }

    public static void Word2Ngram(int lenngram, String txt_encoding) {

        System.out.println("------------- corpus: " + mflfFile);
        int totread = 0;

        try {
            InputStreamReader isrso = new InputStreamReader(new FileInputStream(mflfFile), txt_encoding);
            OutputStreamWriter outmflf = new OutputStreamWriter(new FileOutputStream(mfldNgram), txt_encoding);

            BufferedReader so = new BufferedReader(isrso);
            String wso = so.readLine();
            while (wso != null) {
                if (wso.startsWith("#####")) {
                    totread++;
                    outmflf.append(wso + "\n");
                } else {
                    StringBuilder news = new StringBuilder("");
                    StringBuilder ngramsentence = new StringBuilder("");
                    DoParse a = new DoParse(wso, id.dontIndexThis);

                    String[] seq = a.tokenizeString(id); // get
                    news.append("_" + seq[0] + "_");
                    for (int i = 1; i < seq.length; i++) {
                        news.append(seq[i] + "_");

                    }
                    // alway bigger than lenngram
                    for (int i = 0; i < (news.length() - lenngram); i++) {
                        ngramsentence.append(news.substring(i, i + lenngram) + " ");
                    }

//                    System.out.println(news);
//                    System.out.println(ngramsentence);
//                    System.out.println(news);
//                    System.out.println("---");

                    outmflf.append(ngramsentence.toString() + "\n");
                }

                wso = so.readLine();
            }
            so.close();
            outmflf.close();
            System.out.println("   read entries: " + totread);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
