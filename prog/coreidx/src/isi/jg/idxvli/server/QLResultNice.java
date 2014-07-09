package isi.jg.idxvli.server;

import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import java.io.*;
import java.util.regex.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.extra.*;
import isi.jg.idxvli.util.*;
import isi.jg.conman.server.*;

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
public class QLResultNice implements Serializable {

    /* les documents résultats */
    public int[] result;
    /* les partie du résults */
    public String[] docname;
    public String[] title;
    public String[] clue;
    /* la durée d'exécution */
    public long duration;  // en ms
    /* autour de la requête */
    public String[] termsOfQuery;
    public String query;
    public String query2;
    public String properties;
    public String profile;
    public String alternative;

     /** crée un résultat
     * @param result id des documents
     * @param duration durée
     */
    public QLResultNice(String query1 ,String query2, String properties, String profile, String[] termsOfQuery, int[] result, String[] docname, String[] title, String[] clue, long duration, String alternative) {
        this.query2 = query2;
        this.query = query1;
        this.properties = properties;
        this.profile = profile;
        this.termsOfQuery = termsOfQuery;
        this.result = result;
        this.docname = docname;
        this.title = title;
        this.clue = clue;
        this.duration = duration;
        this.alternative = alternative;
   }

    /** crée un résultat
     * @param result id des documents
     * @param duration durée
     */
    public QLResultNice(String query, String properties, String profile, String[] termsOfQuery, int[] result, String[] docname, String[] title, String[] clue, long duration, String alternative) {
        this.query = query;
        this.properties = properties;
        this.profile = profile;
        this.termsOfQuery = termsOfQuery;
        this.result = result;
        this.docname = docname;
        this.title = title;
        this.clue = clue;
        this.duration = duration;
        this.alternative = alternative;
    }

    public void hilite(int entry) {
        for (int i = 0; i < termsOfQuery.length; i++) {
            //     docname[entry]=showTerm(docname[entry],voc[i]);
            title[entry] = showTerm(title[entry], termsOfQuery[i]);
            clue[entry] = showTerm(clue[entry], termsOfQuery[i]);
        }
    }

    public static String showTerm(String s, String term) {
        if (s != null && term.length() > 2) // au moins trois caractères
        {
            return s.replaceAll(term, "<b>" + term + "</b>");
        }
        return s;
    }

    public void update(IdxStructure id, ContentService cs, String request, int start, int size) {
        boolean contentservice = cs != null;
        Timer time = new Timer(request, true);
        if (result == null) {// rien à faire
            return;
        } else { // il a des résultats
            //msg("nb res:"+result.length);
            for (int i = Math.max(0, start); i < Math.min(start + size, result.length); i++) {
                if (docname[i] == null) { // par encore évalué
                    String currentRef = id.getFileNameForDocument(result[i]);
                    if (contentservice) {
                        try {
                            docname[i] = cs.getRefName(currentRef);
                            title[i] = cs.getTitle(currentRef);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    clue[i] = "";
                    for (int j = 0; j < termsOfQuery.length; j++) {
                        if (termsOfQuery[j].length() > 2) { // marque pas les trop petits
                            // on doit aussi éliminer les termes des la requête AND .... à faire !!!!!!!!!!!!!!!!!!!!!
                            FromTo fromto = DocPosChar.extractIntervalForW(result[i], id, termsOfQuery[j], 4);
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
                }
                hilite(i);
                this.duration = time.getstop(); //update time
            }
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(result);
        out.writeObject(docname);
        out.writeObject(title);
        out.writeObject(clue);
        out.writeLong(duration);
        out.writeObject(termsOfQuery);
        out.writeObject(query);
        out.writeObject(alternative);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        result = (int[]) in.readObject();
        docname = (String[]) in.readObject();
        title = (String[]) in.readObject();
        clue = (String[]) in.readObject();
        duration = in.readLong();
        termsOfQuery = (String[]) in.readObject();
        query = (String) in.readObject();
        alternative = (String) in.readObject();
    }
}
