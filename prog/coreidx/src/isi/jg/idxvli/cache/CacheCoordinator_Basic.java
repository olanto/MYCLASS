package isi.jg.idxvli.cache;

import java.util.*;
import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

/**
 * Implémentation standard minimal du coordinateur.
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
 * Implémentation standard minimal du coordinateur. Ce coordinateur est mono-thread, donc
 * la libération du cache, se fait sans qu'il y ait des activités dans le cache.
 *
 *   modif 25.1.05  int totidxcnt -> long totidxcnt;  // trop petit!
 */
public class CacheCoordinator_Basic implements CacheCoordinator {

    private IdxStructure z;
    private CacheWrite indexdoc;
    private CacheWrite indexpos;
    private CacheTranslate idxtrans;
    private long segcnt;
    private long initStart;
    private long totidxcnt;  // total des termes indexés

    /**
     * Création du coordinateur de cache de l'indexeur (mode mise à jour).
     * @param idx indexeur de référence
     * @param indexdoc cache des documents/occurences
     * @param indexpos cache des positions
     * @param idxtrans cache des translations d'identifiants
     */
    public CacheCoordinator_Basic(IdxStructure idx, CacheWrite indexdoc, CacheWrite indexpos, CacheTranslate idxtrans) {
        z = idx;
        this.indexdoc = indexdoc;
        this.indexpos = indexpos;
        this.idxtrans = idxtrans;
    }

    public final void startTimer() {
        Date stoptime = new Date();
        initStart = stoptime.getTime();
    }

    public final void incTotalIdx() {
        totidxcnt++;
    }

    public final int cacheCurrentSize() {
        return indexdoc.getCurrentSize() + indexpos.getCurrentSize();
    }

    public final int allocate() {
        return idxtrans.allocate();
    }

    public final boolean cacheOverFlow() {
        return (cacheCurrentSize() > INDEXING_CACHE_SIZE) || (IDX_CACHE_COUNT <= allocate());
    }

    public final void freecache() {
        if (KEEP_IN_CACHE == 0) {
            freecacheFull();
        } else {
            freecachepartial();
        }
    }

    private final void freecachepartial() {
        if (IDX_CACHE_COUNT <= allocate()) {
            freecacheFull();  // libère les entrées du dictionnaire de translation
        } else {  // juste chercher les entrèes les plus grandes
            Date starttime = new Date();
            long timer1 = starttime.getTime();
            int cnt = 0, totfree = cacheCurrentSize();
            int freeSize = (int)(INDEXING_CACHE_SIZE / (long) 8192);  // taille minimum des blocs a liberer
            while (cacheCurrentSize() > ((INDEXING_CACHE_SIZE / 100) * KEEP_IN_CACHE)) { // start saving memory
                int last = idxtrans.capacity();
                for (int i = 0; i < last; i++) {
                    if (indexdoc.isLoaded(i) && indexdoc.length(i) > freeSize) {
                        cnt++;
                        segcnt++;
                        z.saveVectorW(i);
                    }
                }
                msg("freeSize:" + freeSize + " cnt:" + cnt + " actual size:" + cacheCurrentSize());
                freeSize /= 2;
            }
            Date stoptime = new Date();
            long timing = stoptime.getTime() - timer1;
            msg("doc:" + z.lastRecordedDoc + " cnt:" + cnt + " free:" + totfree + " word:" + z.wordstable.getCount() +
                    " actual size:" + cacheCurrentSize() + "Mem:" + ((int) (usedMemory() / 1024)) + "(Kb)");
            msg("mean time to save a word:" + (float) timing / (float) cnt + " ms");
            timing = stoptime.getTime() - initStart;
            msg("cnt idx word " + totidxcnt + " time(s)" + timing / 1000 + " Kword/sec:" + (totidxcnt / timing));
            msg("segcnt " + segcnt);
        }
    }

    public final void freecacheFull() {
        /* la libération du cache devrait se faire dans l'ordre des mots
         * afin de favoriser un accès sequentiel dans l'object store
         * ici on le fait dans l'ordre d'allocation du cache (pour les nouveaux mots,
         * l'ordre est respecter pour les autres, c'est le chaos ...
         * (à essayer)
         */

        Date starttime = new Date();
        long timer1 = starttime.getTime();
        int cnt = 0, totfree = cacheCurrentSize();
        int last = idxtrans.capacity();
        for (int i = 0; i < last; i++) {
            if (indexdoc.isLoaded(i)) {
                cnt++;
                segcnt++;
                z.saveVectorW(i);
            }
        }
        idxtrans.resetCache(); // annule toutes info
        msg("freeSize: (NA)  cnt:" + cnt + " actual size:" + cacheCurrentSize());
        Date stoptime = new Date();
        long timing = stoptime.getTime() - timer1;
        msg("doc:" + z.lastRecordedDoc + " cnt:" + cnt + " free:" + totfree + " word:" + z.wordstable.getCount() +
                " actual size:" + (cacheCurrentSize() / 1024) + "(Kb)  Mem:" + ((int) (usedMemory() / 1024)) + "(Kb)");
        msg("mean time to save a word:" + (float) timing / (float) cnt + " ms");
        timing = stoptime.getTime() - initStart;
        msg("cnt idx word " + totidxcnt + " time(s)" + timing / 1000 + " Kword/sec:" + (totidxcnt / timing));
        msg("segcnt " + segcnt);

    }
}
