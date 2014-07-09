package isi.jg.idxvli.mapio;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;

/**
 * gère une structure de fichiers directement mappé en mémoire.
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
 * gère des fichiers à travers les buffers (implémentation de base).
 * cette implémentation est FULL.
 *
 */
public class FullIOMap implements IOMap {

    private static final boolean verbose = true;
    private FileChannel channel;
    private readWriteMode RW;
    private MappedByteBuffer[] mapping;
    private int slice2n = 13; // 13=8k   /// taille des slice
    private int comp32;
    private int sliceSize;
    private int maxSlice2n = 10;// 10=1k  /// nbr max de slice
    private int maxSlice;
    private long maxLength = (long) Math.pow(2, slice2n + maxSlice2n); // longueur maximum du mapping

    protected FullIOMap() {
    }  // sert pour le get

    /** retourne un gestionnaire de buffer des IO */
    public final IOMap get(FileChannel _channel, readWriteMode _RW) throws IOException {
        channel = _channel;
        RW = _RW;
        sliceSize = (int) Math.pow(2, slice2n);
        maxSlice = (int) (maxLength / sliceSize);
        comp32 = 32 - slice2n;
        mapping = new MappedByteBuffer[maxSlice];
        msg("FullIOMap.get slice2n:" + slice2n + " maxSlice:" + maxSlice + " sliceSize:" + sliceSize + " maxLength:" + maxLength);
        return this;
    }

    /** retourne un gestionnaire de buffer des IO, spécifiant les paramètres du mapping*/
    public final IOMap get(FileChannel _channel, readWriteMode _RW,
            int _slice2n, long _maxLength) throws IOException {
        slice2n = _slice2n;
        maxLength = _maxLength;
        return get(_channel, _RW);
    }

    /** fermer le gestionnaire */
    public final void close() throws IOException {
        for (int i = 0; i < maxSlice; i++) {
            if (mapping[i] != null) {
                msg("FullIOMap close i:" + i);
                if (RW == readWriteMode.rw) {
                    mapping[i].force();
                } // force l'écriture
                mapping[i] = null;
            }
        }
    }

    private final int getSlice(long pos) {
        return (int) pos >> slice2n;
    }

    private final int getRelPos(long pos) {
        return ((int) pos) << comp32 >>> comp32;  // peut être optimisé avec un masque !!!
    }

    private final void forceInMemory(int slice) throws IOException {
        if (mapping[slice] != null) {// déja en mémoire
            return;
        } else {  // map la tranche
            switch (RW) {
                case rw:
                    mapping[slice] = channel.map(FileChannel.MapMode.READ_WRITE, slice << slice2n, sliceSize);
                    break;
                case r:
                    mapping[slice] = channel.map(FileChannel.MapMode.READ_ONLY, slice << slice2n, sliceSize);
                    break;
            }
        }
    }

    /**  lire ces bytes à cette position*/
    public final void read(byte[] data, long pos) throws IOException {
        int readed = 0;
        while (data.length - readed != 0) {
            int s = getSlice(pos);
            int r = getRelPos(pos);
            if (verbose) {
                msg("read pos:" + pos + " slice:" + s + " rel:" + r + " readed:" + readed);
            }
            int maxInThisSlice = sliceSize - r;  // maximum que l'on peut écrire dans cette tranche
            int lengthReaded = Math.min(maxInThisSlice, data.length - readed); // longueur à écrire dans cette tranche
            if (verbose) {
                msg("   maxInThisSlice:" + maxInThisSlice + " lengthReaded:" + lengthReaded);
            }
            get(data, readed, lengthReaded, s, r);
            readed += lengthReaded;  // ajuste la longueur écrite;
            pos += lengthReaded; // ajuste la position
        }
    }

    /** lire data[from,from + length] dans la tranche slice depuis la position rel*/
    private final void get(byte[] data, int from, int length, int slice, int rel) throws IOException {
        forceInMemory(slice);
        mapping[slice].position(rel);
        mapping[slice].get(data, from, length);
    }

    /** écrire  ces bytes à cette position*/
    public final void write(byte[] data, long pos) throws IOException {
        int written = 0;
        while (data.length - written != 0) {
            int s = getSlice(pos);
            int r = getRelPos(pos);
            if (verbose) {
                msg("write pos:" + pos + " slice:" + s + " rel:" + r + " written:" + written);
            }
            int maxInThisSlice = sliceSize - r;  // maximum que l'on peut écrire dans cette tranche
            int lengthWritten = Math.min(maxInThisSlice, data.length - written); // longueur à écrire dans cette tranche
            if (verbose) {
                msg("   maxInThisSlice:" + maxInThisSlice + " lengthWritten:" + lengthWritten);
            }
            put(data, written, lengthWritten, s, r);
            written += lengthWritten;  // ajuste la longueur écrite;
            pos += lengthWritten; // ajuste la position
        }
    }

    /** écrire data[from,from + length] dans la tranche slice depuis la position rel*/
    private final void put(byte[] data, int from, int length, int slice, int rel) throws IOException {
        forceInMemory(slice);
        mapping[slice].position(rel);
        mapping[slice].put(data, from, length);
    }

    /**  lire un int à cette position*/
    public final int readInt(long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        if (maxInThisSlice >= 4) {
            forceInMemory(s);
            mapping[s].position(r);
            return mapping[s].getInt();
        } else {
            error_fatal("not align, int across two slice");
        } // pas aligé
        return 0;
    }

    /** écrire un int à cette position*/
    public final void writeInt(int data, long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        // msg("writeInt pos:"+pos+" r:"+r+" maxInThisSlice:"+maxInThisSlice);
        if (maxInThisSlice >= 4) {
            forceInMemory(s);
            mapping[s].position(r);
            mapping[s].putInt(data);
        } else {
            error_fatal("not align, int across two slice pos:" + pos + " r:" + r + " maxInThisSlice:" + maxInThisSlice);
        } // pas aligé

    }

    /**  lire un long à cette position*/
    public final long readLong(long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        if (maxInThisSlice >= 8) {
            forceInMemory(s);
            mapping[s].position(r);
            return mapping[s].getLong();
        } else {
            error_fatal("not align, int across two slice");
        } // pas aligé
        return 0;
    }

    /** écrire un long à cette position*/
    public final void writeLong(long data, long pos) throws IOException {
        int s = getSlice(pos);
        int r = getRelPos(pos);
        int maxInThisSlice = sliceSize - r;
        // msg("writeInt pos:"+pos+" r:"+r+" maxInThisSlice:"+maxInThisSlice);
        if (maxInThisSlice >= 8) {
            forceInMemory(s);
            mapping[s].position(r);
            mapping[s].putLong(data);
        } else {
            error_fatal("not align, int across two slice pos:" + pos + " r:" + r + " maxInThisSlice:" + maxInThisSlice);
        } // pas aligé

    }

    public final long getMaxLength() {
        return maxLength;
    }
}
