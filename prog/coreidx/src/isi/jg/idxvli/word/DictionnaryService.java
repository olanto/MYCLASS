package isi.jg.idxvli.word;

import java.rmi.*;
import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
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
 */
public interface DictionnaryService extends Remote {

    public String getInformation() throws RemoteException;

    public void create(implementationMode implementation,
            String path, String file, int maxSize, int maxLengthSizeOfName) throws RemoteException;

    public void open(implementationMode implementation,
            readWriteMode RW, String path, String file) throws RemoteException;

    public void close() throws RemoteException;

    public int put(String word) throws RemoteException;

    public int get(String word) throws RemoteException;

    public String get(int i) throws RemoteException;

    public int getCount() throws RemoteException;

    public void printStatistic() throws RemoteException;
    }

