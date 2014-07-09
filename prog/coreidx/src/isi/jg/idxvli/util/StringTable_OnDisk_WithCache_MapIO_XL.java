package isi.jg.idxvli.util;

import java.io.*;
import java.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;
import static isi.jg.idxvli.IdxConstant.*;

/**
 * gestionaire de mots avec un niveau de cache.
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
public class StringTable_OnDisk_WithCache_MapIO_XL implements StringRepository {

    private StringRepository onDisk;
    private int count = 0;
    private int get = 0;
    private int getInCache = 0;
    private int countRefresh = 0;
    private int maxInCache = WORD_CACHE_COUNT;
    //private Hashtable<String,Integer> InMemory;
    public StringTable_OnDisk_WithCache_MapIO_XL() {
    }

    /**  crée une word table de la taille 2^_maxSize par défaut à l'endroit indiqué par le path, (maximum=2^31),
     * avec des string de longueur max _lengthString*/
    public final StringRepository create(String _path, String _name, int _maxSize, int _lengthString) {
        return (new StringTable_OnDisk_WithCache_MapIO_XL(_path, _name, _maxSize, _lengthString));
    }

    private StringTable_OnDisk_WithCache_MapIO_XL(String _path, String _name, int _maxSize, int _lengthString) {
        initCache();
        onDisk = new StringTable_HomeHash_InMemory_Clue_XL().create(_path, _name, _maxSize, _lengthString);
    }

    /**  ouvre un gestionnaire de mots  à l'endroit indiqué par le path */
    public final StringRepository open(String _path, String _name) {
        return (new StringTable_OnDisk_WithCache_MapIO_XL(_path, _name));
    }

    private StringTable_OnDisk_WithCache_MapIO_XL(String _path, String _name) {
        initCache();
        onDisk = (new StringTable_HomeHash_InMemory_Clue_XL()).open(_path, _name);
    }

    private final void initCache() {
        count = 0;
    // InMemory=new Hashtable<String,Integer>(2*maxInCache);
    }

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close() {
        onDisk.close();
    }

    /**  ajoute un terme au gestionnaire retourne le numéro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja
     */
    public final int put(String w) {
        return onDisk.put(w);
    }

    /**  cherche le numéro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    synchronized public final int get(String w) { // rafraichir tout le cache
        return onDisk.get(w);  // no cache
//        //msg("get:"+w);
//        get++;
//        if (count>maxInCache){
//            initCache();
//            countRefresh++;
//        }
//        Integer n=InMemory.get(w);
//        if (n!=null) {  // dans le cache
//            getInCache++;
//            return n;
//        } else{
//            int fromDisk=onDisk.get(w);
//            if (fromDisk==EMPTY) return EMPTY; // cas on a pas trouvé
//            InMemory.put(w,fromDisk);
//            count++;
//            return fromDisk;
//        }
//        
    }

    /**  cherche le terme associé à un numéro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i) {
        return onDisk.get(i);
    }

    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount() {
        return onDisk.getCount();
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "String Table with cache statistics -> " +
                "\n  get: " + get + " getInCache: " + getInCache + " countRefresh: " + countRefresh + " maxInCache: " + maxInCache +
                "  use this object:" +
                onDisk.getStatistic();
    }

    public final void modify(int i, String newValue) {
        error("not implemented");
    }
}
