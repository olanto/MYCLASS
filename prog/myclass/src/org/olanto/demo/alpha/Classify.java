/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myCLASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.demo.alpha;

import org.olanto.idxvli.IdxStructure;
import org.olanto.idxvli.DoParse;
import org.olanto.cat.mnn.Categorizer;
import org.olanto.cat.mnn.Guess;
import org.olanto.util.Timer;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 *
 * Test du catégoriseur
 */
class ClassifierRequest {

    int[] docbag;
    int nbchoice;
    String language;

    ClassifierRequest(int[] _docbag, int _nbchoice, String _language) {
        docbag = _docbag;
        nbchoice = _nbchoice;
        language = _language;
    }
}

public class Classify {

    static Categorizer MM;
    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static String advise(String p) {

        init();
        ClassifierRequest rq = parse(p, "3", "EN");
        return guess(rq, "", "4");

    }

    public static String advise3(String p) {

        init();
        ClassifierRequest rq = parse(p, "1", "EN");
        return guessSimple(rq, "", "3");

    }

    public static boolean filtering(String p, String goal, float level) {

        init();
        ClassifierRequest rq = parse(p, "1", "EN");
        return filtering(rq, "", "3", goal, level);

    }

    public static ClassifierRequest parse(String s, String nbguess, String lang) {
        //System.out.println("ok Classifier.parse");
        DoParse a = new DoParse(s, id.dontIndexThis);
        int[] requestDB = a.getDocBag(id); // get the docBag od the request
        ClassifierRequest rq = new ClassifierRequest(requestDB, Integer.parseInt(nbguess), lang);
        return rq;
    }

    public static String guess(ClassifierRequest rq, String from, String to) {
        String res = "\nok Classifier.guess";
        Guess[] guess = MM.classifyDocumentAndPonderate(from, to, rq.docbag, rq.nbchoice);
        if (guess == null) {
            res += "\nNo guess !";
        } else {
            for (int i = 0; i < guess.length; i++) {
                res += "\n  choice:" + (i + 1) + ", " + guess[i].categorie + ", " + guess[i].weight;
            }
        }
        return res;
    }

    public static String guessSimple(ClassifierRequest rq, String from, String to) {
        String res = "";
        Guess[] guess = MM.classifyDocumentAndPonderate(from, to, rq.docbag, rq.nbchoice);
        if (guess == null) {
            res = "No guess !";
        } else {
            for (int i = 0; i < guess.length; i++) {
                res += "\t  choice:" + (i + 1) + "\t  " + guess[i].categorie + "\t  " + guess[i].weight;
            }
        }
        return res;
    }

    public static boolean filtering(ClassifierRequest rq, String from, String to,
            String goal, float level) {
        String res = "";
        Guess[] guess = MM.classifyDocumentAndPonderate(from, to, rq.docbag, rq.nbchoice);
        if (guess == null || guess.length < 1) {
            return false;
        } else {
            if (guess[0].categorie.equals(goal) && guess[0].weight > level) {
                return true;
            }
        }
        return false;
    }

    public static void init() {  // init class
        if (id == null) {
            id = new IdxStructure("QUERY", new ConfigurationForCat());
            id.Statistic.global();
            String nnfile = SomeConstant.ROOTDIR + "SIMPLE_CLASS/mnn/alpha.mnn";
            MM = new Categorizer(nnfile,
                    false, // chargement du r�seau complet
                    1000, // maxclass
                    2000, // cache size
                    16, // start level
                    4 // free cache
                    );
        }

    }
}