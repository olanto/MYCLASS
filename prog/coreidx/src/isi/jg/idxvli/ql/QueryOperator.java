package isi.jg.idxvli.ql;

import isi.jg.idxvli.DoParse;
import isi.jg.idxvli.IdxStructure;
import isi.jg.idxvli.cache.CacheRead_Opti;
import isi.jg.idxvli.cache.PosLength;
import isi.jg.idxvli.doc.DocumentManager;
import static isi.jg.idxvli.util.SetOperation.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;
import static isi.jg.idxvli.IdxConstant.*;
import isi.jg.idxvli.IdxEnum.*;
import static isi.jg.util.Messages.*;

/**
 * Une classe pour effectuer les opérations de requêtage.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 *  implémente les cosinus ...
 */
public class QueryOperator {

    /** Creates a new instance of NewClass */
    public QueryOperator() {
    }

    /** cherche les documents contenant la citation  w1.
     * todo: ajouter le ranking des citations (? utile)
     * - comment: comme pour le next mais sur tout les termes ...
     * @param z  index
     * @param w1  une chaine de caract?res
     * @return vecteur de documents
     */
    public static final QRes getDocforQuotationForW(IdxStructure z, String w1, RankingMode rankType) {
        //msg("citation pour:"+w1);

        DoParse a = new DoParse(w1 + " ", z.dontIndexThis);  // un blanc pour ?tre sur de lire le dernier terme
        int[] tosearch = a.scanString(z);
        if (tosearch == null || tosearch.length == 0) {
            return new QRes(null);
        } // rien à chercher
        if (tosearch.length == 1) {
            return getDocforW(z, tosearch[0], rankType);
        } // seulement un terme
        if (tosearch.length == 2) {
            return getDocforWnextW(z, z.getStringforW(tosearch[0]), z.getStringforW(tosearch[1]), Integer.MAX_VALUE);
        } // seulement deux termes
        //showVector(tosearch);
        return getDocforWseqWN(z, tosearch);
    } // no limit

    /**
     * cherche les documents contenant le terme n
     * @return vecteur de documents
     * @param z index
     * @param n terme
     */
    public static final QRes getDocforW(IdxStructure z, int n, RankingMode rankType) {
        if (n != NOT_FOUND && n < z.lastUpdatedWord) {
            switch (rankType) {
                case NO:
                    return getDocforW_NO(z, n);
                case IDFxTDF:
                    return getDocforW_IDFxTDF(z, n);
                case BM25:
                    return getDocforW_BM25(z, n);
                case BM25TWICE:
                   return getDocforW_BM25Twice(z, n);
            }
        }
        return new QRes(null);
    }

    /**
     * cherche les documents contenant le terme w
     * @return vecteur de documents
     * @param z index
     * @param w terme
     */
    public static final QRes getDocforW(IdxStructure z, String w, RankingMode rankType) {
        //   int n = z.getWforBasic(w);
        //msg("getDocforW:"+w);
        int n = z.getIntForW(w);
        if (n != NOT_FOUND && n < z.lastUpdatedWord) {
            switch (rankType) {
                case NO:
                    return getDocforW_NO(z, n);
                case IDFxTDF:
                    return getDocforW_IDFxTDF(z, n);
                case BM25:
                    return getDocforW_BM25(z, n);
                 case BM25TWICE:
                  return getDocforW_BM25Twice(z, n);
             }
        }
        return new QRes(null);
    }

    /**
     * cherche les documents contenant le terme indexé par n
     * @return vecteur de documents
     * @param z index
     * @param n terme
     */
    public static final QRes getDocforW_NO(IdxStructure z, int n) {
        z.indexread.lockForBasic(n);
        int[] res = copyVector(z.indexread.getNbDoc(n), z.indexread.getReferenceOnDoc(n));
        z.indexread.unlock(n);

        return new QRes(res);
    }

