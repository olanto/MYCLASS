package isi.jg.cat.clefip2010;

import isi.jg.cat.NNBottomGroup;
import isi.jg.deploy.frende.*;
import static isi.jg.cat.GetProp.*;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.cat.*;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * Test de la construction de réseaux de neurone
 */
public class BuildMNNOver{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        String signature = getSignature(SomeConstant.ROOTDIR+"/SIMPLE_CLASS/config/Identification.properties");

        id=new IdxStructure("QUERY",new ConfigurationForCat());
        id.Statistic.global();
        
        
        // chemin pour les catalogues
         String fntrain = "Z:/CLEFIP10/CAT/clefipext.cat";
        String fntest = "Z:/CLEFIP10/CAT/EMPTY.cat";
        
       NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN_OverSampling.CAT_SUBCLASS,false,false);
        
        t1=new Timer("global time --------------------------------");
        
        
        String nnfile="Z:/CLEFIP10/mnn/clefipext-OVER-6800.mnn";
        
        NNBuildTreeOver.init(
        signature,
        BootGroup,                 //catégories et documents d'apprentissage
        id,                        //indexeur
        true,                      // sauver l'arbre
        NNBuildTree.MODE_4,      // stratégie de construction
        1,                      // nbr de réseaux
        650,                     // somme de toutes les catégories de tous les réseaux
        1.05f,                     // alpha
        300.0f,300.0f,             // niveaux d'histeresis
        3,400000,                 // min et max occurence
        3,                         // nbre de choix retenu
        5,                         // nbre de cycle d'apprentissage
        nnfile,                     // fichier pour mémoriser l'arbre.
        true,
        6800);
         
        t1.stop();
    }
    
    
    
}