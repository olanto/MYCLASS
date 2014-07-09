/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.tools;

import java.rmi.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.conman.server.GetContentService.*;
import java.util.*;
import java.net.*;
import isi.jg.idxvli.util.SetOfBits;

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
 */

/* on admet que l'on a que des URL !!!!!! */
public class GetHostList {

    static Hashtable<String, Integer> url2int = new Hashtable<String, Integer>();
    static Hashtable<Integer, String> int2url = new Hashtable<Integer, String>();

    public static void main(String[] args) {

        ContentService is = getServiceCM("rmi://localhost/CM_COLLECT");
        url2int = new Hashtable<String, Integer>();
        int2url = new Hashtable<Integer, String>();
        int count = 0;
        try {
            int lastdoc = is.getSize();
            SetOfBits indexable = is.satisfyThisProperty("TYPE.INDEXABLE");
            for (int i = 0; i < lastdoc; i++) {
                if (i % 1000 == 0) {
                    msg("process:" + i);
                }
                if (indexable.get(i)) {  // on doit donc pouvoir trouver un titre si html
                    String s = is.getDocName(i);
                    URL url = new URL(s);
                    String host = url.getHost();
                    //msg(host);
                    if (url2int.get(host) == null) {
                        url2int.put(host, count);
                        int2url.put(count, host);
                        count++;
                    }
                }
            }
            for (int i = 0; i < int2url.size(); i++) {
                msg(int2url.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
