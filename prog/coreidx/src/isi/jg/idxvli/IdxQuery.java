package isi.jg.idxvli;

import java.io.*;
//import static isi.jg.idxvli.ql.QueryOperator.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxConstant.*;
import isi.jg.idxvli.extra.DocPosChar;
import isi.jg.idxvli.ref.IdxReference;
import isi.jg.idxvli.ql.QLCompiler;

/**
 * Une classe pour interroger l'index.
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
public class IdxQuery {

    IdxStructure glue; // local ref to index

    /** utilisation interne seulement
     * @param id index de référence
     */
    IdxQuery(IdxStructure id) {
        glue = id;
    }

    /** analyseur simple de requête et formatage en XML du résultat (conservée seulement pour la compatibilité).
     * Cette méthode est à utiliser lors d'appel par un noeud LAZY
     * @param command SINGLE,AND,NEAR,NEXT
     * @param p1 premier terme de la requête
     * @param p2 second terme de la requête
     * @return liste de référence en XML
     */
    public final String parseQuery(String command, String p1, String p2) { // from servlet
        if (command.equals("NEAR")) {
            return searchforWnearW(p1, p2);
        }
        if (command.equals("AND")) {
            return searchforWandW(p1, p2);
        }
        if (command.equals("NEXT")) {
            return searchforWnextW(p1, p2);
        }
        if (command.equals("SINGLE")) {
            return searchforW(p1);
        }
        if (command.equals("MULTI")) {
            return searchforString(p1, p2);
        }
        if (command.equals("SIMILAR")) {

            return "<h3>to be implemented</h3>"; //DistLexical.similardoc(Integer.parseInt(p1));
        }
        if (command.equals("")) {
            return "<h3>select a command and a term please</h3>";
        }

        return "<h3>Error in MRQuery Unknow command: " + command + "</h3>";
    }

    private final String niceLemma(String w) {
        String lemma = WORD_DEFINITION.normaliseWord(glue, w);
        String lemmaExpansion = "";
        if (WORD_USE_STEMMER) {
            int n = glue.getIntForW(w);
            if (n != -1) {
                lemmaExpansion = "<ICON><COMMENT>" + glue.getStemList(n) + "</COMMENT><IMG>plus</IMG></ICON>";
            }
        }
        String res = "<b>" + lemma + "</b><i>" + w.substring(lemma.length()) + "</i>" + lemmaExpansion;
        return res;
    }

    private final String searchforW(String w) { // without semantic context
        int[] docs = (new QLCompiler(new StringReader(w), glue)).execute();
        String s = "<p>Cherche: " + niceLemma(w) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w);
    }

    private final String searchforWandW(String w1, String w2) {
        int[] docs = (new QLCompiler(new StringReader(w1 + " AND " + w2), glue)).execute();
        String s = "<p>Cherche: " + niceLemma(w1) + " ET " + niceLemma(w2) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w1);
    }

    private final String searchforWnearW(String w1, String w2) {
        int[] docs = (new QLCompiler(new StringReader("NEAR(" + w1 + "," + w2 + ")"), glue)).execute();
        ;
        String s = "<p>Cherche: " + niceLemma(w1) + " PROCHE DE " + niceLemma(w2) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w1);
    }

    private final String searchforWnextW(String w1, String w2) {
        int[] docs = (new QLCompiler(new StringReader("NEXT(" + w1 + "," + w2 + ")"), glue)).execute();
        ;
        String s = "<p>Cherche: " + niceLemma(w1) + " SUIVI DE " + niceLemma(w2) + "</p>";
        return s + getXMLFNDocforW(getFNDocforD(docs), docs, w1);
    }

    private final String searchforString(String w, String min) { // with semantic context
        Integer n = new Integer(min);
        IdxReference s = new IdxReference(glue, w, n.intValue());
        // return s.getXML();
        return "OK done";
    }

    private final String getXMLFNDocforW(String[] fn, int[] docs, String w1) {
        String r = "";
        if (fn != null) {
            int l = fn.length;
            for (int i = 0; i < l; i++) {
                r = r + XMLrefFN(fn[i], docs[i], w1);
            }
            return r;
        } else {
            return "<paragraphe>no documents for this condition</paragraphe>";
        }
    }

    private final String XMLrefFN(String fn, int doc, String w1) {

        //    "<a href=\"ns?a=similarTo&amp;u="+fn+"\">sim("+fn+") </a>"; test similarity


        String context = "";
        if (IDX_MORE_INFO && w1 != null) {
            context = DocPosChar.extractForW(doc, glue, w1, 5);
        }
        String res = "<P_small><URL target=\"document\" href=\"" + cleanValue(fn) + "\">" + cleanValue(fn) + "</URL>" + context + "</P_small>";
        return res;

    }

    private final String[] getFNDocforD(int[] d) {
        if (d != null) {
            int l = d.length;
            String r[] = new String[l];
            for (int i = 0; i < l; i++) {
                r[i] = glue.getFileNameForDocument(d[i]);
            }
            return r;
        } else {
            return null;
        }
    }

    /**
     * nettoye les caractères pas supportés dans XML
     * @param s chaine à nettoyer
     * @return une chaine plus propre
     */
    protected final static String cleanValue(String s) {  // éliminie les & et x pour être xml compatible
        int ix = 0;
        while ((ix = s.indexOf("&", ix)) > -1) {
            s = s.substring(0, ix) + "&amp;" + s.substring(ix + 1, s.length());
            ix += 4;
        }
        ix = 0;
        while ((ix = s.indexOf("<", ix)) > -1) {
            s = s.substring(0, ix) + "&lt;" + s.substring(ix + 1, s.length());
            ix += 3;
        }
        return s;

    }
}
