package isi.jg.idxvli.test;

import isi.jg.idxvli.*;
import isi.jg.idxvli.knn.*;
import isi.jg.util.Timer;
import java.io.*;
import java.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.util.TimerNano;

/**
 * *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 * Test de l'indexeur, mode query
 */
public class TESTKNNMode_1 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    private static KNNManager KNN;

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY");
        // création de la racine de l'indexation
        id.createComponent(new Configuration());

        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();

        id.Statistic.global();

        Timer t2 = new Timer("init KNN");

        //for (int i=100000;i<101000;i++) msg(i+" : "+id.getOccOfW(i)); // test la fonction de longueur

        KNN = new TFxIDF_ONE();
        KNN.initialize(id, // Indexeur
                5, // Min occurence d'un mot dans le corpus (nbr de documents)
                50, // Max en o/oo d'apparition dans le corpus (par mille!)
                true, // montre les détails
                1, // formule IDF (1,2)
                1 // formule TF (1,2,3) toujours 1
                );
        t2.stop();

        test("telecomunication wifi computer", 10);
        test("The broadcast function provided by  the  MPCP  binding  is  limited  to  the " +
                "subnetwork in which it exists. It may form part of  a  multicast  (selective " +
                "broadcast) function within a larger (containing) subnetwork. " +
                "Four types of Multipoint connections Broadcast, Merge, Composite,  and  Full " +
                "Multipoint are shown in Figure  I.1  using  a  MultiPoint  Connection  Point " +
                "(MPCP). The MPCP denotes the Root  of  the  Multipoint  connection  for  the " +
                "Broadcast, Merge, and Composite  types,  where  the  Connection  Point  (CP) " +
                "denotes the leaf. For the Full Multipoint connection,  the  MPCP  denotes  a " +
                "hybrid Root/Leaf. Note that the directionality refers only  to  the  traffic " +
                "flow, the OAM flow are for further study(see ITU-T Rec. I.610).", 3);

        t2 = new Timer("init KNN FULL");
        KNN = new TFxIDF();
        KNN.initialize(id, // Indexeur
                5, // Min occurence d'un mot dans le corpus (nbr de documents)
                50, // Max en o/oo d'apparition dans le corpus (par mille!)
                true, // montre les détails
                1, // formule IDF (1,2)
                1 // formule TF (1,2,3) toujours 1
                );
        t2.stop();

        test("telecomunication wifi computer", 10);
        test("The broadcast function provided by  the  MPCP  binding  is  limited  to  the " +
                "subnetwork in which it exists. It may form part of  a  multicast  (selective " +
                "broadcast) function within a larger (containing) subnetwork. " +
                "Four types of Multipoint connections Broadcast, Merge, Composite,  and  Full " +
                "Multipoint are shown in Figure  I.1  using  a  MultiPoint  Connection  Point " +
                "(MPCP). The MPCP denotes the Root  of  the  Multipoint  connection  for  the " +
                "Broadcast, Merge, and Composite  types,  where  the  Connection  Point  (CP) " +
                "denotes the leaf. For the Full Multipoint connection,  the  MPCP  denotes  a " +
                "hybrid Root/Leaf. Note that the directionality refers only  to  the  traffic " +
                "flow, the OAM flow are for further study(see ITU-T Rec. I.610).", 3);


        t1.stop();

    }

    private static void test(String s, int nTop) {
        TimerNano t1 = new TimerNano("KNN: " + s + " " + nTop, false);
        System.out.println(KNN.searchforKNN(s, nTop));
        t1.stop(false);
    }
}
