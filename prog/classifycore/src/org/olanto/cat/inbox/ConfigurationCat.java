package org.olanto.cat.inbox;

import org.olanto.idxvli.IdxConstant;
import org.olanto.idxvli.IdxInit;
import org.olanto.util.Timer;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;


/**
 * Une classe pour initialiser les constantes.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2004
 * <p>l'utilisation de cette classe est strictement limitée au groupe ISI et à MetaRead
 * dans le cadre de l'étude préliminaire pour le parlement européen
 *
 * Une classe pour initialiser les constantes. Cette classe doit être implémentée pour chaque application
 */

public class ConfigurationCat implements IdxInit{
    
    /** crée l'attache de cette classe.
     */
    public ConfigurationCat(){}
    
    /** initialisation permanante des constantes.
     * Ces constantes choisies définitivement pour toute la durée de la vie de l'index.
     */
    public  void InitPermanent(){
        DOC_MAXBIT=17;
        WORD_MAXBIT=17;
        DOC_MAX=(int)Math.pow(2,DOC_MAXBIT);  // recalcule
        WORD_MAX=(int)Math.pow(2,WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION=implementationMode.FAST;
        DOC_IMPLEMENTATION=implementationMode.FAST;
        OBJ_IMPLEMENTATION=implementationMode.FAST;
        
       /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2=0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
       OBJ_NB=(int)Math.pow(2,OBJ_PW2);  ///0=>1,1=>2,2=>4,
       
        //PASS_MODE=PassMode.ONE;
        
        IDX_WITHDOCBAG = true;
       // IDX_INDEX_MFL = false;
        WORD_TWOPASS = false;  // ne marche pas pour le moment
        WORD_MINOCCKEEP=1;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP=50;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC=999999;
        
        
        DOC_ENCODING="ISO-8859-1";
      // IDX_MFL_ENCODING = "ISO-8859-1";
        WORD_MINLENGTH=1;
        WORD_DEFINITION=new TokenCat();

            IdxConstant.MODE_RANKING=RankingMode.NO;
        ORTOGRAFIC=false;
        IDX_MARKER=false;
    
        
        
    }
    
    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies définitivement pour toute la durée de la vie du processus.
     */
    public  void InitConfiguration(){
                COMLOG_FILE = "c:/AAA/INBOX/data/common.log";
        DETLOG_FILE = "c:/AAA/INBOX/data/detail.log";

        // les directoire
        String root="c:/AAA/INBOX/data/objsto";
        String root0="c:/AAA/INBOX/data/objsto";
        IdxConstant.COMMON_ROOT=root;
        IdxConstant.DOC_ROOT=root;
        IdxConstant.WORD_ROOT=root;
        SetObjectStoreRoot(root0,0);
        IDX_DONTINDEXTHIS="C:/AAA/CodeRessources/dontindexthiswords.txt";        

        // paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING=implementationMode.FAST;
        IdxConstant.KEEP_IN_CACHE=80;
        INDEXING_CACHE_SIZE=64*MEGA;
    }
    
    
}



