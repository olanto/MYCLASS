package isi.jg.idxvli.util.test;

import isi.jg.idxvli.util.*;

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
public class TestLongVector {

    static LongVector o;

    public static void main(String[] args) {
        String s;
        int i;
        o = (new LongVector_InMemory()).create("C:/JG/VLI_RW/objsto", "test", 10);
        o = (new LongVector_InMemory()).open("C:/JG/VLI_RW/objsto", "test");
        o.set(12, 1212);
        System.out.println(+o.get(12));
        o.set(23, 2323);
        System.out.println(+o.get(23));
        o.close();
        System.out.println("open again ...");
        o = (new LongVector_InMemory()).open("C:/JG/VLI_RW/objsto", "test");
        System.out.println(+o.get(12));
        System.out.println(+o.get(0));
        o.printStatistic();
        o.close();


    }
}
