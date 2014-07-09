package isi.jg.idxvli.cache;

import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * gestionnaire d'un cache d'index en lecture uniquement.
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
 * Comportement d'un cache d'index en lecture uniquement.
 * Les indices des termes sont ceux utilisés par le dictionnaire 
 * (donc par forcemment ceux du cache de termes).
 * <p>
 * Dans cette version le GC doit être organisé à l'extérieur, aucune translation n'est prévue.
 * donc cacheSize = maxWord
 *
 * pour l'essentiel, cette classe crée une facade de CacheIdx sur les documents et sur les positions.
 */
public class CacheRead_Basic implements CacheRead {

    private CacheIdx_ExtGC doc;
    private CacheIdx_ExtGC pos;

    public CacheRead_Basic(int maxWord) {
        //  System.out.println("CacheRead_ExtGC:"+maxWord);
        doc = new CacheIdx_ExtGC(maxWord);
        pos = new CacheIdx_ExtGC(maxWord);
    }

    public final int[] getReferenceOnDoc(int i) {
        return doc.getReferenceOn(i);
    }

    public final int[] getCopyOfDoc(int i) {
        return doc.getCopyOf(i);
    }

    public final int vDoc(int i, int j) {
        return doc.v(i, j);
    }

    public final int[] getvDoc(int i, int from, int length) {
        return doc.getv(i, from, length);
    }

    public final boolean isNotLoadedDoc(int i) {
        return doc.isNotLoaded(i);
    }

    public final boolean isLoadedDoc(int i) {
        return doc.isLoaded(i);
    }

    public final int lengthDoc(int i) {
        return doc.length(i);
    }

    public final void registerVectorDoc(int wn, int[] reg) {
        doc.registerVector(wn, reg);
    }

    public final void releaseVectorDoc(int n) {
        doc.releaseVector(n);
    }

    public final int[] getReferenceOnPos(int i) {
        return pos.getReferenceOn(i);
    }

    public final int[] getCopyOfPos(int i) {
        return pos.getCopyOf(i);
    }

    public final int vPos(int i, int j) {
        return pos.v(i, j);
    }

    public final int[] getvPos(int i, int from, int length) {
        return pos.getv(i, from, length);
    }

    public final boolean isNotLoadedPos(int i) {
        return pos.isNotLoaded(i);
    }

    public final boolean isLoadedPos(int i) {
        return pos.isLoaded(i);
    }

    public final int lengthPos(int i) {
        return pos.length(i);
    }

    public final void registerVectorPos(int wn, int[] reg) {
        pos.registerVector(wn, reg);
    }

    public final void releaseVectorPos(int n) {
        pos.releaseVector(n);
    }

    public final int getCurrentSize() {
        return doc.getCurrentSize() + pos.getCurrentSize();
    }
}
