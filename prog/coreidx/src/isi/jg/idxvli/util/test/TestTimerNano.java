package isi.jg.idxvli.util.test;

import isi.jg.util.TimerNano;
import isi.jg.idxvli.util.*;
import isi.jg.util.Timer;

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
public class TestTimerNano {

    public static void main(String[] args) {
        TimerNano t1;
        for (int i = 0; i < 1000; i++) {
            t1 = new TimerNano("empty", true);
            t1.stop(false);

            t1 = new TimerNano("empty2", true);
            t1.stop(false);
        }
    }
}
