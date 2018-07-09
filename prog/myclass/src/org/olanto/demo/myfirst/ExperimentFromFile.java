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

package org.olanto.demo.myfirst;

import org.olanto.demo.alpha.*;
import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.Experiment;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.NNOneN;
import org.olanto.util.Timer;
import java.util.List;
import static org.olanto.cat.GetProp.*;
import org.olanto.cat.util.SenseOS;


public class ExperimentFromFile {

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

          // path to catalog
        String fntrain = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/alpha/MAINGROUP_TRAIN.cat";
        String fntest = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/alpha/EMPTY.cat";

        // load catalog at the specified level

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_MAINGROUP7, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        List<Experiment> list = Experiment.loadSetOfExperiment(SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/experiment/first/sizetrainingimpact.txt");
        Experiment.runSetOfExperiment(list);
        t1.stop();
    }
}