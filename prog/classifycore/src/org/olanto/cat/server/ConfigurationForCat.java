/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myLCASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.cat.server;

import org.olanto.idxvli.IdxConstant;
import org.olanto.idxvli.IdxInit;
import org.olanto.util.Timer;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;


/**
 * Une classe pour initialiser les constantes.
 * Une classe pour initialiser les constantes. Cette classe doit ï¿½tre implï¿½mentï¿½e pour chaque application
 */

public class ConfigurationForCat implements IdxInit{
    
    /** crï¿½e l'attache de cette classe.
     */
    public ConfigurationForCat(){}
    
    /** initialisation permanante des constantes.
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie de l'index.
     */
     public  void InitPermanent(){
        DOC_MAXBIT=18;
        WORD_MAXBIT=18;
        DOC_MAX=(int)Math.pow(2,DOC_MAXBIT);  // recalcule
        WORD_MAX=(int)Math.pow(2,WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION=implementationMode.FAST;
        DOC_IMPLEMENTATION=implementationMode.FAST;
        OBJ_IMPLEMENTATION=implementationMode.FAST;
        
       /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2=0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        OBJ_NB=(int)Math.pow(2,OBJ_PW2);  ///0=>1,1=>2,2=>4,
       
        //PASS_MODE=PassMode.ONE;
        
        IDX_DONTINDEXTHIS="C:/SIMPLE/config/dontindexthiswords.txt";

        IDX_WITHDOCBAG = true;
        //IDX_INDEX_MFL = false;
        IDX_MORE_INFO=false;
        IDX_SAVE_POSITION = false;

            /** taille maximum des noms de documents */
        DOC_SIZE_NAME=32;

        
        WORD_MINOCCKEEP=2;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP=40;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC=600;
        
        
        //IDX_MFLF_ENCODING = "UTF-8";
        IDX_MFLF_ENCODING = "ISO-8859-1";
        WORD_MINLENGTH=2;
        WORD_MAXLENGTH=40;
        WORD_DEFINITION=new TokenCatNative();

        WORD_USE_STEMMER=false;
        STEM_DOC = false;
        WORD_STEMMING_LANG="english"; // only for initialisation
        ACTUAL_LANGUAGE="_EN";

        /* dï¿½sactive les options qui ne servent pas ï¿½ la classification */
        IdxConstant.MODE_RANKING=RankingMode.NO;
        ORTOGRAFIC=false;
        IDX_MARKER=false;
    }
    
    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies dï¿½finitivement pour toute la durï¿½e de la vie du processus.
     */
    public  void InitConfiguration(){
        
        // les directoire
        COMLOG_FILE = "C:/SIMPLE_CLASS/data/alpha/common.log";
        DETLOG_FILE = "C:/SIMPLE_CLASS/data/alpha/detail.log";

        String root="C:/SIMPLE_CLASS/data/alpha";
        String root0="C:/SIMPLE_CLASS/data/alpha/sto0";
        IdxConstant.COMMON_ROOT=root;
        IdxConstant.DOC_ROOT=root;
        IdxConstant.WORD_ROOT=root;
        SetObjectStoreRoot(root0,0);
        // paramÃ¨tre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING=implementationMode.FAST;
        KEEP_IN_CACHE=90;
        INDEXING_CACHE_SIZE=256*MEGA;
        IDX_CACHE_COUNT=2*(int)MEGA;
        IDX_RESERVE=WORD_NFIRSTOFDOC+4*KILO;
    }
    
    
}



