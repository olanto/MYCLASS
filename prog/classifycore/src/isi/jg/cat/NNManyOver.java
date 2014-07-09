package isi.jg.cat;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.text.*;
import java.lang.reflect.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.extra.*;
import isi.jg.mnn.*;
import isi.jg.util.Timer;

/**
 * Une classe pour effectuer la classification des documents
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * toute autre utilisation est sujette à autorisation
 * <hr>
 *
 * *
 */
class NNManyOver {

    static int MAXNN = 1000; //4+3*8+2*114+451;
    static final int MODE_LEARN = 0;
    static final int BLOCKCOMPRESS = 5000; // compression size factorisation
    static int MAXCLASS = 25000;//460; // in term of disk block of 4k or 8k ...
    static boolean verbose = true; // no comment
    static boolean benchmode = true; // only for benchmark
    int mode;
    static String fileName;
    String NNName;
    Hashtable NNT;  // to store index on a NN exemple C12Q is 33 (NN)
    int lastNNT = 0;
    // global for all NN
    //Hashtable Finaldocgroup, Finalgroupinv, Finalgroup;
    static int maxused;
    static int[] wordAtIdx;
    static int[] wordFreq;
    static int[] lengthcompress;
    static long[] idxcompress;
    static float[] sdf = new float[DocBag.MAXOCCINDOC];  // weighting of feature
    static float[] alfaPn;     // puissance de alfa pour ositive Winnow compact
    static float[] alfaNn;     // puissance de alfa pour ositive Winnow compact
    // for each NN
    Hashtable[] groupinv;
    int[] maxgroup;
    byte[][] worduse;
    int[] firstmnn;          // offset for a net
    // random MNN
    static RandomAccessFile rmnn;
    static RandomAccessFile cmnn;
    static int lastfreemnn;  // next free position
    static final boolean nowrapboolean = true;
    static Deflater def = new Deflater(9, nowrapboolean);
    static Inflater inf = new Inflater(nowrapboolean);

    void initMNNfile() {
        byte[] byteidx = new byte[MAXCLASS];
        Timer t1 = new Timer("Start init MNN file");
        long lastpos = 0;
        try {
            for (int j = 0; j < maxused; j++) { // init all net  -> so they can be compress ... (later)
                rmnn.seek(lastpos);
                rmnn.write(byteidx, 0, MAXCLASS);
                lastpos += MAXCLASS;
            }
        } catch (Exception e) {
            System.err.println("IO error in initMNNfile");
            e.printStackTrace();
        }
        t1.stop();
    }

    static void compressMNN() {
        byte[][] byteidx = new byte[BLOCKCOMPRESS][MAXCLASS];
        Timer t1 = new Timer("compress MNN file");
        lengthcompress = new int[maxused];
        idxcompress = new long[maxused];
        long lastposcompress = 0;
        int NBBLOCK = maxused / BLOCKCOMPRESS + 1;
        try {
            rmnn = new RandomAccessFile(fileName + ".rmnn", "r");
            cmnn = new RandomAccessFile(fileName + ".cmnn", "rw");
            byte[] bytecomp = new byte[MAXCLASS];
            for (int i = 0; i < NBBLOCK; i++) { // nbr of block
                System.out.print(".");
                int readlength = BLOCKCOMPRESS;
                if (i * BLOCKCOMPRESS > maxused) {
                    readlength = maxused - (i - 1) * BLOCKCOMPRESS;
                }
                byte[] slice = new byte[BLOCKCOMPRESS];
                for (int j = 0; j < MAXCLASS; j++) { // load a block
                    //System.out.println(j+","+i+","+maxused+","+((long)j*(long)maxused+(long)i*(long)BLOCKCOMPRESS));
                    rmnn.seek((long) j * (long) maxused + (long) i * (long) BLOCKCOMPRESS);
                    rmnn.read(slice, 0, readlength);
                    for (int k = 0; k < readlength; k++) { // transpose
                        byteidx[k][j] = slice[k];
                    }
                }
                for (int j = 0; j < readlength; j++) { // compresss a block
                    if (j + i * BLOCKCOMPRESS >= maxused) {
                        break; //  the last block is incomplete ...
                    }
                    def.setInput(byteidx[j]);
                    def.finish();
                    def.deflate(bytecomp);
                    int compresslength = def.getTotalOut();
                    def.reset();  // free for a new use
                    cmnn.seek(lastposcompress);
                    cmnn.write(bytecomp, 0, compresslength);
                    idxcompress[j + i * BLOCKCOMPRESS] = lastposcompress;
                    lengthcompress[j + i * BLOCKCOMPRESS] = compresslength;
                    lastposcompress += compresslength;
                //System.out.println("compress "+(j+i*BLOCKCOMPRESS)+" : "+lengthcompress[j]);
                }
            }
        } catch (Exception e) {
            System.err.println("IO error in compressMNN");
            e.printStackTrace();
        }
        System.out.println(".");
        t1.stop();
    }

