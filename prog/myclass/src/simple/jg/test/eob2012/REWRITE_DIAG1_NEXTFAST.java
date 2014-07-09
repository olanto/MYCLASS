package simple.jg.test.eob2012;

import org.olanto.idxvli.IdxStructure;
import org.olanto.idxvli.ql.QueryOperator;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;
import java.io.*;
import java.util.Hashtable;
import org.olanto.idxvli.ql.QueryOperator;

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
public class REWRITE_DIAG1_NEXTFAST {

    private static final int MaxInCache=4000000;
            static IdxStructure id;
    private static Timer t1 = new Timer("global time");
    private static Hashtable<String, Integer> nextocc = new Hashtable<String, Integer>(MaxInCache);

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationForRewrite());
        id.Statistic.global();

        t1 = new Timer("read and rewrite");

        int minocc=3;
        int maxocc=1000; // pour mille 0/00 (1000= no limit)
        rewritemflf("H:/EOB/EXP/"+SomeConstant.GROUP+"/MFLFRW/subwft-40000-rw2gram-3-1000-stopwrd.mflf", minocc, maxocc);

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
            for (int i = 0; i < id.lastRecordedDoc; i++) {
                if (i % 1000 == 0) {
                    msg("doc rewrite:" + i + ", countrealnext:" + countrealnext
                            + ", countnonext:" + countnonext
                             + ", in cache:" + nextocc.size()
                            );
                    if (nextocc.size()>MaxInCache*7/10){ //clear cache
                        nextocc = new Hashtable<String, Integer>(MaxInCache);
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
                                    if (next != null)  {
                                        occ=next.length;
                                    }
                                        nextocc.put(sk + "zz" + sj,occ);
                                    
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
            msg("TOTAL countrealnext:" + countrealnext + ", countnonext:" + countnonext);
            out.close();
        } catch (IOException e) {
            error("IO error", e);
        }
    }
}


