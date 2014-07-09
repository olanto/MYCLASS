package simple.jg.test.eob2012;

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
public class ExperimentFromFile_dispersion {

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

        String drive = "D:";
        String set = "A61Q1";
        String targetCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/subwtf" + ".cat";
        String emptyCAT = drive + "/EOB/ARTS-DATA/MFLF/" + set + "/empty" + ".cat";
        String expfile = drive + "/EOB/EXP/" + set + "/EXPERIMENT/dispersion5-95.txt"  ;
     //  String expfile = drive + "/EOB/EXP/" + set + "/EXPERIMENT/testparam-2.txt"  ;
     //  String expfile = drive + "/EOB/EXP/" + set + "/EXPERIMENT/testparam-80.txt"  ;
        
        String fntrain = targetCAT;
        String fntest = emptyCAT;


        // chargement des catalogues au niveau spécifié

        NNBottomGroup BootGroup = new NNBottomGroup(id, fntrain, fntest, 14, false, false);

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneN.init(signature, BootGroup, id, NNOneN.NORMALISED, NNOneN.SDF_SQUARE);

        List<Experiment> list = Experiment.loadSetOfExperiment(expfile);
        Experiment.runSetOfExperiment(list);
        t1.stop();
    }
}