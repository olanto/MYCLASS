package isi.jg.cat.clefip2010;

import org.olanto.idxvli.IdxInit;
import org.olanto.idxvli.IdxConstant;
import isi.jg.deploy.frende.*;
import org.olanto.util.Timer;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;


/**
 * Une classe pour initialiser les constantes.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2004
 * <p>l'utilisation de cette classe est strictement limit�e au groupe ISI et � MetaRead
 * dans le cadre de l'�tude pr�liminaire pour le parlement europ�en
 *
 * Une classe pour initialiser les constantes. Cette classe doit �tre impl�ment�e pour chaque application
 */

public class ConfigurationForCat implements IdxInit{
    
    /** cr�e l'attache de cette classe.
     */
    public ConfigurationForCat(){}
    
    /** initialisation permanante des constantes.
     * Ces constantes choisies d�finitivement pour toute la dur�e de la vie de l'index.
     */
     public  void InitPermanent(){
        DOC_MAXBIT=23;
        WORD_MAXBIT=25;
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

        IDX_WITHDOCBAG = true;
        //IDX_INDEX_MFL = false;
        IDX_MORE_INFO=false;
        IDX_SAVE_POSITION = false;

            /** taille maximum des noms de documents */
        DOC_SIZE_NAME=32;

        
        WORD_MINOCCKEEP=4;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP=20;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC=1800;
        
        
        IDX_MFLF_ENCODING = "UTF-8";
        //IDX_MFLF_ENCODING = "ISO-8859-1";
        WORD_MINLENGTH=3;
        WORD_MAXLENGTH=40;
        WORD_DEFINITION=new TokenCatNative();

        WORD_USE_STEMMER=false;
        STEM_DOC = false;
        WORD_STEMMING_LANG="english"; // only for initialisation
        ACTUAL_LANGUAGE="_EN";

        /* d�sactive les options qui ne servent pas � la classification */
        IdxConstant.MODE_RANKING=RankingMode.NO;
        ORTOGRAFIC=false;
        IDX_MARKER=false;
    }
    
    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies d�finitivement pour toute la dur�e de la vie du processus.
     */
    public  void InitConfiguration(){
        
        // les directoire
        COMLOG_FILE = "E:/IDX/clefip2010/common.log";
        DETLOG_FILE = "E:/IDX/clefip2010/detail.log";

        String root="E:/IDX/clefip2010";
        String root0="E:/IDX/clefip2010/sto";
        String root1="T:/IDX/clefip2010/sto";
        String root2="U:/IDX/clefip2010/sto";
        String root3="V:/IDX/clefip2010/sto";
        String root4="W:/IDX/clefip2010/sto";
        String root5="X:/IDX/clefip2010/sto";
        String root6="Y:/IDX/clefip2010/sto";
        String root7="Z:/IDX/clefip2010/sto";
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
    }
    
    
}



