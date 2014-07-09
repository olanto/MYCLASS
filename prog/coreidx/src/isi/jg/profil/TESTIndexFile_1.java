package isi.jg.profil;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import java.io.*;
import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;


/**
 * *
 * <p>
 * <b>JG-2008
* <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
  *
 *
 * Test de l'indexeur, création d'un nouvel index
 */
public class TESTIndexFile_1{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        create();
        
    }
    
    private  static void create()   {
        id=new IdxStructure(
                "NEW", // mode
                "dont forget to clean directories before"
                 );
        // création de la racine de l'indexation
        id.createComponent(new Configuration());
        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();
        // indexation du dossier spécifié
        //id.Indexer.indexdir("C:/jdk1.5_old/docs/guide");
        
        //id.indexdir("C:/D");
        //id.indexdir("C:/AAA/WIPO/EN");
        id.indexdir("E:/WIPO/EN");
        //id.indexdir("C:/OMC_corpus/BISD/S02");
        
        
        id.flushIndexDoc();
        
        id.Statistic.global();
        
        id.close();
        
        t1.stop();
        
        System.out.println("======================================================================");
    }
    
    
    
}