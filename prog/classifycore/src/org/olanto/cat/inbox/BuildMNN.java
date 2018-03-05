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

package org.olanto.cat.inbox;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.NNBuildTree;
import org.olanto.cat.NNOneN;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.util.Timer;
import static org.olanto.idxvli.IdxEnum.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.util.Messages.*;
import  javax.swing.*;
import static org.olanto.cat.GetProp.*;


/**
 *
 * construction de réseaux de neurone
 */
public class BuildMNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    public static void trainNN (JTextArea status)   {

 //    String signature = getSignature("C:/SIMPLE_CLASS/config/Identification.properties");
     String signature = getSignature("C:/AAA/CodeRessources/Identification.properties");


        status.append("\nOpen Index");


        id=new IdxStructure("QUERY",new ConfigurationCat());
        
        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();
        
        id.Statistic.global();
        
        
        String fntrain=COMMON_ROOT+"/train-maingroup.cat";

        String fntest =COMMON_ROOT+"/empty.cat";
         
        status.append("\nTraining ...");
        NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN.CAT_MAINGROUP,false,false);
        
        
        // génération de l'arbre de réseaux de neurones
        
        t1=new Timer("global time --------------------------------");
        
        
        String nnfile=COMMON_ROOT+"CLASSIFIER.mnn";
        
        NNBuildTree.init(
        signature,                 // signature
        BootGroup,                 //catégories et documents d'apprentissage
        id,                        //indexeur
        true,                      // sauver l'arbre
        NNBuildTree.MODE_8,     // stratégie de construction 
        100,                      // nbr de réseaux
        100,                     // somme de toutes les catégories de tous les réseaux
        1.06f,                     // alpha
        300.0f,300.0f,             // niveaux d'histeresis
        1,1000000,                 // min et max occurence
        3,                         // nbre de choix retenu
        1,                         // nbre de cycle d'apprentissage
        nnfile                     // fichier pour mémoriser l'arbre.
        );
         
        t1.stop();
        status.append("\nEnd of Training ...");
    }
    
    
    
}