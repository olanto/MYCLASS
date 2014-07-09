package simple.jg.test.eob2012;

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
public class ExperimentManual {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {


        String signature = getSignature("c:/SIMPLE_CLASS/config/Identification.properties");

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();

        // chemin pour les catalogues
        
         String drive = "H:";
            String set = "G06F17";
//           String  targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrain48" + ".cat";
//           String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/manysymbol" + ".cat";
//           
//           String  targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/subwtf" + ".cat";
          String  targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "_IC/heuris2/initTrain38" + ".cat";
           String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/empty" + ".cat";
      
        String fntrain = targetCAT;
        String fntest = emptyCAT;

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, 14, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "last_manual",        //            String experimentName,
                "C:/SIMPLE_CLASS/experiment/",    //            String pathfileSave,
                8,              //            int nbproc,
                true,           //            boolean inmemory,
                14,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                8,              //            int repeatK,
                1000,           //            float qlevel,
                1.08f,          //            float add,
                1,              //            int minocc,
                10000000,         //            int maxocc,
                300,            //            float deltamin,
                300,            //            float deltamax,
                false,          //            boolean verbosetrain,
                true,           //            boolean testtrain,
                80,             //            int trainpart,
                //            // TEST
                3,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                false,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                false,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
x.doIt();
//NNOneN.ConfusionMatrix(false);
        t1.stop();
    }
}