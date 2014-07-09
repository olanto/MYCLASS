/**********
    Copyright © 2003-2014 Olanto Foundation Geneva

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
import org.olanto.cat.NNOneN;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.util.Timer;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.cat.GetProp.*;



/**
  *
 * Test du catégoriseur
 */
public class TESTManyNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        
        id=new IdxStructure("QUERY",new ConfigurationCat());
        
        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();
        
        id.Statistic.global();
        
        
        String fntrain=COMMON_ROOT+"/train-maingroup.cat";
        
        System.out.println(fntrain);

        String fntest =COMMON_ROOT+"/empty.cat";
        
        NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN.CAT_MAINGROUP,true,false);
        
        t1=new Timer("global time MAINGROUP --------------------------");
        
        NNOneN.setINMEMORY(true);
        
        float alpha=1.03f;
        float deltamin=300;
        float deltamax=300;
        int minocc=3;
        int maxnbocc=500000;
        int nfirst=3;
        int repeatK=5;
    
        String signature = getSignature("C:/SIMPLE_CLASS/config/Identification.properties");

        
        NNOneN.init(signature,BootGroup,id,NNOneN.NORMALISED,NNOneN.SDF_SQUARE); // initialise le réseau de neurone ...
        
        
        NNOneN.TrainWinnow(NNOneN.CAT_MAINGROUP,"",repeatK,1000,alpha,minocc,maxnbocc,deltamin,deltamax,
                false, // detail des catégories (numéro des catégories et répartition)
                true, // si false=> utilise le catalogue de test, true => test avec le training set
                80     // si testtrain = true indique la part en % utilisée pour le training
                );
        NNOneN.testWinnow4(     false,   // detail class precision
                false,   // detail document prediction
                nfirst);
        NNOneN.testWinnow4Multi(false,   // detail class precision
                nfirst);
               
        t1.stop();
    }
    
    
    
}