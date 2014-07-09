package isi.jg.idxvli.cache;

import java.util.*;
import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.util.SetOperation.*;
import static isi.jg.idxvli.IdxConstant.*;

/**
 * Cette classe est une quasi implémentation de CacheRead.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * <p>
 * Cette classe est une quasi implémentation de CacheRead. Pour des raisons de performance,
 * et de sécurité elle n'est pas une implémenation. et les méthodes release sont private.
 *
 * <p>
 * par rapport à CacheRead_Basic, elle gère parfaitement les caches, les gc pendant son utilisation
 * et elle ajoute des accès direct au cache.
 *
 *
 *  modif 9.2.2006 pour ne pas tout jeter lors des gc()
 *  on utilise un compteur de d'utilisation pour jeter les index les moins accédés
 */

/* les méthodes du query doivent être optimisées pour utilisé les méthodes directes */
public class CacheRead_Opti /* implements CacheRead */ {

    static CacheIdx_ExtGC_InMemory doc;
    static CacheIdx_ExtGC_InMemory pos;
    static int[] lock;
    static UsageMark[] usage;
    static int[] nbDoc;
    static int[] countUsage; // count les utilisations
    static int maxSize;
    static long cacheSize;
    static IdxStructure z;
    static Hashtable<Integer, Integer> wc;  // du word id -> cache id
    static Hashtable<Integer, Integer> cw;  // du word id -> cache id
    static int nextCacheId = 0;  // next val =0;
    //   static long vdoc, getvdoc, vpos, getvpos;  // to debug
    static long loadAsk,  loadFoundInCache,  loadFromDisk;

    /**
     * crée un dictionnaire de cache pouvant alloué _maxSize termes et ayant une taille de _cacheSize
     * @param idx indexeur de référence
     * @param _maxSize nbre de termes max dans le cache
     * @param _cacheSize taille total du cache
     */
    public CacheRead_Opti(IdxStructure idx, int _maxSize, long _cacheSize) {
        //  System.out.println("CacheRead_ExtGC:"+maxSize);
        z = idx;
        maxSize = _maxSize;
        cacheSize = _cacheSize;
        doc = new CacheIdx_ExtGC_InMemory(maxSize);
        pos = new CacheIdx_ExtGC_InMemory(maxSize);
        lock = new int[maxSize];
        usage = new UsageMark[maxSize];
        countUsage = new int[maxSize];
        nbDoc = new int[maxSize];
        for (int i = 0; i < maxSize; i++) {
            usage[i] = UsageMark.UNUSED;
        }  // init buffer
        wc = new Hashtable<Integer, Integer>(2 * maxSize);
        cw = new Hashtable<Integer, Integer>(2 * maxSize);
    }

    /**
     * retourne une référence sur le cache du terme i. On travaille donc directement dans le cache!
     * @param i terme
     * @return veteur original
     */
    public final int[] getReferenceOnDoc(int i) {
        return doc.getReferenceOn(wc.get(i));
    }

    /**
     * retourne une copie du cache du terme i
     * @param i terme
     * @return veteur
     */
    public final int[] getCopyOfDoc(int i) {
        return doc.getCopyOf(wc.get(i));
    }

    /**
     * retourne la valeur du cache du terme i à la position j
     * @param i terme
     * @param j position
     * @return valeur
     */
    public final int vDoc(int i, int j) {
        return doc.v(wc.get(i), j);
    }

    /**
     * retourne la valeur du cache du terme cacheID à la position j
     * @param cacheID identifiant du cache
     * @param j position
     * @return valeur
     */
    public final int vDocDirect(int cacheID, int j) {
        return doc.v(cacheID, j);
    }

    /**
     * retourne le vecteur des valeurs du cache du terme i de from sur la longueur donnée
     * @param i terme
     * @param from position
     * @param length longeur
     * @return vecteur des valeurs
     */
    public final int[] getvDoc(int i, int from, int length) {
        return doc.getv(wc.get(i), from, length);
    }

    /**
     * détermine si le terme i est pas dans le cache
     * @param i terme
     * @return true=le terme i est pas dans le cache
     */
    public final boolean isNotLoadedDoc(int i) {
        return doc.isNotLoaded(wc.get(i));
    }

    /**
     * détermine si le terme i est  dans le cache
     * @param i terme
     * @return true=le terme i est  dans le cache
     */
    public final boolean isLoadedDoc(int i) {
        return doc.isLoaded(wc.get(i));
    }

