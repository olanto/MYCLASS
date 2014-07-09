package simple.jg.test.eob2012;

import eob.heuristic.Init_BaseLine_CatAndBuildNB;
import eob.heuristic.MixCatAndNBPrediction;
import isi.jg.cat.Experiment;
import isi.jg.cat.NNBottomGroup;
import isi.jg.cat.NNOneN;
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
public class COMPUTE_EVAL {

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
        
        Init_BaseLine_CatAndBuildNB.main(null);
        init();
        runfull(1);
        runfull(2);
        runfull(3);
        runfull(4);
        runfull(5);
        runfull(10);
        runfull(15);
        runfull(20);
        runfull(25);
        runfull(30);
        runfull(35);
        runfull(40);
        runfull(45);
        runfull(50);
        runfull(55);
        runfull(60);
        runfull(65);
        runfull(70);
        runfull(75);
        runfull(80);
        runfull(85);
        runfull(90);
        runfull(95);
        MixCatAndNBPrediction.main(null);
    }

    public static void runfull(int pcttrain) {
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

        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/Train" + pcttrain + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/Test" + pcttrain + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/";
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

        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/TrainNB" + pcttrain + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/TestNB" + pcttrain + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/NB";
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

        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/TrainCOUNT" + pcttrain + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/TestCOUNT" + pcttrain + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/eval/COUNT";
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