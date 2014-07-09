package isi.jg.idxvli.server;

import isi.jg.util.TimerNano;
import java.rmi.*;
import java.util.*;
import java.rmi.server.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.knn.*;
import isi.jg.idxvli.extra.*;
import isi.jg.idxvli.doc.*;
import isi.jg.idxvli.util.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxConstant.*;
import java.util.concurrent.locks.*;
import static isi.jg.conman.server.GetContentService.*;

/**
 *  service d'indexation.
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * <p>
 * *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - écrivain en exclusion avec tous
 *  doit être le point d'accès pour toutes les structures utilisées !
 *  </pre>
 *   
 */
public class IndexService_BASIC extends UnicastRemoteObject implements IndexService {

    private IdxStructure id;
    private KNNManager KNN;
    private ContentService cs;  // service associé


    public IndexService_BASIC() throws RemoteException {
        super();
    }

    public String getInformation() throws RemoteException {
        return "this service is alive ... :IndexService_BASIC";
    }
    /** opération sur documentName verrous ------------------------------------------*/
    private final ReentrantReadWriteLock serverRW = new ReentrantReadWriteLock();
    private final Lock serverR = serverRW.readLock();
    private final Lock serverW = serverRW.writeLock();

    public synchronized String getStatistic() throws RemoteException {
        if (id != null) {
            return id.Statistic.getGlobal();
        }
        return "index is not opened";
    }

    public void startANewOne(IdxInit client) throws RemoteException {
        serverW.lock();
        try {
            if (id != null) {
                error("already init: -> bad usage");
            } else {
                id = new IdxStructure("NEW", client);
                id.Statistic.global();
                id.close();

                msg("wait a little and start server ");

            }
        } finally {
            serverW.unlock();
        }

    }

    public void getAndInit(IdxInit client, String mode, boolean OpenCM) throws RemoteException {
        serverW.lock();
        try {
            if (id != null) {
                error("already init: getAndInit");
            } else {

                msg("current indexer is opening ...");

                id = new IdxStructure(mode, client);
                if (!mode.equals("NEW")) {
                    id.Statistic.global();
                }
                msg("current indexer is opened ...");
                if (OpenCM) {
                    cs = getServiceCM("rmi://localhost/CM_CLEAN");
                } else {
                    msg("No Content Manager in this configuration (CM_CLEAN)");
//            if (mfl!=null){
//                // init mfl
//                documents=new MFL(id);
//                documents.openManyFile(mfl, 1, IdxConstant.IDX_MFL_ENCODING);
//                msg("IndexService_BASIC:open mfl OK");
//            }
                // init knn
//            Timer t2=new Timer("init KNN");
//            KNN=new TFxIDF_ONE();
//            KNN.initialize(id,    // Indexeur
//                    5,     // Min occurence d'un mot dans le corpus (nbr de documents)
//                    50,    // Max en o/oo d'apparition dans le corpus (par mille!)
//                    true,   // montre les détails
//                    1,        // formule IDF (1,2)
//                    1       // formule TF (1,2,3) toujours 1
//                    );
//            t2.stop();
//            msg("IndexService_BASIC:open knn OK");
                }
            }
        } finally {
            serverW.unlock();
        }
    }

    public void quit() throws RemoteException {
        if (id == null) {
            error("already close: quit");
        } else {
            serverW.lock();
            try {

                msg("current indexer is closing ...");
                id.Statistic.global();
                id.close();
                msg("current indexer is closed");
                msg("server must be stopped  & restarted ...");
                id = null;
            } finally {
                serverW.unlock();
            }
        }
    }

    public void flush() throws RemoteException {
        msg("current indexer is flusing ...");
        id.flushIndexDoc();
        msg("current indexer is flushed");
    }

    public void showFullIndex() throws RemoteException {
        serverW.lock();
        try {
            msg("current indexer is upgrading ...");
            id.showFullIndex();
            msg("current indexer is upgraded");
        } finally {
            serverW.unlock();
        }
    }

    // ces deux méthodes doivent être en exclusion mutuelle
    public synchronized void indexdir(String path) throws RemoteException {
        msg("current indexer is indexing ...");
        id.indexdir(path);
        id.flushIndexDoc(); // décharge les caches

        msg("current indexer is waiting for more documents...");
    }

    public void indexThisContent(String path, String content) throws RemoteException {
        id.indexThisContent(path, content);
    }

    public void indexThisContent(String path, String content, String lang) throws RemoteException {
        id.indexThisContent(path, content);
        int n = id.getIntForDocument(path);
        id.docstable.setPropertie(n, "LANG." + lang);
    }

