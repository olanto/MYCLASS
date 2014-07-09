package isi.jg.idxvli.util.test;

import isi.jg.idxvli.util.*;
import isi.jg.util.TimerNano;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

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
public class TestCompression {

    public static void main(String[] args) {

        int sl = 10000000;
        int[] vl = new int[sl];

        for (int i = 0; i < sl; i++) {
            vl[i] = i;
        }

        TimerNano t = new TimerNano("compress", true);
        byte[] bb = compressVInt(vl);
        t.stop(false);

        TimerNano t1 = new TimerNano("decompress", true);
        int[] res = decompressVInt(bb, sl);
        t1.stop(false);

        for (int i = 0; i < sl; i++) {
            if (res[i] != vl[i]) {
                msg("error " + i);
            }
        }





    }
}
