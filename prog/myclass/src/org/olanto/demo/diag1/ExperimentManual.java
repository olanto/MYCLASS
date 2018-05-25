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

import org.olanto.demo.alpha.SomeConstant;
import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.Experiment;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.NNOneN;
import static org.olanto.cat.GetProp.*;
import org.olanto.util.Timer;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * Test du catégoriseur
 */
public class ExperimentManual {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {


        String signature = getSignature(SomeConstant.ROOTDIR+"/SIMPLE_CLASS/config/Identification.properties");

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();

        // chemin pour les catalogues
        String fntrain = SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/alpha/MAINGROUP_TRAIN.cat";
        String fntest = SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/alpha/EMPTY.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "alphaMan",        //            String experimentName,
                "C:/SIMPLE_CLASS/experiment/",    //            String pathfileSave,
                8,              //            int nbproc,
                true,           //            boolean inmemory,
                7,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                5,              //            int repeatK,
                1000,           //            float qlevel,
                1.05f,          //            float add,
                2,              //            int minocc,
                400000,         //            int maxocc,
                300,            //            float deltamin,
                300,            //            float deltamax,
                false,          //            boolean verbosetrain,
                true,           //            boolean testtrain,
                80,             //            int trainpart,
                //            // TEST
                3,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                true,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
x.doIt();
        t1.stop();
         NNOneN.explainGroup(20,false);
    }
}