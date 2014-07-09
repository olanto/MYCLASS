package isi.jg.idxvli.distibuted.test;

import isi.jg.idxvli.server.test.*;
import isi.jg.idxvli.*;
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
public class ConfigurationNative1 implements IdxInit {

    /** crée l'attache de cette classe.
     */
    public ConfigurationNative1() {
    }

    /** initialisation permanante des constantes.
     * Ces constantes choisies définitivement pour toute la durée de la vie de l'index.
     */
    public void InitPermanent() {

        DOC_MAXBIT = 25;
        WORD_MAXBIT = 26;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule
        WORD_MAX = (int) Math.pow(2, WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION = implementationMode.XL;
        DOC_IMPLEMENTATION = implementationMode.XL;
        OBJ_IMPLEMENTATION = implementationMode.XL;

        IDX_WITHDOCBAG = false;
        IDX_MORE_INFO = true;
        IDX_SAVE_POSITION = true;
        MODE_RANKING = RankingMode.BM25;
        MAX_RESPONSE = 20;

        DOC_SIZE_NAME = 128-4;
        DOC_PROPERTIES_MAXBIT=12; // 6=64
        IdxConstant.DOC_ENCODING = "UTF-8";
        IDX_MFLF_ENCODING = "UTF-8";
        WORD_MINLENGTH = 3;
        WORD_MAXLENGTH = 32-4;
        WORD_DEFINITION = new TokenNative();


        WORD_USE_STEMMER = false;
        WORD_STEMMING_LANG = "french"; // only for initialisation

        /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2 = 2;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        /** nbre d'object storage actif */
        OBJ_NB = (int) Math.pow(2, OBJ_PW2);  ///0=>1,1=>2,2=>4,


    }

    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies définitivement pour toute la durée de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "w:/IDX/dist1";
        String root0 = root;
        String root1 = "x:/IDX/dist1";
        String root2 = "y:/IDX/dist1";
        String root3 = "f:/IDX/dist1";
        COMMON_ROOT = root;
        DOC_ROOT = root1;
        WORD_ROOT = root3;
        SetObjectStoreRoot(root0, 0);
        SetObjectStoreRoot(root1, 1);
        SetObjectStoreRoot(root2, 2);
        SetObjectStoreRoot(root3, 3);

        // paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.BIG;
        CACHE_IMPLEMENTATION_READ = implementationMode.BIG;
        IDX_CACHE_COUNT = 4 * (int)MEGA;
        IDX_RESERVE = 2 * KILO;
        QUERY_CACHE_COUNT = 256 * KILO;




        KEEP_IN_CACHE = 90;
        INDEXING_CACHE_SIZE = 1024 * MEGA;

        QUERY_CACHE_SIZE = 1024 * MEGA;
        NEAR_DISTANCE = 8;
    }
}