    /**
     * retourne la taille du vecteur i du cache (attention pas forcemment le nombre de documents)
     * @param i terme
     * @return  taille du vecteur i
     */
    public final int lengthDoc(int i) {
        return doc.length(wc.get(i));
    }

    /**
     * retourne le nombre de documents contenant le terme i
     * @param i terme
     * @return  nombre de documents
     */
    public final int getNbDoc(int i) {
        return nbDoc[wc.get(i)];
    }

    /**
     * inscrit un nouveau terme dans le cache avec un vecteur initial
     * @param wordId terme
     * @param reg vecteur initial
     */
    public final void registerVectorDoc(int wordId, int[] reg) {
        doc.registerVector(wc.get(wordId), reg);
    }

    /**
     * libère le terme i dans le cache
     * @param i terme
     */
    private final void releaseVectorDoc(int wordId) {
        doc.releaseVector(wc.get(wordId));
    }

    /**
     * retourne une référence sur le cache du terme i. On travaille donc directement dans le cache!
     * @param i terme
     * @return veteur original
     */
    public final int[] getReferenceOnPos(int i) {
        return pos.getReferenceOn(wc.get(i));
    }

    /**
     * retourne une copie du cache du terme i
     * @param i terme
     * @return veteur
     */
    public final int[] getCopyOfPos(int i) {
        return pos.getCopyOf(wc.get(i));
    }

    /**
     * retourne la valeur du cache du terme i à la position j
     * @param i terme
     * @param j position
     * @return valeur
     */
    public final int vPos(int i, int j) {
        return pos.v(wc.get(i), j);
    }

    /**
     * retourne la valeur du cache du terme cacheId à la position j
     * @param cacheId terme
     * @param j position
     * @return valeur
     */
    public final int vPosDirect(int cacheId, int j) {
        return pos.v(cacheId, j);
    }

    /**
     * retourne le vecteur des valeurs du cache du terme i de from sur la longueur donnée
     * @param i terme
     * @param from position
     * @param length longeur
     * @return vecteur des valeurs
     */
    public final int[] getvPos(int i, int from, int length) {
        return pos.getv(wc.get(i), from, length);
    }

    /**
     * détermine si le terme i est pas dans le cache
     * @param i terme
     * @return true=le terme i est pas dans le cache
     */
    public final boolean isNotLoadedPos(int i) {
        return pos.isNotLoaded(wc.get(i));
    }

    /**
     * détermine si le terme i est  dans le cache
     * @param i terme
     * @return true=le terme i est  dans le cache
     */
    public final boolean isLoadedPos(int i) {
        return pos.isLoaded(wc.get(i));
    }

    /**
     * retourne la taille du vecteur i du cache
     * @param i terme
     * @return  taille du vecteur i
     */
    public final int lengthPos(int i) {
        return pos.length(wc.get(i));
    }

    /**
     * inscrit un nouveau terme dans le cache avec un vecteur initial
     * @param wordId terme
     * @param reg vecteur initial
     */
    public final void registerVectorPos(int wordId, int[] reg) {
        pos.registerVector(wc.get(wordId), reg);
    }

    /**
     * libère le terme i dans le cache
     * @param i terme
     */
    private final void releaseVectorPos(int wordId) {
        pos.releaseVector(wc.get(wordId));
    }

    /**
     * retourne la taille total du cache
     * @return la taille total du cache
     */
    public final int getCurrentSize() {
        return doc.getCurrentSize() + pos.getCurrentSize();
    }

    /**
     * place un verrou pour une utilisation Basic (sans les positions) pour le terme n
     * @param n terme
     */
    public final void lockForBasic(int n) {
        mark(n, StateMark.LOCK, UsageMark.BASIC);
    }

    /**
     * place un verrou pour une utilisation complète (avec les positions) pour le terme n
     * @param n terme
     */
    public final void lockForFull(int n) {
        mark(n, StateMark.LOCK, UsageMark.FULL);
    }

    /**
     * libère le verrou pour le terme n
     * @param n terme
     */
    public final void unlock(int n) {
        mark(n, StateMark.UNLOCK, UsageMark.UNUSED);
    }

    /** doit être appelée depuis mark qui est protégé */
    private final int nextCacheId() {
        if (nextCacheId == maxSize) {  // on a besoin du gc sur le cache
            gc("from nextCacheId");
            nextCacheId = 0;
        }
        while (nextCacheId != maxSize && usage[nextCacheId] != UsageMark.UNUSED) {
            nextCacheId++;
        }
        if (nextCacheId == maxSize) {
            return nextCacheId();
        } // on a rencontré la fin
        return nextCacheId; // on a le bon candidat
    }

