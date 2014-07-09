package isi.jg.idxvli.mapio.test;

import java.io.IOException;
import static java.lang.Math.*;
import isi.jg.idxvli.mapio.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;
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
 * tests de base (écriture lecture de blocs débordant les slices).
 *
 */
public class Test_1a {

    /**
     * @param args the command line arguments
     */
    static int fileSize = (int) pow(2, 8);
    static byte[] vect = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 1; i++) {
                String fname;
                msg("\nTest DirectIOFile fileSize:" + fileSize);
                DirectIOFile file = new MappedFile();
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
        file.open(fileName, mapMode, readWriteMode.rw, 2, (long) Math.pow(2, 10));
        file.seek(1);
        for (int i = 0; i < 2; i++) {
            file.write(vect);
        }
        msg("taille max:" + file.getMaxLength());
        file.close();
        t.stop();
    }

    private static void testRead(DirectIOFile file, String fileName, MappingMode mapMode)
            throws IOException {
        Timer t = new Timer("----- " + file.getClass().getName());
        file.open(fileName, mapMode, readWriteMode.r, 2, (long) Math.pow(2, 10));
        file.seek(1);
        for (int i = 0; i < 2; i++) {
            byte b[] = new byte[16];
            file.read(b);
            showVector(b);
        }
        file.close();
        t.stop();
    }

//    private static void test3(MappedIOFile file, String fileName)
//            throws IOException {
//        System.out.println();
//        System.out.println("lecture aléatoire");
//        long tm = System.currentTimeMillis();
//        file.open(fileName,fileSize);
//        for (int n = 0; n < 100000; n += 1) {
//            long pos = (long)(Math.random()*fileSize);
//            file.seek(pos);
//            byte b[] = new byte[1];
//            file.read(b);
//        }
//        file.close();
//        tm = System.currentTimeMillis()-tm;
//        System.out.println(file.getClass().getName() + ": " + tm);
//        System.gc();
//    }
//
//    private static void test4(MappedIOFile file, String fileName)
//            throws IOException {
//        System.out.println();
//        System.out.println("lecture/écriture aléatoire");
//        long tm = System.currentTimeMillis();
//        file.open(fileName,fileSize);
//        for (int n = 0; n < 1000000; n += 1) {
//            long pos = (long)(Math.random()*10000000);
//            file.seek(pos);
//            byte b[] = new byte[1];
//            file.read(b);
//            pos = (long)(Math.random()*10000000);
//            file.seek(pos);
//            file.read(new byte[] {(byte)(n%256)});
//        }
//        file.close();
//        tm = System.currentTimeMillis()-tm;
//        System.out.println(file.getClass().getName() + ": " + tm);
//        System.gc();
//    }
}