    /**
     * cherche les documents contenant le terme indexé par n
     * @return vecteur de documents
     * @param z index
     * @param n terme
     */
    public static final QRes getDocforW_IDFxTDF(IdxStructure z, int n) {
        z.indexread.lockForBasic(n);
        int nbdoc = z.indexread.getNbDoc(n);
//        msg("word id:"+n);
//        msg("getDocforW_IDFxTDF nbdoc:"+nbdoc);
        int[] doc = copyVector(nbdoc, z.indexread.getReferenceOnDoc(n));
//        msg("  doc:"+doc.length);
//        showVector(doc);

        //TimerNano t1=new TimerNano("evalue IDFxTDF",true);
        int[] occ = z.indexread.getvDoc(n, nbdoc, nbdoc);  // les occurences
//        msg("  occ:"+occ.length);
//        showVector(occ);

        float idf = (float) Math.log((double) z.lastUpdatedDoc / (double) nbdoc);  //calcule idf
        //msg("idf:"+idf);
        float[] wgt = new float[nbdoc];

        for (int i = 0; i < nbdoc; i++) {
            { // log(df/ds)+1 * idf
                //msg("log(df/ds)+1:"+((float)Math.log((double)occ[i]/(double)z.docstable.getSize(doc[i]))+1));
                // old wgt[i]=((float)Math.log((double)occ[i]/(double)z.docstable.getSize(doc[i]))+1)*idf;

                // df/ds * idf   // peut être optimisé pour faire toute les divisions des ds à la fin des requêtes
                //msg(""+(float)occ[i]/(float)z.docstable.getSize(doc[i]));
                wgt[i] = (float) occ[i] / (float) z.docstable.getSize(doc[i]) * idf;  //new 
            }

        }
        //t1.stop(false);

        //showVector(wgt);

        z.indexread.unlock(n);
        return new QRes(doc, wgt);
    }

    public static final QRes getDocforW_BM25(IdxStructure z, int n) {
        z.indexread.lockForBasic(n);
        int nbdoc = z.indexread.getNbDoc(n);
//        msg("word id:"+n);
//        msg("getDocforW_IDFxTDF nbdoc:"+nbdoc);
        int[] doc = copyVector(nbdoc, z.indexread.getReferenceOnDoc(n));
//        msg("  doc:"+doc.length);
//        showVector(doc);

        //TimerNano t1=new TimerNano("evalue IDFxTDF",true);
        int[] occ = z.indexread.getvDoc(n, nbdoc, nbdoc);  // les occurences
//        msg("  occ:"+occ.length);
//        showVector(occ);

        float idf = (float) Math.log((double) z.lastUpdatedDoc / (double) nbdoc);  //calcule idf !! cette formule est simplifiée par rapport a la BM25 mais il n'y pas de vrai diffférence (testé sur la collection ENG de clef) 
        //float idf=(float)Math.log(((double)z.lastUpdatedDoc-(double)nbdoc+0.5)/((double)nbdoc+0.5));  //calcule idf , BM25 non simplifiée
//        msg("idf:"+idf);
        float[] wgt = new float[nbdoc];


        //!!! doit être mis en constante pour optimiser !!!!
        float avgl=1000;
        if (z.cntpos!=0)  avgl = z.cntpos / z.lastUpdatedDoc; // longueur moyenne des documents
//       msg("z.cntpos:"+z.cntpos);
//        msg("z.lastUpdatedDoc:"+z.lastUpdatedDoc);
         float k1 = 2.0f;
        float b = 0.75f;
        float unmimusb = 1 - b;
        float k1plus1 = k1 + 1;
        //!!!!!!

        for (int i = 0; i < nbdoc; i++) {
            {
                float f = (float) occ[i];
                float lenOfDoc = (float) z.docstable.getSize(doc[i]);
                float bm25 = (f * k1plus1) / (f + k1 * (unmimusb + b * lenOfDoc / avgl));
                wgt[i] = bm25 * idf;  //new 
//                msg("f:"+f+", k1plus1 "+k1plus1+",(f * k1plus1)"+(f * k1plus1));
//                msg("f:"+f+", k1 "+k1+",(f + k1 * (unmimusb + b * lenOfDoc / avgl))"+(f + k1 * (unmimusb + b * lenOfDoc / avgl)));
//                msg("Bm25:"+bm25+", "+lenOfDoc+", "+f+", "+wgt[i]);
            }

        }
        //t1.stop(false);

//        showVector(wgt);

        z.indexread.unlock(n);
        return new QRes(doc, wgt);
    }
 
