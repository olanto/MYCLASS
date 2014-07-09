package isi.jg.idxvli.util.test;

import isi.jg.idxvli.util.*;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;

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
public class TestSetOfBits {

    static SetOfBits o;

    public static void main(String[] args) {
        String s;
        o = new SetOfBits(1024 * 1024);
        int i = 0;
        boolean b = false;
        msg("-------------set and get ------------------------------------");
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

        msg("-------------and oper ------------------------------------");

        SetOfBits A = new SetOfBits(1024 * 1024);
        SetOfBits B = new SetOfBits(1024 * 1024);
        b = true;
        A.set(1, b);
        A.set(61, b);
        A.set(82, b);
        A.set(123, b);
        B.set(1, b);
        A.set(62, b);
        A.set(83, b);
        ;
        B.set(123, b);
        A.and(B, SetOfBits.ALL);
        msg("1:" + A.get(1) + " 61:" + A.get(61) + " 62:" + A.get(62) + " 82:" + A.get(82) + " 83:" + A.get(83) + " 123:" + A.get(123));

        msg("-------------performance compare ------------------------------------");

        Timer t1 = new Timer("Home BitSet set");
        for (int k = 0; k < 100; k++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                o.set(j, true);
            }
        }
        t1.stop();

        java.util.BitSet std = new java.util.BitSet(1024 * 1024);
        t1 = new Timer("Sun BitSet set");
        for (int k = 0; k < 100; k++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                std.set(j, true);
            }
        }
        t1.stop();

        t1 = new Timer("Home BitSet get");
        for (int k = 0; k < 100; k++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                boolean x = o.get(j);
            }
        }
        t1.stop();

        t1 = new Timer("Sun BitSet get");
        for (int k = 0; k < 100; k++) {
            for (int j = 0; j < 1024 * 1024; j++) {
                boolean x = std.get(j);
            }
        }
        t1.stop();

        t1 = new Timer("Home BitSet and");
        for (int j = 0; j < 10000; j++) {
            A.and(B, SetOfBits.ALL);
        }
        t1.stop();

        java.util.BitSet oper = new java.util.BitSet(1024 * 1024);
        std.set(1, b);
        std.set(61, b);
        std.set(82, b);
        std.set(1024 * 1024 - 1, b);
        oper.set(1, b);
        oper.set(62, b);
        oper.set(83, b);
        oper.set(1024 * 1024 - 1, b);
        t1 = new Timer("Sun BitSet get");
        for (int j = 0; j < 10000; j++) {
            std.and(oper);
        }
        t1.stop();

    }
}
