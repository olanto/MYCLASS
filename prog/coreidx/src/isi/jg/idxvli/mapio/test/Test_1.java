package isi.jg.idxvli.mapio.test;

import java.io.IOException;
import static java.lang.Math.*;
import isi.jg.idxvli.mapio.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;

/**
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
 * tests de base (écriture lecture de 1 caractère).
 *
 */
public class Test_1 {

    /**
     * @param args the command line arguments
     */
    static int fileSize = (int) pow(2, 8);

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 1; i++) {
                String fname;

                msg("Test StandardFile fileSize:" + fileSize);
                DirectIOFile file = new MappedFile();
                fname = "c:/temp/mapfile_ajeter.data";
                testWrite(file, fname, MappingMode.NOMAP);
                System.gc(); // efface les caches
                testRead(file, fname, MappingMode.NOMAP);
                System.gc(); // efface les caches
                msg("\nTest DirectIOFile fileSize:" + fileSize);
                file = new MappedFile();
                fname = "c:/temp/mapfile_direct_ajeter.data";
                testWrite(file, fname, MappingMode.FULL);
                System.gc(); // efface les caches
                testRead(file, fname, MappingMode.FULL);
//                test3(file, fname);
//                test4(file, fname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testWrite(DirectIOFile file, String fileName, MappingMode mapMode)
            throws IOException {
        Timer t = new Timer("----- " + file.getClass().getName());
        file.open(fileName, mapMode, readWriteMode.rw);
        for (int i = 0; i < fileSize; i++) {
            file.write(new byte[]{(byte) (i % 256)});
        }
        file.close();
        t.stop();
    }

    private static void testRead(DirectIOFile file, String fileName, MappingMode mapMode)
            throws IOException {
        Timer t = new Timer("----- " + file.getClass().getName());
        file.open(fileName, mapMode, readWriteMode.r);
        for (int i = 0; i < fileSize; i++) {
            byte b[] = new byte[1];
            file.read(b);
            if (b[0] != (byte) (i % 256)) {
                throw new IOException("Read failed");
            }
        }
        file.close();
        t.stop();
    }
}
