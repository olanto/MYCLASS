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

package org.olanto.cat.util;



import org.olanto.idxvli.IdxStructure;
import java.util.*;



/**
 * Une classe pour déterminer artificiellement les catégories et les jeux d'apprentissage et de test.
  *
*/
public class NNArtificialGroup extends NNBottomGroup{
    
    
    
    /** construit les catégories du classifieur et détermine un catalogue pour les documents d'apprentissage et de  test
     * @param _sprouts catalogue pour les documents
     * @param _Indexer Indexeur contenant les libellés des documents
     * @param _bottomgrouplength niveau maximum de construction des catégories
     * <br>NNOne.CAT_SECTION
     * <br>NNOne.CAT_CLASS
     * <br>NNOne.CAT_SUBCLASS
     * <br>NNOne.CAT_MAINGROUP
     * @param _verbose affiche les détails de la construction des catégories
     * @param _showerror affiche les erreurs du catalogue (fichiers et catégories erronnée) */
    
    public NNArtificialGroup(IdxStructure _Indexer,String[] _sprouts, int _bottomgrouplength,
    boolean _verbose, boolean _showerror){
        Indexer=_Indexer;
        verbose=_verbose;
        showerror=_showerror;
        bottomgrouplength=_bottomgrouplength;
        doctype=new byte[Indexer.lastRecordedDoc];
        docgroup=new int[Indexer.lastRecordedDoc][];
        group=new Hashtable();
        groupinv=new Hashtable();
        maxbottomgroup=0;
        // find all the bottom group in the training doc
        // this groups will be the reference for all tests
            getNextFirst(_sprouts);
        // enregistre tous les documents comme training
            getNext(_sprouts);
   }
    
  
    void getNextFirst(String[] _sprouts){   
       for (int i=0;i<_sprouts.length;i++){ 
                addFirst(_sprouts[i]+" ");  // complete whit a blanck
            }
    }
    void getNext(String[] _sprouts){  
       for (int i=0;i<_sprouts.length;i++){
                add(_sprouts[i]+" ",TRAINDOC);  // complete whit a blanck
            }
   }
    

}





