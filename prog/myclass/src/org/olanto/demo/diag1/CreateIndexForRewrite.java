package org.olanto.demo.diag1;

import org.olanto.demo.alpha.SomeConstant;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2004
 * @version 1.1
 *
 * Test de l'indexeur, cr�ation d'un nouvel index
 */
public class CreateIndexForRewrite{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
   public static void main(String[] args)   {
       
        id=new IdxStructure("NEW", new ConfigurationForRewrite());
        
        id.indexdir(SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/alpha");// indexation du dossier 
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global(); 
        id.close();       
        t1.stop();
        
    }
    
    
}