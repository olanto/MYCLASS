/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myCLASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.demo.diag1;

import org.olanto.idxvli.IdxStructure;
import org.olanto.demo.alpha.SomeConstant;
import org.olanto.idxvli.ql.QueryOperator;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;
import java.io.*;

/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2006
 * @version 1.1
 *
 * permet de g�n�rer une collection avec 2 dimensions
 */
public class Lexical_Distribution {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args) {

        id = new IdxStructure("QUERY", new ConfigurationForRewrite());
        id.Statistic.global();

        t1 = new Timer("read and rewrite");

        distribution(SomeConstant.ROOTDIR + "SIMPLE_CLASS/sample/rewrite/lexical_distibution.log");

        t1.stop();
    }

    private static final void distribution(String f) {
        // no mfl no stemming no moreinfo
        //String IDX_MFL_ENCODING="ISO-8859-1";
        String IDX_MFL_ENCODING = "UTF-8";

        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f), IDX_MFL_ENCODING);

            for (int minocc = 2; minocc <= 24; minocc++) {
                int count = 0;
                for (int i = 0; i < id.lastRecordedWord; i++) {

                    int nbocc = id.getOccOfW(i);
                    if (nbocc >= minocc) { // terme atteind le seuil
                        count++;
                    }
                }
                msg("minocc:" + minocc + ", tot occ:" + count);
                out.write("minocc:" + minocc + ", tot occ:" + count + "n");
            }

            for (int maxocc = 100; maxocc >= 10; maxocc -= 5) {
                int count = 0;
                for (int i = 0; i < id.lastRecordedWord; i++) {

                    int nbocc = id.getOccOfW(i);
                    if (nbocc * 1000 / id.lastRecordedDoc >= (maxocc-5)
                            &&nbocc * 1000 / id.lastRecordedDoc < maxocc) { // terme atteind le seuil
                        count++;
                        msg("  -"+id.getStringforW(i));
                        out.write("  -"+id.getStringforW(i) + "n");
                    }
                }
                msg("maxocc (-5):" + maxocc + ", tot occ:" + count);
                out.write("maxocc (-5:" + maxocc + ", tot occ:" + count + "n");
            }

            out.close();
        } catch (IOException e) {
            error("IO error", e);
        }
    }
}


