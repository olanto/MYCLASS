package isi.jg.idxvli.ql;

import java.io.*;
import java.util.*;
import java.sql.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.SetOperation.*;
import static isi.jg.idxvli.ql.QueryOperator.*;
import static isi.jg.idxvli.IdxConstant.*;
import isi.jg.util.TimerNano;
import static isi.jg.idxvli.IdxEnum.*;
import java.util.regex.*;

/**
 * Compilateur pour un langage de requête d'interrogation pour une recherche documentaire.
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
 * query_operator= "AND" | ("OR"|"|") | ("MINUS"|"-") .
 * subquery = expression [ filter ] .
 * filter = "IN" "[" exp_property "]" .
 * expression = term < query_operator term > .
 * term =   qString
 *           | ("NEAR"|"~") "(" qString [","] qString ")"
 *           | "NEXT" "(" qString [","] qString ")"
 *           | "QUOTATION" "(" qString ")"
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
public class QLCompiler {

    /** version du compilateur */
    public static final String COMPILER_VERSION = "1.02";
    /** caractère pour quoter les symboles*/
    public static final char QUOTE_CHAR = '"';
    public static final char MINUS_CHAR = '-';
    public static final char NEAR_CHAR = '~';
    public static final char OR_CHAR = '|';

    private static enum Operation {

        AND, OR, MINUS, ERROR
    };

    private static enum Relation {

        LESS, GREATHER, ERROR
    };
    private static boolean verboseparsing = true;  // pour les tests

    private static boolean verbosenext = false; // pour les tests

    private static Pattern p = Pattern.compile("[\\s\\.+~*,\"\\(\\)]");  // les fins de mots

    private static Pattern pspace = Pattern.compile("\\s");  // pour couper les properties

    private int errCount;
    private StreamTokenizer sym;
    private BufferedReader txt;
    private IdxStructure z;
    private String properties = "";
    private String profile = "";
    public boolean existAlternative = false;
    /* les termes de la requête*/
    public List<String> termsOfQuery = new Vector<String>();
    /* la requête oups*/
    public String oupsQuery = "";
    private static int TT_WORD = StreamTokenizer.TT_WORD;
    private static int TT_NUMBER = StreamTokenizer.TT_NUMBER;
    private static int TT_EOL = StreamTokenizer.TT_EOL;
    private static int TT_EOF = StreamTokenizer.TT_EOF;

    /**
     * Création d'un compilateur pour une requête et son indexeur
     * @param rdr source à compiler
     * @param id indexeur de référence
     */
    public QLCompiler(Reader rdr, IdxStructure id) {
        z = id;
        init(rdr);
    }

    /**
     * Création d'un compilateur pour une requête et son indexeur
     * @param rdr source à compiler
     * @param id indexeur de référence
     */
    public QLCompiler(Reader rdr, String properties, IdxStructure id) {
        z = id;
        this.properties = properties;
        init(rdr);
    }

    /**
     * Création d'un compilateur pour une requête et son indexeur
     * @param rdr source à compiler
     * @param id indexeur de référence
     */
    public QLCompiler(Reader rdr, String properties, String profile, IdxStructure id) {
        z = id;
        this.properties = properties;
        this.profile = profile;
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
        sym.parseNumbers();
        next();
    }

    private final void next() {
        try {
            sym.nextToken();
            if (term("AND")) {
                next();
            } // élimine les  AND car implicite et aussi certaines erreurs

        } catch (IOException e) {
            error("IO error in QL compiler", e);
        }
        if (verbosenext) {
            msg("next->" + sym.sval + "-" + sym.ttype);
        }
    }

    private final void copy() {
        if (sym.ttype == TT_WORD) {
            if (ORTOGRAFIC) {
                String alt = null;
                if (this.isIdentifier()) {
                    alt = z.oups.guessWord(sym.sval);
                }
                if (alt == null) {
                    oupsQuery += " " + sym.sval;
                } else {
                    oupsQuery += " " + alt;
                    existAlternative = true;
                }
            }
            if (this.isIdentifier()) {
                termsOfQuery.add(sym.sval);
            }
            return;
        }
        if (sym.ttype == QUOTE_CHAR) {
            oupsQuery += " \"" + sym.sval + "\"";
            String[] quoteWord = p.split(sym.sval); // décompose la citation en mot

            if (quoteWord != null) {
                for (int i = 0; i < quoteWord.length; i++) {
                    termsOfQuery.add(quoteWord[i]);
                }
            }
            return;
        }
        if (sym.ttype == TT_NUMBER) {
            oupsQuery += " " + ((long) sym.nval);
            termsOfQuery.add("" + ((long) sym.nval));
            return;
        }
        oupsQuery += " " + (char) sym.ttype;
    }

    private final boolean term(String s) {
        return sym.ttype == TT_WORD && sym.sval.equals(s);
    }

    private final boolean term(char c) {
        return sym.ttype == c;
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
            msg("truncated parsing at :" + sym.sval + "-" + sym.ttype);
        } // il reste encore des caractères

        if (verboseparsing) {
            msg("oups found:" + existAlternative);
            msg("oupsQuery=" + oupsQuery);
            for (int i = 0; i < termsOfQuery.size(); i++) {
                msg("t " + i + " :" + termsOfQuery.get(i));
            }
        }
        return res;
    }

    public String[] getTermsOfQuery() {
        if (termsOfQuery == null) {
            return new String[0];
        }
        String[] res = new String[termsOfQuery.size()];
        for (int i = 0; i < termsOfQuery.size(); i++) {
            res[i] = termsOfQuery.get(i);
        }
        return res;
    }

    public String getOupsQuery() {
        if (!existAlternative) {
            return null;
        }
        return oupsQuery;
    }

    //query = subquery < query_operator subquery >.
    private final QRes query() {
        QRes res1 = subquery();
        while (term("AND") || term("OR") || term("MINUS")) {
            Operation op = sub_query_operator();
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

        msg("filtering by properties:" + properties);
        if (!properties.equals("")) {
            String[] prop = pspace.split(properties);
            for (int i = 0; i < prop.length; i++) {
                msg("   properties :" + prop[i]);
                SetOfBits propSOB = z.docstable.satisfyThisProperty(prop[i]);
                res = QRes.filtering(res1, propSOB, true, MODE_RANKING);  // élimine les documents effacés (invalid=true)

            }
        }
        msg("profiling by profile:" + profile);
        if (!profile.equals("")) {
            SetOfBits profileSOB = z.docstable.satisfyThisProperty(profile);
            res = QRes.profiling(res1, profileSOB, MODE_RANKING);  // élimine les documents effacés (invalid=true)

        }
        //msg("ranking before sorting:");showVector(res.rank);
        res.topNDoc(MODE_RANKING, MAX_RESPONSE);
        //msg("ranking after sorting:");showVector(res.rank);
        return res;
    }

    // sub_query_operator= "AND" | "OR" | "MINUS" .
    private final Operation sub_query_operator() {
        if (term("AND")) {
            if (verboseparsing) {
                msg("query_operator: AND");
            }
            copy();
            next();
            return Operation.AND;
        } else if (term("OR")) {
            if (verboseparsing) {
                msg("query_operator: OR");
            }
            copy();
            next();
            return Operation.OR;
        } else if (term("MINUS")) {
            if (verboseparsing) {
                msg("query_operator: MINUS");
            }
            copy();
            next();
            return Operation.MINUS;
        } else {
            msg("waiting for AND, OR, MINUS receive:" + sym.sval + "-" + sym.ttype);
        }
        return Operation.ERROR;
    }
    // query_operator= "AND" | "OR" | "MINUS" .

    private final Operation query_operator() {
        if (term("AND")) {
            if (verboseparsing) {
                msg("query_operator: AND");
            }
            copy();
            next();
            return Operation.AND;
        } else if (term("OR") || term(OR_CHAR)) {
            if (verboseparsing) {
                msg("query_operator: OR");
            }
            copy();
            next();
            return Operation.OR;
        } else if (term("MINUS") || term(MINUS_CHAR)) {
            if (verboseparsing) {
                msg("query_operator: MINUS");
            }
            copy();
            next();
            return Operation.MINUS;
        } else if (term("QUOTATION") || term("NEAR") || term(NEAR_CHAR) || term("NEXT") || term(QUOTE_CHAR) || term('(') // implicite AND
                || sym.ttype == TT_WORD || sym.ttype == TT_NUMBER) {
            if (verboseparsing) {
                msg("query_operator: AND (default)" + sym.sval + "-" + sym.ttype);
            }
            // pas  copy();next(); le symbole est utilisé plus loin
            return Operation.AND;
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
            copy();
            next();
            if (sym.ttype == '[') {
                if (verboseparsing) {
                    msg("filter: [");
                }
                copy();
                next();
                SetOfBits res = exp_property();
                // if (res==null) msg("res is null");
                if (sym.ttype == ']') {
                    if (verboseparsing) {
                        msg("filter: ]");
                    }
                    copy();
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
        while (!term("IN") && (term("AND") || term("OR") || term("MINUS") || term(MINUS_CHAR) || term(OR_CHAR) || term("QUOTATION") || term("NEAR") || term(NEAR_CHAR) || term("NEXT") || term(QUOTE_CHAR) || term('(') // implicite AND
                || sym.ttype == TT_WORD || sym.ttype == TT_NUMBER)) {
            Operation op = query_operator();
            QRes res2 = term();
            //        msg("term2");
            //        showVector(res2);
            switch (op) {
                case AND:
//                    msg("mode rankink:"+MODE_RANKING);
//                    msg("debug beforew AND ...");
//                    showVector(res1.rank);
//                    showVector(res2.rank);
                                      res1 = QRes.and(res1, res2, MODE_RANKING);
//                    msg("debug after AND ...");
//                   showVector(res1.rank);
                 break;
                case OR:
//                    msg("debug beforew OR ...");
//                    showVector(res1.rank);
//                    showVector(res2.rank);
                    res1 = QRes.or(res1, res2, MODE_RANKING);
//                    msg("debug after OR ...");
//                   showVector(res1.rank);
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
    //           | "quotation" "(" qString "," qString ")"
    //           | "near" "(" qString "," qString ")"
    //           | "next" "(" qString "," qString ")"
    //          | "(" query ")".
    private final QRes term() {
        if (sym.ttype == QUOTE_CHAR) { // aussi une quotation

            String term1 = qString();
            if (verboseparsing) {
                msg("QUOTATION : " + term1);
            }
            return getDocforQuotationForW(z, term1, MODE_RANKING);
        } else if (term("QUOTATION")) {
            if (verboseparsing) {
                msg("term: QUOTATION");
            }
            copy();
            next();
            // parameters -----------------------------
            if (sym.ttype == '(') {
                if (verboseparsing) {
                    msg("QUOTATION: (");
                }
                copy();
                next();
            } else {
                msg("waiting for ( receive:" + sym.sval + "-" + sym.ttype);
            }
            String term1 = qString();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("QUOTATION: )");
                }
                copy();
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            if (verboseparsing) {
                msg("QUOTATION : " + term1);
            }
            return getDocforQuotationForW(z, term1, MODE_RANKING);

        } else if (term("NEAR") || term(NEAR_CHAR)) {
            if (verboseparsing) {
                msg("term: NEAR");
            }
            copy();
            next();
            // parameters -----------------------------
            if (sym.ttype == '(') {
                if (verboseparsing) {
                    msg("near: (");
                }
                copy();
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            String term1 = qString();
            if (sym.ttype == ',') { // option skip it

                if (verboseparsing) {
                    msg("near: ,");
                }
                copy();
                next();
            }
            String term2 = qString();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("near: )");
                }
                copy();
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
            copy();
            next();
            // parameters -----------------------------
            if (sym.ttype == '(') {
                if (verboseparsing) {
                    msg("next: (");
                }
                copy();
                next();
            } else {
                msg("waiting for ) receive:" + sym.sval + "-" + sym.ttype);
            }
            String term1 = qString();
            if (sym.ttype == ',') {// option skip it

                if (verboseparsing) {
                    msg("next: ,");
                }
                copy();
                next();
            }
            String term2 = qString();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("next: )");
                }
                copy();
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
        } else if (sym.ttype == QUOTE_CHAR || sym.ttype == TT_WORD || sym.ttype == TT_NUMBER) {
            return factor();
        } else if (sym.ttype == '(') {
            if (verboseparsing) {
                msg("term: (");
            }
            copy();
            next();
            QRes res = query();
            if (sym.ttype == ')') {
                if (verboseparsing) {
                    msg("term: )");
                }
                copy();
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
            copy();
            next();
            return res;
        } else if (sym.ttype == TT_NUMBER) {
            if (verboseparsing) {
                msg("factor number (int): " + ((long) sym.nval));
            }
            String res = " " + ((long) sym.nval);
            copy();
            next();
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
            copy();
            next();
            //showVector(res);
            return res;
        } else if (sym.ttype == TT_NUMBER) {
            if (verboseparsing) {
                msg("factor numeric (int): " + (long) sym.nval);
            }
            QRes res = getDocforW(z, "" + (long) sym.nval, MODE_RANKING);
            copy();
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
            copy();
            next();
            return Operation.AND;
        } else if (term("OR")) {
            if (verboseparsing) {
                msg("filter_operator: OR");
            }
            copy();
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
            copy();
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
            copy();
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
                copy();
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

            copy();
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
            copy();
            next();
            return Relation.GREATHER;
        } else if (sym.ttype == '<') {
            if (verboseparsing) {
                msg("filter_operator: <");
            }
            copy();
            next();
            return Relation.LESS;
        } else {
            msg("waiting for >, < receive:" + sym.sval + "-" + sym.ttype);
        }
        copy();
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
            copy();
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
            copy();
            next();
        } else {
            error("illegal date litteral receive:" + sym.sval + "-" + sym.ttype);
        }
        return -1;
    }
}

