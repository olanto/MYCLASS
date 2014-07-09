package isi.jg.cat.clefip2010;

import org.olanto.idxvli.IdxStructure;
import isi.jg.deploy.demo.alpha.*;
import org.olanto.cat.Experiment;
import org.olanto.cat.NNBottomGroup;
import org.olanto.cat.NNOneN_OverSampling;
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
public class ExperimentManualOverSampling {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {


        String signature = getSignature(SomeConstant.ROOTDIR + "/SIMPLE_CLASS/config/Identification.properties");

        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();

        // chemin pour les catalogues
            String fntrain = "Z:/CLEFIP10/CAT/FRENDE-CLEF.cat";
        String fntest = "Z:/CLEFIP10/CAT/Clefip2010-IPC-TEST.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN_OverSampling.CAT_SUBCLASS, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN_OverSampling.init(signature, BootGroup, id, NNOneN_OverSampling.NORMALISED, NNOneN_OverSampling.SDF_SQUARE);

        Experiment x = new Experiment(
                "clefip4_OVER", //            String experimentName,
                "Z:/CLEFIP10/EXPERIMENT/", //            String pathfileSave,
                4, //            int nbproc,
                true, //            boolean inmemory,
                4,//            int categorylevel,
                // TRAIN
                "", //            String prefix,
                5, //            int repeatK,
                1000, //            float qlevel,
                1.05f, //            float add,
                4, //            int minocc,
                400000, //            int maxocc,
                300, //            float deltamin,
                300, //            float deltamax,
                false, //            boolean verbosetrain,
                false, //            boolean testtrain,
                80, //            int trainpart,
                //            // TEST
                3,//            int Nfirst,
                true,//            boolean maintest,
                true,//            boolean maintestGroupdetail,
                true,//            boolean maintestDocumentdetail,
                true,//            boolean multitest,
                true,//           boolean multitestGroupdetail ,
                false //            boolean multitestDocumentdetail (not implemented)
                );
       // ERROR NNOneN_OverSampling.setCollectResult(x);

        float alpha = 1.05f;
        float deltamin = 300;
        float deltamax = 300;
        int minocc = 4;
        int maxnbocc = 400000;
        int nfirst = 3;
        int repeatK = 5;
        NNOneN_OverSampling.setOverSampling(true);
        NNOneN_OverSampling.setMaxOver(6800);

        NNOneN_OverSampling.TrainWinnow(NNOneN_OverSampling.CAT_SUBCLASS, "", repeatK, 1000, alpha, minocc, maxnbocc, deltamin, deltamax,
                false, // detail des cat�gories (num�ro des cat�gories et r�partition)
                false, // si false=> utilise le catalogue de test, true => test avec le training set
                80 // si testtrain = true indique la part en % utilis�e pour le training
                );
//        NNOneN_OverSampling.testWinnow4(true, // detail class precision
//                false, // detail document prediction
//                nfirst);
        NNOneN_OverSampling.testWinnow4Multi(true, // detail class precision
                nfirst);
        t1.stop();
    }
}