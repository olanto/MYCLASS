package isi.jg.profil;

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

public class Configuration implements IdxInit{
    
    /** crée l'attache de cette classe.
     */
    public Configuration(){}
    
    /** initialisation permanante des constantes. 
     * Ces constantes choisies définitivement pour toute la durée de la vie de l'index.
     */
    public  void InitPermanent(){
        DOC_MAXBIT=20;
        WORD_MAXBIT=20;
        DOC_MAX=(int)Math.pow(2,DOC_MAXBIT);  // recalcule
        WORD_MAX=(int)Math.pow(2,WORD_MAXBIT); // recalcule

        IdxConstant.WORD_IMPLEMENTATION=implementationMode.FAST;
        IdxConstant.DOC_IMPLEMENTATION=implementationMode.FAST;
        IdxConstant.OBJ_IMPLEMENTATION=implementationMode.FAST;
        
        IDX_MORE_INFO=false;
        IDX_SAVE_POSITION = false;
        MODE_RANKING=RankingMode.IDFxTDF;
        MAX_RESPONSE=200;

    IDX_DONTINDEXTHIS="C:/JG/VLI_RW/data/dontindexthiswords.txt";
        DOC_SIZE_NAME=32;

        IdxConstant.DOC_ENCODING="UTF-8";
        IDX_MFLF_ENCODING="UTF-8";
        WORD_MINLENGTH=3;
        WORD_DEFINITION=new TokenIndexing();

        
        WORD_USE_STEMMER=false;
        WORD_STEMMING_LANG="french"; // only for initialisation
        
            /** nbre d'object storage actif = 2^OBJ_PW2 */
    OBJ_PW2=0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
    /** nbre d'object storage actif */
    OBJ_NB=(int)Math.pow(2,OBJ_PW2);  ///0=>1,1=>2,2=>4,
 OBJ_COMPRESSION=Compression.NO;
    }
    
    /** initialisation des constantes de configuration (modifiable). 
     * Ces constantes choisies définitivement pour toute la durée de la vie du processus.
     */
    public  void InitConfiguration(){
        
        // les directoire
    String root="c:/JG/VLI_RW/data/objsto";
         String root0="c:/JG/VLI_RW/data/objsto/idx0";
           IdxConstant.COMMON_ROOT=root;
        IdxConstant.DOC_ROOT=root;
        IdxConstant.WORD_ROOT=root;
        SetObjectStoreRoot(root0,0);
        
        // paramètre de fonctionnement
       CACHE_IMPLEMENTATION_INDEXING=implementationMode.FAST;
        CACHE_IMPLEMENTATION_READ=implementationMode.FAST;
        KEEP_IN_CACHE=80;
        INDEXING_CACHE_SIZE=64*MEGA;
       QUERY_CACHE_SIZE=128*MEGA;

       IDX_MARKER = false;
       ORTOGRAFIC=false;
    }
    

}


    
