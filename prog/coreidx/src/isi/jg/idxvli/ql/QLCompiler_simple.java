package isi.jg.idxvli.ql;

import java.io.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.server.QLResultAndRank;
import isi.jg.idxvli.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.SetOperation.*;
import static isi.jg.idxvli.ql.QueryOperator.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * Compilateur pour un langage de requête d'interrogation pour une recherche documentaire version simple et verbose.
 * 
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
 * Compilateur pour un langage de requête d'interrogation pour une recherche documentaire.
 *
 * <pre>
 * query = subquery < query_operator subquery >.
 * query_operator= "AND" | "OR" | "MINUS" .
 * subquery = expression [ filter ] .
 * filter = "IN" "[" exp_property "]" .
 * expression = term < query_operator term > .
 * term =   qString
 *           | "near" "(" qString "," qString ")"
 *           | "next" "(" qString "," qString ")"
 *           | QUOTATION "(" qString ")"
 *           | "(" query ")".
 * qString = ""STRING"".
 * exp_property = term_property <filter_operator term_property >
 * filter_operator="AND" | "OR"
 * term_property = [ "NOT" ] ""property_name""
 *               | "LENGTH" rel_operator ""numeric_value""
 *               | "DATE" rel_operator ""date_value"".
 * rel_operator = "<" || ">"
 * </pre>
 *
 * exemples:
 *
 * <pre>
 * test("prenant");
 * test("italie");
 * test("italie AND prenant");
 * test("italie OR prenant");
 * test("italie MINUS prenant");
 * test("italie AND prenant AND gouvernement");
 * test("prenant MINUS (italie AND prenant AND gouvernement)");
 * test("undertaking");
 * test("italy");
 * test("italy AND undertaking");
 * test("italy OR undertaking");
 * test("(italie AND prenant) AND (italy AND undertaking)");
 * test("(italie AND prenant) OR (italy AND undertaking)");
 * test("prenant AND acte");
 * test("NEXT(prenant,acte)");
 * test("NEAR(prenant,gouvernement)");
 * test("tarif");
 * test("tarif IN[\"_EN\"]");
 * test("(tarif IN[\"_EN\"])IN[\"_FR\"]");
 * test("tarif IN[\"_FR\"]");
 * test("tarif IN[ NOT \"_FR\"]");
 * test("tarif IN[\"_EN\" OR \"_FR\"]");
 * test("tarif IN[LENGTH > \"1000\"]");
 * test("tarif IN[LENGTH < \"1000\"]");
 * test("tarif IN[DATE < \"09-02-2004\"]");
 * test("((italie AND prenant)IN[\"_FR\"] OR (italy AND undertaking)IN[\"_EN\"]) IN [LENGTH < \"1000\"]");
 * * </pre>
 *
 *
 * to do:
 *  il reste à implementer les opérations sur LENGTH ET DATE ...
 *
 * modification ajout de QUOTATION("les droits de l'homme sont")
 */
public class QLCompiler_simple {

    /** version du compilateur */
    public static final String COMPILER_VERSION = "1.02";
    /** caractère pour quoter les symboles*/
    public static final char QUOTE_CHAR = '"';

    private static enum Operation {

        AND, OR, MINUS, ERROR
    };

    private static enum Relation {

        LESS, GREATHER, ERROR
    };
    private static boolean verboseparsing = false;  // pour les tests

    private static boolean verbosenext = false; // pour les tests

    private int errCount;
    private StreamTokenizer sym;
    private BufferedReader txt;
    private IdxStructure z;
    private static int TT_WORD = StreamTokenizer.TT_WORD;
    private static int TT_NUMBER = StreamTokenizer.TT_NUMBER;
    private static int TT_EOL = StreamTokenizer.TT_EOL;
    private static int TT_EOF = StreamTokenizer.TT_EOF;

    /**
     * Création d'un compilateur pour une requête et son indexeur
     * @param rdr source à compiler
     * @param id indexeur de référence
     */
    public QLCompiler_simple(Reader rdr, IdxStructure id) {
        z = id;
        init(rdr);
    }

    private final void init(Reader rdr) {
        sym = new StreamTokenizer(rdr);
        sym.wordChars('_', '_');
        sym.wordChars('-', '-');
        sym.wordChars('\'', '\'');
        sym.wordChars('0', '9');
        sym.slashSlashComments(true);
        sym.slashStarComments(true);
        sym.eolIsSignificant(false);
        sym.whitespaceChars(' ', ' ');
        sym.quoteChar(QUOTE_CHAR);
        next();
    }

    private final void next() {
        try {
            sym.nextToken();
        } catch (IOException e) {
            error("IO error in QL compiler", e);
        }
        if (verbosenext) {
            msg("next->" + sym.sval + "-" + sym.ttype);
        }
    }

