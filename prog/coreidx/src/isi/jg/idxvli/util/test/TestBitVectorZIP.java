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
public class TestBitVectorZIP {

    static BitArrayVector o;

    public static void main(String[] args) {
        implementationMode imp = implementationMode.BIG;
        String s;
        o = (new BitArrayVector_ZIP()).create(imp, "C:/JG/gigaversion/data/objsto", "test", 10, 1024 * 1024);
        o = (new BitArrayVector_ZIP()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        msg("12,123:" + o.get(12, 123));
        o.set(12, 123, true);
        msg("12,123:" + o.get(12, 123));

        o.close();
        o = (new BitArrayVector_ZIP()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        msg("12,123:" + o.get(12, 123));
        o.set(12, 123, false);
        msg("12,123:" + o.get(12, 123));
        Timer t1 = new Timer("set eval");
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                o.set(i, j, true);
            }
        }
        t1.stop();
        o.printStatistic();
        o.close();

        o = (new BitArrayVector_ZIP()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.r);
        msg("12,123:" + o.get(12, 123));
        o.set(12, 123, true);
        msg("12,123:" + o.get(12, 123));

        o.close();


    }
}
