package simple.jg.test.eob2012;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.NNOneNMultiFiles;
import org.olanto.cat.ExperimentMultiFiles;
import org.olanto.cat.NNBottomGroup;
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
public class ExperimentMultiFiles_eob20121 {

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
        String expfile = drive + "/EOB/EXP/" + set + "/EXPERIMENT/evalheuris2.txt"  ;
        String multifile = drive + "/EOB/EXP/" + set + "/EXPERIMENT/multiheuris2.txt"  ;

        t1 = new Timer("global time MAINGROUP --------------------------");

        // initialise le réseau de neurone ...
        NNOneNMultiFiles.init(signature, null, id, NNOneNMultiFiles.NORMALISED, NNOneNMultiFiles.SDF_SQUARE);

        List<ExperimentMultiFiles> list = ExperimentMultiFiles.loadSetOfExperiment(expfile);
        ExperimentMultiFiles.runSetOfExperiment(list);
        
        list = ExperimentMultiFiles.loadSetOfExperiment(multifile);
        ExperimentMultiFiles.runSetOfExperiment(list);
     
        
        t1.stop();
    }
}