       public static final QRes getDocforW_BM25Twice(IdxStructure z, int n) {
        z.indexread.lockForBasic(n);
        int nbdocfull = z.indexread.getNbDoc(n);
//        msg("word id:"+n);
//        msg("getDocforW_IDFxTDF nbdoc:"+nbdoc);
        int[] docfull = copyVector(nbdocfull, z.indexread.getReferenceOnDoc(n));
        int[] doc = andVector(docfull,z.resQ1);
        int nbdoc = doc.length;
        msg("  docfull:"+docfull.length+"  doc:"+doc.length);
//        showVector(doc);

        //TimerNano t1=new TimerNano("evalue IDFxTDF",true);
        int[] occ = z.indexread.getvDoc(n, nbdocfull, nbdocfull);  // les occurences
//        msg("  occ:"+occ.length);
//        showVector(occ);

        //float idf = (float) Math.log((double) z.lastUpdatedDoc / (double) nbdoc);  //calcule idf !! cette formule est simplifiée par rapport a la BM25 mais il n'y pas de vrai diffférence (testé sur la collection ENG de clef) 
        float idf=(float)Math.log(((double)z.resQ1.length-(double)nbdoc+0.5)/((double)nbdoc+0.5));  //calcule idf , BM25 non simplifiée
//        msg("idf:"+idf);
        float[] wgt = new float[nbdoc];


        //!!! doit être mis en constante pour optimiser !!!!
        float avgl=1000;
        if (z.cntpos!=0)  avgl = z.cntpos / z.lastUpdatedDoc; // longueur moyenne des documents
//       msg("z.cntpos:"+z.cntpos);
//        msg("z.lastUpdatedDoc:"+z.lastUpdatedDoc);
         float k1 = 2.0f;
        float b = 0.75f;
        float unmimusb = 1 - b;
        float k1plus1 = k1 + 1;
        //!!!!!!

        for (int i = 0; i < nbdoc; i++) {
            {
                int posOfDoc = getIdxOfValue(docfull, docfull.length, doc[i]);
                if (posOfDoc==-1){
                    error("in getDocforW_BM25Twice");
                    return null;
                }
                //msg(i+" posOfDoc "+posOfDoc);        
                float f = (float) occ[posOfDoc];
                float lenOfDoc = (float) z.docstable.getSize(doc[i]);
                float bm25 = (f * k1plus1) / (f + k1 * (unmimusb + b * lenOfDoc / avgl));
                wgt[i] = bm25 * idf;  //new 
//                msg("f:"+f+", k1plus1 "+k1plus1+",(f * k1plus1)"+(f * k1plus1));
//                msg("f:"+f+", k1 "+k1+",(f + k1 * (unmimusb + b * lenOfDoc / avgl))"+(f + k1 * (unmimusb + b * lenOfDoc / avgl)));
//                msg("Bm25:"+bm25+", "+lenOfDoc+", "+f+", "+wgt[i]);
            }

        }
        //t1.stop(false);

//        showVector(wgt);

        z.indexread.unlock(n);
        return new QRes(doc, wgt);
    } 
       
    public static final QRes getDocforW_BM25TwiceXXXX(IdxStructure z, int n) {
        z.indexread.lockForBasic(n);
        int nbdoc = z.indexread.getNbDoc(n);
//        msg("word id:"+n);
//        msg("getDocforW_IDFxTDF nbdoc:"+nbdoc);
        int[] doc = copyVector(nbdoc, z.indexread.getReferenceOnDoc(n));
        
//        msg("  doc:"+doc.length);
//        showVector(doc);

        //TimerNano t1=new TimerNano("evalue IDFxTDF",true);
        int[] occ = z.indexread.getvDoc(n, nbdoc, nbdoc);  // les occurences
//        msg("  occ:"+occ.length);
//        showVector(occ);
        //int totalCorpus=z.resQ1.length;  // recalcule la taille du corpus ramené au résultat de la première question
        int totalCorpus=z.lastUpdatedDoc;  // recalcule la taille du corpus ramené au résultat de la première question
        //int[] docInQ1=andVector(z.resQ1,doc); // restreint la réponse à Q1        
        int[] docInQ1=copyVector(nbdoc, z.indexread.getReferenceOnDoc(n));       
        int nbdocInQ1=docInQ1.length; // nb de réponses restreint  à Q1

        float idf = (float) Math.log((double) totalCorpus / (double) nbdocInQ1);  //calcule idf !! cette formule est simplifiée pas testée encore dans le cas restreint
        //float idf=(float)Math.log(((double)totalCorpus-(double)nbdocInQ1+0.5)/((double)nbdocInQ1+0.5));  //calcule idf , BM25 non simplifiée
//        msg("idf:"+idf);
        float[] wgt = new float[nbdocInQ1];


        //!!! doit être mis en constante pour optimiser !!!!
        float avgl=1000;
        if (z.cntpos!=0)  avgl = z.lastUpdatedDoc / totalCorpus; // longueur moyenne des documents, on admet que c'est la même
//       msg("z.cntpos:"+z.cntpos);
//        msg("z.lastUpdatedDoc:"+z.lastUpdatedDoc);
         float k1 = 2.0f;
        float b = 0.75f;
        float unmimusb = 1 - b;
        float k1plus1 = k1 + 1;
        //!!!!!!

        for (int i = 0; i < nbdocInQ1; i++) {
            {
                int posOfDoc = getIdxOfValue(doc, doc.length, docInQ1[i]);
                if (posOfDoc==-1){
                    error("in getDocforW_BM25Twice");
                    return null;
                }
                //msg(i+" posOfDoc "+posOfDoc);        
                float f = (float) occ[posOfDoc];
                float lenOfDoc = (float) z.docstable.getSize(docInQ1[i]);
                float bm25 = (f * k1plus1) / (f + k1 * (unmimusb + b * lenOfDoc / avgl));
                wgt[i] = bm25 * idf;  //new 
//                msg("f:"+f+", k1plus1 "+k1plus1+",(f * k1plus1)"+(f * k1plus1));
//                msg("f:"+f+", k1 "+k1+",(f + k1 * (unmimusb + b * lenOfDoc / avgl))"+(f + k1 * (unmimusb + b * lenOfDoc / avgl)));
//                msg("Bm25:"+bm25+", "+lenOfDoc+", "+f+", "+wgt[i]);
            }

        }
        //t1.stop(false);

//        showVector(wgt);

        z.indexread.unlock(n);
        return new QRes(docInQ1, wgt);
    }

