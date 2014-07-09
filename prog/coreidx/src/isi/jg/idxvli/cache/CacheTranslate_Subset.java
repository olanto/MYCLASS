package isi.jg.idxvli.cache;

import java.util.*;
import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.util.SetOperation.*;

/**
 * gère un espace d'adressage qui est plus grand que celui du cache.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * gère un espace d'adressage qui est plus grand que celui du cache.
 */
public class CacheTranslate_Subset implements CacheTranslate {

    static int cacheSize;
    static Hashtable<Integer, Integer> wc;  // du word id -> cache id
    static Hashtable<Integer, Integer> cw;  // du word id -> cache id
    static int nextCacheId = -1;  // next val =0;
    static long vdoc,  getvdoc,  vpos,  getvpos;  // to debug

    /** crée un tranlateur vers un espace de taille max _cacheSize
     * @param _cacheSize taille max
     */
    public CacheTranslate_Subset(int _cacheSize) {
        //  System.out.println("CacheRead_ExtGC:"+maxSize);
        cacheSize = _cacheSize;
        wc = new Hashtable<Integer, Integer>(2 * cacheSize);
        cw = new Hashtable<Integer, Integer>(2 * cacheSize);
        nextCacheId = 0;
    }

    private final int nextCacheId() {
        return nextCacheId++; // on a le bon candidat
    }

    public final int registerCacheId(int wordId) {
        Integer res = wc.get(wordId);
        if (res == null) {
            int nextId = nextCacheId();
            wc.put(wordId, nextId);
            cw.put(nextId, wordId);
            return nextId;
        }
        return res.intValue();
    }

    public final int getCacheId(int wordId) {
        return wc.get(wordId);
    }

    public final int getWordId(int cacheId) {
        return cw.get(cacheId);
    }

    public final void resetCache() {
        wc = new Hashtable<Integer, Integer>(2 * cacheSize);
        cw = new Hashtable<Integer, Integer>(2 * cacheSize);
        nextCacheId = 0;
    }

    public final int capacity() {
        return cacheSize;
    }

    public final int allocate() {
        return nextCacheId + 1;
    } // 
}
