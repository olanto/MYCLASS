package isi.jg.cat.clefip2010;

import isi.jg.deploy.diag1.*;
import isi.jg.deploy.demo.alpha.SomeConstant;
import isi.jg.idxvli.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;


/**
 * Une classe pour initialiser les constantes.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * Une classe pour initialiser les constantes. Cette classe doit �tre impl�ment�e pour chaque application
 */

public class ConfigurationForRewrite implements IdxInit{
    
    /** cr�e l'attache de cette classe.
     */
    public ConfigurationForRewrite(){}
    
    /** initialisation permanante des constantes.
     * Ces constantes choisies d�finitivement pour toute la dur�e de la vie de l'index.
     */
     public  void InitPermanent(){
         DOC_MAXBIT=22;
        WORD_MAXBIT=24;
        DOC_MAX=(int)Math.pow(2,DOC_MAXBIT);  // recalcule
        WORD_MAX=(int)Math.pow(2,WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION=implementationMode.XL;
        DOC_IMPLEMENTATION=implementationMode.XL;
        OBJ_IMPLEMENTATION=implementationMode.XL;

       /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2=3;  ///0=>1,1=>2,2=>4,3=>8,4=>16
       OBJ_NB=(int)Math.pow(2,OBJ_PW2);  ///0=>1,1=>2,2=>4,
       
        //PASS_MODE=PassMode.ONE;
        
        IDX_DONTINDEXTHIS="C:/SIMPLE_CLASS/config/dontindexthiswords.txt";

        IDX_WITHDOCBAG = false;
        //IDX_INDEX_MFL = false;
        IDX_MORE_INFO=true;
        IDX_SAVE_POSITION = true;

            /** taille maximum des noms de documents */
        DOC_SIZE_NAME=32;

        
        WORD_MINOCCKEEP=4;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP=20;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC=1800;
        
        
        //IDX_MFLF_ENCODING = "UTF-8";
         IDX_MFLF_ENCODING = "UTF-8";
       WORD_MINLENGTH=2;
        WORD_MAXLENGTH=40;
        WORD_DEFINITION=new TokenCatNative();

        WORD_USE_STEMMER=false;
        STEM_DOC = false;
        WORD_STEMMING_LANG="french"; // only for initialisation
        ACTUAL_LANGUAGE="_FR";

        /* d�sactive les options qui ne servent pas � la classification */
        IdxConstant.MODE_RANKING=RankingMode.NO;
        ORTOGRAFIC=false;
        IDX_MARKER=false;
    }
    
    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies d�finitivement pour toute la dur�e de la vie du processus.
     */
    public  void InitConfiguration(){
        
       COMLOG_FILE = "E:/IDX/clefip2010W/common.log";
        DETLOG_FILE = "E:/IDX/clefip2010W/detail.log";

        String root="E:/IDX/clefip2010W";
        String root0="E:/IDX/clefip2010W/sto";
        String root1="T:/IDX/clefip2010W/sto";
        String root2="U:/IDX/clefip2010W/sto";
        String root3="V:/IDX/clefip2010W/sto";
        String root4="W:/IDX/clefip2010W/sto";
        String root5="X:/IDX/clefip2010W/sto";
        String root6="Y:/IDX/clefip2010W/sto";
        String root7="Z:/IDX/clefip2010W/sto";
        IdxConstant.COMMON_ROOT=root;
        IdxConstant.DOC_ROOT=root;
        IdxConstant.WORD_ROOT=root;
        SetObjectStoreRoot(root0,0);
        SetObjectStoreRoot(root1,1);
        SetObjectStoreRoot(root2,2);
        SetObjectStoreRoot(root3,3);
        SetObjectStoreRoot(root4,4);
        SetObjectStoreRoot(root5,5);
        SetObjectStoreRoot(root6,6);
        SetObjectStoreRoot(root7,7);
        // paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING=implementationMode.FAST;
        KEEP_IN_CACHE=90;
        INDEXING_CACHE_SIZE=2000*MEGA;
        IDX_CACHE_COUNT=2*(int)MEGA;

         IDX_RESERVE=WORD_NFIRSTOFDOC+4*KILO;
         
         QUERY_CACHE_SIZE=2000*MEGA;
         QUERY_CACHE_COUNT=1*(int)MEGA;


    }
    
    
}



