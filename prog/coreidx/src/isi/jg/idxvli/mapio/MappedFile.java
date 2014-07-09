package isi.jg.idxvli.mapio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;

/**
 *
 * utilise la struture standard des RandomAccessFile (version de référence pour comparer la performance).
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
 * dans cette implémentation:
 * <pre> 
 *  - NOMAP: utilise les randomfile
 *  - CACHE et FULL sont implémentés avec les IO_Map
 *
 * </pre>
 */
public class MappedFile implements DirectIOFile {

    /** taille des buffer 2^DEFAULT_SLICE_SIZE = 8kb */
    public static final int DEFAULT_SLICE_SIZE = 13; // 8k
    /** nbre max des buffers=1024 */
    public static final int DEFAULT_MAX_SLICE = 10;  // 1024 buffer
    private RandomAccessFile file;
    private FileChannel channel;
    private MappedByteBuffer buffer;
    private IOMap map;
    private readWriteMode RW = readWriteMode.rw;
    private long currentPosition;
    private MappingMode mapType;

    /** Creates a new instance of MappedFile */
    public MappedFile() {
    }

    public final void open(String fileName, MappingMode _mapType, readWriteMode _RW) throws IOException {
        open(fileName, _mapType, _RW, DEFAULT_SLICE_SIZE, DEFAULT_MAX_SLICE);
    }

    public final void open(String fileName, MappingMode _mapType, readWriteMode _RW,
            int _slice2n, long _maxLength) throws IOException {
        mapType = _mapType;
        RW = _RW;
        currentPosition = 0;   // la première lecture ou écriture doit se faire à 0 si aucun seek n'est fait
        file = new RandomAccessFile(fileName, RW.name());
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            channel = file.getChannel();
            map = (new FullIOMap()).get(channel, RW, _slice2n, _maxLength);
        }
    }

    public final void close() throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.close(); // map=null; attention le close est trop lent -> erreurs
            channel.close();// channel = null;
        //System.gc();  //actuellement c'est le seul moyen pour rendre les buffeurs (vérifier l'évolution chez SUN!!)
        }
        file.close();//file = null;
    }

    public final void read(byte[] data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.read(data, currentPosition);
            currentPosition += data.length;
        } else {
            file.read(data);
        } // pas de mapping
    }

    public final void write(byte[] data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.write(data, currentPosition);
            currentPosition += data.length;
        } else {
            file.write(data);
        } // pas de mapping
    }

    /**  lire un int */
    public final int readInt() throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            int res = map.readInt(currentPosition);
            currentPosition += 4;
            return res;
        } else {
            return file.readInt();
        } // pas de mapping
    }

    /** écrire un int */
    public final void writeInt(int data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.writeInt(data, currentPosition);
            currentPosition += 4;
        } else {
            file.writeInt(data);
        } // pas de mapping
    }

    /**  lire un long */
    public final long readLong() throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            long res = map.readLong(currentPosition);
            currentPosition += 8;
            return res;
        } else {
            return file.readLong();
        } // pas de mapping
    }

    /** écrire un long */
    public final void writeLong(long data) throws IOException {
        if (mapType != MappingMode.NOMAP) { // il existe un mapping
            map.writeLong(data, currentPosition);
            currentPosition += 8;
        } else {
            file.writeLong(data);
        } // pas de mapping
    }

    public final void seek(long pos) throws IOException {
        if (mapType != MappingMode.NOMAP) // il existe un mapping
        {
            currentPosition = pos;
        } else {
            file.seek(pos);
        } // pas de mapping
    }

    /** taille maximum du fichier */
    public final long getMaxLength() throws IOException {
        if (mapType != MappingMode.NOMAP) // il existe un mapping
        {
            return map.getMaxLength();
        } else {
            return Long.MAX_VALUE;
        }
    }
}
