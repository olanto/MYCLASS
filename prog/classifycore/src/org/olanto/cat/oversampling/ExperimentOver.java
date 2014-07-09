package org.olanto.cat.oversampling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Vector;
import static org.olanto.util.Messages.*;

/**
 * Une classe pour effectuer la classification des documents
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * toute autre utilisation est sujette � autorisation
 * <hr>
 *
 */
public class ExperimentOver extends Object {

    public String experimentName = "";
    public String pathfileSave = "";
    public int nbproc = 4;
    public boolean inmemory = true;
    // training
    public boolean overSampling = true;
    public int overSamplingLevel = 50;
    public int categorylevel = 7;
    public String prefix = "";
    public int repeatK = 5;
    public float qlevel = 1000;
    public float add = 1.05f;
    public int minocc = 3;
    public int maxocc = 40;
    public float deltamin = 300;
    public float deltamax = 300;
    public boolean verbosetrain = false;
    public boolean testtrain = true;
    public int trainpart = 80;
    //test
    public int Nfirst = 3;
    public boolean maintest = true;
    public boolean maintestGroupdetail = false;
    public boolean maintestDocumentdetail = false;
    public boolean multitest = true;
    public boolean multitestGroupdetail = false;
    public boolean multitestDocumentdetail = false;
    //result
    public int docbagAvgLength = -1;
    public int docbagMinLength = -1;
    public int docbagMaxLength = -1;
    public int maxUsed = -1;
    public int maxgroup = -1;
    public int maxtrain = -1;
    public int lastdoc = -1;
    public int nbfeatures = -1;
    public int trainingTime = -1;
    public float MainTotal = -1;
    public float MainError = -1;
    public float Main1 = -1;
    public float Main2 = -1;
    public float Main3 = -1;
    public float MultiTotal = -1;
    public float MultiError = -1;
    public float Multi1 = -1;
    public float Multi2 = -1;
    public float Multi3 = -1;
    public float AvgMainTotal = -1;
    public float AvgMain1 = -1;
    public float AvgMain2 = -1;
    public float AvgMain3 = -1;
    public float AvgMultiTotal = -1;
    public float AvgMulti1 = -1;
    public float AvgMulti2 = -1;
    public float AvgMulti3 = -1;
   //belong to this
    static String expFileName;

    public ExperimentOver(String experimentName,
            String pathfileSave,
            int nbproc,
            boolean inmemory,
            int categorylevel,
            // TRAIN
 boolean overSampling,
    int overSamplingLevel,
            String prefix,
            int repeatK,
            float qlevel,
            float add,
            int minocc,
            int maxocc,
            float deltamin,
            float deltamax,
            boolean verbosetrain,
            boolean testtrain,
            int trainpart,
            // TEST
            int Nfirst,
            boolean maintest,
            boolean maintestGroupdetail,
            boolean maintestDocumentdetail,
            boolean multitest,
            boolean multitestGroupdetail,
            boolean multitestDocumentdetail) {
        this.experimentName = experimentName;
        this.pathfileSave = pathfileSave;
        this.nbproc = nbproc;
        this.inmemory = inmemory;
        this.overSampling = overSampling;
    this.overSamplingLevel = overSamplingLevel;

        this.categorylevel = categorylevel;
        this.prefix = prefix;
        this.repeatK = repeatK;
        this.qlevel = qlevel;
        this.add = add;
        this.minocc = minocc;
        this.maxocc = maxocc;
        this.deltamin = deltamin;
        this.deltamax = deltamax;
        this.verbosetrain = verbosetrain;
        this.testtrain = testtrain;
        this.trainpart = trainpart;
        this.Nfirst = Nfirst;
        this.maintest = maintest;
        this.maintestGroupdetail = maintestGroupdetail;
        this.maintestDocumentdetail = maintestDocumentdetail;
        this.multitest = multitest;
        this.multitestGroupdetail = multitestGroupdetail;
        this.multitestDocumentdetail = multitestDocumentdetail;
    }

    public void doIt() {
        NNOneN_OverSampling.setCollectResult(this);
        NNOneN_OverSampling.setNB_PROC(nbproc);
        NNOneN_OverSampling.setINMEMORY(inmemory);
        NNOneN_OverSampling.setOverSampling(overSampling);
        NNOneN_OverSampling.setMaxOver(overSamplingLevel);

        NNOneN_OverSampling.TrainWinnow(categorylevel, prefix, repeatK, qlevel, add, minocc, maxocc, deltamin, deltamax, verbosetrain, testtrain, trainpart);

        if (maintest) {
            NNOneN_OverSampling.testWinnow4(maintestGroupdetail, maintestDocumentdetail, Nfirst);
        }
        if (multitest) {
            NNOneN_OverSampling.testWinnow4Multi(multitestGroupdetail, Nfirst);
        }

    }

