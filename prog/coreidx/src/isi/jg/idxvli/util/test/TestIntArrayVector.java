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
public class TestIntArrayVector {

    static IntArrayVector o;

    public static void main(String[] args) {
        String s;
        int[] val3 = new int[3];
        int[] val2 = new int[2];
        int[] val6 = new int[6];
        val3[0] = 13;
        val3[1] = 23;
        val3[2] = 33;
        val2[0] = 12;
        val2[1] = 22;
        o = (new IntArrayVector_InMemory()).create("C:/JG/VLI_RW/objsto", "test", 20, 3);
        o = (new IntArrayVector_InMemory()).open("C:/JG/VLI_RW/objsto", "test");
        o.set(12, val3);
        System.out.println("" + o.get(12)[2]);
        o.set(13, val2);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(13, 0));
        o.set(14, val6);
        o.close();
        System.out.println("open again ...");
        o = (new IntArrayVector_InMemory()).open("C:/JG/VLI_RW/objsto", "test");
        System.out.println("" + o.get(12)[2]);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(200));
        o.printStatistic();

        // performance test

        o.close();


    }
}
