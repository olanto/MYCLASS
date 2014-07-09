package isi.jg.idxvli.cache;

import java.util.*;
import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.util.SetOperation.*;

/**
 * implémentation trivial sans translation.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Cette implémentation est utilisée dans les stratégies FAST qui peuvent tenir en mémoire.
 */
public class CacheTranslate_OneOne implements CacheTranslate {

    static int cacheSize;

    public CacheTranslate_OneOne(int _cacheSize) {
        cacheSize = _cacheSize;
    }

    public final int registerCacheId(int wordId) {
        return wordId;
    }

    public final int getCacheId(int wordId) {
        return wordId;
    }

    public final int getWordId(int wordId) {
        return wordId;
    }

    public final void resetCache() {
    }

    public final int capacity() {
        return cacheSize;
    }

    public final int allocate() {
        return 0;
    } // ne peut pas déborder
}