    public static List<ExperimentOver> loadSetOfExperiment(String fname) {
        expFileName = fname;
        Vector<ExperimentOver> res = new Vector<ExperimentOver>();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname));
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            int count = 0;
            while (w != null) {
                count++;
                String[] fromfile = w.split("\t");
                if (fromfile.length != 26) {
                    msg("line:" + count + " nb field must be 24 found:" + fromfile.length);
                } else {

                    ExperimentOver x = new ExperimentOver(
                            fromfile[0], //            String experimentName,
                            fromfile[1], //            String pathfileSave,
                            Integer.parseInt(fromfile[2]), //            int nbproc,
                            Boolean.parseBoolean(fromfile[3]), //            boolean inmemory,
                            Integer.parseInt(fromfile[4]),//            int categorylevel,
                            // TRAIN
                            Boolean.parseBoolean(fromfile[23]),//            boolean overSampling
                            Integer.parseInt(fromfile[24]),//             int overSamplingLevel
                           fromfile[5], //            String prefix,
                            Integer.parseInt(fromfile[6]), //            int repeatK,
                            Integer.parseInt(fromfile[7]), //            float qlevel,
                            Float.valueOf(fromfile[8]), //            float add,
                            Integer.parseInt(fromfile[9]), //            int minocc,
                            Integer.parseInt(fromfile[10]), //            int maxocc,
                            Integer.parseInt(fromfile[11]), //            float deltamin,
                            Integer.parseInt(fromfile[12]), //            float deltamax,
                            Boolean.parseBoolean(fromfile[13]), //            boolean verbosetrain,
                            Boolean.parseBoolean(fromfile[14]), //            boolean testtrain,
                            Integer.parseInt(fromfile[15]), //            int trainpart,
                            //            // TEST
                            Integer.parseInt(fromfile[16]),//            int Nfirst,
                            Boolean.parseBoolean(fromfile[17]),//            boolean maintest,
                            Boolean.parseBoolean(fromfile[18]),//            boolean maintestGroupdetail,
                            Boolean.parseBoolean(fromfile[19]),//            boolean maintestDocumentdetail,
                            Boolean.parseBoolean(fromfile[20]),//            boolean multitest,
                            Boolean.parseBoolean(fromfile[21]),//            boolean multitestGroupdetail,
                            Boolean.parseBoolean(fromfile[22])//            boolean multitestDocumentdetail
                            );
                    res.add(x);
                }

                w = in.readLine();
            }
        } catch (Exception e) {
            error("in read experiment file", e);
        }
        return res;
    }

    public static void runSetOfExperiment(List<ExperimentOver> list) {
        msg(".............................................................");
        try {
            OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(expFileName + ".res"));
            BufferedWriter out = new BufferedWriter(osr);
            for (ExperimentOver item : list) {
                item.doIt();
                out.write(item.getNice() + "\n");
                out.flush();
            }
            out.close();
        } catch (Exception e) {
            error("in running experiment file", e);
        }
    }

    public String getNice() {
        String sep = "\t";
        String res = experimentName + sep +
                pathfileSave + sep +
                nbproc + sep +
                inmemory + sep +
                categorylevel + sep +
                overSampling + sep +
                overSamplingLevel + sep +
                prefix + sep +
                repeatK + sep +
                qlevel + sep +
                add + sep +
                minocc + sep +
                maxocc + sep +
                deltamin + sep +
                deltamax + sep +
                verbosetrain + sep +
                testtrain + sep +
                trainpart + sep +
                Nfirst + sep +
                maintest + sep +
                maintestGroupdetail + sep +
                maintestDocumentdetail + sep +
                multitest + sep +
                multitestGroupdetail + sep +
                multitestDocumentdetail + sep +
                docbagAvgLength + sep +
                docbagMinLength + sep +
                docbagMaxLength + sep +
                maxUsed + sep +
                maxgroup + sep +
                maxtrain + sep +
                lastdoc + sep +
                nbfeatures + sep +
                trainingTime + sep +
                MainTotal + sep +
                MainError + sep +
                Main1 + sep +
                Main2 + sep +
                Main3 + sep +
                AvgMainTotal + sep +
                AvgMain1 + sep +
                AvgMain2 + sep +
                AvgMain3 + sep +
                MultiTotal + sep +
                MultiError + sep +
                Multi1 + sep +
                Multi2 + sep +
                Multi3+ sep +
                AvgMultiTotal + sep +
                AvgMulti1 + sep +
                AvgMulti2 + sep +
                AvgMulti3;
        return res;
    }
}