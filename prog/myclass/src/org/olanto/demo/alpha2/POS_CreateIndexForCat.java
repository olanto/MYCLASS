package org.olanto.demo.alpha2;

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
public class POS_CreateIndexForCat{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
   public static void main(String[] args)   {
       
        id=new IdxStructure("NEW", new POS_ConfigurationForCat());
        
        id.indexdirOnlyCount(SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/alpha_pos");// indexation du dossier
        
        id.flushIndexDoc();  //  vide les buffers       
        
        Timer t2=new Timer("doc bag build");
        id.indexdirBuildDocBag(SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/alpha_pos");// indexation du dossier
        t2.stop();
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global(); 
        id.close();       
        t1.stop();
        
    }
    
    
}