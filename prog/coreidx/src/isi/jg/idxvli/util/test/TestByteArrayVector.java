package isi.jg.idxvli.util.test;

import isi.jg.idxvli.util.*;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

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
public class TestByteArrayVector {

    static ByteArrayVector o;

    public static void main(String[] args) {
        String s;
        byte[] val3 = new byte[3];
        byte[] val2 = new byte[2];
        byte[] val6 = new byte[6];
        val3[0] = 13;
        val3[1] = 23;
        val3[2] = 33;
        val2[0] = 12;
        val2[1] = 22;
        o = (new ByteArrayVector_OnDisk()).create("C:/JG/gigaversion/data/objsto", "test", 20, 3);
        o = (new ByteArrayVector_OnDisk()).open("C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        showVector(val3);
        o.set(12, val3);
        showVector(o.get(12));
        o.set(13, val2);
        System.out.println("" + o.get(13)[1]);
        System.out.println("13,0:" + o.get(13, 0));
        o.set(14, val6);
        o.clear(14);
        o.close();
        System.out.println("open again ...");
        o = (new ByteArrayVector_OnDisk()).open("C:/JG/gigaversion/data/objsto", "test", readWriteMode.r);
        System.out.println("" + o.get(12)[2]);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(200));
        o.printStatistic();

        // performance test

        o.close();


    }
}
