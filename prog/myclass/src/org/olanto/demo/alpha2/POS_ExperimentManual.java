package org.olanto.demo.alpha2;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.Experiment;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.NNOneN;
import static org.olanto.cat.GetProp.*;
import org.olanto.util.Timer;
import static org.olanto.cat.GetProp.*;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * Test du catégoriseur
 */
public class POS_ExperimentManual {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {


        String signature = getSignature(SomeConstant.ROOTDIR+"/SIMPLE_CLASS/config/Identification.properties");

        id = new IdxStructure("QUERY", new POS_ConfigurationForCat());
        id.Statistic.global();

        // chemin pour les catalogues
        String fntrain = SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/alpha/ALPHA3-TRAIN.cat";
        String fntest = SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/alpha/ALPHA3-TEST.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "alphaMan",        //            String experimentName,
                "C:/SIMPLE_CLASS/experiment/",    //            String pathfileSave,
                2,              //            int nbproc,
                true,           //            boolean inmemory,
                1,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                10,              //            int repeatK,
                1000,           //            float qlevel,
                1.07f,          //            float add,
                2,              //            int minocc,
                400000,         //            int maxocc,
                350,            //            float deltamin,
                350,            //            float deltamax,
                false,          //            boolean verbosetrain,
                false,           //            boolean testtrain,
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
    NNOneN.explainGroup(20);
    }
    
}