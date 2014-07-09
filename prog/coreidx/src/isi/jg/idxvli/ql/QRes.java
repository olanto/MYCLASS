package isi.jg.idxvli.ql;

import static isi.jg.util.Messages.*;
import java.io.*;
import java.util.*;
import isi.jg.idxvli.util.SetOfBits;
import static isi.jg.idxvli.util.BytesAndFiles.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.idxvli.util.SetOperation;
import isi.jg.util.TimerNano;

/**
 * Classe stockant les  résultats d'une requête.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class QRes {

    /* les documents résultats */
    public int[] doc;
    /* le degré de pertinence du résultat */
    public float[] rank;
    /* les documents résultats triés*/
    public int[] topdoc;
    /* le degré de pertinence du résultat triés*/
    public float[] toprank;
    private float max = Integer.MIN_VALUE;
    private int imax = 0;

    /* initialise un resultat avec un ranking 0*/
    public QRes(int[] doc) {
        this.doc = doc;
        if (doc != null) {
            rank = new float[doc.length];
        }
    }
    /* initialise un resultat et un ranking */

    public QRes(int[] doc, float[] rank) {
        this.doc = doc;
        this.rank = rank;
    }

    public final void topNDoc(RankingMode rankingMode, int topn) { // could be optimise !!!
//        msg("top ndoc :"+rankingMode.name());
//        showVector(doc);
//        showVector(rank); 
        if (doc != null) {
            //TimerNano t1=new TimerNano("topNDoc from :"+doc.length,true);
            if (rankingMode != RankingMode.NO) {
                topdoc = new int[topn];
                toprank = new float[topn];
                max = Float.NEGATIVE_INFINITY;
                imax = 0;
                for (int i = 0; i < doc.length; i++) { // for each document
                    //msg("i:"+i+" max:"+max+" rank[i]:"+rank[i]);
                    if (max < rank[i]) {
                        addInTop(i, topn);
                    }
                }
                if (imax < topn) {// ajuste la taille
                    topdoc = copyVector(imax, topdoc);
                    toprank = copyVector(imax, toprank);
                }
            } else {// pas de ranking
                //msg("pas de ranking");
                topdoc = copyVector(Math.min(topn, doc.length), doc);
            }
        //t1.stop(false);
        }

    }

    private final void addInTop(int i, int topn) {
        if (imax < topn) {// par encore plein
            if (imax == 0) { // le premier
                topdoc[0] = doc[i];
                toprank[0] = rank[i];
                imax = 1;
            } else { // insert un nouveau
                //msg("par encore plein");
                imax++;
                insertInTop(doc[i], rank[i], imax - 1);

                if (imax == topn) {
                    max = rank[topn - 1];
                } // définit le niveau pour entrer dans la liste
            //msg("imax:"+imax+" max:"+max);
            }
        } else { // pleine
            //msg("plein");
            insertInTop(doc[i], rank[i], imax - 1); // le dernier est poussé
            max = rank[topn - 1]; // définit le niveau pour entrer dans la liste
        }
    }

    private final void insertInTop(int doc, float rank, int last) {
        //msg("insert:"+last+" doc:"+doc);
        //showVector(toprank);
        int insertpoint = last;
        for (int i = 0; i < last; i++) {  // cherche le point d'insertion
            if (toprank[i] < rank) {
                insertpoint = i;
                break;
            }
        }
        for (int i = last; i > insertpoint; i--) {  // decale
            toprank[i] = toprank[i - 1];
            topdoc[i] = topdoc[i - 1];
        }
        toprank[insertpoint] = rank;  // insert
        topdoc[insertpoint] = doc;
    }

    /**
     * filtre un vecteur avec un bitset
     * @return vecteur de documents filtré
     * @param filter (true=on garde les true, false on garde les false)
     * @param res un vecteur de documents
     * @param sob une propriété
     */
    public final static QRes filtering(QRes res, SetOfBits sob, boolean filter, RankingMode rankingMode) {
        if (res==null||res.doc == null) {
            //msg ("filtering: res is empty");
            return res;  // rien à filtrer
        }
        if (sob == null) {
            //msg ("filtering: sob is null");
            return res;  // pas de filtrage tout passe
        }
        if (rankingMode != RankingMode.NO) {
            int[] DD = new int[res.doc.length];
            float[] RR = new float[res.doc.length];
            int last = 0;
            for (int i = 0; i < res.doc.length; i++) {
                //msg("filter doc:"+res[i]+"="+sob.get(res[i]));
                if (sob.get(res.doc[i]) == filter) { // il possède la propriété
                    DD[last] = res.doc[i];
                    RR[last] = res.rank[i];
                    last++;
                }
            }
            if (last == DD.length) {
                return new QRes(DD, RR);
            } // ils possèdent tous la propriété
            return new QRes(
                    copyVector(last, DD),
                    copyVector(last, RR)); // ajuste la taille
        } else { // no ranking
            int[] DD = new int[res.doc.length];
            int last = 0;
            for (int i = 0; i < res.doc.length; i++) {
                //msg("filter doc:"+res[i]+"="+sob.get(res[i]));
                if (sob.get(res.doc[i]) == filter) { // il possède la propriété
                    DD[last] = res.doc[i];
                    last++;
                }
            }
            if (last == DD.length) {
                return new QRes(DD);
            } // ils possèdent tous la propriété
            return new QRes(
                    copyVector(last, DD)); // ajuste la taille
        }
    }

    /**
     * profile un vecteur avec un bitset
     * @return vecteur de documents filtré
     * @param res un vecteur de documents
     * @param sob un un profil
     */
    public final static QRes profiling(QRes res, SetOfBits sob, RankingMode rankingMode) {
        if (res.doc == null) {
            msg("profiling: res is empty");
            return res;  // rien à profiler
        }
        if (sob == null) {
            msg("profiling: sob is null");
            return res;  // pas de profil tout passe
        }
        if (rankingMode != RankingMode.NO) {
            msg("profiling RankingMode.IDFxTDF");
            for (int i = 0; i < res.doc.length; i++) { // for each document
                if (sob.get(res.doc[i])) { // il est dans le profil
                    res.rank[i] += 1000; // boost ce document
                //msg("#"+i+" doc:"+res.doc[i]+ " new rank:"+res.rank[i]);
                }
            }
            return new QRes(res.doc, res.rank);
        } else { // no ranking // pas de profil
            return new QRes(res.doc, res.rank);
        }
    }

    /** calcul l'intersection de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param qr1 vecteur1
     * @param qr2 vecteur2
     * @return intersection de 1 et 2
     */
    public static QRes and(QRes qr1, QRes qr2, RankingMode rankingMode) { // and de deux resultats avec les poids du ranking
         if (qr2 == null || qr1 == null) {
                return new QRes(new int[0]);
            }
          int[] r1 = qr1.doc;
        int[] r2 = qr2.doc;
        if (rankingMode != RankingMode.NO) {
            if (r2 == null || r1 == null) {
                return new QRes(new int[0]);
            }
            int id = 0;
            int il1 = r1.length;
            int il2 = r2.length;
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[Math.min(il1, il2)];
            float rank[] = new float[Math.min(il1, il2)];
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (wc1 >= il1) {
                    break;
                }
                if (wc2 >= il2) {
                    break;
                }
                // System.out.println(wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
                if (r1[wc1] == r2[wc2]) { // and ok
                    doc[id] = r1[wc1];// the first operand stay the reference for occurrences
                    //mod090408 rank[id] = (float)Math.log(Math.exp(qr1.rank[wc1])+Math.exp(qr2.rank[wc2]));  // additionne les deux ranking (ET)
                    rank[id] = qr1.rank[wc1] + qr2.rank[wc2];  // additionne les deux ranking (ET)
                    wc1++;
                    wc2++;
                    id++;
                } else if (r1[wc1] < r2[wc2]) {
                    wc1++;
                } else {
                    wc2++;
                }
            }
            return new QRes(copyVector(id, doc), copyVector(id, rank));
        } else {
            return new QRes(SetOperation.and(r1, r2));
        }
    }

    /** calcul l'union de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param qr1 vecteur1
     * @param qr2 vecteur2
     * @return intersection de 1 et 2
     */
    public static QRes or(QRes qr1, QRes qr2, RankingMode rankingMode) { // or de deux resultats avec les poids du ranking
          if (qr2 == null || qr1 == null) {
                return new QRes(new int[0]);
            }
       int[] r1 = qr1.doc;
        int[] r2 = qr2.doc;
        if (rankingMode != RankingMode.NO) {
            if (r2 == null && r1 == null) {
                new QRes(new int[0]);
            }
            if (r2 == null) {
                return qr1;
            }
            if (r1 == null) {
                return qr2;
            }
            int id = 0;
            int il1 = r1.length;
            int il2 = r2.length;
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[il1 + il2];
            float rank[] = new float[il1 + il2];
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (wc1 >= il1) {
                    break;
                }
                if (wc2 >= il2) {
                    break;
                }
                // System.out.println(wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
                if (r1[wc1] == r2[wc2]) { // and ok
                    doc[id] = r1[wc1];// the first operand stay the reference for occurrences
//                    rank[id] = (float)Math.log(Math.exp(qr1.rank[wc1])+Math.exp(qr2.rank[wc2]));  // additionne les deux ranking (OU)
                    rank[id] = qr1.rank[wc1] + qr2.rank[wc2];  // additionne les deux ranking (OU)
                    wc1++;
                    wc2++;
                    id++;
                } else if (r1[wc1] < r2[wc2]) {
                    doc[id] = r1[wc1];
                    rank[id] = qr1.rank[wc1];  //prend celui qui existe
                    wc1++;
                    id++;
                } else {
                    doc[id] = r2[wc2];
                    rank[id] = qr2.rank[wc2];  //prend celui qui existe
                    wc2++;
                    id++;
                }
            }
            if (wc1 < il1) {  // copie la fin de r1
                for (int i = wc1; i < il1; i++) {
                    doc[id] = r1[i];
                    rank[id] = qr1.rank[wc1];  //prend celui qui existe
                    id++;
                }
            }
            if (wc2 < il2) {  // copie la fin de r2
                for (int i = wc2; i < il2; i++) {
                    doc[id] = r2[i];
                    rank[id] = qr2.rank[wc2];  //prend celui qui existe
                    id++;
                }
            }
            return new QRes(copyVector(id, doc), copyVector(id, rank));
        } else {
            return new QRes(SetOperation.or(r1, r2));
        }
    }

    /** calcul la différence de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param qr1 vecteur1
     * @param qr2 vecteur2
     * @return intersection de 1 et 2
     */
    public static QRes minus(QRes qr1, QRes qr2, RankingMode rankingMode) { // minus de deux resultats avec les poids du ranking
         if (qr2 == null || qr1 == null) {
                return new QRes(new int[0]);
            }
        int[] r1 = qr1.doc;
        int[] r2 = qr2.doc;
        if (rankingMode != RankingMode.NO) {
            if (r2 == null && r1 == null) {
                new QRes(new int[0]);
            }
            if (r2 == null) {
                return qr1;
            }
            if (r1 == null) {
                new QRes(new int[0]);
            }
            int id = 0;
            int il1 = r1.length;
            int il2 = r2.length;
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[il1];
            float rank[] = new float[il1];
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (wc1 >= il1) {
                    break;
                }
                if (wc2 >= il2) {
                    break;
                }
                // System.out.println(wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
                if (r1[wc1] == r2[wc2]) { // ne pas copier
                    wc1++;
                    wc2++;
                } else if (r1[wc1] < r2[wc2]) {
                    doc[id] = r1[wc1];
                    rank[id] = qr1.rank[wc1];  //prend celui qui existe
                    wc1++;
                    id++;
                } else {
                    wc2++;
                }
            }
            if (wc1 < il1) {  // copie la fin de r1
                for (int i = wc1; i < il1; i++) {
                    doc[id] = r1[i];
                    rank[id] = qr1.rank[wc1];  //prend celui qui existe
                    id++;
                }
            }
            return new QRes(copyVector(id, doc), copyVector(id, rank));
        } else {
            return new QRes(SetOperation.minus(r1, r2));
        }
    }
}
