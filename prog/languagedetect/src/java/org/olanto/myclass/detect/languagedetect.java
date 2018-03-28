/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.olanto.myclass.detect;


import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.olanto.myclass.core.detect.Detectprocess;

/**
 * REST Web Service
 *
 * @author simple
 */
@Path("detect")
public class languagedetect {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of languagedetect
     */
    public languagedetect() {
    }

    /**
     * Example of parameters passed as URL parameters:
     * http://localhost:8080/olanto/detect?q="Ceci est un phrase en ?"
     */
  
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml(
            @DefaultValue("") @QueryParam("content") String content,
            @DefaultValue("3") @QueryParam("numberofpredictions") int nbPrediction,
            @DefaultValue("DEMO") @QueryParam("key") String key) {

        return Detectprocess.classifyThis(content,nbPrediction,  key);
    }

    /**
     * POST method
     *
     * @param content xml parameter
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String postXml(String content) {

        /*
         <ipccat>
         <startfromsymbol>G03</startfromsymbol>
         <hierarchiclevel>MAINGROUP</hierarchiclevel>
         <lang>fr</lang>
         <numberofpredictions>3</numberofpredictions>
         <text>automatic text classification for patent</text>
         </ipccat>     
         */

        XML2Parameter param = new XML2Parameter(content);
        return Detectprocess.classifyThis(param.getContent(),
                Integer.parseInt(param.getNbPrediction()),
                param.getKey()
                );
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String putXml(String content) {
        /*
         <ipccat>
         <startfromsymbol>G03</startfromsymbol>
         <hierarchiclevel>MAINGROUP</hierarchiclevel>
         <lang>fr</lang>
         <numberofpredictions>3</numberofpredictions>
         <text>automatic text classification for patent</text>
         </ipccat>  
         */

        XML2Parameter param = new XML2Parameter(content);
        return Detectprocess.classifyThis(param.getContent(),
                Integer.parseInt(param.getNbPrediction()),
                param.getKey()
                );
    }
}
