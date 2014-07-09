package isi.jg.idxvli.ql;

import isi.jg.util.TimerNano;
import java.io.*;
import java.util.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.server.*;
import isi.jg.idxvli.extra.*;
import isi.jg.conman.server.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

/**
 * exécuteur de requête.
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
 * exécuteur de requête.
 */
public class QL_Basic implements QLManager {

    private static Hashtable<String, QLResultNice> InMemory;
    private static int curentSize;
    private static int get = 0;
    private static int getInCache = 0;
    private static int countRefresh = 0;
    private static long totalTime = 0;

    /** retourne la liste des documents valides correspondants à la requête
     * pour rester compatible avec les versions précendentes
     */
    public final int[] get(String request, IdxStructure id) {
        return (new QLCompiler_simple(new StringReader(request), id)).execute();
    }

    public final QLResultAndRank getMore(String request, IdxStructure id) {
        return (new QLCompiler_simple(new StringReader(request), id)).executeMore();
    }

    public final QLResultNice get(IdxStructure id, ContentService cs, String request, int start, int size) {
        return get(id, cs, request, "", "", start, size);
    }

    public final QLResultNice get(IdxStructure id, ContentService cs, String request1, String request2, int start, int size1, int size2) {
        return getTwice(id, cs, request1, request2, "", "", start, size1, size2);
    }

    public final QLResultNice get(IdxStructure id, ContentService cs, String request, String properties, int start, int size) {
        return get(id, cs, request, properties, "", start, size);
    }

    public final QLResultNice get(IdxStructure id, ContentService cs, String request, String properties, String profile, int start, int size) {
        get++;
        if (InMemory == null) {
            initCache();
        }  // premier appel

        String idOfQuery = request + "-" + properties + "-" + profile;
        msg("idOfQuery:" + idOfQuery);
        QLResultNice nice = getFromCache(idOfQuery);

        if (nice != null) { // dans le cache

            getInCache++;
            nice.update(id, cs, request, start, size);  // met à jour la fenêtre

        } else { // pas dans le cache

            nice = evalQLNice(id, cs, request, properties, profile, start, size);
            putInCache(idOfQuery, nice);
        }
        totalTime += nice.duration;
        printStatistic();
        return nice;
    }
    public final QLResultNice getTwice(IdxStructure id, ContentService cs, String request1,
            String request2, String properties, String profile, int start, int size1, int size2) {
        get++;
        if (InMemory == null) {
            initCache();
        }  // premier appel

        String idOfQuery = request1 + "-"+request2 + "-" + properties + "-" + profile;
        msg("idOfQuery:" + idOfQuery);
        QLResultNice nice = getFromCache(idOfQuery);

        if (nice != null) { // dans le cache

            getInCache++;
            nice.update(id, cs, request1, start, size2);  // met à jour la fenêtre

        } else { // pas dans le cache

            nice = evalQLTwiceNice(id, cs, request1, request2, properties, profile, start, size1, size2);
            putInCache(idOfQuery, nice);
        }
        totalTime += nice.duration;
        printStatistic();
        return nice;
    }

    private void initCache() {
        InMemory = new Hashtable<String, QLResultNice>();
        curentSize = 0;
    }

    public final QLResultNice getFromCache(String request) {
        return InMemory.get(request);
    }

    public final synchronized void putInCache(String request, QLResultNice nice) {
        if (curentSize > MAX_QUERY_IN_CACHE) {  // on doit purger

            printStatistic();
            initCache();    // on jette tout !!

            countRefresh++;
        }
        InMemory.put(request, nice);
        curentSize++;
    }

    /**  imprime des statistiques */
    public final void printStatistic() {
        msg(getStatistic());
    }

    /**  imprime des statistiques */
    public final String getStatistic() {
        return "QUERY cache statistics -> " +
                " get: " + get + " getInCache: " + getInCache + " countRefresh: " + countRefresh + " maxInCache: " + MAX_QUERY_IN_CACHE +
                "\n totalTime for queries: " + totalTime / 1000 + " [s] meanTime: " + totalTime / get + " [ms]";
    }

