package isi.jg.idxvli.cache;

import java.util.*;
import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.util.SetOperation.*;

/**
 * impl�mentation trivial sans translation.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Cette impl�mentation est utilis�e dans les strat�gies FAST qui peuvent tenir en m�moire.
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
    } // ne peut pas d�border
}
