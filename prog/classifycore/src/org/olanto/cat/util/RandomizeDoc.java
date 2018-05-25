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

package org.olanto.cat.util;


/**
 * Une classe pour effectuer la classification des documents
 *
*/

import org.olanto.idxvli.IdxStructure;
import java.util.*;

public class RandomizeDoc extends Object {
    
    public static int[] rand;
    public static int lasttraindoc=0,lasttestdoc=0;
    public static boolean RANDOMIZE=true;

    public RandomizeDoc(){}
    
    /** initialisation du mélangeur de documents
     * @param Indexer lien avec l'indexeur
     * @param BootGroup documents de référence
     * @param prefix prefix du groupe d'apprentissage
     */    
    public static void init(IdxStructure Indexer, NNBottomGroup  BootGroup, String prefix) {  // default parameters
        rand=new int[Indexer.lastRecordedDoc];
        lasttraindoc=0;
        for (int i=1;i<Indexer.lastRecordedDoc;i++){
            //System.out.println(Indexer.getFileNameForDocument(i));
            if (BootGroup.inThisGroup(i, prefix, NNBottomGroup.TRAINDOC)){
                rand[lasttraindoc]=i;
                lasttraindoc++;
            }
        }
        lasttestdoc=lasttraindoc;
        for (int i=1;i<Indexer.lastRecordedDoc;i++){
            if (BootGroup.inThisGroup(i, prefix, NNBottomGroup.TESTDOC)){
                rand[lasttestdoc]=i;
                lasttestdoc++;
            }
        }
       System.out.println("lasttraindoc:"+lasttraindoc);
       System.out.println("lasttestdoc:"+lasttestdoc);
       if (RANDOMIZE){  // normalement ok par initialisation
       int shuffle=lasttraindoc ; // randomize traindoc
        Random gen=new Random(13);  // THE TEST
        for (int i=1;i<2*shuffle;i++){
            int a=gen.nextInt(shuffle);
            int b=gen.nextInt(shuffle);
            int tempo=rand[b];
            rand[b]=rand[a];
            rand[a]=tempo;
        }
       }
    }

    

}
