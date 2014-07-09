package isi.jg.cat.inbox;

import isi.jg.cat.NNBottomGroup;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.cat.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.util.Messages.*;
import  javax.swing.*;
import static isi.jg.cat.GetProp.*;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2005
 * @version 1.1
 *
 * construction de réseaux de neurone
 */
public class BuildMNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    public static void trainNN (JTextArea status)   {

 //    String signature = getSignature("C:/SIMPLE_CLASS/config/Identification.properties");
     String signature = getSignature("C:/AAA/CodeRessources/Identification.properties");


        status.append("\nOpen Index");


        id=new IdxStructure("QUERY",new ConfigurationCat());
        
        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();
        
        id.Statistic.global();
        
        
        String fntrain=COMMON_ROOT+"/train-maingroup.cat";

        String fntest =COMMON_ROOT+"/empty.cat";
         
        status.append("\nTraining ...");
        NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN.CAT_MAINGROUP,false,false);
        
        
        // génération de l'arbre de réseaux de neurones
        
        t1=new Timer("global time --------------------------------");
        
        
        String nnfile=COMMON_ROOT+"CLASSIFIER.mnn";
        
        NNBuildTree.init(
        signature,                 // signature
        BootGroup,                 //catégories et documents d'apprentissage
        id,                        //indexeur
        true,                      // sauver l'arbre
        NNBuildTree.MODE_8,     // stratégie de construction 
        100,                      // nbr de réseaux
        100,                     // somme de toutes les catégories de tous les réseaux
        1.06f,                     // alpha
        300.0f,300.0f,             // niveaux d'histeresis
        1,1000000,                 // min et max occurence
        3,                         // nbre de choix retenu
        1,                         // nbre de cycle d'apprentissage
        nnfile                     // fichier pour mémoriser l'arbre.
        );
         
        t1.stop();
        status.append("\nEnd of Training ...");
    }
    
    
    
}