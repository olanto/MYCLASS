package isi.jg.deploy.demo.alpha;

import isi.jg.cat.Experiment;
import isi.jg.cat.NNBottomGroup;
import isi.jg.cat.NNOneN;
import static isi.jg.cat.GetProp.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import static isi.jg.cat.GetProp.*;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * Test du catégoriseur
 */
public class ExperimentManualSVM {

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
                2,              //            int nbproc,
                true,           //            boolean inmemory,
                3,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                10,              //            int repeatK,
                1000,           //            float qlevel,
                1.07f,          //            float add,
                6,              //            int minocc,
                4000,         //            int maxocc,
                350,            //            float deltamin,
                350,            //            float deltamax,
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
NNOneN.exportTrainSVM("C:/Documents and Settings/jg/Bureau/articles/programmation/svm/multiclasssvm/alphasvm/train.dat");
NNOneN.exportTestSVM("C:/Documents and Settings/jg/Bureau/articles/programmation/svm/multiclasssvm/alphasvm/test.dat");
        t1.stop();

    }
}