package isi.jg.deploy.gamma;

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
public class CreateIndexForCat{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
   public static void main(String[] args)   {
       
        id=new IdxStructure("NEW", new ConfigurationForCat()); 
        
        id.indexdirOnlyCount("Z:/IPCCAT/finalbega");// indexation du dossier 
        
        id.flushIndexDoc();  //  vide les buffers       
        
        Timer t2=new Timer("doc bag build");
        id.indexdirBuildDocBag("Z:/IPCCAT/finalbega");// indexation du dossier 
        t2.stop();
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global(); 
        id.close();       
        t1.stop();
        
    }
    
    
}