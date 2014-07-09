package isi.jg.cat.clefip2010;

import org.olanto.idxvli.IdxStructure;
import isi.jg.deploy.diag1.*;
import isi.jg.deploy.demo.alpha.*;
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
        
        id.indexdir("C:/TEMPOMFLF");// indexation du dossier
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global(); 
        id.close();       
        t1.stop();
        
    }
    
    
}