    /** cherche les documents contenant le terme w1 et le terme w2 ( sans limite sur le résultat)
     * @param z  index
     * @param w1  terme1
     * @param w2  terme1
     * @return vecteur de documents
     */
    public static final QRes getDocforWandW(IdxStructure z, String w1, String w2) {
        return getDocforWandW(z, w1, w2, Integer.MAX_VALUE);
    } // no limit

    /** cherche les documents contenant le terme w1 et le terme w2 (limite sur le résultat)
     * @param z  index
     * @param w1  terme1
     * @param w2  terme1
     * @param maxResult  limite la taille de la réponse
     * @return vecteur de documents
     */
    public static final QRes getDocforWandW(IdxStructure z, String w1, String w2, int maxResult) { // migrate
        // initialise les références locales à l'index
        CacheRead_Opti indexread = z.indexread;
        //
        int n1 = z.getIntForW(w1);
        int n2 = z.getIntForW(w2);
        int id = 0;

        if ((n1 != NOT_FOUND) && (n2 != NOT_FOUND)) {
            indexread.lockForBasic(n1);
            indexread.lockForBasic(n2);

            // zone protégée pour n1 et n2
            /**time*/ // TimerNano t=new TimerNano(w1+" and "+w2,true);
            int[] res = andVector(indexread.getReferenceOnDoc(n1), indexread.getNbDoc(n1),
                    indexread.getReferenceOnDoc(n2), indexread.getNbDoc(n2));
            if (res.length > maxResult) {
                res = copyVector(maxResult, res);
            } // tronque le résultat
            /**time*/ // t.stop(false);
            /**time*/ // msg("res length "+res.length);
            // fin de zone
            indexread.unlock(n1);
            indexread.unlock(n2);

            return new QRes(res);
        } else {
            return null;
        }
    }

    /** cherche les documents contenant le terme w1 proche du terme terme w2.
     * La distance est fixée par la constante nearDistance de IdxStructure
     * @param z  index
     * @param w1  terme1
     * @param w2  terme1
     * @return vecteur de documents
     */
    public static final QRes getDocforWnearW(IdxStructure z, String w1, String w2) {
        return getDocforWnearW(z, w1, w2, Integer.MAX_VALUE, NEAR_DISTANCE);
    } // no limit

