package simple.jg.deploy.wipo2013;

import isi.jg.deploy.gamma.*;
import simple.jg.deploy.wipo2011.ConfigurationForCat;
import java.io.*;
import isi.jg.idxvli.*;
import isi.jg.onto.LexicManager;
import isi.jg.onto.Terms;

/**Test de l'indexeur, mode query */
public class GetListOfDoc {

    private static IdxStructure id;

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();
        //id.Statistic.topIndexByLength(0);
        exportEntry("e:/PATDB/debug/exportDoc.txt");
    }

    static void exportEntry(String filename) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            //        FileWriter out= new FileWriter(filename,"UTF-8");
            int n, occofW;
            n=0;
            for (int i = 0; i < id.lastRecordedDoc; i++) {
                String entry = id.getFileNameForDocument(i);
                  out.write(entry +"\n");
                    n++;
            }
            out.flush();
            out.close();
            System.out.println("nb docs:"+n);
        } catch (Exception e) {
            System.err.println("IO error ExportEntry");
            e.printStackTrace();

        }
    }
}