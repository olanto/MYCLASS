package simple.jg.test.eob2012;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2004
 * @version 1.1
 *
 * Test de l'indexeur, cr�ation d'un nouvel index
 */
public class CreateIndexForCatRW{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
   public static void main(String[] args)   {
       

             String drive = SomeConstant.ROOTDIR;
            String set = SomeConstant.GROUP;
           String targetMFLF = drive + "/EOB/EXP/"+set+"/MFLFRW"  ;
            
        id=new IdxStructure("NEW", new ConfigurationForCat()); 
        
        id.indexdirOnlyCount(targetMFLF);// indexation du dossier 
        
        id.flushIndexDoc();  //  vide les buffers       
        
        Timer t2=new Timer("doc bag build");
        id.indexdirBuildDocBag(targetMFLF);// indexation du dossier 
        t2.stop();
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global(); 
        id.close();       
        t1.stop();
        
    }
    
    
}