package isi.jg.idxvli.util.test;

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
public class TestLongVector_DirectIO_2 {

    static LongVector o;

    public static void main(String[] args) {
        String s;
        int max = 26;
        o = (new LongVector_DirectIO()).create("C:/JG/gigaversion/data/objsto", "test", max);
        o = (new LongVector_DirectIO()).open("C:/JG/gigaversion/data/objsto", "test");
        int last = (int) Math.pow(2, max);
        for (int i = 0; i < last; i++) {
            o.set(i, i);
        }
        o.close();
        System.out.println("open again ...");
        o = (new LongVector_DirectIO()).open("C:/JG/gigaversion/data/objsto", "test");
        System.out.println(+o.get(12));
        System.out.println(+o.get(0));
        System.out.println(+o.get(last - 1));
        o.printStatistic();

        o.close();


    }
}
