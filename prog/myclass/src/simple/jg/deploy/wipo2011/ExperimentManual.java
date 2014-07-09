package simple.jg.deploy.wipo2011;

import org.olanto.idxvli.IdxStructure;
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
        String fntrain = "e:/PATDB/catalog/EN.cat.val";
        String fntest = "e:/PATDB/catalog/EMPTY.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "last_manual",        //            String experimentName,
                "C:/SIMPLE_CLASS/experiment/",    //            String pathfileSave,
                6,              //            int nbproc,
                true,           //            boolean inmemory,
                3,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                5,              //            int repeatK,
                1000,           //            float qlevel,
                1.04f,          //            float add,
                8,              //            int minocc,
                10000000,         //            int maxocc,
                300,            //            float deltamin,
                300,            //            float deltamax,
                true,          //            boolean verbosetrain,
                true,           //            boolean testtrain,
                80,             //            int trainpart,
                //            // TEST
                3,//            int Nfirst,
                false,//            boolean maintest,
                false,//            boolean maintestGroupdetail,
                false,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
x.doIt();
        t1.stop();
    }
}