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


package org.olanto.myclass.client.detect;

/**
 *
 * @author olanto.org J. Guyot
 */
public class VerySimpleClient {

    public static void main(String[] args) {

        // prepare call to WebService
      //  DectectCallWS.BASE_URI = "http://localhost:8080/olanto/detect";
       DectectCallWS.BASE_URI = "http://192.168.1.32:8080/languagedetect/detect";
        DectectCallWS client = new DectectCallWS();
        System.out.println("WS REST Url :" + DectectCallWS.BASE_URI);

 
        // call & print response
        String response = client.getLanguage("texte pour une détection automatique de la langue");
        System.out.println("\n\npost call Response:\n\n" + response);
        response = client.getLanguage("texte pour une détection < automatique de la langue");
        System.out.println("\n\npost call Response:\n\n" + response);
   
    }
}
