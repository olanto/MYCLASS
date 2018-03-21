package org.olanto.myclass.core.detect;


/**
 *
 * @author jacques guyot 2012
 */
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
