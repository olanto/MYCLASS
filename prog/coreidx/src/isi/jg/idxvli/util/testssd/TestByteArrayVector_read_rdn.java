package isi.jg.idxvli.util.testssd;

import isi.jg.idxvli.util.ByteArrayVector;
import isi.jg.util.Timer;
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
public class TestByteArrayVector_read_rdn {

    static ByteArrayVector o;

    public static void main(String[] args) {
        String volume = "f:/ajeter";
        int size = 124;
        int nb = 27;
        int init = 10000;
        byte[] val = new byte[size];
        val[0] = 13;
        val[1] = 23;
        val[2] = 33;
        Timer t0 = new Timer("open struct");
        o = (new ByteArrayVector_OnDisk()).open(volume, "test", readWriteMode.rw);
        t0.stop();
        //showVector(val);

        for (int j = 17; j < 28; j++) {
            t0 = new Timer("init struct span 2^" + j);
            double span = Math.pow(2, j);
            for (int i = 0; i < init; i++) {
                int rnd = (int) (Math.random() * span);
                val=o.get(rnd);
            }
            t0.stop();
        }
        o.close();

    }
}
