package isi.jg.cat.clefip2010;

import org.olanto.idxvli.IdxStructure;
import isi.jg.deploy.frende.*;
import org.olanto.cat.Experiment;
import org.olanto.cat.NNBottomGroup;
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
        String fntrain = "Z:/CLEFIP10/CAT/Clefip2010-IPC.cat";
        String fntest = "Z:/CLEFIP10/EMPTY.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_SUBCLASS, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        Experiment x = new Experiment(
                "clefip",        //            String experimentName,
                "Z:/CLEFIP10/EXPERIMENT",    //            String pathfileSave,
                4,              //            int nbproc,
                true,           //            boolean inmemory,
                4,//            int categorylevel,
                                   // TRAIN
                "",             //            String prefix,
                5,              //            int repeatK,
                1000,           //            float qlevel,
                1.05f,          //            float add,
                4,              //            int minocc,
                400000,         //            int maxocc,
                300,            //            float deltamin,
                300,            //            float deltamax,
                true,          //            boolean verbosetrain,
                true,           //            boolean testtrain,
                80,             //            int trainpart,
                //            // TEST
                3,//            int Nfirst,
                false,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                false,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
x.doIt();
        t1.stop();
    }
}