/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.idxvli.distibuted.test;

import isi.jg.idxvli.ql.QResDistributed;
import java.rmi.*;
import isi.jg.idxvli.server.*;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class SubmitDistributedQuery {

    static int nbNode = 1;
    static Remote[] r;
    static IndexService[] nodes;

    public static void main(String[] args) {
        connectToNode();
        test("napoléon");
        test("hathor");
        test("harvard");
        test("Hélène");
        test("viagra");

    }

    public static void connectToNode() {
        r = new Remote[nbNode];
        nodes = new IndexService[nbNode];
        try {
            for (int i = 0; i < nbNode; i++) {
                int name = i + 1;
                System.out.println("connect to serveur : " + name);
                r[i] = Naming.lookup("rmi://localhost/VLI" + name);
                System.out.println("access to serveur");
                if (r[i] instanceof IndexService) {
                    nodes[i] = ((IndexService) r[i]);
                    String s = nodes[i].getInformation();
                    System.out.println("chaîne renvoyée par " + name + " = " + s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test(String s) {
        Timer t1 = new Timer("distribute:" + s, false);
        QLResultAndRank[] res = new QLResultAndRank[nbNode];
        for (int i = 0; i < nbNode; i++) {  // cette partie doit être revue pour être parallèle et asynchrone !!!!!!
            try {
                res[i] = nodes[i].evalQLMore(s);
                if (res[i] != null&&res[i].result!=null) {
                    msg("node " + i + " time:" + res[i].duration + " #res:" + res[i].result.length);
                } else {
                    msg("node " + i + " empty result");
                }
            } catch (RemoteException ex) {
                error("during query process ", ex);
            }
        }
        QResDistributed globalRes = new QResDistributed(res, 100);
        if (globalRes == null) {
            msg("global result is null");
        } else {
            msg("global result is ndoc:" + globalRes.topdoc.length);
            for (int i = 0; i < globalRes.topdoc.length; i++) {
                msg(" doc " + globalRes.topdoc[i] + " from node " + globalRes.topsource[i] + " : " + globalRes.topname[i]);
            }
        }
        t1.stop();


    }
}
