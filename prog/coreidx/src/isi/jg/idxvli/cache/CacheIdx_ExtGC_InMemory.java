package isi.jg.idxvli.cache;

import static isi.jg.util.Messages.*;

/**
 *cache avec gestion externe du garbage collecting.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class CacheIdx_ExtGC_InMemory implements CacheIdx {

    int maxCache; // size of  cache
    private int actualSize = 0;
    protected int[][] v; // cache of idx

    /** Creates a new instance of Cache 
     * @param size nombre de termes maximum dans le cache
     */
    public CacheIdx_ExtGC_InMemory(int size) {  // default parameters
        maxCache = size;
        v = new int[maxCache][];
    }

    public final int[] getReferenceOn(int i) {
        return v[i];
    }

    public final int[] getCopyOf(int i) {
        // msg("v"+i+" ("+v[i].length+")");
        int[] copy = new int[v[i].length];
        System.arraycopy(v[i], 0, copy, 0, v[i].length);
        return copy;
    }

    public final int v(int i, int j) {
        return v[i][j];
    }

    public final int[] getv(int i, int from, int length) {
        //msg("getv for i:"+i+","+from+","+length);
        //showVector(v[i]);
        int[] copy = new int[length];
        System.arraycopy(v[i], from, copy, 0, length);
        //showVector(copy);
        return copy;
    }

    public final void setv(int i, int j, int value) {
        v[i][j] = value;
    }

    public final void incv(int i, int j) {
        v[i][j]++;
    }

    public final int getCurrentSize() {
        return 4 * actualSize;
    }

    public final boolean isNotLoaded(int i) {
        return v[i] == null;
    }

    public final boolean isLoaded(int i) {
        return v[i] != null;
    }

    public final int length(int i) {
        return v[i].length;
    }

    public final void newVector(int wn, int initialSize) {
        v[wn] = new int[initialSize]; // allocate the new vector in a free space
        actualSize += initialSize;
    //System.out.println(actualSize+" ,"+nextErase+" ,"+Array.getLength(v[nextSlot]));
    }

    public final void registerVector(int wn, int[] reg) {
        v[wn] = reg; // record this vector in the cache
        actualSize += reg.length;
    }

    public final void releaseVector(int n) { // set to null
        if (v[n] != null) {
            actualSize -= v[n].length;
            v[n] = null;
        }
    }

    public final void resize(int n, int newSize) { // resize the vector and copy first bytes
        int oldSize = v[n].length;
        if (oldSize != newSize) {
            int[] it = new int[newSize];
            if (oldSize < newSize) {
                System.arraycopy(v[n], 0, it, 0, oldSize);
            } else {
                System.arraycopy(v[n], 0, it, 0, newSize);
            }
            v[n] = it;
            actualSize += (newSize - oldSize);
        }
    }
}
