/**********
    Copyright © 2003-2014 Olanto Foundation Geneva

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

package org.olanto.cat.inbox;

import org.olanto.idxvli.IdxInit;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;
import java.io.*;
import static org.olanto.idxvli.IdxConstant.*;
import static org.olanto.util.Messages.*;
import javax.swing.*;


/**
 *
 * création d'un nouvel index
 */
public class CreateIndexFile{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    
    private static void createCatalog(String trainingRoot){
        try{
            msg("create catalog:");
            OutputStreamWriter  osr=new OutputStreamWriter(new FileOutputStream(COMMON_ROOT+"/train-maingroup.cat"));
            BufferedWriter   out=new BufferedWriter(osr);
            for(int i=0;i<id.lastRecordedDoc;i++){
                String docName=id.getFileNameForDocument(i);
                String fileName=docName.substring(trainingRoot.length()+1);
                msg(fileName);
                int idx=fileName.indexOf("/");
                if (idx!=-1){
                    String className=fileName.substring(0,idx);
                    msg(className);
                    out.write(docName+"\t"+className+"\n");
                } else error("No class (/) for :"+fileName);
            }
            out.flush();
            out.close();
            
            // le fichier vide des tests
            osr=new OutputStreamWriter(new FileOutputStream(COMMON_ROOT+"/empty.cat"));
            out=new BufferedWriter(osr);
            out.write("\n");
            out.flush();
            
            out.close();
        } catch (Exception e){
            error("in createCatalog"+e);
        }
    }
    
    public  static void create(String trainingRoot, JTextArea status)   {
        status.setText("");
        status.append("\ndelete and init a new Index");

        
        // force la configuration
        IdxInit forceInitialValue=new ConfigurationCat();
        forceInitialValue.InitPermanent();
        forceInitialValue.InitConfiguration();
        
        
        
        String rootfile=COMMON_ROOT+"/rootidx";
        msg("delete root file:"+rootfile);
        File froot=new File(rootfile);
        froot.delete();
        
        id=new IdxStructure("NEW", new ConfigurationCat());
        
        
        // indexation du dossier spécifié
        status.append("\nindexation of "+trainingRoot);
       
     DO_DOCBAG = true;
    DO_DOCRECORD = true;
    NO_IDX_ONLY_COUNT=false;
    MODIFY_IDX=true;
    IDX_WITHDOCBAG=true;
    WORD_LIST=true;
       
        id.indexdir(trainingRoot);
        
        
        id.flushIndexDoc();
        
        id.Statistic.global();
        
        status.append("\ncreate a new catalog for "+trainingRoot);
       
        createCatalog(trainingRoot);
        
        id.close();
        
        t1.stop();
        
        status.append("\nend of indexation "+trainingRoot);
       
        System.out.println("======================================================================");
    }
    
    
    
}