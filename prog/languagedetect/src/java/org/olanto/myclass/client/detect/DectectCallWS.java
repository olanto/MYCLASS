/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
