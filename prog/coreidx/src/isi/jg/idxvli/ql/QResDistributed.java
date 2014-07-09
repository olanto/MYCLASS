package isi.jg.idxvli.ql;

import isi.jg.idxvli.server.QLResultAndRank;
import static isi.jg.util.Messages.*;

/**
 * Classe stockant les  r�sultats d'une requ�te distribu�e sur plusieurs indexeurs.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class QResDistributed {

    /* les documents r�sultats tri�s*/
    public int[] topdoc;
    /* num�ro du noeud d'�ndexation*/
    public int[] topsource;
    /* les noms des documents tri�s*/
    public String[] topname;
    /* le degr� de pertinence du r�sultat tri�s*/
    public float[] toprank;
    /* le degr� de pertinence du r�sultat tri�s*/
    private QLResultAndRank[] resFromNet;
    private int maxToKeep = Integer.MAX_VALUE;
    /* les compteurs */
    private int nbNode = 0;
    private int nbNodeNotNull = 0;
    private int nbRes = 0;
    private int maxKept = 0;
    /* les variables de la fusion */
    private int[] next;
    private boolean[] active;

    /* initialise un resultat avec un ranking 0*/
    public QResDistributed(QLResultAndRank[] resFromNet, int maxToKeep) {
        this.resFromNet = resFromNet;
        this.maxToKeep = maxToKeep;
        initAndSort();
    }
    /* initialise le tri */

    public void initAndSort() {
        if (resFromNet != null) {
            nbNode = resFromNet.length;
            for (int i = 0; i < nbNode; i++) { // calcul le nombre noeud ayant donn� une r�ponse

                if (resFromNet[i] != null && resFromNet[i].result != null && resFromNet[i].result.length != 0) {
                    nbNodeNotNull++;
                }
            }
            for (int i = 0; i < nbNode; i++) { // calcul le nombre de r�ponses

                if (resFromNet[i] != null && resFromNet[i].result != null && resFromNet[i].result.length != 0) {
                    nbRes += resFromNet[i].result.length;
                }
            }
            maxKept = Math.min(nbRes, maxToKeep);
            msg(" nbNode: " + nbNode);
            msg(" nbNodeNotNull: " + nbNodeNotNull);
            msg(" nbRes: " + nbRes);
            msg(" maxKept: " + maxKept);
            next = new int[nbNode];
            active = new boolean[nbNode];
            for (int i = 0; i < nbNode; i++) { // initialise la fusion

                if (resFromNet[i] != null && resFromNet[i].result != null && resFromNet[i].result.length != 0) {
                    active[i] = true;
                    next[i] = 0;
                } else {
                    active[i] = false;
                }
            }
            topdoc = new int[maxKept];
           topsource = new int[maxKept];
            toprank = new float[maxKept];
            topname = new String[maxKept];
            for (int i = 0; i < maxKept; i++) {
                int topNode = getTopNode();
//                msg(i + " - " + topNode);
                topdoc[i] = resFromNet[topNode].result[next[topNode]];
                topsource[i] = topNode;
                toprank[i] = resFromNet[topNode].rank[next[topNode]];
                topname[i] = resFromNet[topNode].docName[next[topNode]];
                next[topNode]++;
            }
        } else {
            msg("no vector of Queries");
        }
    }

    private int getTopNode() {
        float max = -1;
        int topNode = -1;
        for (int i = 0; i < nbNode; i++) { // cherche le noeud le plus grand

            if (active[i]) {
 //               msg(i + ", " + next[i] + ", " + resFromNet[i].rank[next[i]] + ", " + max + ", " + topNode);
                if (next[i] < resFromNet[i].result.length &&
                        resFromNet[i].rank[next[i]] > max) {
                    topNode = i;
                    max = resFromNet[i].rank[next[i]];
//                msg(i + ", " + next[i] + ", " + resFromNet[i].rank[next[i]] + ", " + max + ", " + topNode);
                }
            }
        }
        return topNode;
    }
}
