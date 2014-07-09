package isi.jg.deploy.en;

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
        String fntrain = "H:/IPCATNEW/EN2003/BETA1.cat";
        String fntest = "H:/IPCATNEW/EN2003/EMPTY.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "EN_Beta",        //            String experimentName,
                "C:/SIMPLE_CLASS/experiment/",    //            String pathfileSave,
                8,              //            int nbproc,
                true,           //            boolean inmemory,
                6,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                5,              //            int repeatK,
                1000,           //            float qlevel,
                1.05f,          //            float add,
                24,              //            int minocc,
                40000,         //            int maxocc,
                300,            //            float deltamin,
                300,            //            float deltamax,
                true,          //            boolean verbosetrain,
                true,           //            boolean testtrain,
                80,             //            int trainpart,
                //            // TEST
                3,//            int Nfirst,
                true,//            boolean maintest,
                false,//            boolean maintestGroupdetail,
                false,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                false,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
x.doIt();
        t1.stop();
    }
}