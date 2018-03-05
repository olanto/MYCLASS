package org.olanto.demo.bleloc;

import org.olanto.demo.myfirst.*;
import org.olanto.idxvli.IdxConstant;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.IdxEnum.RankingMode;
import org.olanto.idxvli.IdxEnum.implementationMode;
import org.olanto.idxvli.IdxInit;

/**
 * Une classe pour initialiser les constantes.
 *
 * Une classe pour initialiser les constantes. Cette classe doit �tre
 * implémentée pour chaque application
 */
public class ConfigurationForCat implements IdxInit {

    /**
     * cr�e l'attache de cette classe.
     */
    public ConfigurationForCat() {
    }

    /**
     * initialisation permanante des constantes. Ces constantes choisies
     * définitivement pour toute la durée de la vie de l'index.
     */
    public void InitPermanent() {
        DOC_MAXBIT = 18;
        WORD_MAXBIT = 18;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule
        WORD_MAX = (int) Math.pow(2, WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION = implementationMode.FAST;
        DOC_IMPLEMENTATION = implementationMode.FAST;
        OBJ_IMPLEMENTATION = implementationMode.FAST;

        /**
         * nbre d'object storage actif = 2^OBJ_PW2
         */
        OBJ_PW2 = 0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        OBJ_NB = (int) Math.pow(2, OBJ_PW2);  ///0=>1,1=>2,2=>4,
         OBJ_STORE_ASYNC = false;
        
        //PASS_MODE=PassMode.ONE;

        IDX_DONTINDEXTHIS = SomeConstant.ROOTDIR + "SIMPLE/config/dontindexthiswords.txt";

        IDX_WITHDOCBAG = true;
        //IDX_INDEX_MFL = false;
        IDX_MORE_INFO = false;
        IDX_SAVE_POSITION = false;

        /**
         * taille maximum des noms de documents
         */
        DOC_SIZE_NAME = 32;


        WORD_MINOCCKEEP = 2;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP = 1000;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC = 6000000;


        //IDX_MFLF_ENCODING = "UTF-8";
        IDX_MFLF_ENCODING = "ISO-8859-1";
        WORD_MINLENGTH = 2;
        WORD_MAXLENGTH = 40;
        WORD_DEFINITION = new TokenCatNative();

        WORD_USE_STEMMER = false;
        STEM_DOC = false;
        WORD_STEMMING_LANG = "english"; // only for initialisation
        ACTUAL_LANGUAGE = "_EN";

        /* désactive les options qui ne servent pas à la classification */
        IdxConstant.MODE_RANKING = RankingMode.NO;
        ORTOGRAFIC = false;
        IDX_MARKER = false;
    }

    /**
     * initialisation des constantes de configuration (modifiable). Ces
     * constantes choisies définitivement pour toute la durée de la vie du
     * processus.
     */
    public void InitConfiguration() {

        // les directoire
        COMLOG_FILE = SomeConstant.ROOTDIR + "SIMPLE_CLASS/data/bleloc/common.log";
        DETLOG_FILE = SomeConstant.ROOTDIR + "SIMPLE_CLASS/data/bleloc/detail.log";

        String root = SomeConstant.ROOTDIR + "SIMPLE_CLASS/data/bleloc";
        String root0 = SomeConstant.ROOTDIR + "SIMPLE_CLASS/data/bleloc/sto0";
        IdxConstant.COMMON_ROOT = root;
        IdxConstant.DOC_ROOT = root;
        IdxConstant.WORD_ROOT = root;
        SetObjectStoreRoot(root0, 0);
       
// paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING = implementationMode.FAST;
        KEEP_IN_CACHE = 90;
        INDEXING_CACHE_SIZE = 256 * MEGA;
        IDX_CACHE_COUNT = 2 * (int) MEGA;
        IDX_RESERVE = WORD_NFIRSTOFDOC + 4 * KILO;
    }
}
