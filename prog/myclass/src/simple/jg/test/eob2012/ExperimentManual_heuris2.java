package simple.jg.test.eob2012;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.Experiment;
import org.olanto.cat.util.NNBottomGroup;
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
public class ExperimentManual_heuris2 {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    static String signature;

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {
        init();
        /*
        run(0);
        eob.heuristic.SelectSet.select(0);
        run(1);
         */
        for (int i = 0; i < 100; i++) {
            System.out.println("--------------------- run" + i + "----------------------------");
            run(i);
            eob.heuristic.SelectSetMulti.select(i);
        }
    }

    public static void init() {


        signature = getSignature("c:/SIMPLE_CLASS/config/Identification.properties");

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();
    }

    public static void run(int nbstep) {
        // chemin pour les catalogues

        String drive = SomeConstant.ROOTDIR;
        String set = SomeConstant.GROUP;
        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrain" + nbstep + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTest" + nbstep + ".cat";
        String detailpath = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/";
        String detailfile = "" + nbstep;

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
                3,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                true,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                false,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
        x.doIt();
//NNOneN.ConfusionMatrix(false);
        t1.stop();
    }
}