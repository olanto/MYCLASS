package isi.jg.idxvli.doc.test;

import isi.jg.idxvli.doc.*;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

/** tests.
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
 */
public class TestProperties {

    static PropertiesManager o;

    public static void main(String[] args) {
        implementationMode imp = implementationMode.BIG;
        String s;
        o = (new Properties1()).create(imp, "C:/JG/gigaversion/data/objsto", "test", 10, 32, 20);
        o = (new Properties1()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        msg("set test");
        o.put("_EN", 14);
        o.put("_FR", 15000);

        o.close();
        o = (new Properties1()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.rw);
        msg("get test");
        msg("_EN,14" + o.get("_EN", 14));
        msg("_EN,15000" + o.get("_EN", 15000));
        msg("_FR,15000" + o.get("_FR", 15000));
        msg("_FR,14" + o.get("_FR", 14));
        o.printStatistic();
        o.close();

        msg("------------------------READ_ONLY-----------------------------------------------");
        o = (new Properties1()).open(imp, "C:/JG/gigaversion/data/objsto", "test", readWriteMode.r);
        o.close();


    }
}