    /** cherche les documents contenant le terme w1 proche du terme terme w2 en fixant la proximité
     * @param z  index
     * @param w1  terme1
     * @param w2  terme1
     * @param maxResult  limite max du résultat
     * @param nearvalue  limite max séparant w1 et w2
     * @return vecteur de documents
     */
    public static final QRes getDocforWnearW(IdxStructure z, String w1, String w2, int maxResult, int nearvalue) {
        // initialise les références locales à l'index
        CacheRead_Opti indexread = z.indexread;
        //
        int n1 = z.getIntForW(w1);
        int n2 = z.getIntForW(w2);
        // msg("near n :"+n1+","+n2);

        PosLength p1, p2;
        int pl1, pl2, pc1, pc2;
        if ((n1 != NOT_FOUND) & (n2 != NOT_FOUND)) {
            indexread.lockForFull(n1);
            indexread.lockForFull(n2);
            // zone protégée pour n1 et n2

            //TimerNano t = new TimerNano(w1+" near "+w2,true);
            if (indexread.getNbDoc(n1) > indexread.getNbDoc(n2)) {  // permute n1,n2 , ATTENTION LES LIBELLES W1,W2 NE SONT PAS PERMUTES
                int temp = n1;
                n1 = n2;
                n2 = temp;
            }
            int cid1 = indexread.getCacheId(n1);
            int cid2 = indexread.getCacheId(n2);
            int id = 0;
            int r1[] = indexread.getReferenceOnDoc(n1);
            int il1 = indexread.getNbDoc(n1);
            int r2[] = indexread.getReferenceOnDoc(n2);
            int il2 = indexread.getNbDoc(n2);
            // showVector(r1);
            // showVector(r2);
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[Math.min(Math.min(il1, il2), maxResult)];
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (id == maxResult || wc1 >= il1 || wc2 >= il2) {
                    break;
                }
                if (r1[wc1] == r2[wc2]) { // and ok
                    // msg("near:"+r1[wc1]+","+r2[wc2]);
                    p1 = indexread.getWPosLengthDirect(cid1, wc1);
                    pl1 = p1.pos + p1.length;
                    p2 = indexread.getWPosLengthDirect(cid2, wc2);
                    pl2 = p2.pos + p2.length;
                    pc1 = p1.pos;
                    pc2 = p2.pos;
                    while (true) { // initialize a new merge sort  r1 and r2 must be ordered !!!!!
                        if (pc1 >= pl1 || pc2 >= pl2) {
                            break;
                        }
                        int ref1 = indexread.vPosDirect(cid1, pc1);
                        int ref2 = indexread.vPosDirect(cid2, pc2);
                        // System.out.println("  merge2 "+pc1+", "+r1[pc1]+", "+pc2+", "+r2[pc2]);
                        if (Math.abs(ref1 - ref2) < nearvalue) { // near ok and test third, ...
                            doc[id] = r1[wc1]; // the first operand stay the reference for occurrences
                            id++;
                            break;
                        } else if (ref1 < ref2) {
                            pc1++;
                        } else {
                            pc2++;
                        }
                    } // while
//                    if (nearTest(z.getWposition(n1,wc1),z.getWposition(n2,wc2),nearvalue)) { // code non optimisé remplacé par le while audessus!
//                        doc[id] = r1[wc1]; // the first operand stay the reference for occurrences
//                        id++;
//                    }
                    wc1++;
                    wc2++;// next document in any case
                } else if (r1[wc1] < r2[wc2]) {
                    wc1++;
                } else {
                    wc2++;
                }
            }
            doc = copyVector(id, doc);
            //t.stop(false);
            /**time*/ // msg("res length "+doc.length);
            // fin de zone
            indexread.unlock(n1);
            indexread.unlock(n2);
//            msg("end near");
//            showVector(doc);
            return new QRes(doc);

        } else {
            return new QRes(null);
        }
    }

    /** cherche les documents contenant le terme w2 succéde au terme w1.
     * @param z  index
     * @param w1  terme1
     * @param w2  terme1
     * @return vecteur de documents
     */
    public static final QRes getDocforWnextW(IdxStructure z, String w1, String w2) {
        return getDocforWnextW(z, w1, w2, Integer.MAX_VALUE);
    } // no limit

    /** cherche les documents contenant le terme w2 succéde au terme w1 (en limitant le résultat)
     * @param z  index
     * @param w1  terme1
     * @param w2  terme1
     * @param maxResult  limite max du résultat
     * @return vecteur de documents
     */
    public final static QRes getDocforWnextW(IdxStructure z,String w1, String w2, int maxResult) {
        int n1 = z.getIntForW(w1);
        int n2 = z.getIntForW(w2);
        return getDocforWnextW(z, n1,  n2,  maxResult);
    }
    /** cherche les documents contenant le terme n2 succéde au terme n1 (en limitant le résultat)
     * @param z  index
     * @param n1  terme1
     * @param n2  terme1
     * @param maxResult  limite max du résultat
     * @return vecteur de documents
     */
    public final static QRes getDocforWnextW(IdxStructure z,int n1, int n2, int maxResult) {  // modification 8.5.2006 JG
        // doit être optimisé pour ne pas travailler sur les copies comme le NEAR !!!!!
        // initialise les références locales à l'index
        CacheRead_Opti indexread=z.indexread;
        
        int pc1, pc2, pl1, pl2;
        PosLength p1,p2;
        if ((n1 != NOT_FOUND) & (n2 != NOT_FOUND)) {
            indexread.lockForFull(n1);
            indexread.lockForFull(n2);
            // zone protégée pour n1 et n2

            //TimerNano t = new TimerNano(n1 + " next " + n2, true);
            int id = 0;
            int r1[] = indexread.getReferenceOnDoc(n1);
            int il1 = indexread.getNbDoc(n1);
            int r2[] = indexread.getReferenceOnDoc(n2);
            int il2 = indexread.getNbDoc(n2);
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[Math.min(Math.min(il1, il2), maxResult)];
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (id == maxResult) {
                    break;
                }
                if (wc1 >= il1 || wc2 >= il2) {
                    break;
                }
                //System.out.println("merge2 "+wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
                if (r1[wc1] == r2[wc2]) { // and ok
                    p1 = z.getWPosLength(n1, wc1);
                    pl1 = p1.pos + p1.length;// doit être optimisé pour ne pas travailler sur les copies
                    p2 = z.getWPosLength(n2, wc2);
                    pl2 = p2.pos + p2.length;// doit être optimisé pour ne pas travailler sur les copies
                    pc1 = p1.pos;
                    pc2 = p2.pos;
                    while (true) { // initialize a new merge sort  r1 and r2 must be ordered !!!!!
                        if (pc1 >= pl1 || pc2 >= pl2) {
                            break;
                        }
                        // System.out.println("  merge2 "+pc1+", "+r1[pc1]+", "+pc2+", "+r2[pc2]);
                        int ref2 = indexread.vPos(n2, pc2);
                        if ((ref2 - indexread.vPos(n1, pc1)) == 1) { // near ok
                            doc[id] = r1[wc1]; // the first operand stay the reference for occurrences
                            id++;
                            break;
                        } else if (indexread.vPos(n1, pc1) < ref2) {
                            pc1++;
                        } else {
                            pc2++;
                        }
                    } // while
                    wc1++;
                    wc2++;// next document in any case
                } else if (r1[wc1] < r2[wc2]) {
                    wc1++;
                } else {
                    wc2++;
                }
            }
            doc = copyVector(id, doc);
            //t.stop(false);
            //msg("res length "+doc.length);

            // fin de zone
            indexread.unlock(n1);
            indexread.unlock(n2);

            return new QRes(doc);
        } else {
            return new QRes(null);
        }
    }

    /** cherche les documents contenant la séquence de termes w1,w2,w3
     * @param z  index
     * @param w1 terme1
     * @param w2 terme2
     * @param w3 terme3
     * @return vecteur de documents
     */
    public static final int[] getDocforWseq3(IdxStructure z, String w1, String w2, String w3) {
        int n1 = z.getIntForW(w1);
        int n2 = z.getIntForW(w2);
        int n3 = z.getIntForW(w3);
        /**time*/ // msg("seq3 "+w1+","+w2+","+w3);
        return getDocforWseqW3(z, n1, n2, n3);
    }

    /** cherche les documents contenant la séquence de termes n1,n2,n3
     * @param z  index
     * @param n1 terme1
     * @param n2 terme2
     * @param n3 terme3
     * @return vecteur de documents
     */
    public static final int[] getDocforWseqW3(IdxStructure z, int n1, int n2, int n3) {
        // initialise les références locales à l'index
        CacheRead_Opti indexread = z.indexread;
        int pc1, pc2, pl1, pl2;
        PosLength p1, p2;
        if ((n1 != NOT_FOUND) & (n2 != NOT_FOUND) & (n3 != NOT_FOUND)) {
            indexread.lockForFull(n1);
            indexread.lockForFull(n2);
            indexread.lockForFull(n3);
            // zone protégée

            /**time*/ //TimerNano t=new TimerNano(" seq3 "+n1+","+n2+","+n3,true);
            int id = 0;
            int r1[] = indexread.getReferenceOnDoc(n1);
            int il1 = indexread.getNbDoc(n1);
            int r2[] = indexread.getReferenceOnDoc(n2);
            int il2 = indexread.getNbDoc(n2);
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[Math.min(il1, il2)];
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (wc1 >= il1 || wc2 >= il2) {
                    break;
                }
                // System.out.println("merge2 "+wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
                if (r1[wc1] == r2[wc2]) { // and ok
                    p1 = z.getWPosLength(n1, wc1);
                    pl1 = p1.pos + p1.length;
                    p2 = z.getWPosLength(n2, wc2);
                    pl2 = p2.pos + p2.length;
                    pc1 = p1.pos;
                    pc2 = p2.pos;
                    while (true) { // initialize a new merge sort  r1 and r2 must be ordered !!!!!
                        if (pc1 >= pl1 || pc2 >= pl2) {
                            break;
                        }
                        // System.out.println("  merge2 "+pc1+", "+r1[pc1]+", "+pc2+", "+r2[pc2]);
                        int ref2 = indexread.vPos(n2, pc2);
                        if (((ref2 - indexread.vPos(n1, pc1))) == 1 && (indexread.chekforWatPinD(n3, ref2 + 1, r1[wc1]))) { // near ok and test third
                            doc[id] = r1[wc1]; // the first operand stay the reference for occurrences
                            id++;
                            break;
                        } else if (indexread.vPos(n1, pc1) < ref2) {
                            pc1++;
                        } else {
                            pc2++;
                        }
                    } // while
                    wc1++;
                    wc2++;// next document in any case
                } else if (r1[wc1] < r2[wc2]) {
                    wc1++;
                } else {
                    wc2++;
                }
            }
            doc = copyVector(id, doc);
            /**time*/ //t.stop(false);
            /**time*/ //msg("res length "+doc.length);
            // fin de zone
            indexread.unlock(n1);
            indexread.unlock(n2);
            indexread.unlock(n3);

            return removeInvalid(z.docstable, doc);
        } else {
            return null;
        }
    }

    /* seule cette méthode est optimisée pour ne pas copier des vecteurs mais travailler sur le cache !*/
    /** pour le référenceur, séquence sur 6 termes
     * @param z  index
     * @param n1 terme1
     * @param n2 terme2
     * @param n3 terme3
     * @param n4 terme4
     * @param n5 terme5
     * @param n6 terme6
     * @return vecteur de documents
     */
    public static final int[] getDocforWseqW6(IdxStructure z, int n1, int n2, int n3, int n4, int n5, int n6) {
        // initialise les références locales à l'index
        CacheRead_Opti indexread = z.indexread;
        int pc1, pc2, pl1, pl2;
        PosLength p1, p2;
        if ((n1 != NOT_FOUND) & (n2 != NOT_FOUND) & (n3 != NOT_FOUND) & (n4 != NOT_FOUND) & (n5 != NOT_FOUND) & (n6 != NOT_FOUND)) {
            indexread.lockForFull(n1);
            int cid1 = indexread.getCacheId(n1);
            indexread.lockForFull(n2);
            int cid2 = indexread.getCacheId(n2);
            indexread.lockForFull(n3);
            int cid3 = indexread.getCacheId(n3);
            indexread.lockForFull(n4);
            int cid4 = indexread.getCacheId(n4);
            indexread.lockForFull(n5);
            int cid5 = indexread.getCacheId(n5);
            indexread.lockForFull(n6);
            int cid6 = indexread.getCacheId(n6);
            // zone protégée

            //TimerNano t=new TimerNano(" seq3 "+n1+","+n2+","+n3,true);
            int id = 0;
            int r1[] = indexread.getReferenceOnDoc(n1);
            int il1 = indexread.getNbDoc(n1);
            int r2[] = indexread.getReferenceOnDoc(n2);
            int il2 = indexread.getNbDoc(n2);
            int wc1 = 0;
            int wc2 = 0;
            int doc[] = new int[Math.min(il1, il2)];
            //msg("merge1 "+n1+", "+il1+", "+n2+", "+il2);
            while (true) { // merge sort  r1 and r2 must be ordered !!!!!
                if (wc1 >= il1 || wc2 >= il2) {
                    break;
                }
                //System.out.println("merge2 "+wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
                if (r1[wc1] == r2[wc2]) { // and ok
                    p1 = indexread.getWPosLengthDirect(cid1, wc1);
                    pl1 = p1.pos + p1.length;
                    p2 = indexread.getWPosLengthDirect(cid2, wc2);
                    pl2 = p2.pos + p2.length;
                    pc1 = p1.pos;
                    pc2 = p2.pos;
                    while (true) { // initialize a new merge sort  r1 and r2 must be ordered !!!!!
                        if (pc1 >= pl1 || pc2 >= pl2) {
                            break;
                        }
                        int ref1 = indexread.vPosDirect(cid1, pc1);
                        int ref2 = indexread.vPosDirect(cid2, pc2);
                        // System.out.println("  merge2 "+pc1+", "+r1[pc1]+", "+pc2+", "+r2[pc2]);
                        if (((ref2 - ref1) == 1) && (indexread.chekforWatPinDDirect(cid3, ref2 + 1, r1[wc1])) && (indexread.chekforWatPinDDirect(cid4, ref2 + 2, r1[wc1])) && (indexread.chekforWatPinDDirect(cid5, ref2 + 3, r1[wc1])) && (indexread.chekforWatPinDDirect(cid6, ref2 + 4, r1[wc1]))) { // near ok and test third, ...
                            doc[id] = r1[wc1]; // the first operand stay the reference for occurrences
                            id++;
                            break;
                        } else if (ref1 < ref2) {
                            pc1++;
                        } else {
                            pc2++;
                        }
                    } // while
                    wc1++;
                    wc2++;// next document in any case
                } else if (r1[wc1] < r2[wc2]) {
                    wc1++;
                } else {
                    wc2++;
                }
            }
            doc = copyVector(id, doc);
            //t.stop(false);
            //msg("res length "+doc.length);

            // fin de zone
            indexread.unlock(n1);
            indexread.unlock(n2);
            indexread.unlock(n3);
            indexread.unlock(n4);
            indexread.unlock(n5);
            indexread.unlock(n6);

            return removeInvalid(z.docstable, doc);
        } else {
            return null;
        }
    }

    /* seule cette méthode est optimisée pour ne pas copier des vecteurs mais travailler sur le cache !*/
    /** pour le référenceur, séquence sur n termes
     * @param z  index
     * @param n vecteur de termes
     * @return vecteur de documents
     */
    public static final QRes getDocforWseqWN(IdxStructure z, int[] n) {
        // initialise les références locales à l'index
        CacheRead_Opti indexread = z.indexread;
        int pc1, pc2, pl1, pl2;
        PosLength p1, p2;
        if (n == null) {
            return null;
        }
        int nl = n.length;
        if (nl == 0) {
            return null;
        } // 0 termes
        if (nl == 1) {
            return getDocforW_NO(z, n[0]);
        }// 1 termes
        // 2 termes ou plus
        int[] cid = new int[nl];
        for (int i = 0; i < nl; i++) {
            indexread.lockForFull(n[i]);
            cid[i] = indexread.getCacheId(n[i]);
        }
        int n1 = n[0];
        int cid1 = cid[0];   // optimise les références
        int n2 = n[1];
        int cid2 = cid[1];   // optimise les références
        //TimerNano t=new TimerNano(" seq3 "+n1+","+n2+","+n3,true);
        int id = 0;
        int r1[] = indexread.getReferenceOnDoc(n1);
        int il1 = indexread.getNbDoc(n1);
        int r2[] = indexread.getReferenceOnDoc(n2);
        int il2 = indexread.getNbDoc(n2);
        int wc1 = 0;
        int wc2 = 0;
        int doc[] = new int[Math.min(il1, il2)];
        //msg("merge1 "+n1+", "+il1+", "+n2+", "+il2);
        while (true) { // merge sort  r1 and r2 must be ordered !!!!!
            if (wc1 >= il1 || wc2 >= il2) {
                break;
            }
            //System.out.println("merge2 "+wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
            if (r1[wc1] == r2[wc2]) { // and ok
                p1 = indexread.getWPosLengthDirect(cid1, wc1);
                pl1 = p1.pos + p1.length;
                p2 = indexread.getWPosLengthDirect(cid2, wc2);
                pl2 = p2.pos + p2.length;
                pc1 = p1.pos;
                pc2 = p2.pos;
                while (true) { // initialize a new merge sort  r1 and r2 must be ordered !!!!!
                    if (pc1 >= pl1 || pc2 >= pl2) {
                        break;
                    }
                    int ref1 = indexread.vPosDirect(cid1, pc1);
                    int ref2 = indexread.vPosDirect(cid2, pc2);
                    // System.out.println("  merge2 "+pc1+", "+r1[pc1]+", "+pc2+", "+r2[pc2]);
                    if ((ref2 - ref1) == 1) {
                        int ok;
                        for (ok = 2; ok < nl; ok++) {
                            if (!indexread.chekforWatPinDDirect(cid[ok], ref1 + ok, r1[wc1])) {
                                break;
                            }
                        }
                        if (ok == nl) { // near ok tous tester
                            doc[id] = r1[wc1]; // the first operand stay the reference for occurrences
                            id++;
                            break;
                        } else if (ref1 < ref2) {
                            pc1++;
                        } else {
                            pc2++;
                        }
                    } else if (ref1 < ref2) {
                        pc1++;
                    } else {
                        pc2++;
                    }
                } // while
                wc1++;
                wc2++;// next document in any case
            } else if (r1[wc1] < r2[wc2]) {
                wc1++;
            } else {
                wc2++;
            }
        }
        doc = copyVector(id, doc);
        //t.stop(false);
        //msg("res length "+doc.length);

        // fin de zone
        for (int i = 0; i < nl; i++) {
            indexread.unlock(n[i]);
        }

        return new QRes(removeInvalid(z.docstable, doc));
    }

    /**
     * élimine les documents invalides (effacés).
     * @return vecteur de documents valides
     * @param docstable le gestionnaire de corpus
     * @param res le vecteur de documens à nettoyer
     */
    public static final int[] removeInvalid(DocumentManager docstable, int[] res) {
        // filtrage des invalides
        //        msg("removeInvalid:");
        //        showVector(res);
        int[] DD = new int[res.length];
        int last = 0;
        for (int i = 0; i < res.length; i++) {
            if (docstable.isValid(res[i])) { // il est valide
                DD[last] = res[i];
                last++;
            }
        }
        if (last == DD.length) {
            return DD;
        } // pas d'invalidation
        return copyVector(last, DD); // ajuste la taille
    }
}
