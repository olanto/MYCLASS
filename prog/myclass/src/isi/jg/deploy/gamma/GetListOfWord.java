package isi.jg.deploy.gamma;

import org.olanto.idxvli.IdxStructure;
import simple.jg.deploy.wipo2011.ConfigurationForCat;
import java.io.*;

/**Test de l'indexeur, mode query */
public class GetListOfWord {

    private static IdxStructure id;

    public static void main(String[] args) {
        id = new IdxStructure("QUERY", new ConfigurationForCat());
        id.Statistic.global();
        //id.Statistic.topIndexByLength(0);
        exportEntry("c:/ENFR/config/exportTerm.txt");
    }

    static void exportEntry(String filename) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            //        FileWriter out= new FileWriter(filename,"UTF-8");
            int n, occofW;
            n=0;
            for (int i = 0; i < id.lastRecordedWord; i++) {
                String entry = id.getStringforW(i);
                occofW = id.getOccOfW(i);
                //if (entry.length()<3) out.write(entry+"|"+occofW+"|"+occCourpusofW+"\n");
                if (occofW > 8 && occofW < 20000) {
                    //out.write(entry + "|" + occofW + "\n");
                  out.write(entry +"\n");
                    n++;
                }
            }
            out.flush();
            out.close();
            System.out.println("nb feature:"+n);
        } catch (Exception e) {
            System.err.println("IO error ExportEntry");
            e.printStackTrace();

        }
    }
}