package isi.jg.cat.n2.test;

import java.io.*;
import java.util.Hashtable;
import static isi.jg.util.Messages.*;

/**
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class Test28 {

    static final int max = 15049;
    static final int n2 = 28;
    static final int group = 8;
    static String[] docname = new String[max];
    static String[] mainclass = new String[max];
    static String[][] pred = new String[max][28];
    static int[][] score = new int[max][28];
    static int[][] byclass = new int[max][];
    static int[][] sumscore = new int[max][];
    static int[] topclass = new int[max];
    static int[] topscore = new int[max];
    static int load = 0;
    static String[] section;
    static Hashtable<String, Integer> map = new Hashtable<String, Integer>();
    static String[][] classic = new String[max][3];
    static int[][] classicscore = new int[max][3];

    public static void main(String[] args) {



        msg("init");

        section = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

        for (int i = 0; i < 7; i++) {
            for (int k = i + 1; k < 8; k++) {
                String filter = section[i] + section[k];
                map.put(filter, load);
                String fname = "C:/SIMPLE_CLASS/experiment/detail/" + filter + "-filter-MainDetail-Doc.txt";
                openResult(fname);
            }
        }
        openClassicResult("C:/SIMPLE_CLASS/experiment/detail/ABCDEFGH-filter-MainDetail-Doc.txt");
        msg(pred[0][0] + "," + pred[0][1] + "," + pred[0][2] + "," + pred[0][3]);
        countClass();
        sumScore();
        msg(byclass[0][0] + "," + byclass[0][1] + "," + byclass[0][2] + "," + byclass[0][3] + "," + byclass[0][4] + "," + byclass[0][5] + "," + byclass[0][6] + "," + byclass[0][7]);
        msg(sumscore[0][0] + "," + sumscore[0][1] + "," + sumscore[0][2] + "," + sumscore[0][3] + "," + sumscore[0][4] + "," + sumscore[0][5] + "," + sumscore[0][6] + "," + sumscore[0][7]);
        topClass();
        topScore();
        testTopClass();
        testTopScore();
        testTopScoreAndClass();
        testTopReclassClassic();
        msg("end ...");
    }

    private static void countClass() {
        for (int k = 0; k < max; k++) {
            int[] res = new int[group];
            for (int i = 0; i < group; i++) {
                int count = 0;
                for (int j = 0; j < n2; j++) {
                    if (pred[k][j].equals(section[i])) {
                        count++;
                    }
                }
                res[i] = count++;
            }
            byclass[k] = res;
        }
    }

    private static void sumScore() {
        for (int k = 0; k < max; k++) {
            int[] res = new int[group];
            for (int i = 0; i < group; i++) {
                int count = 0;
                for (int j = 0; j < n2; j++) {
                    if (pred[k][j].equals(section[i])) {
                        count += Math.pow(score[k][j], 0.3);
                    }
                }
                res[i] = count++;
            }
            sumscore[k] = res;
        }
    }

    private static void topClass() {
        for (int k = 0; k < max; k++) {
            int res = 0;
            int max = -1;
            for (int i = 0; i < group; i++) {
                if (byclass[k][i] > res) {
                    res = byclass[k][i];
                    max = i;
                }
            }
            topclass[k] = max;
        }
    }

    private static void topScore() {
        for (int k = 0; k < max; k++) {
            int res = 0;
            int max = -1;
            for (int i = 0; i < group; i++) {
                if (sumscore[k][i] > res) {
                    res = sumscore[k][i];
                    max = i;
                }
            }
            topscore[k] = max;
        }
    }

    private static void testTopClass() {
        int ok = 0;
        for (int k = 0; k < max; k++) {
            if (mainclass[k].equals(section[topclass[k]])) {
                ok++;
            }
        }
        msg("TopClass score:" + ((float) ok / (float) max));
    }

    private static void testTopScore() {
        int ok = 0;
        for (int k = 0; k < max; k++) {
            if (mainclass[k].equals(section[topscore[k]])) {
                ok++;
            }
        }
        msg("TopScore score:" + ((float) ok / (float) max));
    }

    private static void testTopScoreAndClass() {
        int okSC = 0;
        int okS = 0;
        int okC = 0;
        for (int k = 0; k < max; k++) {
            if (mainclass[k].equals(section[topscore[k]])) {
                if (mainclass[k].equals(section[topclass[k]])) {
                    okSC++;
                } else {
                    okS++;
                }
            } else {
                if (mainclass[k].equals(section[topclass[k]])) {
                    okC++;
                }
            }
        }
        msg("score SC:" + ((float) okSC / (float) max));
        msg("score S:" + ((float) okS / (float) max));
        msg("score C:" + ((float) okC / (float) max));
    }

    private static void testTopReclassClassic() {
        int ok = 0;
        int okinit = 0;
        for (int k = 0; k < max; k++) {
            String balance = "";
            if (mainclass[k].equals(classic[k][0])) {
                okinit++;
            }
            if (classic[k][0].toCharArray()[0] < classic[k][1].toCharArray()[0]) {
                balance = classic[k][0] + classic[k][1];  // les deux premiers de classic
            } else {
                balance = classic[k][1] + classic[k][0];
            }
            //msg("balance:"+balance);
            int numclass2 = map.get(balance); // le numéro du classifieur binaire
            if (mainclass[k].equals(pred[k][numclass2])) {
                ok++;
            }
        }
        msg("score init classic:" + ((float) okinit / (float) max));
        msg("score reclassify:" + ((float) ok / (float) max));
    }

    private static void openResult(String f) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader fin = new BufferedReader(isr, 1000000);
            int count = 0;
            String w = null;
            w = fin.readLine();  // sauter l'entete
            w = fin.readLine();
            while (w != null) {
                String[] res = w.split(",");
                if (load == 0) {
                    docname[count] = res[0];
                    mainclass[count] = res[1];
                }
                pred[count][load] = res[2];
                score[count][load] = Integer.parseInt(res[3]);
                w = fin.readLine();
                count++;
            }
            msg("open: " + f + " count:" + count);
            fin.close();
            load++;
        } catch (IOException e) {
            error("IO error", e);
        }
    }

    static void openClassicResult(String f) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader fin = new BufferedReader(isr, 1000000);
            int count = 0;
            String w = null;
            w = fin.readLine();  // sauter l'entete
            w = fin.readLine();
            while (w != null) {
                String[] res = w.split(",");
                classic[count][0] = res[2];
                classicscore[count][0] = Integer.parseInt(res[3]);
                classic[count][1] = res[4];
                classicscore[count][1] = Integer.parseInt(res[5]);
                classic[count][2] = res[6];
                classicscore[count][2] = Integer.parseInt(res[7]);
                w = fin.readLine();
                count++;
            }
            msg("open: " + f + " count:" + count);
            fin.close();
            load++;
        } catch (IOException e) {
            error("IO error", e);
        }
    }
}
