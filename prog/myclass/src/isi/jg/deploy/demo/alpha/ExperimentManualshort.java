package isi.jg.deploy.demo.alpha;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.otherexp.Experimentshort;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.otherexp.NNOneNshort;
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
public class ExperimentManualshort {

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

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneNshort.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneNshort.init(signature, BootGroup, id, NNOneNshort.NORMALISED, NNOneNshort.SDF_SQUARE);

        Experimentshort x = new Experimentshort(
                "alphaMan",        //            String experimentName,
                "C:/SIMPLE_CLASS/experiment/",    //            String pathfileSave
                2,              //            int nbproc,
                true,           //            boolean inmemory,
                3,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                11,              //            int repeatK,
                1000,           //            float qlevel,
                1.07f,          //            float add,
                2,              //            int minocc,
                400000,         //            int maxocc,
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
        t1.stop();
    }
}