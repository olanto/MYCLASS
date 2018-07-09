/**
 * ********
 * Copyright © 2003-2018 Olanto Foundation Geneva
 *
 * This file is part of myCLASS.
 *
 * myCLASS is free software: you can redistribute it and/or modify it under the
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
package org.olanto.myclass.core.detect;

import org.olanto.idxvli.IdxStructure;
import org.olanto.idxvli.DoParse;
import org.olanto.cat.mnn.Categorizer;
import org.olanto.cat.mnn.Guess;
import org.olanto.cat.util.SenseOS;
import org.olanto.util.Timer;

/**
 * *
 * @author Jacques Guyot copyright Jacques Guyot 2009
 *
 * Test du catégoriseur
 */
class ClassifierRequest {

    int[] docbag;
    int nbchoice;

    ClassifierRequest(int[] _docbag, int _nbchoice) {
        docbag = _docbag;
        nbchoice = _nbchoice;
    }
}

public class Classify {

    static Categorizer MM;
    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     *
     * @param args sans
     */
    public static void main(String[] args) {

        String test="Concerning the conditions and arrangements for admission of the republic of Bulgaria and Romania to the European Union.";

        
        init();
        System.out.println(test+" -->"+Sentence2Ngram(test, 5));
        
        
        System.out.println(advise(test));
       System.out.println(advise(2,test));

    }

public static String advise(String p) {
     return  advise(3,p);
    }
    
    public static String advise(int nbPrediction,String p) {

        init();
        ClassifierRequest rq = parse(Sentence2Ngram(p, 5) + ".",""+ nbPrediction);
        return guess(rq, "", "2");

    }

    public static String Sentence2Ngram(String w, int lenngram) {

        StringBuilder news = new StringBuilder("");
        StringBuilder ngramsentence = new StringBuilder("");
        DoParse a = new DoParse(w, id.dontIndexThis);

        String[] seq = a.tokenizeString(id); // get
        news.append("_" + seq[0] + "_");
        for (int i = 1; i < seq.length; i++) {
            news.append(seq[i] + "_");

        }
        // alway bigger than lenngram
        if (news.length()<=lenngram) return news.toString();
        for (int i = 0; i < (news.length() - lenngram); i++) {
            ngramsentence.append(news.substring(i, i + lenngram) + " ");
        }
        return ngramsentence.toString();
    }

    public static ClassifierRequest parse(String s, String nbguess) {

        DoParse a = new DoParse(s, id.dontIndexThis);
        int[] requestDB = a.getDocBag(id); // get the docBag od the request
        ClassifierRequest rq = new ClassifierRequest(requestDB, Integer.parseInt(nbguess));
        return rq;
    }

    public static String guess(ClassifierRequest rq, String from, String to) {
        String res = "";
        Guess[] guess = MM.classifyDocumentAndPonderate(from, to, rq.docbag, rq.nbchoice);
        if (guess == null) {
            res += "<msg>No result</msg>\n";
        } else {
            res += "<msg>ok</msg>\n";
            for (int i = 0; i < guess.length; i++) {
                res += "<prediction>\n"
                        + "    <rank>" + (i + 1) + "</rank>"
                        + "<category>" + guess[i].categorie + "</category>"
                        + "<score>" + (int) guess[i].weight + "</score>\n"
                        + "</prediction>\n";
            }
        }
        return res;
    }  
    

    public static void init() {  // init class
        if (id == null) {
            System.out.println("MYCLASS_ROOT: "+SenseOS.getMYCLASS_ROOT());
            id = new IdxStructure("QUERY", new org.olanto.demo.langdetection.ConfigurationForCat());
            id.Statistic.global();
            String nnfile = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/data/langdetectngram/mnn/langdetectngram.mnn";
            MM = new Categorizer(nnfile,
                    true, // chargement du réseau complet
                    22, // maxclass
                    1000, // cache size
                    16, // start level
                    4 // free cache
                    );
        }

    }
}