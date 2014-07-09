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
public class TestObjectStore4_Async {

    static ObjectStorage4 o;

    public static void main(String[] args) {

        implementationMode mode = implementationMode.BIG;

        o = (new ObjectStore4_Async()).create(mode, "C:/JG/VLI_RW/objsto", 20, 32);
        o.close();
        o = o.open(mode, "C:/JG/VLI_RW/objsto", readWriteMode.rw);

        int[] big = new int[100];
        big[1] = 51;
        big[2] = 52;

        msg("---------------------------------------------------------------");
        showVector(big);
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 1000; i++) {
                o.append(big, i, big.length);
            }
        }
        o.close();

    }
}
