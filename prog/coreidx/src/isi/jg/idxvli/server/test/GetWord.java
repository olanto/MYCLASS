package isi.jg.idxvli.server.test;

import java.io.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.ql.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxConstant.*;

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
public class GetWord {

    private static IdxStructure id;

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationNative());
        id.Statistic.global();
        //id.Statistic.topIndexByLength(0);
        exportEntry("c:/AAA/BLAISE/exportTerm.cat");
    }

    static void exportEntry(String filename) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1");
            //        FileWriter out= new FileWriter(filename,"UTF-8");
            for (int i = 0; i < id.lastUpdatedWord; i++) {
                String entry = id.getStringforW(i);
                int occofW = id.getOccCorpusOfW(i);
                if (occofW > 0) {
                    out.write(entry + "\t" + occofW + "\t" + "\n");
                }
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("IO error ExportEntry");
        }
    }
}
