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
public class TestIntVector_DirectIO_1 {

    static IntVector o;

    public static void main(String[] args) {
        String s;
        o = (new IntVector_InMemory()).create("C:/JG/gigaversion/data/objsto", "test", 20);
        o = (new IntVector_InMemory()).open("C:/JG/gigaversion/data/objsto", "test");
        o.set(12, 1212);
        System.out.println(+o.get(12));
        o.set(23, 2323);
        System.out.println(+o.get(23));
        o.close();
        System.out.println("open again ...");
        o = (new IntVector_InMemory()).open("C:/JG/gigaversion/data/objsto", "test");
        System.out.println(+o.get(12));
        System.out.println(+o.get(0));
        o.printStatistic();

        // performance test
        int res;
        int outer = 100;
        Timer t1;
        int[] v = new int[1024 * 1024];
        t1 = new Timer("IntVector set");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                o.set(j, j);
            }
        }
        t1.stop();
        t1 = new Timer("Int[] set");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                v[j] = j;
            }
        }
        t1.stop();
        t1 = new Timer("IntVector get");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                res = o.get(j);
            }
        }
        t1.stop();
        t1 = new Timer("Int[] get");
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                res = v[j];
            }
        }
        t1.stop();

        o.close();


    }
}
