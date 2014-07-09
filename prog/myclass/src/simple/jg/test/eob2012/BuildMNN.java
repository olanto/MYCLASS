package simple.jg.test.eob2012;

import org.olanto.idxvli.IdxStructure;
import org.olanto.cat.NNBuildTree;
import org.olanto.cat.NNOneN;
import org.olanto.cat.util.NNBottomGroup;
import isi.jg.deploy.frende.*;
import static org.olanto.cat.GetProp.*;
import org.olanto.util.Timer;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2009
 * @version 1.1
 *
 * Test de la construction de réseaux de neurone
 */
public class BuildMNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        String signature = getSignature("c:/SIMPLE_CLASS/config/Identification.properties");

        id=new IdxStructure("QUERY",new ConfigurationForCat());
        id.Statistic.global();
        
        
        // chemin pour les catalogues
        String fntrain = "e:/PATDB/catalog/ENFR.cat.val";
        String fntest = "e:/PATDB/catalog/EMPTY.cat";
        
       NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN.CAT_MAINGROUP,false,false);
        
        t1=new Timer("global time --------------------------------");
        
        
        String nnfile="e:/PATDB/mnn/enfr.mnn";
        
        NNBuildTree.init(
        signature,
        BootGroup,                 //catégories et documents d'apprentissage
        id,                        //indexeur
        true,                      // sauver l'arbre
        NNBuildTree.MODE_1348,      // stratégie de construction
        1000,                      // nbr de réseaux
        31000,                     // somme de toutes les catégories de tous les réseaux
        1.04f,                     // alpha
        300.0f,300.0f,             // niveaux d'histeresis
        8,10000000,                 // min et max occurence
        3,                         // nbre de choix retenu
        5,                         // nbre de cycle d'apprentissage
        nnfile                     // fichier pour mémoriser l'arbre.
        );
         
        t1.stop();
    }
    
    
    
}