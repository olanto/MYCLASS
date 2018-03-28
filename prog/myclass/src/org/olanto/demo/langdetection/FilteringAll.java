/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myLCASS is free software: you can redistribute it and/or modify
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

package org.olanto.demo.langdetection;


import java.io.*;
import org.olanto.cat.util.SenseOS;
import static org.olanto.util.Messages.*;

/**
 *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class FilteringAll {

    public static void main(String[] args) {


            msg("init clssifier ...");
            Classify.init();
            classifyAmflf(SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/langdetectngram/test.txt",
                          SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/langdetectngram/testresults.txt"
                         ,"UTF-8"
                         );
            msg("end ...");
    }

    private static void classifyAmflf(String f,String fo, String IDX_MFL_ENCODING) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f), IDX_MFL_ENCODING);
            BufferedReader fin = new BufferedReader(isr, 1000000);
            OutputStreamWriter result=new OutputStreamWriter(new FileOutputStream(fo), IDX_MFL_ENCODING);
            msg(f + ":open in: " + IDX_MFL_ENCODING);
            String w = fin.readLine();
    
            while (w != null) {  
                    result.write("\n-----------------------------\n"+w+"\n"+Classify.advise(w)+"\n");
                    w = fin.readLine();
            }  // while
            fin.close();
            result.close();
        } catch (IOException e) {
            error("IO error", e);
        }
    }
}