    /** doit être appelée depuis mark qui est protégé */
    private final int registerCacheId(int wordId) {
        Integer res = wc.get(wordId);
        if (res == null) {
            int nextId = nextCacheId();
            wc.put(wordId, nextId);
            cw.put(nextId, wordId);
            return nextId;
        }
        return res.intValue();
    }

    /**
     * retourne le cacheId associé au WordId. Ceci est indispensable pour travailler avec
     * les méthodes ....Direct
     * @param wordId terme
     * @return cacheId
     */
    public final int getCacheId(int wordId) {
        return wc.get(wordId);
    }

    private final synchronized void mark(int wordid, StateMark state, UsageMark usage) {
        int cacheId = registerCacheId(wordid);
        countUsage[cacheId]++;  // compte une utilisation
        switch (state) {
            case LOCK:
                lock[cacheId]++;
                load(wordid, usage);
                break;
            case UNLOCK:
                lock[cacheId]--;
                break;
            default:
                error_fatal("Unknow state:" + usage.name());
                break;
        }
    }

    /** doit être appelée depuis mark qui est protégé */
    private final void load(int wordid, UsageMark _usage) {
        loadAsk++;
        switch (_usage) {
            case BASIC:
                basicLoad(wordid);
                break;
            case FULL:
                fullLoad(wordid);
                break;
            case UNUSED:
                error_fatal("illegal usage:" + _usage.name());
                break;
            default:
                error_fatal("Unknow usage:" + _usage.name());
                break;
        }

    }

    /** doit être appelée depuis mark qui est protégé */
    private final void basicLoad(int wordid) {
//        msg("basicLoad:"+wordid);
        switch (usage[wc.get(wordid)]) {
            case BASIC:  // rien à faire
                loadFoundInCache++;
                break;
            case FULL:   // qui peut le plus peut le moins
                loadFoundInCache++;
                break;
            case UNUSED:
                loadFromDisk++;
                checkOverFlow();
                z.loadVectorWforBasic(wordid);
                switch (MODE_RANKING) {
                    case NO:
                        nbDoc[wc.get(wordid)] = lengthDoc(wordid);  // seul les documents sont chargés
                        break;
                    case IDFxTDF:
                    case BM25:
                case BM25TWICE:
                        nbDoc[wc.get(wordid)] = lengthDoc(wordid) / 2;  // seul les documents et occ sont chargés
                        break;
                }
                usage[wc.get(wordid)] = UsageMark.BASIC;
                break;
            default:
                error_fatal("Unknow usage:" + usage[wc.get(wordid)].name());
                break;
        }

    }

    /** doit être appelée depuis mark qui est protégé */
    private final void fullLoad(int wordid) {
        switch (usage[wc.get(wordid)]) {
            case BASIC:  //
                loadFromDisk++;
                releaseVectorDoc(wordid);
                checkOverFlow();
                z.loadVectorWforFull(wordid);
                nbDoc[wc.get(wordid)] = lengthDoc(wordid) / 3;
                usage[wc.get(wordid)] = UsageMark.FULL;
                break;
            case FULL:   // rien à faire
                loadFoundInCache++;
                break;
            case UNUSED:
                loadFromDisk++;
                checkOverFlow();
                z.loadVectorWforFull(wordid);
                nbDoc[wc.get(wordid)] = lengthDoc(wordid) / 3;
                usage[wc.get(wordid)] = UsageMark.FULL;
                break;
            default:
                error_fatal("Unknow usage:" + usage[wc.get(wordid)].name());
                break;
        }
    }

    /** doit être appelée depuis mark qui est protégé */
    private final void checkOverFlow() {
       //msg("checkOverFlow:"+getCurrentSize()+">"+cacheSize);
        if (getCurrentSize() > cacheSize) {
            gc("from checkOverFlow");  // récupère de l'espace
        }
    }

