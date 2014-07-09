package isi.jg.idxvli.ql.test;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import java.io.*;
import isi.jg.idxvli.ql.*;
import static isi.jg.util.Messages.*;
import isi.jg.util.TimerNano;

/**
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Test de l'indexeur, mode query
 */
public class TESTQL {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationQL());
        id.Statistic.global();


        test("prenant");
        test("italie");
        test("italie AND prenant");
        test("italie OR prenant");
        test("italie MINUS prenant");
        test("italie AND prenant AND gouvernement");
        test("prenant MINUS (italie AND prenant AND gouvernement)");

        test("undertaking");
        test("italy");
        test("italy AND undertaking");
        test("italy OR undertaking");

        test("(italie AND prenant) AND (italy AND undertaking)");
        test("(italie AND prenant) OR (italy AND undertaking)");

        test("prenant AND acte");
        test("NEXT(prenant,acte)");
        test("NEAR(prenant,gouvernement)");

        test("tarif");
        test("tarif IN[\"_EN\"]");
        test("(tarif IN[\"_EN\"])IN[\"_FR\"]");
        test("tarif IN[\"_FR\"]");
        test("tarif IN[ NOT \"_FR\"]");
        test("tarif IN[\"_EN\" OR \"_FR\"]");
        test("tarif IN[LENGTH > \"1000\"]");
        test("tarif IN[LENGTH < \"1000\"]");
        test("tarif IN[DATE < \"09-02-2004\"]");

        test("((italie AND prenant)IN[\"_FR\"] OR (italy AND undertaking)IN[\"_EN\"]) IN [LENGTH < \"1000\"]");

        // limité filtering aux documents indexés.!!!!!!!!!!!!!!!!!!!!!!


        System.out.println("id également:" + id.getIntForW("également"));
        ////         id.indexread.lockForBasic(427);
        ////         showVector(id.indexread.getCopyOfDoc(427));
        ////         //showVector(id.indexread.getCopyOfPos(165));
        ////         id.indexread.unlock(427);
        //        test ("\"pomme\" AND \"terre\"");
        //        test ("(\"pomme\" AND \"terre\")");
        //        test ("NEXT(\"pomme\" , \"terre\")");
        //        test ("pomme AND terre");
        //        test ("pomme AND terre AND parmentier");
        //        test ("(pomme AND terre) OR (haricot AND vert)");
        //        test ("(pomme OR terre) AND (haricot OR vert)");
        //        test ("pomme AND terre IN [ \"FRENCH\" ]");
        //        test ("pomme AND terre IN [ FRENCH ]");
        //         test ("pomme AND terre IN [ FRENCH  AND DATE < \"01.01.2003\" ]");


        t1.stop();
    }

    public static void test(String s) {
        TimerNano t1 = new TimerNano("parse:" + s, false);

        QLCompiler ql = new QLCompiler(new StringReader(s), id);
        int[] res = ql.execute();

        if (res == null) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + res.length);
        }
        showVector(res);

        t1.stop(false);
    }

    private static void QCMD(String command, String w1, String w2) {
        TimerNano t1 = new TimerNano(command + " " + w1 + " " + w2, false);
        //System.out.println((id.Query.parseQuery(command, w1, w2)).length());
        System.out.println((id.Query.parseQuery(command, w1, w2)));
        t1.stop(false);
    }
}