    public QLResultNice evalQLNice(IdxStructure id, ContentService cs, String request, String properties, String profile, int start, int size) {
        QLResultNice res = null;
        boolean contentservice = cs != null;
        TimerNano time = new TimerNano(request, true);
        QLCompiler compiler = new QLCompiler(new StringReader(request), properties, profile, id);
        int[] doc = compiler.execute();
        String[] termsOfQuery = compiler.getTermsOfQuery();
        String alternative = compiler.getOupsQuery();
        msg("alternative query:" + alternative);
        if (doc == null) {// forme une réponse vide

            res = new QLResultNice(request, properties, profile, termsOfQuery, new int[0], null, null, null, time.stop(true) / 1000, alternative);
            return res;
        } else { // il a des résultats

            String[] docname = new String[doc.length];
            String[] title = new String[doc.length];
            String[] clue = new String[doc.length];
            for (int i = Math.max(0, start); i < Math.min(start + size, doc.length); i++) {
                String currentRef = id.getFileNameForDocument(doc[i]);
                if (contentservice) {
                    try {
                        docname[i] = cs.getRefName(currentRef);
                        title[i] = cs.getTitle(currentRef);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    clue[i] = "";
                    for (int j = 0; j < termsOfQuery.length; j++) {
                        if (termsOfQuery[j].length() > 2) { // marque pas les trop petits
                            // on doit aussi éliminer les termes des la requête AND .... à faire !!!!!!!!!!!!!!!!!!!!!

                            FromTo fromto = DocPosChar.extractIntervalForW(doc[i], id, termsOfQuery[j], 4);
                            if (fromto != null) {
                                if (contentservice) {
                                    try {
                                        clue[i] += cs.getCleanText(currentRef, fromto.from, fromto.to) + "...";
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } else {// pas de contentmanager

                    docname[i] = currentRef;
                    title[i] = "";
                    clue[i] = "";
                }
            }
            res = new QLResultNice(request, properties, profile, termsOfQuery, doc, docname, title, clue, time.stop(true) / 1000, alternative);
            for (int i = Math.max(0, start); i < Math.min(start + size, doc.length); i++) {  // hilite terms in result

                res.hilite(i);
            }
        }
        return res;
    }
 public QLResultNice evalQLTwiceNice(IdxStructure id, ContentService cs, String request1,
         String request2, String properties, String profile, int start, int size1, int size2) {
        QLResultNice res = null;
        boolean contentservice = cs != null;
        TimerNano time = new TimerNano(request1, true);
        QLCompiler compiler = new QLCompiler(new StringReader(request1), properties, profile, id);
        int[] doc = compiler.execute();
        
        if (doc == null) {// forme une réponse vide
            res = new QLResultNice(request1, properties, profile, null, new int[0], null, null, null, time.stop(true) / 1000, null);
            return res;
        } else { // il a des résultats pour la première requête.
            int[] firstdoc=copyVector(size1, doc);
            Arrays.sort(firstdoc); // on trie à nouveau les documents pour les join ...
            id.resQ1=firstdoc;  // assigne le vecteur (retour au common du fortan, ... il faut revoir cela ...)
            if(IdxConstant.MODE_RANKING!=IdxEnum.RankingMode.BM25){
                error("must be in RankingMode.BM25");
                return null;
            }
            IdxConstant.MODE_RANKING=IdxEnum.RankingMode.BM25TWICE; // prépare la réévaluation
            QLCompiler compiler2 = new QLCompiler(new StringReader(request2), properties, profile, id);
            int[] doc2 = compiler2.execute();
            String[] termsOfQuery2 = compiler2.getTermsOfQuery();
        String alternative2 = compiler2.getOupsQuery();
        msg("alternative query:" + alternative2);
        IdxConstant.MODE_RANKING=IdxEnum.RankingMode.BM25;  // remet en mode normal

            String[] docname = new String[doc2.length];
            String[] title = new String[doc2.length];
            String[] clue = new String[doc2.length];
            for (int i = Math.max(0, start); i < Math.min(start + size2, doc2.length); i++) {
                String currentRef = id.getFileNameForDocument(doc2[i]);
                if (contentservice) {
                    try {
                        docname[i] = cs.getRefName(currentRef);
                        title[i] = cs.getTitle(currentRef);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    clue[i] = "";
                    for (int j = 0; j < termsOfQuery2.length; j++) {
                        if (termsOfQuery2[j].length() > 2) { // marque pas les trop petits
                            // on doit aussi éliminer les termes des la requête AND .... à faire !!!!!!!!!!!!!!!!!!!!!

                            FromTo fromto = DocPosChar.extractIntervalForW(doc[i], id, termsOfQuery2[j], 4);
                            if (fromto != null) {
                                if (contentservice) {
                                    try {
                                        clue[i] += cs.getCleanText(currentRef, fromto.from, fromto.to) + "...";
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } else {// pas de contentmanager

                    docname[i] = currentRef;
                    title[i] = "";
                    clue[i] = "";
                }
            }
            res = new QLResultNice(request1,request2, properties, profile, termsOfQuery2, doc2, docname, title, clue, time.stop(true) / 1000, alternative2);
            for (int i = Math.max(0, start); i < Math.min(start + size2, doc2.length); i++) {  // hilite terms in result
                res.hilite(i);
            }
        }
        return res;
    }
}
