package isi.jg.cat.inbox;

import isi.jg.cat.NNBottomGroup;
import isi.jg.idxvli.*;
import isi.jg.util.Timer;
import isi.jg.cat.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.cat.GetProp.*;



/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2004
 * @version 1.1
 *
 * Test du catégoriseur
 */
public class TESTManyNN{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        
        id=new IdxStructure("QUERY",new ConfigurationCat());
        
        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();
        
        id.Statistic.global();
        
        
        String fntrain=COMMON_ROOT+"/train-maingroup.cat";
        
        System.out.println(fntrain);

        String fntest =COMMON_ROOT+"/empty.cat";
        
        NNBottomGroup BootGroup=new NNBottomGroup(id,fntrain,fntest,NNOneN.CAT_MAINGROUP,true,false);
        
        t1=new Timer("global time MAINGROUP --------------------------");
        
        NNOneN.setINMEMORY(true);
        
        float alpha=1.03f;
        float deltamin=300;
        float deltamax=300;
        int minocc=3;
        int maxnbocc=500000;
        int nfirst=3;
        int repeatK=5;
    
        String signature = getSignature("C:/SIMPLE_CLASS/config/Identification.properties");

        
        NNOneN.init(signature,BootGroup,id,NNOneN.NORMALISED,NNOneN.SDF_SQUARE); // initialise le réseau de neurone ...
        
        
        NNOneN.TrainWinnow(NNOneN.CAT_MAINGROUP,"",repeatK,1000,alpha,minocc,maxnbocc,deltamin,deltamax,
                false, // detail des catégories (numéro des catégories et répartition)
                true, // si false=> utilise le catalogue de test, true => test avec le training set
                80     // si testtrain = true indique la part en % utilisée pour le training
                );
        NNOneN.testWinnow4(     false,   // detail class precision
                false,   // detail document prediction
                nfirst);
        NNOneN.testWinnow4Multi(false,   // detail class precision
                nfirst);
               
        t1.stop();
    }
    
    
    
}