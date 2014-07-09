package simple.jg.deploy.wipo2011;

import org.olanto.idxvli.IdxInit;
import org.olanto.idxvli.IdxConstant;
import isi.jg.deploy.gamma.TokenCatNative;
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
        DOC_MAXBIT=25;
        WORD_MAXBIT=23;
        DOC_MAX=(int)Math.pow(2,DOC_MAXBIT);  // recalcule
        WORD_MAX=(int)Math.pow(2,WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION=implementationMode.XL;
        DOC_IMPLEMENTATION=implementationMode.XL;
        OBJ_IMPLEMENTATION=implementationMode.XL;
        
       /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2=0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        OBJ_NB=(int)Math.pow(2,OBJ_PW2);  ///0=>1,1=>2,2=>4,
       
        //PASS_MODE=PassMode.ONE;
        
        IDX_DONTINDEXTHIS="C:/SIMPLE_CLASS/config/dontindexthiswordsENFR.txt";

        IDX_WITHDOCBAG = true;
        //IDX_INDEX_MFL = false;
        IDX_MORE_INFO=false;
        IDX_SAVE_POSITION = false;

            /** taille maximum des noms de documents */
        DOC_SIZE_NAME=10;

        
        WORD_MINOCCKEEP=4;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP=10;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC=600;
        
        
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
        COMLOG_FILE = "e:/PATDB/data/wipo2011/common.log";
        DETLOG_FILE = "e:/PATDB/data/wipo2011/detail.log";

        String root="e:/PATDB/data/wipo2011";
        String root0="e:/PATDB/data/wipo2011/sto0";
        IdxConstant.COMMON_ROOT=root;
        IdxConstant.DOC_ROOT=root;
        IdxConstant.WORD_ROOT=root;
        SetObjectStoreRoot(root0,0);
        // paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING=implementationMode.FAST;
        KEEP_IN_CACHE=90;
        INDEXING_CACHE_SIZE=2000*MEGA;
        IDX_CACHE_COUNT=4*(int)MEGA;
        IDX_RESERVE=WORD_NFIRSTOFDOC+4*KILO;
    }
    
    
}



