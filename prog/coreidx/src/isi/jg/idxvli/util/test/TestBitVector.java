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
public class TestBitVector {

    static BitVector o;

    public static void main(String[] args) {
        String s;
        o = (new BitVector_InMemoryZIP()).create("C:/JG/gigaversion/data/objsto", "test", 1024 * 1024);
        o = (new BitVector_InMemoryZIP()).open("C:/JG/gigaversion/data/objsto", "test");
        int i = 0;
        boolean b = false;
        i = 0;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        o.close();
        System.out.println("open again ...");
        o = (new BitVector_InMemoryZIP()).open("C:/JG/gigaversion/data/objsto", "test");
        System.out.println(o.get(12));
        System.out.println(o.get(23));
        i = 0;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = true;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 0;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 0;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 12;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 13;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 31;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 32;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        i = 63;
        b = false;
        System.out.print(i + " " + o.get(i));
        o.set(i, b);
        System.out.println(b + ">" + o.get(i));
        o.printStatistic();

        Timer t1 = new Timer("IntVector get");
        for (int k = 0; k < 100000; k++) {
            for (int j = 0; j < 1024; j++) {
                boolean x = o.get(j);
            }
        }
        t1.stop();

        o.close();


    }
}
