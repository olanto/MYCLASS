package isi.jg.idxvli.util.test;

import isi.jg.idxvli.util.*;
import isi.jg.util.TimerNano;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.SetOperation.*;

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
public class TestSetOperation {

    public static void main(String[] args) {

        int sl = 1000000;
        int[] vl = new int[sl];

        for (int i = 0; i < sl; i++) {
            vl[i] = 100 * i;
        }

        int ss = 101;
        int[] vs = new int[ss];

        for (int i = 0; i < ss; i++) {
            vs[i] = 1 * i;
        }

        TimerNano t = new TimerNano("1", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 10 * i;
        }
        t = new TimerNano("10", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);


        for (int i = 0; i < ss; i++) {
            vs[i] = 100 * i;
        }
        t = new TimerNano("100", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 1000 * i;
        }
        t = new TimerNano("1000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 10000 * i;
        }
        t = new TimerNano("10000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 100000 * i;
        }
        t = new TimerNano("100000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

        for (int i = 0; i < ss; i++) {
            vs[i] = 1000000 * i;
        }
        t = new TimerNano("1000000", true);
        andVector(vs, vs.length, vl, vl.length);
        t.stop(false);

    }
}
