package isi.jg.idxvli.util.testssd;

import isi.jg.idxvli.util.ByteArrayVector;
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
         String volume="F:/ajeter";
        int size = 124;
        int nb = 27; 
        int init=1000000;
         byte[] val = new byte[size];
        val[0] = 13;
        val[1] = 23;
        val[2] = 33;
        Timer t0=new Timer("create struct");
       o = (new ByteArrayVector_OnDisk()).create(volume, "test", nb, size);
       t0.stop();
        t0=new Timer("open struct");
        o = (new ByteArrayVector_OnDisk()).open(volume, "test", readWriteMode.rw);
        t0.stop();
        showVector(val);
        
        t0=new Timer("init struct");
        for(int i=0;i<init;i++){
                o.set(i, val);
        }
        t0.stop();
        showVector(o.get(12));
        o.close();
        System.out.println("open again ...");
        o = (new ByteArrayVector_OnDisk()).open(volume, "test", readWriteMode.r);
        System.out.println("" + o.get(12)[2]);
        System.out.println("" + o.get(13)[1]);
        System.out.println("" + o.get(200));
        o.printStatistic();

        // performance test

        o.close();


    }
}
