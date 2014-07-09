package isi.jg.idxvli.util.test;

/* générique test pour les différentes implémenations remplacer le nom de l'implémentation .... */
import isi.jg.idxvli.util.*;
import isi.jg.util.Timer;

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
 *
 */
public class TestStringRepository_Stress2 {

    static StringRepository o;

    public static void main(String[] args) {
        o = (new StringTable_HomeHash_OnDisk_Clue_XXL()).create("C:/JG/gigaversion/data/objsto", "test", 29, 8);
        o = (new StringTable_HomeHash_OnDisk_Clue_XXL()).open("C:/JG/gigaversion/data/objsto", "test");
        Timer t0 = new Timer("global put");
        Timer t1 = new Timer("put 0");
        int max = 1000000;
        for (int i = 1; i < max; i++) {
            if (i % 100000 == 0) {
                t1.stop();
                t1 = new Timer("put:" + (i / 100000 + 1));
            }
            o.put(String.valueOf(i));
        }
        t0.stop();
        o.printStatistic();
        o.close();
        o = (new StringTable_HomeHash_OnDisk_Clue_XXL()).open("C:/JG/gigaversion/data/objsto", "test");
        t0 = new Timer("global get");
        t1 = new Timer("get 0");
        for (int i = max; i > 0; i--) {
            if (i % 100000 == 0) {
                t1.stop();
                t1 = new Timer("get:" + (i / 100000 + 1));
            }
            o.get(String.valueOf(i));
        }
        t0.stop();
//          for (int i=1;i<max/1000;i++){
//           System.out.println(o.get(i));
//        }
        o.printStatistic();


    }
}
