package isi.jg.idxvli.jjbg.test;

import isi.jg.idxvli.jjbg.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * test de base.
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
public class TestObjectStore4 {

    static ObjectStorage4 o;

    public static void main(String[] args) {

        implementationMode mode = implementationMode.BIG;

        o = (new ObjectStore4()).create(mode, "C:/JG/gigaversion/data/objsto", 18, 32);
        o = o.open(mode, "C:/JG/gigaversion/data/objsto", readWriteMode.rw);

        int[] big = new int[5];
        big[1] = 51;
        big[2] = 52;
        int bigref = 5;
        int[] small = new int[3];
        small[2] = 32;
        int smallref = 6;

        msg("---------------------------------------------------------------");
        showVector(big);
        o.append(big, bigref, big.length);
        o.printNiceId(bigref);
        msg("5 bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));
        showVector(o.readInt(bigref));
        msg("---------------------------------------------------------------");
        msg("" + o.append(small, smallref, small.length));
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));
        msg("----append big---------------------------------------------");
        o.append(big, bigref, big.length);
        o.printNiceId(bigref);
        showVector(o.readInt(bigref));
        msg("10 bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));
        o.append(big, bigref, big.length);
        o.printNiceId(bigref);
        showVector(o.readInt(bigref));
        msg("15 bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));
        msg("----append small---------------------------------------------");
        int[] unit = new int[1];
        unit[0] = 11;
        o.append(unit, smallref, unit.length);
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));
        unit[0] = 12;
        o.append(unit, smallref, unit.length);
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));

        o.append(unit, smallref + 1, unit.length);
        o.printNiceId(smallref + 1);
        showVector(o.readInt(smallref + 1));
        o.printStatistic();

        o.close();
        o = o.open(mode, "C:/JG/gigaversion/data/objsto", readWriteMode.rw);
        o.printNiceId(smallref);
        showVector(o.readInt(smallref));
        o.printNiceId(smallref + 1);
        showVector(o.readInt(smallref + 1));
        msg("bigreal:" + o.realSize(bigref) + " bigstore:" + o.storedSize(bigref));

        o.close();


    }
}
