/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