    /** doit être appelée depuis mark qui est protégé */
    private final void gc(String callby) {
        int countlocked = 0;
        int countunlocked = 0;
        int countkeep = 0;
        for (int i = 0; i < maxSize; i++) {
            if (lock[i] == 0) {

                if (usage[i] != UsageMark.UNUSED && countUsage[i] < 2) {  // à libérer
                    doc.releaseVector(i);
                    pos.releaseVector(i);
                    usage[i] = UsageMark.UNUSED;
                    nbDoc[i] = 0;
                    int wordId = cw.get(i);  // cherche la référence libérée
                    cw.remove(i);
                    wc.remove(wordId);
                    countUsage[i] = 0;
                    countunlocked++;
                } else {
                    if (usage[i] != UsageMark.UNUSED) {
                        countUsage[i] /= 2;  // diminue l'utilisation /2 => diminue l'importance des importants qui ne sont plus utilisés'
                        countkeep++;
                    }
                }
            } else if (lock[i] > 0) {  // à garder
                countlocked++;
                countUsage[i] = 1;
            } else { // mauvaise utilisation des lock - unlock
                error_fatal("in cache management checkOverFlow missused of lock/unlock :" + i);
            }
        }
        if (countunlocked == 0 && countkeep != 0) {
            msg("try again gc()");
            gc(callby);
        } else {
            msg(callby + " / stay locked:" + countlocked + " release:" + countunlocked + " keep:" + countkeep);
            msg("  cache usage :" + loadFoundInCache * 100 / loadAsk + "% loadAsk:" + loadAsk + " loadFoundInCache:" + loadFoundInCache + " loadFromDisk:" + loadFromDisk);
        //msg("vdoc:"+vdoc+" getvdoc:"+getvdoc+" vpos:"+vpos+" getvpos:"+getvpos);
        }
    }

    /** test si le terme n1 apparaît dans le document d à la position p
     * ! Doit-être utilisé sur un terme protégé
     * @param n1 terme
     * @param d  document
     * @param p  position
     * @return vrai si ...
     */
    public final boolean chekforWatPinD(int n1, int p, int d) {// test if word n1 appears in doc d at position p
        int cacheId = wc.get(n1);
        return chekforWatPinDDirect(cacheId, p, d);
    }

    /**
     * test si le terme n1 apparaît dans le document d à la position p
     * ! Doit-être utilisé sur un terme protégé
     * @return vrai si ...
     * @param cacheId terme
     * @param d document
     * @param p position
     */
    public final boolean chekforWatPinDDirect(int cacheId, int p, int d) {// test if word n1 appears in doc d at position p
        int tl = nbDoc[cacheId];
        int i = getIdxOfValue(doc.getReferenceOn(cacheId), tl, d);  // look for doc
        if (i == z.notFind) {
            return false;
        }  // le document n'existe pas
        return checkIdxOfValue(cacheId, doc.v(cacheId, tl * z.VEC + i), doc.v(cacheId, tl * z.OCC + i), p);
    }

    private final boolean checkIdxOfValue(int cacheId, int first, int length, int v) {
        //System.out.println("search for :"+v);
        int i, min = first, max = first + length - 1;
        //System.out.println(" ------ min:"+min+" max:"+max);
        if (max == first) // only one element
        {
            if (pos.v(cacheId, max) == v) {
                return true;
            } else {
                return false;
            }
        }
        while (true) {
            i = (min + max) / 2;
            //System.out.println("min:"+min+" i:"+i+" max:"+max+"  r[i]:"+r[i]);
            if (pos.v(cacheId, i) == v) {
                return true;
            }
            if (min + 1 == max) {
                if (pos.v(cacheId, max) == v) {
                    return true;
                } else {
                    break;
                }
            }
            ;
            if (pos.v(cacheId, i) < v) {
                min = i;
            } else {
                max = i;
            }
        }
        return false;
    }

    /** retourne les bornes des positions du terme wordId pour le documenti.
     * ! Doit-être utilisé sur un terme protégé
     * @param wordId terme
     * @param i document
     * @return vecteur de positions
     */
    public final PosLength getWPosLength(int wordId, int i) {
        int cacheId = wc.get(wordId);
        return getWPosLengthDirect(cacheId, i);
    }

    /**
     * retourne les bornes des positions du cache cacheId pour le document i.
     * ! Doit-être utilisé sur un terme protégé
     * @return vecteur de positions
     * @param i document
     * @param cacheId terme dans le cache
     */
    public final PosLength getWPosLengthDirect(int cacheId, int i) {
        //  msg("word: "+i+" n: "+n);
        int tl = nbDoc[cacheId];
        return new PosLength(vDocDirect(cacheId, tl * z.VEC + i), vDocDirect(cacheId, tl * z.OCC + i));
    }
}
