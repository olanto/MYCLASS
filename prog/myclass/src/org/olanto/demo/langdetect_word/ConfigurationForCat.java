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

package org.olanto.demo.langdetect_word;


import org.olanto.cat.util.SenseOS;
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
    @Override
    public void InitPermanent() {
        DOC_MAXBIT = 22;
        WORD_MAXBIT = 22;
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

        IDX_DONTINDEXTHIS = SenseOS.getMYCLASS_ROOT()+ "MYCLASS_MODEL/config/dontindexthiswords_EMPTY.txt";

        IDX_WITHDOCBAG = true;
        //IDX_INDEX_MFL = false;
        IDX_MORE_INFO = false;
        IDX_SAVE_POSITION = false;

        /**
         * taille maximum des noms de documents
         */
        DOC_SIZE_NAME = 12;


        WORD_MINOCCKEEP = 4;  // pour une indexation en deux passes
        WORD_MAXOCCDOCKEEP = 100;  // pour une indexation en deux passes
        WORD_NFIRSTOFDOC = 600;


        IDX_MFLF_ENCODING = "UTF-8";
        //IDX_MFLF_ENCODING = "ISO-8859-1";
        WORD_MINLENGTH = 1;
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
    @Override
    public void InitConfiguration() {

        // les directoire
        COMLOG_FILE = SenseOS.getMYCLASS_ROOT()+ "MYCLASS_MODEL/data/langdetect/common.log";
        DETLOG_FILE = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/data/langdetect/detail.log";

        String root = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/data/langdetect";
        String root0 = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/data/langdetect/sto0";
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
