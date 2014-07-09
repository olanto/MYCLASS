package simple.jg.test.eob2012;

import isi.jg.deploy.diag1.*;
import isi.jg.deploy.demo.alpha.SomeConstant;
import isi.jg.idxvli.*;
import isi.jg.idxvli.ql.QueryOperator;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import java.io.*;
import java.util.Hashtable;

/**
 * TOTAL 4,40 countrealnext:1002428
STOP: read and rewrite - 233000 ms
BUILD SUCCESSFUL (total time: 4 minutes 4 seconds)

TOTAL countrealnext:2753028
STOP: read and rewrite - 2393656 ms
BUILD SUCCESSFUL (total time: 39 minutes 59 seconds)
 *
 *
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * permet de g�n�rer une collection avec 2 dimensions
 */
public class REWRITE_DIAG2MIX3_NEXTFAST1 {

    private static final int MaxInCache = 4000000;
    static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    private static Hashtable<String, Integer> nextocc = new Hashtable<String, Integer>(MaxInCache);
   private static Hashtable<String, Integer> seq3occ = new Hashtable<String, Integer>(MaxInCache);

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationForRewrite());
        id.Statistic.global();

        t1 = new Timer("read and rewrite");

        int minocc = 3;
        int maxocc = 1000; // pour mille 0/00 (1000= no limit)
        rewritemflf("D:/EOB/EXP/A61Q1/MFLFRW/subrw3gram-3-1000-wstop-3atbegin.mflf", minocc, maxocc);

        t1.stop();
    }

    private static final void rewritemflf(String f, int minocc, int maxocc) {
        // no mfl no stemming no moreinfo
        //String IDX_MFL_ENCODING="ISO-8859-1";
        String IDX_MFL_ENCODING = "UTF-8";
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f), IDX_MFL_ENCODING);
            int countrealnext = 0;
            int countnonext = 0;
           int countrealseq3 = 0;
            int countnoseq3 = 0;

            for (int i = 0; i < id.lastRecordedDoc; i++) {
                if (i % 10 == 0) {
                    msg("doc rewrite 2 grams:" + i + ", countrealnext:" + countrealnext
                            + ", countnonext:" + countnonext
                            + ", in cache:" + nextocc.size());
                    if (nextocc.size() > MaxInCache * 7 / 10) { //clear cache
                        nextocc = new Hashtable<String, Integer>(MaxInCache);
                    }msg("doc rewrite 3 grams:" + i + ", countrealseq3:" + countrealseq3
                            + ", countnonext:" + countnoseq3
                            + ", in cache:" + seq3occ.size());
                    if (seq3occ.size() > MaxInCache * 7 / 10) { //clear cache
                        seq3occ = new Hashtable<String, Integer>(MaxInCache);
                    }
                }
                String title = id.getFileNameForDocument(i);
                out.write("\n#####" + title + "#####\n");
                int[] seq = id.getSeqOfDoc(i);
                if (seq.length > 0) {
                    int nbocc = id.getOccOfW(seq[0]);
                    if (nbocc >= minocc && nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme atteind le seuil
                        out.write(" " + id.getStringforW(seq[0]));
                    }
                     // 3-gram
                    for (int j = 2; j < seq.length; j++) {
                        nbocc = id.getOccOfW(seq[j]);
                        if (nbocc >= minocc && nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme atteind le seuil
                            nbocc = id.getOccOfW(seq[j - 1]);
                            if (nbocc >= minocc && nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme-1 atteind le seuilnbocc = id.getOccOfW(seq[j - 1]);
                                nbocc = id.getOccOfW(seq[j - 2]);
                                if (nbocc >= minocc && nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme-2 atteind le seuil
                                    String sj = id.getStringforW(seq[j]); // cherche les termes
                                    String sk = id.getStringforW(seq[j - 1]);
                                     String sl = id.getStringforW(seq[j - 2]);
                                    // int[] and=QueryOperator.getDocforWandW(id,sk,sj).doc;
                                    // met dans un cache les requetes
                                    Integer occ = seq3occ.get(sl + "zz" +sk + "zz" + sj);
                                    if (occ == null) {
                                        occ = 0;
                                        int[] next = QueryOperator.getDocforWseq3(id, sl, sk, sj);
                                        if (next != null) {
                                            occ = next.length;
                                        }
                                        seq3occ.put(sl + "zz" +sk + "zz" + sj, occ);

                                    }

                                    if (occ >= minocc) { // la paire existe dans un nombre suffisant de document
                                        out.write(" " + id.getStringforW(seq[j - 2]) + "zz" +id.getStringforW(seq[j - 1]) + "zz" + id.getStringforW(seq[j]));
                                        countrealseq3++;
                                    } else {
                                        countnoseq3++;
                                    }
                                }
                            }
                        }
                    }
                  // 2-gram
                    for (int j = 1; j < seq.length; j++) {
                        nbocc = id.getOccOfW(seq[j]);
                        if (nbocc >= minocc && nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme atteind le seuil
                            out.write(" " + id.getStringforW(seq[j]));
                            nbocc = id.getOccOfW(seq[j - 1]);
                            if (nbocc >= minocc && nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme-1 atteind le seuil
                                String sj = id.getStringforW(seq[j]); // cherche les termes
                                String sk = id.getStringforW(seq[j - 1]);
                                // int[] and=QueryOperator.getDocforWandW(id,sk,sj).doc;
                                // met dans un cache les requetes
                                Integer occ = nextocc.get(sk + "zz" + sj);
                                if (occ == null) {
                                    occ = 0;
                                    int[] next = QueryOperator.getDocforWnextW(id, sk, sj).doc;
                                    if (next != null) {
                                        occ = next.length;
                                    }
                                    nextocc.put(sk + "zz" + sj, occ);

                                }

                                if (occ >= minocc) { // la paire existe dans un nombre suffisant de document
                                    out.write(" " + id.getStringforW(seq[j - 1]) + "zz" + id.getStringforW(seq[j]));
                                    countrealnext++;
                                } else {
                                    countnonext++;
                                }
                            }
                        }
                    }
                 }
            }
            msg("TOTAL 2grams countrealnext:" + countrealnext + ", countnonext:" + countnonext);
            msg("TOTAL 3grams countrealseq3:" + countrealnext + ", countnoseq3:" + countnonext);
            out.close();
        } catch (IOException e) {
            error("IO error", e);
        }
    }
}