    private final boolean term(String s) {
        return sym.ttype == TT_WORD && sym.sval.equals(s);
    }

    private final boolean isIdentifier() {
        if (sym.ttype == TT_WORD) {
            if (sym.sval.equals("AND") || sym.sval.equals("OR") || sym.sval.equals("MINUS") || sym.sval.equals("IN") || sym.sval.equals("NEAR") || sym.sval.equals("NEXT") || sym.sval.equals("QUOTATION") || sym.sval.equals("NOT") || sym.sval.equals("LENGTH") || sym.sval.equals("DATE")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private final boolean isSpecial(char s) {
        return sym.ttype == s;
    }

    ////////////////////////////// Parser ///////////////////////////////
    //query = subquery < query_operator subquery >.
    /**
     * Calcule la requète sur le corpus indexé.
     * @return une liste de numéro de documents
     */
    public final int[] execute() {
        int[] res = query().topdoc;
        if (sym.ttype != TT_EOF) {
            msg("truncated parsing");
        } // il reste encore des caractères

        return res;
    }

    /**
     * Calcule la requète sur le corpus indexé.
     * @return une liste de numéro de documents et le ranking
     */
    public final QLResultAndRank executeMore() {
        QRes res = query();
        if (sym.ttype != TT_EOF) {
            msg("truncated parsing");
        } // il reste encore des caractères
        if (res == null) {
            return null;
        }
        QLResultAndRank resMore = new QLResultAndRank(res.topdoc, res.toprank, 0);
        if (res.topdoc != null) {
            resMore.docName = new String[resMore.result.length];
            for (int i = 0; i < resMore.result.length; i++) {
                resMore.docName[i] = z.getFileNameForDocument(resMore.result[i]);
            }
        }
        return resMore;
    }

    //query = subquery < query_operator subquery >.
    private final QRes query() {
        QRes res1 = subquery();
        while (term("AND") || term("OR") || term("MINUS")) {
            Operation op = query_operator();
            QRes res2 = subquery();
            switch (op) {
                case AND:
                    res1 = QRes.and(res1, res2, MODE_RANKING);
                    break;
                case OR:
                    res1 = QRes.or(res1, res2, MODE_RANKING);
                    break;
                case MINUS:
                    res1 = QRes.minus(res1, res2, MODE_RANKING);
                    break;
                default:
                    res1 = null;
                    break;
            }
        }

        //msg("before filtering invalid");
        //showVector(res1);
        SetOfBits invalid = z.docstable.satisfyThisProperty(z.docstable.INVALID_NAME);
        //msg("invalid:"+invalid.length());
        QRes res = QRes.filtering(res1, invalid, false, MODE_RANKING);  // élimine les documents effacés (invalid=true)
        //       msg("sort results");

        if (res != null) {
            res.topNDoc(MODE_RANKING, MAX_RESPONSE);
        }
        return res;
    }

    // query_operator= "AND" | "OR" | "MINUS" .
    private final Operation query_operator() {
        if (term("AND")) {
            if (verboseparsing) {
                msg("query_operator: AND");
            }
            next();
            return Operation.AND;
        } else if (term("OR")) {
            if (verboseparsing) {
                msg("query_operator: OR");
            }
            next();
            return Operation.OR;
        } else if (term("MINUS")) {
            if (verboseparsing) {
                msg("query_operator: MINUS");
            }
            next();
            return Operation.MINUS;
        } else {
            msg("waiting for AND, OR, MINUS receive:" + sym.sval + "-" + sym.ttype);
        }
        return Operation.ERROR;
    }

    // subquery = expression [ filter ] .
    private final QRes subquery() {
        QRes res = expression();
        if (term("IN")) {
            res = new QRes(filtering(res.doc, filter(), true));
        }
        return res;
    }

    // filter = "IN" "[" exp_property "]"
    private final SetOfBits filter() {
        if (term("IN")) {
            if (verboseparsing) {
                msg("filter: IN");
            }
            next();
            if (sym.ttype == '[') {
                if (verboseparsing) {
                    msg("filter: [");
                }
                next();
                SetOfBits res = exp_property();
                // if (res==null) msg("res is null");
                if (sym.ttype == ']') {
                    if (verboseparsing) {
                        msg("filter: ]");
                    }
                    next();
                    return res;
                } else {
                    msg("waiting ] receive:" + sym.sval + "-" + sym.ttype);
                }
            } else {
                msg("waiting [ receive:" + sym.sval + "-" + sym.ttype);
            }
        } else {
            msg("waiting IN receive:" + sym.sval + "-" + sym.ttype);
        }
        return null;
    }

    // expression = term < query_operator term > .
    private final QRes expression() {
        QRes res1 = term();
        //        msg("term1");
        //        showVector(res1);
        while (term("AND") || term("OR") || term("MINUS")) {
            Operation op = query_operator();
            QRes res2 = term();
            //        msg("term2");
            //        showVector(res2);
            switch (op) {
                case AND:
                    res1 = QRes.and(res1, res2, MODE_RANKING);
                    break;
                case OR:
                    res1 = QRes.or(res1, res2, MODE_RANKING);
                    break;
                case MINUS:
                    res1 = QRes.minus(res1, res2, MODE_RANKING);
                    break;
                default:
                    res1 = null;
                    break;
            }
        }
        return res1;
    }

    // term =   factor
    //           | "near" "(" qString "," qString ")"
    //           | "next" "(" qString "," qString ")"
    //          | "(" query ")".
    private final QRes term() {
        if (term("QUOTATION")) {
            if (verboseparsing) {
                msg("term: QUOTATION");
            }
            next();
            // parameters -----------------------------
            if (sym.ttype == '(') {
                if (verboseparsing) {
                    msg("QUOTATION: (");
                }
                next();
            } else {
                msg("waiting for ( receive:" + sym.sval + "-" + sym.ttype);
            }
            String term1 = qString();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("QUOTATION: )");
                }
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            if (verboseparsing) {
                msg("QUOTATION : " + term1);
            }
            return getDocforQuotationForW(z, term1, MODE_RANKING);

        }
        if (term("NEAR")) {
            if (verboseparsing) {
                msg("term: NEAR");
            }
            next();
            // parameters -----------------------------
            if (sym.ttype == '(') {
                if (verboseparsing) {
                    msg("near: (");
                }
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            String term1 = qString();
            if (sym.ttype == ',') {
                if (verboseparsing) {
                    msg("near: ,");
                }
                next();
            } else {
                msg("waiting for , receive:" + sym.sval + "-" + sym.ttype);
            }
            String term2 = qString();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("near: )");
                }
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            if (verboseparsing) {
                msg("near : " + term1 + ", " + term2);
            }
            //TimerNano t1=new TimerNano("QL exec getDocforWnearW:",false);
            // l'opérateur getDocforWnearW demande une optimisation (il travaille avec des copies ...)
            QRes res = getDocforWnearW(z, term1, term2);
            if (res.doc != null && MODE_RANKING != RankingMode.NO) { // évalue les poids par les and, évite d'intégrer dans la méthode positionnelle les poids'

                res = QRes.and(res, getDocforW(z, term1, MODE_RANKING), MODE_RANKING); // recopie les poids du term1

                res = QRes.and(res, getDocforW(z, term2, MODE_RANKING), MODE_RANKING); // recopie les poids du term1

            } else {
            } // fait rien
            //t1.stop(false);

            return res;

        } else if (term("NEXT")) {
            if (verboseparsing) {
                msg("term: NEXT");
            }
            next();
            // parameters -----------------------------
            if (sym.ttype == '(') {
                if (verboseparsing) {
                    msg("next: (");
                }
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            String term1 = qString();
            if (sym.ttype == ',') {
                if (verboseparsing) {
                    msg("next: ,");
                }
                next();
            } else {
                msg("waiting for , receive:" + sym.sval + "-" + sym.ttype);
            }
            String term2 = qString();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("next: )");
                }
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            if (verboseparsing) {
                msg("next : " + term1 + ", " + term2);
            }
            QRes res = getDocforWnextW(z, term1, term2);
            if (res.doc != null && MODE_RANKING != RankingMode.NO) { // évalue les poids par les and, évite d'intégrer dans la méthode positionnelle les poids'

                res = QRes.and(res, getDocforW(z, term1, MODE_RANKING), MODE_RANKING); // recopie les poids du term1

                res = QRes.and(res, getDocforW(z, term2, MODE_RANKING), MODE_RANKING); // recopie les poids du term1

            } else {
            } // fait rien

            return res;
        } else if (sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD) {
            return factor();
        } else if (sym.ttype == '(') {
            if (verboseparsing) {
                msg("term: (");
            }
            next();
            QRes res = query();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("term: )");
                }
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            return res;
        } else {
            msg("waiting for TERM receive:" + sym.sval + "-" + sym.ttype);
        }
        return null;
    }

    // qString = ""STRING"".
    private final String qString() {  // factor

        if (sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD) {
            if (verboseparsing) {
                msg("factor: " + sym.sval);
            }
            String res = sym.sval;
            next();
            //showVector(res);
            return res;
        } else {
            error("illegal string receive:" + sym.sval + "-" + sym.ttype);
        }
        return null;
    }

    private final QRes factor() {  // factor

        if (sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD) {
            if (verboseparsing) {
                msg("factor: " + sym.sval);
            }
            QRes res = getDocforW(z, sym.sval, MODE_RANKING);
            next();
            //showVector(res);
            return res;
        } else {
            error("illegal string receive:" + sym.sval + "-" + sym.ttype);
        }
        return null;
    }

    // exp_property = term_property <filter_operator term_property >
    private final SetOfBits exp_property() {
        SetOfBits res1 = term_property();
        while (term("AND") || term("OR")) {
            Operation op = filter_operator();
            SetOfBits res2 = term_property();
            //        msg("term2");
            //        showVector(res2);
            switch (op) {
                case AND:
                    res1.and(res2, z.lastUpdatedDoc);
                    break;
                case OR:
                    res1.or(res2, z.lastUpdatedDoc);
                    break;
                default:
                    res1 = null;
                    break;
            }
        }
        return res1;
    }

    // filter_operator="AND" | "OR"
    private final Operation filter_operator() {
        if (term("AND")) {
            if (verboseparsing) {
                msg("filter_operator: AND");
            }
            next();
            return Operation.AND;
        } else if (term("OR")) {
            if (verboseparsing) {
                msg("filter_operator: OR");
            }
            next();
            return Operation.OR;
        } else {
            msg("waiting for AND, OR receive:" + sym.sval + "-" + sym.ttype);
        }
        return Operation.ERROR;
    }

    // term_property = [ "NOT" ] ""property_name""
    //                | "LENGTH" rel_operator ""value""
    //                | "DATE" rel_operator ""valeur"".
    //  rel_operator = "<" || ">"
    private final SetOfBits term_property() {
        if (term("LENGTH")) {
            if (verboseparsing) {
                msg("term_property: LENGTH");
            }
            next();
            Relation op = rel_operator();
            int length = numeric_value();
            switch (op) {
                case LESS:
                    msg("to be implemented");
                    return null;
                case GREATHER:
                    msg("to be implemented");
                    return null;
                default:
                    return null;
            }

        } else if (term("DATE")) {
            if (verboseparsing) {
                msg("term_property: DATE");
            }
            next();
            Relation op = rel_operator();
            long date = date_value();
            switch (op) {
                case LESS:
                    msg("to be implemented");
                    return null;
                case GREATHER:
                    msg("to be implemented");
                    return null;
                default:
                    return null;
            }
        } else if (term("NOT") || sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD) {
            boolean positive = true;
            if (term("NOT")) {
                if (verboseparsing) {
                    msg("term_operator: NOT");
                }
                next();
                positive = false;
            }
            if (sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD) {
                return factor_property(positive);
            }
        } else {
            msg("waiting for TERM receive:" + sym.sval + "-" + sym.ttype);
        }
        return null;
    }

    // factor = ""STRING"".
    private final SetOfBits factor_property(boolean positive) {  // factor

        if (sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD) {
            if (verboseparsing) {
                msg("factor_property: " + sym.sval);
            }
            SetOfBits res = z.docstable.satisfyThisProperty(sym.sval);
            if (!positive) {
                res.not(z.lastUpdatedDoc);
            }  // renverse toutes les valeurs de la propriété

            next();
            return res;
        } else {
            error("illegal string receive:" + sym.sval + "-" + sym.ttype);
        }
        return null;
    }

    // rel_operator= "<" | ">"
    private final Relation rel_operator() {
        if (sym.ttype == '>') {
            if (verboseparsing) {
                msg("filter_operator: >");
            }
            next();
            return Relation.GREATHER;
        } else if (sym.ttype == '<') {
            if (verboseparsing) {
                msg("filter_operator: <");
            }
            next();
            return Relation.LESS;
        } else {
            msg("waiting for >, < receive:" + sym.sval + "-" + sym.ttype);
        }
        next();
        return Relation.ERROR;
    }

    private final int numeric_value() {  // parse un int

        if (sym.ttype == QUOTE_CHAR) {
            if (verboseparsing) {
                msg("value: " + sym.sval);
            }
            try {
                return Integer.parseInt(sym.sval);
            } catch (Exception e) {
                error("during conversion:", e);
            }
            next();
        } else {
            error("illegal numeric litteral receive:" + sym.sval + "-" + sym.ttype);
        }
        return 0;
    }

    private final long date_value() {  // parse une date 'DD-MM-YYYY' et retourne un long

        if (sym.ttype == QUOTE_CHAR) {
            if (verboseparsing) {
                msg("value: " + sym.sval);
            }
            try {
                msg("to be implemented");
                return -1;
            } catch (Exception e) {
                error("during conversion:", e);
            }
            next();
        } else {
            error("illegal date litteral receive:" + sym.sval + "-" + sym.ttype);
        }
        return -1;
    }
}

