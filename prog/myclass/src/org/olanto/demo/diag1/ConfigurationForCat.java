/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myCLASS is free software: you can redistribute it and/or modify
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

package org.olanto.demo.diag1;

import org.olanto.idxvli.IdxInit;
import org.olanto.idxvli.IdxConstant;
import org.olanto.demo.alpha.SomeConstant;
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
        DOC_MAXBIT=18;
        WORD_MAXBIT=19;
        DOC_MAX=(int)Math.pow(2,DOC_MAXBIT);  // recalcule
        WORD_MAX=(int)Math.pow(2,WORD_MAXBIT); // recalcule

        WORD_IMPLEMENTATION=implementationMode.FAST;
        DOC_IMPLEMENTATION=implementationMode.FAST;
        OBJ_IMPLEMENTATION=implementationMode.FAST;
        
       /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2=0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        OBJ_NB=(int)Math.pow(2,OBJ_PW2);  ///0=>1,1=>2,2=>4,
       
        //PASS_MODE=PassMode.ONE;
        
        IDX_DONTINDEXTHIS=SomeConstant.ROOTDIR+"SIMPLE/config/dontindexthiswords.txt";

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
        
        // les directoire
        COMLOG_FILE = SomeConstant.ROOTDIR+"SIMPLE_CLASS/data/rewritecat/common.log";
        DETLOG_FILE = SomeConstant.ROOTDIR+"SIMPLE_CLASS/data/rewritecat/detail.log";

        String root=SomeConstant.ROOTDIR+"SIMPLE_CLASS/data/rewritecat";
        String root0=SomeConstant.ROOTDIR+"SIMPLE_CLASS/data/rewritecat/sto0";
        IdxConstant.COMMON_ROOT=root;
        IdxConstant.DOC_ROOT=root;
        IdxConstant.WORD_ROOT=root;
        SetObjectStoreRoot(root0,0);
        // paramètre de fonctionnement
        CACHE_IMPLEMENTATION_INDEXING=implementationMode.FAST;
        KEEP_IN_CACHE=90;
        INDEXING_CACHE_SIZE=256*MEGA;
        IDX_CACHE_COUNT=2*(int)MEGA;
        IDX_RESERVE=WORD_NFIRSTOFDOC+4*KILO;
    }
    
    
}



