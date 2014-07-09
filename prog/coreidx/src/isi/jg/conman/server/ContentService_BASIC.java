package isi.jg.conman.server;

import java.rmi.*;
import java.util.*;
import java.rmi.server.*;
import isi.jg.conman.*;
import static isi.jg.util.Messages.*;
import java.util.concurrent.locks.*;
import isi.jg.idxvli.util.*;
import isi.jg.idxvli.doc.*;

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
 * <p>
 * *  <pre>
 *  concurrence:
 *   - // pour les lecteurs
 *   - écrivain en exclusion avec tous
 *  doit être le point d'accès pour toutes les structures utilisées !
 *  </pre>
 *   */
public class ContentService_BASIC extends UnicastRemoteObject implements ContentService {

    private ContentStructure id;

    public ContentService_BASIC() throws RemoteException {
        super();
    }

    public String getInformation() throws RemoteException {
        return "this service is alive ... :ContentService_BASIC";
    }
    /** opération sur documentName verrous ------------------------------------------*/
    private final ReentrantReadWriteLock serverRW = new ReentrantReadWriteLock();
    private final Lock serverR = serverRW.readLock();
    private final Lock serverW = serverRW.writeLock();
    private final Lock serverCLEAN = serverRW.writeLock();

    public void startANewOne(ContentInit client) throws RemoteException {
        serverW.lock();
        try {
            if (id != null) {
                error("already init: -> bad usage");
            } else {
                id = new ContentStructure("NEW", client);
                id.Statistic.global();
                id.close();

                msg("wait a little and start server ");

            }
        } finally {
            serverW.unlock();
        }

    }

    public void getAndInit(ContentInit client, String mode) throws RemoteException {
        serverW.lock();
        try {
            if (id != null) {
                error("already init: getAndInit");
            } else {
                msg("current content manager is opening ...");
                id = new ContentStructure(mode, client);
                id.Statistic.global();
                msg("current content manager is opened");
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
                msg("current content manager is closing ...");
                id.Statistic.global();
                id.close();
                msg("current content manager is closed");
                msg("server must be stopped  & restarted ...");
                id = null;
            } finally {
                serverW.unlock();
            }
        }
    }

    public void addURL(String url) throws RemoteException {
        id.addURL(url);
    }

    public synchronized void addContent(String docName, String content, String lang, String collection) throws RemoteException {
        id.addContent(docName, content, lang, collection);
    }

    public void saveContent(int docID, String content, String type) throws RemoteException {
        id.saveContent(docID, content, type);
    }

    public void saveContent(int docID, byte[] content, String type) throws RemoteException {
        id.saveContent(docID, content, type);
    }

    public synchronized String getStatistic() throws RemoteException {
        if (id != null) {
            return id.Statistic.getGlobal();
        }
        return "content is not opened";
    }

    public synchronized void getFromDirectory(String path) throws RemoteException {
        msg("current content manager is loading ...");
        id.getFromDirectory(path);
        msg("current content manager is loaded ...");
    }

    public String getDocName(int doc) throws RemoteException {
        serverR.lock();
        try {
            String res = id.getFileNameForDocument(doc);
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
            return id.getStringContent(docId);
        } finally {
            serverR.unlock();
        }
    }

    public String getDoc(String docName) throws RemoteException {
        serverR.lock();
        try {
            //msg("debug getDoc:"+docName);
            int docId = id.getIntForDocument(docName);
            //msg("debug getDocID:"+docName);
            if (docId == -1) {
                return null;
            } // pas trouvé
            return id.getStringContent(docId);
        } finally {
            serverR.unlock();
        }
    }

    public byte[] getBin(int docId) throws RemoteException {
        serverR.lock();
        try {
            return id.getByteContent(docId);
        } finally {
            serverR.unlock();
        }
    }

    public byte[] getBin(String docName) throws RemoteException {
        serverR.lock();
        try {
            int docId = id.getIntForDocument(docName);
            if (docId == -1) {
                return null;
            } // pas trouvé
            return id.getByteContent(docId);
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

    public String getPartOfDoc(int docId, int from, int to) throws RemoteException {
        serverR.lock();
        try {
            return id.getStringContent(docId, from, to);
        } finally {
            serverR.unlock();
        }
    }

    public int getSize() throws RemoteException {
        serverR.lock();
        try {
            return id.lastdoc;
        } finally {
            serverR.unlock();
        }
    }

    public boolean isIndexable(int doc) throws RemoteException {
        return id.isIndexable(doc);
    }

    public void setIndexed(int doc) throws RemoteException {
        id.setIndexed(doc);
    }

    public SetOfBits satisfyThisProperty(String properties) throws RemoteException {
        return id.satisfyThisProperty(properties);
    }

    public void setDocumentPropertie(int docID, String properties) throws RemoteException {
        id.setDocumentPropertie(docID, properties);
    }

    public void clearDocumentPropertie(int docID, String properties) throws RemoteException {
        id.clearDocumentPropertie(docID, properties);
    }

    public PropertiesList getDictionnary() throws RemoteException {
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
    }

    public PropertiesList getDictionnary(String prefix) throws RemoteException {
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
    }

    public void setRefDoc(String docName, String refName, String title, String cleantxt) throws RemoteException {
       serverCLEAN.lock();
        try {
         id.setRefDoc(docName, refName, title, cleantxt);
         } finally {
            serverCLEAN.unlock();
        }
   }

    public String getRefName(String docName) throws RemoteException {
        serverCLEAN.lock();
        try {
       return id.getRefName(docName);
         } finally {
            serverCLEAN.unlock();
        }
   }

    public String getTitle(String docName) throws RemoteException {
        serverCLEAN.lock();
        try {
       return id.getTitle(docName);
         } finally {
            serverCLEAN.unlock();
        }
   }

    public String getCleanText(String docName) throws RemoteException {
        serverCLEAN.lock();
        try {
     return id.getCleanText(docName);
          } finally {
            serverCLEAN.unlock();
        }
    }

    public String getCleanText(String docName, int from, int to) throws RemoteException {
       serverCLEAN.lock();
        try {
        return id.getCleanText(docName, from, to);
        } finally {
            serverCLEAN.unlock();
        }
    }
}



