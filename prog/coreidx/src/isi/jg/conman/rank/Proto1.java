package isi.jg.conman.rank;

import static isi.jg.util.Messages.*;
import isi.jg.util.TimerNano;

/**
 * *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * prototype de ranking par les liens
 */
public class Proto1 {

    static final int LEVEL = 20;
    static final int MAX = (int) Math.pow(2, LEVEL);
    static int[][] invlink = new int[MAX - 1][];
    static int[] cntlink = new int[MAX - 1];
    static float[] wnode = new float[MAX - 1];
    static int last = 0;
    static int lastzero = 0;

    public static void main(String[] args) {


        TimerNano t1 = new TimerNano("global", false);
        init();

        for (int k = 0; k < 16 * LEVEL; k++) {
            relax();
            msg(k + "," + wnode[0] + "," + wnode[1] + "," + wnode[3] + "," + wnode[5] + "," + wnode[7] + "," + wnode[9] + "," + wnode[11] + "," + wnode[13] + "," + wnode[15] + "," + wnode[17]);
        }
        t1.stop(false);

    }

    static void relax() {
        for (int k = 0; k < last; k++) {
            int i = (int) (Math.random() * last);

            float sum = 0;
            for (int j = 0; j < invlink[i].length; j++) {
                sum += wnode[invlink[i][j]] / (float) cntlink[invlink[i][j]];
            }
            wnode[i] = sum;
        }
    }

    static void init() {
        last = 1;
        invlink[0] = new int[MAX / 2];
        for (int i = 1; i < MAX - 1; i++) {
            invlink[i] = new int[1];
        }
        bi(0, 0);
        msg("last:" + last + " lastzero:" + lastzero);
//        showVector(invlink[0]);
//        showVector(invlink[1]);
//        showVector(invlink[2]);
//        showVector(invlink[3]);
//        showVector(invlink[4]);
//        showVector(invlink[5]);
//        showVector(cntlink);
        for (int i = 0; i < last; i++) {
            wnode[i] = MAX;
        }
    }

    static void bi(int s, int level) {
        if (level != LEVEL - 1) {
            int loc = last;
            invlink[last][0] = s;
            cntlink[s]++;
            invlink[last + 1][0] = s;
            cntlink[s]++;
            last += 2;
            bi(loc, level + 1);
            bi(loc + 1, level + 1);
        } else {
            invlink[0][lastzero] = s;
            cntlink[s]++;
            lastzero++;
        }
    }
}


