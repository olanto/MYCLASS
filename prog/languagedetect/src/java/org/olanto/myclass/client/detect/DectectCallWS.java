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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * Jersey REST client generated for REST resource:olanto [detect]<br>
 *  USAGE:
 * <pre>
 *        DectectCallWS client = new DectectCallWS();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author simple
 */
public class DectectCallWS {
    private WebResource webResource;
    private Client client;
    public static  String BASE_URI = "http://localhost:8080/olanto/detect";

    public DectectCallWS() {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI);
    }

    public static void main(String[] args){
       DectectCallWS client=new DectectCallWS();
       String response=client.getXml("texte pour une détection automatique de la langue", "3", "DEMO");
       System.out.println(response);
    }
    
     public String getLanguage(String content) throws UniformInterfaceException {
        return getXml(content,  null,  null);
     }
     public String getLanguage(String content, String nbPrediction) throws UniformInterfaceException {
        return getXml(content,  nbPrediction,  null);
     }

     public String getLanguage(String content, String nbPrediction, String key) throws UniformInterfaceException {
        return getXml(content,  nbPrediction,  key);
     }
    
    public String getXml(String content, String nbPrediction, String key) throws UniformInterfaceException {
        WebResource resource = webResource;
        if (content != null) {
            resource = resource.queryParam("content", content);
        }
        if (nbPrediction != null) {
            resource = resource.queryParam("numberofpredictions", nbPrediction);
        }
        if (key != null) {
            resource = resource.queryParam("key", key);
        }
        
        
        System.out.println("resource:"+resource.getURI());
        return resource.accept(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);
    }

    public String postXml(Object requestEntity) throws UniformInterfaceException {
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_XML).post(String.class, requestEntity);
    }

    public String putXml(Object requestEntity) throws UniformInterfaceException {
        return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_XML).put(String.class, requestEntity);
    }

    public void close() {
        client.destroy();
    }
    
}
