package simple.jg.test.eob2012;

import org.olanto.idxvli.IdxStructure;
import eob.heuristic.Init_Heuris2;
import eob.heuristic.MixCatAndNBPrediction_heuris2;
import org.olanto.cat.Experiment;
import org.olanto.cat.NNBottomGroup;
import org.olanto.cat.NNOneN;
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
public class COMPUTE_HEURIS2 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    static String signature;
    static String drive = SomeConstant.ROOTDIR;
    static String set = SomeConstant.GROUP;

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {
        Init_Heuris2.main(null);
        ExperimentManual_heuris2.main(null);
        init();
        runfull(1);
        runfull(2);
        runfull(4);
        runfull(8);
        runfull(18);
        runfull(28);
        runfull(38);
        runfull(48);
        runfull(58);
        runfull(68);
        runfull(78);
        runfull(88);
        runfull(98);
         MixCatAndNBPrediction_heuris2.main(null);
    }

    public static void runfull(int pcttrain) {
        GenerateCatForNBCOUNT.generate(drive, set, pcttrain);
        run(pcttrain);
        runNB(pcttrain);
        runCOUNT(pcttrain);
    }

    public static void init() {


        signature = getSignature("c:/SIMPLE_CLASS/config/Identification.properties");

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();
    }

    public static void run(int pcttrain) {
        // chemin pour les catalogues
        System.out.println("--------------------- run" + pcttrain + "----------------------------");

        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrain" + pcttrain + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTest" + pcttrain + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/";
        String detailfile = "" + pcttrain;

//           String  targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/mono1000-iter1" + ".cat";
//           String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/test1000-iter1" + ".cat";
//      
        String fntrain = targetCAT;
        String fntest = emptyCAT;

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, 14, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                detailfile, //            String experimentName,
                detailpath, //            String pathfileSave,
                8, //            int nbproc,
                true, //            boolean inmemory,
                14,//            int categorylevel,
                // TRAIN
                "", //            String prefix,
                8, //            int repeatK,
                1000, //            float qlevel,
                1.08f, //            float add,
                1, //            int minocc,
                10000000, //            int maxocc,
                300, //            float deltamin,
                300, //            float deltamax,
                false, //            boolean verbosetrain,
                false, //            boolean testtrain,
                5, //            int trainpart,
                //            // TEST
                6,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                true,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
        x.doIt();
//NNOneN.ConfusionMatrix(false);
        t1.stop();
    }

    public static void runNB(int pcttrain) {
        // chemin pour les catalogues
        System.out.println("--------------------- runNB" + pcttrain + "----------------------------");

        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrainNB" + pcttrain + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTestNB" + pcttrain + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/NB";
        String detailfile = "" + pcttrain;

//           String  targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/mono1000-iter1" + ".cat";
//           String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/test1000-iter1" + ".cat";
//      
        String fntrain = targetCAT;
        String fntest = emptyCAT;

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, 14, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                detailfile, //            String experimentName,
                detailpath, //            String pathfileSave,
                8, //            int nbproc,
                true, //            boolean inmemory,
                14,//            int categorylevel,
                // TRAIN
                "", //            String prefix,
                8, //            int repeatK,
                1000, //            float qlevel,
                1.08f, //            float add,
                1, //            int minocc,
                10000000, //            int maxocc,
                300, //            float deltamin,
                300, //            float deltamax,
                false, //            boolean verbosetrain,
                false, //            boolean testtrain,
                5, //            int trainpart,
                //            // TEST
                1,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                true,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
        x.doIt();
//NNOneN.ConfusionMatrix(false);
        t1.stop();
    }

    public static void runCOUNT(int pcttrain) {
        // chemin pour les catalogues
        System.out.println("--------------------- runNB" + pcttrain + "----------------------------");

        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrainCOUNT" + pcttrain + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTestCOUNT" + pcttrain + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/COUNT";
        String detailfile = "" + pcttrain;

//           String  targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/mono1000-iter1" + ".cat";
//           String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/test1000-iter1" + ".cat";
//      
        String fntrain = targetCAT;
        String fntest = emptyCAT;

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, 14, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                detailfile, //            String experimentName,
                detailpath, //            String pathfileSave,
                8, //            int nbproc,
                true, //            boolean inmemory,
                14,//            int categorylevel,
                // TRAIN
                "", //            String prefix,
                8, //            int repeatK,
                1000, //            float qlevel,
                1.08f, //            float add,
                1, //            int minocc,
                10000000, //            int maxocc,
                300, //            float deltamin,
                300, //            float deltamax,
                false, //            boolean verbosetrain,
                false, //            boolean testtrain,
                5, //            int trainpart,
                //            // TEST
                1,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                true,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
        x.doIt();
//NNOneN.ConfusionMatrix(false);
        t1.stop();
    }
}