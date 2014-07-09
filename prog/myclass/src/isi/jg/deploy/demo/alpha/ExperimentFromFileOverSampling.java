package isi.jg.deploy.demo.alpha;

import isi.jg.cat.ExperimentOver;
import isi.jg.cat.NNBottomGroup;
import isi.jg.cat.NNOneN_OverSampling;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import java.util.List;
import static isi.jg.cat.GetProp.*;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2004
 * @version 1.1
 *
 * Test du cat�goriseur
 */
public class ExperimentFromFileOverSampling {

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

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN_OverSampling.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN_OverSampling.init(signature, BootGroup, id, NNOneN_OverSampling.NORMALISED, NNOneN_OverSampling.SDF_SQUARE);

        List<ExperimentOver> list = ExperimentOver.loadSetOfExperiment("C:/SIMPLE_CLASS/experiment/fullalphaOver.txt");
        ExperimentOver.runSetOfExperiment(list);
        t1.stop();
    }
}