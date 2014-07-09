package isi.jg.idxvli.test;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;

/**
 * Une classe pour initialiser les constantes.
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
 * Une classe pour initialiser les constantes. Cette classe doit être implémentée pour chaque application
 */
public class Configuration implements IdxInit {

    /** crée l'attache de cette classe.
     */
    public Configuration() {
    }

    /** initialisation permanante des constantes. 
     * Ces constantes choisies définitivement pour toute la durée de la vie de l'index.
     */
    public void InitPermanent() {
        DOC_MAXBIT = 20;
        WORD_MAXBIT = 20;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule
        WORD_MAX = (int) Math.pow(2, WORD_MAXBIT); // recalcule

        IdxConstant.WORD_IMPLEMENTATION = implementationMode.FAST;
        IdxConstant.DOC_IMPLEMENTATION = implementationMode.FAST;
        IdxConstant.OBJ_IMPLEMENTATION = implementationMode.FAST;
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ = implementationMode.FAST;

    }

    /** initialisation des constantes de configuration (modifiable). 
     * Ces constantes choisies définitivement pour toute la durée de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "c:/JG/gigaversion/data/objsto";
        String root0 = "c:/JG/gigaversion/data/objsto0";
        String root1 = "c:/JG/gigaversion/data/objsto1";
        String root2 = "c:/JG/gigaversion/data/objsto2";
        String root3 = "c:/JG/gigaversion/data/objsto3";
        IdxConstant.COMMON_ROOT = root;
        IdxConstant.DOC_ROOT = root;
        IdxConstant.WORD_ROOT = root;
        SetObjectStoreRoot(root0, 0);
        SetObjectStoreRoot(root1, 1);
        SetObjectStoreRoot(root2, 2);
        SetObjectStoreRoot(root3, 3);

        // paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ = implementationMode.FAST;
        KEEP_IN_CACHE = 80;
        INDEXING_CACHE_SIZE = 64 * MEGA;
        QUERY_CACHE_SIZE = 64 * MEGA;

    }
}


    