    public void indexThisContent(String path, String content, String lang, String[] collection) throws RemoteException {
        indexThisContent(path, content, lang);
        if (collection != null) {
            int n = id.getIntForDocument(path);
            for (int i = 0; i < collection.length; i++) {
                id.docstable.setPropertie(n, "COLLECTION." + collection[i]);
            }
        }
    }

    public QLResult evalQL(String request) throws RemoteException {
        serverR.lock();
        try {
            TimerNano time = new TimerNano(request, true);
            return new QLResult(id.executeRequest(request), time.stop(true) / 1000);
        } finally {
            serverR.unlock();
        }
    }

    public QLResultAndRank evalQLMore(String request) throws RemoteException {
        serverR.lock();
        try {
            TimerNano time = new TimerNano(request, true);
            QLResultAndRank res = id.executeRequestMore(request);
            res.duration = time.stop(true) / 1000;
            return res;
        } finally {
            serverR.unlock();
        }
    }

    public QLResultNice evalQLNice(String request, int start, int size) throws RemoteException {
        serverR.lock();
        try {
            return id.evalQLNice(cs, request, start, size);

        } finally {
            serverR.unlock();
        }
    }

    public QLResultNice evalQLNice(String request, String properties, int start, int size) throws RemoteException {
        serverR.lock();
        try {
            return id.evalQLNice(cs, request, properties, start, size);

        } finally {
            serverR.unlock();
        }
    }

    public QLResultNice evalQLNice(String request, String properties, String profile, int start, int size) throws RemoteException {
        serverR.lock();
        try {
            return id.evalQLNice(cs, request, properties, profile, start, size);

        } finally {
            serverR.unlock();
        }
    }

    public QLResult evalQL(String lang, String request) throws RemoteException {
        serverR.lock();
        try {
            msg("evalQL(String lang, String request) not implemented");
            return null;
        } finally {
            serverR.unlock();
        }
    }

    public String[] getDocName(int[] docList) throws RemoteException {
        serverR.lock();
        try {
            String[] res = new String[docList.length];
            for (int i = 0; i < docList.length; i++) {
                res[i] = id.getFileNameForDocument(docList[i]);
            }
            return res;
        } finally {
            serverR.unlock();
        }
    }

    public boolean docIsValid(int doc) throws RemoteException {
        serverR.lock();
        try {
            return id.docstable.isValid(doc);
        } finally {
            serverR.unlock();
        }
    }

    public String getDoc(int docId) throws RemoteException {
        serverR.lock();
        try {
            msg("not implemented");
            return "no text!";
        } finally {
            serverR.unlock();
        }
    }

    public int getDocId(String docName) throws RemoteException {
        serverR.lock();
        try {
            return id.getIntForDocument(docName);
        } finally {
            serverR.unlock();
        }
    }

    public KNNResult KNNForDoc(int doc, int N) throws RemoteException {
        serverR.lock();
        try {
            return KNN.KNNForDoc(doc, N);
        } finally {
            serverR.unlock();
        }
    }

    public String getDOC_ENCODING() throws RemoteException {
        serverR.lock();
        try {
            return DOC_ENCODING;
        } finally {
            serverR.unlock();
        }
    }

    public void exportEntry(long min, long max) throws RemoteException {
        serverR.lock();
        try {
            id.exportEntry(min, max);
        } finally {
            serverR.unlock();
        }
    }

    public PropertiesList getDictionnary() throws RemoteException {
        serverR.lock();
        try {
            String[] res;
            List<String> p = id.getDictionnary();
            if (p != null) {
                int l = p.size();
                res = new String[l];
                for (int i = 0; i < l; i++) {
                    res[i] = p.get(i);
                }
                return new PropertiesList(res);
            } else {
                return null;
            }
        } finally {
            serverR.unlock();
        }
    }

    public PropertiesList getDictionnary(String prefix) throws RemoteException {
        serverR.lock();
        try {
            String[] res;
            List<String> p = id.getDictionnary(prefix);
            if (p != null) {
                int l = p.size();
                res = new String[l];
                for (int i = 0; i < l; i++) {
                    res[i] = p.get(i);
                }
                return new PropertiesList(res);
            } else {
                return null;
            }
        } finally {
            serverR.unlock();
        }
    }

    public String getLanguage(String txt) throws RemoteException {
        serverR.lock();
        try {
            return id.getLanguage(txt);
        } finally {
            serverR.unlock();
        }
    }

    public String[] getCollection(String txt) throws RemoteException {
        serverR.lock();
        try {
            return id.getCollection(txt);
        } finally {
            serverR.unlock();
        }
    }
}



