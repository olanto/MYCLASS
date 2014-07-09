package isi.jg.idxvli.util;

import java.io.*;
import java.util.zip.*;
import static isi.jg.util.Messages.*;
import isi.jg.util.TimerNano;

/**
 * Classe gérant des opérations courante (fichiers, copie, compression, etc).
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
 * Classe gérant des opérations courante (fichiers, copie, compression, etc).
 */
public class BytesAndFiles {

    /** valeur OK */
    public static final int STATUS_OK = 0;
    /** valeur ERREUR */
    public static final int STATUS_ERROR = -1;
    /** valeur SIMULATION */
    public static final int STATUS_SIMULATION = -2;
    private static final boolean nowrapboolean = true;  // no wrap pour le ZIP

    private static Deflater def = new Deflater(Deflater.DEFAULT_COMPRESSION, nowrapboolean);
    private static Deflater def2 = new Deflater(Deflater.DEFAULT_COMPRESSION, nowrapboolean);
    private static Inflater inf = new Inflater(nowrapboolean);
    private static Inflater inf2 = new Inflater(nowrapboolean);

    /**
     * lit le contenu d'un fichier texte encodé
     * @param fname nom du fichier
     * @param txt_encoding encodage
     * @return le contenu du fichier
     */
    public static final String file2String(String fname, String txt_encoding) {
        StringBuffer txt = new StringBuffer("");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), txt_encoding);
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                txt.append(w);
                txt.append("\n");
                w = in.readLine();
            }
            return txt.toString();
        } catch (Exception e) {
            //error("file2String", e);
            return null;
        }

    }

    /**
     *  stocke w encodé à la position pos du fichier r (si n'excède pas maxLengthString) et retourne 0 si ok sinon <0
     * @param w chaîne à écrire
     * @param pos position
     * @param encode encodage
     * @param maxLengthString  longueur max du string converti en bytes
     * @param r fichier
     * @return status
     */
    public static final int writeString(String w, long pos, String encode, int maxLengthString, RandomAccessFile r) {
        byte[] bw = convertString2Bytes(w, encode);
        if (bw.length > maxLengthString) {
            error("*** error name too long '" + w + "' maxbyteslength:" + maxLengthString);
            writeString("!", pos, encode, maxLengthString, r); // on écrit une marque d'erreur
            return STATUS_ERROR;
        }
        try {
            r.seek(pos); // position la tête sur le début du block

            r.writeInt(bw.length); // écrit la longueur du string

            r.write(bw);
            return STATUS_OK;
        } catch (Exception e) {
            error("IO error writeString", e);
            return STATUS_ERROR;  // en erreur

        }
    }

    /**
     *  lit w à la position pos du fichier r
     * @param pos position
     * @param encode encodage
     * @param r fichier
     * @return chaîne lue
     */
    public static final String readString(long pos, String encode, RandomAccessFile r) {
        try {
            r.seek(pos); // position la tête sur le début du block

            byte[] bw = new byte[r.readInt()];
            r.read(bw);
            return convertBytes2String(bw, encode);
        } catch (Exception e) {
            error("IO error readString", e);
            return null;  // en erreur

        }
    }

    /**
     *  stocke n à cette position et retourne 0 si ok sinon <0
     * @param n entier à écrire
     * @param pos position
     * @param r fichier
     * @return status
     */
    public static final int writeInt(int n, long pos, RandomAccessFile r) {
        int[] b = new int[1];
        b[0] = n;
        byte[] byteidx = new byte[b.length * 4];  // convertit les int en byte

        intTobyte(b, b.length * 4, byteidx);
        return writeBytes(byteidx, pos, r);
    }

    /**
     *  stock sur le disk ces bytes à cette position
     * retourne 0 si ok sinon <0
     * @param b byte à écrire
     * @param pos position
     * @param r fichier
     * @return status
     */
    public static final int writeBytes(byte[] b, long pos, RandomAccessFile r) {
        try {
            r.seek(pos); // position la tête sur le début du block

            r.write(b); // ecrit le vecteur
            //msg("write "+b.length+" Bytes at "+pos+" in "+r);showVector(b);

            return STATUS_OK;
        } catch (Exception e) {
            error("IO error instoreNBytes", e);
            return STATUS_ERROR;  // en erreur

        }
    }

    /**
     *  read int à la position pos
     *   il est évident que le status ne peut être testé si on travaille avec -1 !!!
     * @param pos position
     * @param r fichier
     * @return valeur
     */
    public static final int readInt(long pos, RandomAccessFile r) {
        try {
            int[] b = new int[1];
            byte[] byteidx = new byte[4];
            r.seek(pos);
            r.read(byteidx);
            byteToint(b, 4, byteidx);
            return b[0];
        } catch (Exception e) {
            error("IO error readInt", e);
            return STATUS_ERROR;  // en erreur

        }
    }

    /**
     *  lire n bytes à cette position
     * retourne le vecteur si ok sinon null
     * @param n nombres de bytes à lire
     * @param pos position
     * @param r fichier
     * @return valeurs
     */
    public static final byte[] readBytes(int n, long pos, RandomAccessFile r) {
        if (n <= 0) {
            return null;
        }
        try {
            byte[] byteidx = new byte[n];
//            TimerNano trSEEK= new TimerNano("read byte",true);
            r.seek(pos); // position la tête sur le début du block
//            long readtimeseek=trSEEK.stop(true);
//            msg("pos :"+pos+" length:"+byteidx.length+" : time "+readtimeseek+"[us]");
//            TimerNano trPOS= new TimerNano("read byte",true);
//        Runtime runtime = Runtime.getRuntime();
//        msg("memory alloc/free: " + (runtime.totalMemory()/1024/1024)+" / "+(runtime.freeMemory()/1024/1024)); //la mémoire actuellement utilisée

            r.read(byteidx); // lire le vecteur
//            long readtime=trPOS.stop(true);
//            msg("     readbyte time "+ readtime+"[us]");//msg("read "+n+" Bytes at "+pos+" in "+r);showVector(byteidx);

            return byteidx;
        } catch (Exception e) {
            error("IO error readBytes", e);
            return null;  // en erreur

        }
    }

    /**
     *  converti de byte en String avec un encodage 'UTF-8' par exemple
     * @param bs bytes à convertir
     * @param encode encodage
     * @return valeur convertie
     */
    public static final String convertBytes2String(byte[] bs, String encode) {
        //showVector(bs);
        try {
            String s = new String(bs, encode);
            //msg(s);
            return s;
        } catch (Exception e) {
            error("IO error convertBytes2String", e);
        }
        return null;
    }

    /**
     *  converti de String en byte avec un encodage 'UTF-8' par exemple
     * @param s chaîne à convertir
     * @param encode encodage
     * @return bytes
     */
    public static final byte[] convertString2Bytes(String s, String encode) {
        try {
            byte[] bs = s.getBytes(encode);
            //msg(s);
            //showVector(bs);
            return bs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** copier le vecteur d'entiers dans un  vecteur de bytes.
     * @param v vecteur d'entier
     * @param lb longeur de copie en byte (*4)
     * @param b vecteur de bytes
     */
    public static final void intTobyte(int[] v, int lb, byte[] b) {
        // limit to 500'000'000!
        int r;
        for (int i = 0; i < lb; i += 4) {
            //msg(i+","+(i>>>2));
            r = v[i >>> 2]; // div 4 !!!

            b[i + 3] = (byte) r;
            r >>= 8;
            b[i + 2] = (byte) r;
            r >>= 8;
            b[i + 1] = (byte) r;
            r >>= 8;
            b[i] = (byte) r;
        }
    }

    /** copier le vecteur de bytes dans un vecteur d'entiers.
     * @param v vecteur d'entier
     * @param lb longeur de copie en byte (*4)
     * @param b vecteur de bytes
     */
    public static final void byteToint(int[] v, int lb, byte[] b) {
        // limit to 500'000'000!
        int t;
        for (int i = 0; i < lb; i += 4) {
            // msg(i+","+(i>>>2));
            t = b[i];
            t <<= 8;
            t |= (b[i + 1] << 24) >>> 24;
            t <<= 8;
            t |= (b[i + 2] << 24) >>> 24;
            t <<= 8;
            t |= (b[i + 3] << 24) >>> 24;
            v[i >>> 2] = t; // div 4 !!!

        }
    }

    /** incrémente la taille d'un vecteur et recopie la totalité si plus grand,
     * si l'incrément est négatif seul les n premiers bytes sont copiés
     * @param v vecteur d'entier
     * @param increment +ajout/-troncation
     * @return nouveau vecteur
     */
    public static final int[] incrementSize(int[] v, int increment) {
        // si l'incrément est négatif seul les n premiers bytes sont copiés
        int oldSize = v.length;
        int newSize = oldSize + increment;
        int[] it = new int[newSize];
        if (oldSize < newSize) {
            System.arraycopy(v, 0, it, 0, oldSize);
        } else {
            System.arraycopy(v, 0, it, 0, newSize);
        }
        return it;
    }

    /** copier le vecteur p dans un nouveau vecteur de longueur l.
     * Si p est plus petit que l alors la fin sera remplie de zéro.
     * Si p est plus grand alors la fin de p sera tronquée.
     * @param p vecteur
     * @param l longueur du nouveau vecteur
     * @return nouveau vecteur
     */
    public static final int[] copyVector(int l, int[] p) {
        int[] r = new int[l];
        int maxi = Math.min(l, p.length); //  if p.lenght<l last will be fill with 0

        System.arraycopy(p, 0, r, 0, maxi);
        return r;
    }

    /** copier le vecteur p dans un nouveau vecteur de longueur l.
     * Si p est plus petit que l alors la fin sera remplie de zéro.
     * Si p est plus grand alors la fin de p sera tronquée.
     * @param p vecteur
     * @param l longueur du nouveau vecteur
     * @return nouveau vecteur
     */
    public static final float[] copyVector(int l, float[] p) {
        float[] r = new float[l];
        int maxi = Math.min(l, p.length); //  if p.lenght<l last will be fill with 0

        System.arraycopy(p, 0, r, 0, maxi);
        return r;
    }

    /** copier le vecteur p dans un nouveau vecteur de longueur l.
     * Si p est plus petit que l alors la fin sera remplie de zéro.
     * Si p est plus grand alors la fin de p sera tronquée.
     * @param p vecteur
     * @param l longueur du nouveau vecteur
     * @return nouveau vecteur
     */
    public static final byte[] copyVector(int l, byte[] p) {
        byte[] r = new byte[l];
        int maxi = Math.min(l, p.length); //  if p.lenght<l last will be fill with 0

        System.arraycopy(p, 0, r, 0, maxi);
        return r;
    }

    /** compresser un int[], genre ZIP
     * @param bi le vecteur a compresser
     * @return le vecteur de byte compressé
     */
    public synchronized static final byte[] compress(int[] bi) { // partage un compresseur commun en cas de //

        byte[] bb = new byte[bi.length * 4];
        byte[] bytecomp = new byte[bi.length * 4];
        intTobyte(bi, bi.length * 4, bb);

        def2.setInput(bb);
        def2.finish();
        def2.deflate(bytecomp);
        //showVector(bytecomp);
        int compresslength = def2.getTotalOut();
        def2.reset();  // free for a new use
        //msg("save :"+compresslength+" for:"+bi.length);

        return copyVector(compresslength, bytecomp);
    }

    /** compresser un byte[], genre ZIP
     * @param bb le vecteur a compresser
     * @return le vecteur de byte compressé
     */
    public synchronized static final byte[] compress(byte[] bb) { // partage un compresseur commun en cas de //

        byte[] bytecomp = new byte[bb.length + 100];  // pour les petites choses la compression peut être plus grande

        def.setInput(bb);
        def.finish();
        def.deflate(bytecomp);
        //showVector(bytecomp);
        int compresslength = def.getTotalOut();
        def.reset();  // free for a new use
        //msg("save :"+compresslength+" for:"+bi.length);

        return copyVector(compresslength, bytecomp);
    }

    /** compresser un int[] Variable Int
     * @param bi le vecteur a compresser
     * @return le vecteur de byte compressé
     */
    public static final byte[] compressVInt(int[] bi) { //

        byte[] bb = new byte[bi.length * 4];  // normalement doit être *5, on espère une compression peut donc générer des erreurs si pas adapté!!!

        int k = 0;
        for (int j = 0; j < bi.length; j++) {
            int itov = bi[j];
            while ((itov & ~0x7F) != 0) {
                bb[k] = (byte) ((itov & 0x7f) | 0x80);
                k++;
                itov >>>= 7;
            }
            bb[k] = (byte) itov;
            k++;
        }
        // recopie la partie ok
        byte[] r = new byte[k];
        System.arraycopy(bb, 0, r, 0, k);
        return r;
    }

    /** décompresser un int[] Variable Int
     * @param bb le vecteur a décompresser
     * @param maxSize la taille maximum du vecteur attendu
     * @return le vecteur de int décompressé
     */
    public static final int[] decompressVInt(byte[] bb, int maxSize) {
        //  TimerNano trPOS= new TimerNano("decompress",true);
        //msg("decompressVInt:"+maxSize);
        int[] bi = new int[maxSize];
        int k = 0;
        for (int j = 0; j < maxSize; j++) {
            byte b = bb[k];
            k++;
            bi[j] = b & 0x7F;
            for (int shift = 7; (b & 0x80) != 0; shift += 7) {
                b = bb[k];
                k++;
                bi[j] |= (b & 0x7F) << shift;
            //msg("j:"+j+" k:"+k+" bi[j]"+bi[j]);
            }
        }
//        long readtime=trPOS.stop(true);
//        msg("   decompress+"+bb.length+" > "+bi.length*4+" : time "+ readtime+"[us]");
//        showVector(bi);
        return bi;
    }

    /** compresser un int[] Variable Int
     * @param bi le vecteur a compresser
     * @return le vecteur de byte compressé
     */
    public static final byte[] compressVInt(int[] bi, int to) { //

        byte[] bb = new byte[to * 4];  // normalement doit être *5, on espère une compression peut donc générer des erreurs si pas adapté!!!

        int k = 0;
        for (int j = 0; j < to; j++) {
            int itov = bi[j];
            while ((itov & ~0x7F) != 0) {
                bb[k] = (byte) ((itov & 0x7f) | 0x80);
                k++;
                itov >>>= 7;
            }
            bb[k] = (byte) itov;
            k++;
        }
        // recopie la partie ok
        byte[] r = new byte[k];
        System.arraycopy(bb, 0, r, 0, k);
        return r;
    }

    /** décompresser un byte[]
     * @param bb le vecteur a décompresser
     * @param maxSize la taille maximum du vecteur attendu
     * @return le vecteur de int décompressé
     */
    public synchronized static final int[] decompress(byte[] bb, int maxSize) { // partage un compresseur commun en cas de //
        //msg("bb length:"+bb.length);
        //msg("maxSize:"+maxSize);

        int[] bi = new int[maxSize];
        byte[] bytedecomp = new byte[maxSize * 4];
        try {
            inf2.setInput(bb);
            inf2.inflate(bytedecomp);
            inf2.reset();

        } catch (Exception e) {
            error("ZIP decompress", e);
            return null;
        }
        byteToint(bi, bytedecomp.length, bytedecomp);
        return bi;
    }

    /** décompresser un byte[]
     * @param bb le vecteur a décompresser
     * @param realSize la taille maximum du vecteur attendu
     * @return le vecteur de int décompressé
     */
    public synchronized static final byte[] decompress(int realSize, byte[] bb) { // partage un compresseur commun en cas de //

        byte[] bytedecomp = new byte[realSize];
        try {
            inf.setInput(bb);
            inf.inflate(bytedecomp);
            inf.reset();

        } catch (Exception e) {
            error("ZIP decompress", e);
            return null;
        }
        return bytedecomp;
    }

    /**
     * indique la mémoire utilisée par l'indexeur
     * @return la mémoire utilisée
     */
    public static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory(); //la mémoire actuellement utilisée

    }

    /**
     * affiche la mémoire utilisée par l'indexeur
     * @param s libellé de l'affichage
     */
    public static void usedMemory(String s) {
        Runtime runtime = Runtime.getRuntime();
        msg(s + ": " + (runtime.totalMemory() - runtime.freeMemory())); //la mémoire actuellement utilisée

    }

    /**
     * Compacte la mémoire et affiche la mémoire utilisée par l'indexeur 
     * @param s libellé de l'affichage
     */
    public static void compactMemory(String s) {
        usedMemory("Before GC " + s);
        System.gc();
        usedMemory("After GC " + s);
    }
}
