package isi.jg.deploy.gamma;

import simple.jg.deploy.wipo2011.ConfigurationForCat;
import isi.jg.cat.Experiment;
import isi.jg.cat.NNBottomGroup;
import isi.jg.cat.NNOneN;
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
public class ExperimentFromFile {

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
        String fntrain = "C:/ENFR/catalog/MAINGROUP_TRAIN_BLANK.cat";
        String fntest = "C:/ENFR/catalog/EMPTY.cat";

        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, NNOneN.CAT_MAINGROUP, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        List<Experiment> list = Experiment.loadSetOfExperiment("C:/SIMPLE_CLASS/experiment/fullgammaEN.txt");
        Experiment.runSetOfExperiment(list);
        t1.stop();
    }
}