package isi.jg.cat;



import isi.jg.idxvli.IdxStructure;
import java.util.*;



/**
 * Une classe pour d�terminer artificiellement les cat�gories et les jeux d'apprentissage et de test.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limit�e au groupe ISI 
 * toute autre utilisation est sujette � autorisation
 *
*/
public class NNArtificialGroup extends NNBottomGroup{
    
    
    
    /** construit les cat�gories du classifieur et d�termine un catalogue pour les documents d'apprentissage et de  test
     * @param _sprouts catalogue pour les documents
     * @param _Indexer Indexeur contenant les libell�s des documents
     * @param _bottomgrouplength niveau maximum de construction des cat�gories
     * <br>NNOne.CAT_SECTION
     * <br>NNOne.CAT_CLASS
     * <br>NNOne.CAT_SUBCLASS
     * <br>NNOne.CAT_MAINGROUP
     * @param _verbose affiche les d�tails de la construction des cat�gories
     * @param _showerror affiche les erreurs du catalogue (fichiers et cat�gories erronn�e) */
    
    public NNArtificialGroup(IdxStructure _Indexer,String[] _sprouts, int _bottomgrouplength,
    boolean _verbose, boolean _showerror){
        Indexer=_Indexer;
        verbose=_verbose;
        showerror=_showerror;
        bottomgrouplength=_bottomgrouplength;
        doctype=new byte[Indexer.lastRecordedDoc];
        docgroup=new int[Indexer.lastRecordedDoc][];
        group=new Hashtable();
        groupinv=new Hashtable();
        maxbottomgroup=0;
        // find all the bottom group in the training doc
        // this groups will be the reference for all tests
            getNextFirst(_sprouts);
        // enregistre tous les documents comme training
            getNext(_sprouts);
   }
    
  
    void getNextFirst(String[] _sprouts){   
       for (int i=0;i<_sprouts.length;i++){ 
                addFirst(_sprouts[i]+" ");  // complete whit a blanck
            }
    }
    void getNext(String[] _sprouts){  
       for (int i=0;i<_sprouts.length;i++){
                add(_sprouts[i]+" ",TRAINDOC);  // complete whit a blanck
            }
   }
    

}