    void save() {
        try {
            rmnn.close();  // random file
            compressMNN();
            FileOutputStream ostream = new FileOutputStream(fileName);
            ObjectOutputStream p = new ObjectOutputStream(ostream);
            p.writeObject(NNName);
            p.writeObject(NNT);
            p.writeInt(lastNNT);
            p.writeObject(wordAtIdx);
            p.writeObject(wordFreq);
            p.writeObject(idxcompress);
            p.writeObject(lengthcompress);
            p.writeInt(maxused);
            p.writeObject(sdf);
            p.writeObject(alfaPn);
            p.writeObject(alfaNn);
            p.writeObject(groupinv);
            p.writeObject(maxgroup);
            p.writeObject(worduse);
            // p.writeObject(nnc);
            p.writeInt(lastfreemnn);
            p.writeObject(firstmnn);
            System.err.println("end of NNmany save");
            p.flush();
            ostream.close();
            System.out.println("#NN:" + lastNNT + "#NN-cat:" + lastfreemnn);

        } catch (IOException e) {
            System.err.println("IO error in save NN");
            e.printStackTrace();
        }
    }

    NNManyOver(String _NNName, int _mode, String _fileName,
            int _MAXNN, int _MAXCLASS) {
        mode = _mode;
        fileName = _fileName;
        NNName = _NNName;
        MAXNN = _MAXNN;
        MAXCLASS = _MAXCLASS;

        if (mode == MODE_LEARN) {
            worduse = new byte[MAXNN][];
            NNT = new Hashtable();
            groupinv = new Hashtable[MAXNN];
            maxgroup = new int[MAXNN];
            firstmnn = new int[MAXNN];
            // a NNOne must be init a this point
            maxused =  NNOneN_OverSampling.maxused;
            wordAtIdx =  NNOneN_OverSampling.wordAtIdx;
            wordFreq =  NNOneN_OverSampling.wordFreq;
            sdf =  NNOneN_OverSampling.sdf;
            alfaPn =  NNOneN_OverSampling.alfaPn;
            alfaNn =  NNOneN_OverSampling.alfaNn;
            try {
                rmnn = new RandomAccessFile(fileName + ".rmnn", "rw");
            } catch (Exception e) {
                System.err.println("IO error in open MNN file");
                e.printStackTrace();
            }
            initMNNfile();      // only if maxused is modified
            lastfreemnn = 0;
        } else {
        }

    }

    void RecordNN() {
        String NetName =  NNOneN_OverSampling.categorylevel + "." +  NNOneN_OverSampling.prefix;
        Integer n = (Integer) NNT.get(NetName);
        if (n == null) { // a new one
            if (lastNNT < MAXNN) {
                if (verbose) {
                    System.out.println("recording NN: " + NetName + " @" + lastNNT);
                }
                NNT.put(NetName, new Integer(lastNNT));
                // copy ref from group
                groupinv[lastNNT] =  NNOneN_OverSampling.ActiveGroup.groupinv;
                // copy ref from NNOne
                worduse[lastNNT] =  NNOneN_OverSampling.worduse;
                // nnc[lastNNT]=NNOne.nnc;
                maxgroup[lastNNT] =  NNOneN_OverSampling.maxgroup;
                if (lastfreemnn +  NNOneN_OverSampling.maxgroup <= MAXCLASS) {
                    try {
                        // transpose before saving
                        byte[] btr = new byte[maxused];
                        for (int j = 0; j <  NNOneN_OverSampling.maxgroup; j++) { // for each group
                            for (int i = 0; i < maxused; i++) { // for each feature
                                btr[i] =  NNOneN_OverSampling.nnc[i][j];
                            }
                            rmnn.seek((long) maxused * ((long) j + (long) lastfreemnn));
                            rmnn.write(btr, 0, maxused);
                        }
                    } catch (Exception e) {
                        System.err.println("IO error in recording MNN file");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("*** ERROR*** recording aborted MAXCLASS is too small: " + MAXCLASS);
                }
                firstmnn[lastNNT] = lastfreemnn;
                lastNNT++; // next
                lastfreemnn +=  NNOneN_OverSampling.maxgroup;
            } else {
                System.out.println("*** ERROR*** recording aborted MAXNNT is too small: " + MAXNN);
            }
        } else {
            System.out.println("*** ERROR*** already recorded: " + NetName);
        }
    }
}
