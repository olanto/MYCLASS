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

package org.olanto.demo.alpha;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.oversampling.ExperimentOver;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.oversampling.NNOneN_OverSampling;
import org.olanto.util.Timer;
import java.util.List;
import static org.olanto.cat.GetProp.*;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2004
 * @version 1.1
 *
 * Test du cat�goriseur
 */
public class ExperimentFromFileOverSampling {

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

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN_OverSampling.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN_OverSampling.init(signature, BootGroup, id, NNOneN_OverSampling.NORMALISED, NNOneN_OverSampling.SDF_SQUARE);

        List<ExperimentOver> list = ExperimentOver.loadSetOfExperiment("C:/SIMPLE_CLASS/experiment/fullalphaOver.txt");
        ExperimentOver.runSetOfExperiment(list);
        t1.stop();
    }
}