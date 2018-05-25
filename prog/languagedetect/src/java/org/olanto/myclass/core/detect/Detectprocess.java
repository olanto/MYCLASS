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

package org.olanto.myclass.core.detect;



public class Detectprocess {

    public static String classifyThis(
           String content,
            int nbPrediction,
            String key
          ) {

        String callArgs = "<call> \n"
                  + "<content>" + content + "</content>" + "\n"
                  + "<numberofpredictions>" + nbPrediction + "</numberofpredictions>" + "\n"
                   + "<key>" + key + "</key>" + "\n"
                + "</call> \n";

        String response = "<predictions>\n"; //+callArgs;
        int nblevel = 2;  // length of language symbols
          if (nbPrediction < 1 || nbPrediction > 5) {
           // return response += "<msg>Invalid parameter</msg>\n</predictions>\n";
             return response += "<msg>numberofpredictions should be between 1 and 5</msg>\n</predictions>\n";
        }
        if (content == null||content.length() == 0) {
           //  return response += "<msg>Invalid parameter</msg>\n</predictions>\n";
            return response += "<msg>content is empty</msg>\n</predictions>\n";
        }
        response += Classify.advise(nbPrediction,content );
        response += "</predictions>\n";
        return response;
    }

  
}
