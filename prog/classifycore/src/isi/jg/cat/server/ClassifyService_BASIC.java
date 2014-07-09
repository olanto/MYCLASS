package isi.jg.cat.server;

import isi.jg.idxvli.DoParse;
import isi.jg.idxvli.IdxStructure;
import isi.jg.mnn.Categorizer;
import isi.jg.mnn.Guess;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *  service de classification.
 *
 * <p>
 * <b>JG-2010
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2010
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
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

public class ClassifyService_BASIC extends UnicastRemoteObject implements ClassifyService {

    static Categorizer MM;
    private static IdxStructure id;

    public ClassifyService_BASIC() throws RemoteException {
        super();
    }

    public String getInformation() throws RemoteException {
        return "this service is alive ... :ClassifyService_BASIC";
    }
//    /** opération sur les verrous ------------------------------------------*/
//    private final ReentrantReadWriteLock serverRW = new ReentrantReadWriteLock();
//    private final Lock serverR = serverRW.readLock();
//    private final Lock serverW = serverRW.writeLock();
//

    public void init() throws RemoteException {  // init class
        if (id == null) {
            id = new IdxStructure("QUERY", new ConfigurationForCat());
            id.Statistic.global();
            String nnfile = "C:/SIMPLE_CLASS/mnn/alpha.mnn";
            MM = new Categorizer(nnfile,
                    false, // chargement du r?seau complet
                    1000, // maxclass
                    2000, // cache size
                    16, // start level
                    4 // free cache
                    );
        }
    }

    public ClassifyResult advise(String p, int nbAdvise) throws RemoteException {

        init();
        ClassifierRequest rq = parse(p, nbAdvise, "EN");
        return guessSimple(rq, "", "3");

    }

    ClassifierRequest parse(String s, int nbAdvise, String lang) {
        //System.out.println("ok Classifier.parse");
        DoParse a = new DoParse(s, id.dontIndexThis);
        int[] requestDB = a.getDocBag(id); // get the docBag od the request
        ClassifierRequest rq = new ClassifierRequest(requestDB, nbAdvise, lang);
        return rq;
    }

    ClassifyResult guessSimple(ClassifierRequest rq, String from, String to) {
        Guess[] guess = MM.classifyDocumentAndPonderate(from, to, rq.docbag, rq.nbchoice);
        String[] res = new String[guess.length];
        if (guess == null) {
            res[0] = "No guess !";
        } else {
            for (int i = 0; i < guess.length; i++) {
                res[i] = guess[i].categorie;
            }
        }
        return new ClassifyResult(res);
    }
}



