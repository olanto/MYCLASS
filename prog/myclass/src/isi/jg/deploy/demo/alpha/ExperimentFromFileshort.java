package isi.jg.deploy.demo.alpha;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.otherexp.Experimentshort;
import org.olanto.cat.util.NNBottomGroup;
import org.olanto.cat.otherexp.NNOneNshort;
import org.olanto.util.Timer;
import java.util.List;
import static org.olanto.cat.GetProp.*;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2004
 * @version 1.1
 *
 * Test du cat�goriseur
 */
public class ExperimentFromFileshort {

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

        List<Experimentshort> list = Experimentshort.loadSetOfExperiment("C:/SIMPLE_CLASS/experiment/fullparamalpha.txt");
        Experimentshort.runSetOfExperiment(list);
        t1.stop();
    }
}