package isi.jg.cat;


/**
 * Une classe pour effectuer la classification des documents
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée au groupe ISI
 * toute autre utilisation est sujette à autorisation
 *
*/

import java.util.*;
import isi.jg.idxvli.*;

public class RandomizeDoc extends Object {
    
    static int[] rand;
    static int lasttraindoc=0,lasttestdoc=0;
    public static boolean RANDOMIZE=true;

    RandomizeDoc(){}
    
    /** initialisation du mélangeur de documents
     * @param Indexer lien avec l'indexeur
     * @param BootGroup documents de référence
     * @param prefix prefix du groupe d'apprentissage
     */    
    static void init(IdxStructure Indexer, NNBottomGroup  BootGroup, String prefix) {  // default parameters
        rand=new int[Indexer.lastRecordedDoc];
        lasttraindoc=0;
        for (int i=1;i<Indexer.lastRecordedDoc;i++){
            //System.out.println(Indexer.getFileNameForDocument(i));
            if (BootGroup.inThisGroup(i, prefix, NNBottomGroup.TRAINDOC)){
                rand[lasttraindoc]=i;
                lasttraindoc++;
            }
        }
        lasttestdoc=lasttraindoc;
        for (int i=1;i<Indexer.lastRecordedDoc;i++){
            if (BootGroup.inThisGroup(i, prefix, NNBottomGroup.TESTDOC)){
                rand[lasttestdoc]=i;
                lasttestdoc++;
            }
        }
       System.out.println("lasttraindoc:"+lasttraindoc);
       System.out.println("lasttestdoc:"+lasttestdoc);
       if (RANDOMIZE){  // normalement ok par initialisation
       int shuffle=lasttraindoc ; // randomize traindoc
        Random gen=new Random(13);  // THE TEST
        for (int i=1;i<2*shuffle;i++){
            int a=gen.nextInt(shuffle);
            int b=gen.nextInt(shuffle);
            int tempo=rand[b];
            rand[b]=rand[a];
            rand[a]=tempo;
        }
       }
    }

    

}
