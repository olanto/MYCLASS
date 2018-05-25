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

package org.olanto.cat.server;

import org.olanto.idxvli.DoParse;
import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.mnn.Categorizer;
import org.olanto.cat.mnn.Guess;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *  service de classification.
 *
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



