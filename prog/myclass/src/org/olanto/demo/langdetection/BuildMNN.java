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


package org.olanto.demo.langdetection;

import static org.olanto.cat.GetProp.*;
import org.olanto.cat.NNBuildTree;
import org.olanto.cat.NNOneN;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.util.SenseOS;
import org.olanto.demo.alpha.*;
import org.olanto.idxvli.IdxStructure;


import org.olanto.util.Timer;


/**
 * 
 *
 * Test de la construction de réseaux de neurone
 */
public class BuildMNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        String signature = null;

        id=new IdxStructure("QUERY",new ConfigurationForCat());
        id.Statistic.global();
        
        
      // path to catalog
        String fntrain = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/langdetectngram/corpus_dgt2014.cat";
        String fntest = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/langdetectngram/EMPTY.cat";
        
       NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest, 2,false,false);
        
        t1=new Timer("global time --------------------------------");
        
        
        String nnfile=SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/data/langdetectngram/mnn/langdetectngram.mnn";
        
        NNBuildTree.init(
        signature,
        BootGroup,                 //catégories et documents d'apprentissage
        id,                        //indexeur
        true,                      // sauver l'arbre
        NNBuildTree.MODE_2,      // stratégie de construction
        1,                      // nbr de réseaux
        22,                     // somme de toutes les catégories de tous les réseaux
        1.05f,                     // alpha
        300.0f,300.0f,             // niveaux d'histeresis
        3,1000000,                 // min et max occurence
        3,                         // nbre de choix retenu
        5,                         // nbre de cycle d'apprentissage
        nnfile                     // fichier pour mémoriser l'arbre.
        );
         
        t1.stop();
    }
    
    
    
}