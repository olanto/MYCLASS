package isi.jg.deploy.frende;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.mnn.*;

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
    public static String advise(String p, int level) {

        init();
        ClassifierRequest rq = parse(p, "3", "EN");
        return guess(rq, "", ""+level);

    }

    public static String advise3(String p) {

        init();
        ClassifierRequest rq = parse(p, "1", "EN");
        return guessSimple(rq, "", "3");

    }
    
    public static String adviseSimple(String p, int level) {

        init();
        ClassifierRequest rq = parse(p, "4", "EN");
        return guessSimple(rq, "", ""+level);

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
                res += " " + guess[i].categorie;
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
            String nnfile = SomeConstant.ROOTDIR + "SIMPLE_CLASS/mnn/frende.mnn";
            MM = new Categorizer(nnfile,
                    false, // chargement du r�seau complet
                    1000, // maxclass
                    20000, // cache size
                    16, // start level
                    4 // free cache
                    );
        }

    }
}