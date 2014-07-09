package isi.jg.idxvli.ql.test;

import java.io.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.ql.*;
import static isi.jg.util.Messages.*;

/**Test de l'indexeur, mode query
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
public class TESTQL_1 {

    private static IdxStructure id;

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationQL());
        id.Statistic.global();
        test("coconut AND rice");
    }

    public static void test(String s) {
        int[] res = id.executeRequest(s);
        if (res == null) {
            msg("result is null");
        } else {
            msg("result is ndoc:" + res.length);
        }
        showVector(res);
    }
}
