/**
 * ********
 * Copyright © 2003-2018 Olanto Foundation Geneva
 *
 * This file is part of myCLASS.
 *
 * myCLASS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.demo.langdetect_word;

import org.olanto.cat.Experiment;
import org.olanto.cat.NNOneN;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.util.SenseOS;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;

/**
 *
 * Testing the classifier
 */
public class ExperimentManual {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * manual test
     *
     * @param args sans
     */
    public static void main(String[] args) {


        //String signature = getSignature(SomeConstant.ROOTDIR + "/MYCLASS_MODEL/config/Identification.properties");
        String signature = null;

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();

        // path to catalog
        String fntrain = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/langdetect/corpus_dgt2014.cat";
        String fntest = SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/langdetect/EMPTY.cat";

        // load catalog at the specified level

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, 2, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise the neural network ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "worddetect", //            String experimentName,
                SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/experiment/langdetect/", //            String pathfileSave,
                8, //            int nbproc,
                true, //         boolean inmemory,
                2,//             int categorylevel,
                // TRAIN -- parameters for the training
                "", //           String prefix,
                5, //            int repeatK,
                1000, //         float qlevel,
                1.06f, //        float add,
                2, //            int minocc,
                100000000, //       int maxocc,
                300, //          float deltamin,
                300, //          float deltamax,
                false, //        boolean verbosetrain,
                true, //         boolean testtrain,
                80, //           int trainpart,
                //TEST -- parameters for the test
                3,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                false,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //           boolean multitestOtherdetail 
                );
        x.doIt();
      NNOneN.ConfusionMatrix(false);
        NNOneN.explainGroup(50, true);
        t1.stop();
    }
}