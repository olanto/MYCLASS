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

package org.olanto.myclass.detect;

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.List;
import java.util.Iterator;

/**
 *
 * @author jg
 */
public class XML2Parameter {

    private org.jdom.Document document;
    private Element racine;
    private String key = "DEMO"; // for future usage
    private String nbPrediction = "3";
    ;
    private String content = "automatic text classification for language";

    public XML2Parameter() {
    }

    public XML2Parameter(String xml) {
        init(xml);
    }

    public static void main(String[] args) {
        String tested = "<detect>\n"
               + "  <content>Ceci est un texte en langue?</content>\n"
                + "  <numberofpredictions>3</numberofpredictions>\n"
                + "  <key>DEMO</key>\n"
                + "</detect>\n";

        XML2Parameter param = new XML2Parameter(tested);
        System.out.println(param.toString());
    }

    public String toString() {
        return "<detect>\n"
                + "  <content>" + getContent() + "</content>\n"
                + "  <numberofpredictions>" + getNbPrediction() + "</numberofpredictions>\n"
                + "  <key>" + getKey() + "</key>\n"
                + "</detect>";
    }

    private void init(String xml) {

        SAXBuilder sxb = new SAXBuilder();
        try {
            document = sxb.build(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
        racine = document.getRootElement();
        convertFromXML();
    }

    private String getDoc(Element e) {

        boolean localverbose = true;
        if (localverbose) {
            System.out.println(e.getName());      //On affiche le nom de l'element courant
        }
        if (localverbose) {
            System.out.println(e.getText());     //On affiche le texte
        }
        return "";
    }

    private String getAtt(Element e, String att, boolean localverbose) {
        String val = e.getAttributeValue(att);
        if (localverbose) {
            System.out.println(att + ":" + val);
        }
        return val;
    }

    private String getText(Element e, boolean localverbose) {
        String val = e.getTextNormalize();
        if (localverbose) {
            System.out.println("--> " + val);
        }
        return val;
    }

    private void convertFromXML() {
        System.out.println("racine");
        //On crée une liste contenant tous les noeuds "detect" de l'Element racine
        List listNode = racine.getChildren();

        //On crée un Iterator sur notre liste
        Iterator i = listNode.iterator();
        int count = 0;
        while (i.hasNext()) {

            Element courant = (Element) i.next();
            getDoc(courant);
            count++;

            if (courant.getName().equals("key")) {
                key = courant.getText();
            }
             if (courant.getName().equals("numberofpredictions")) {
                nbPrediction = courant.getText();
            }
            if (courant.getName().equals("content")) {
                content = courant.getText();
            }

            System.out.println("end racine count:" + count);

        }
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

     /**
     * @return the nbPrediction
     */
    public String getNbPrediction() {
        return nbPrediction;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
}
