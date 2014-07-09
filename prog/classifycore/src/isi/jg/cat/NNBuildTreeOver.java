package isi.jg.cat;


import isi.jg.cat.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.*;
import isi.jg.idxvli.*;

/**
 * Génération d'un arbre de réseaux de neurones.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * toute autre utilisation est sujette à autorisation
 * <hr>
 * <b>principe de fonctionnement</b>
 * <p>Cette classe génére un arbre de réseau de neurone, pour un système interactif de catégorisation assistée.
 * <p>Les documents d'apprentissage doivent être indexés au préalable.
 * <p>On doit aussi posséder un catalogue d'apprentissage (celui de test peut être vide!).
 * <p>Après construction, nous obtenons trois fichiers:
 * <br>  MON_ARBRE_DE_RN.mnn : fichier contenant la description de l'arbre
 * <br>  MON_ARBRE_DE_RN.mnn.cmnn : fichier contenant les réseaux de neurones
 * <br>  MON_ARBRE_DE_RN.mnn.rmnn : fichier temporaire de travail (peut être effacé)
 * <p>Le fichier temporaire de travail est grand. son espace est égale au produit
 * du nombre des mots conservés par le nombre max de classes de tout l'arbre.
 * <p> dans l'example de l'IPC7, au niveau maingroup (4 niveaux dans l'arbre), il faut compter avec plus de 20'000 classes et 300'000 mots conservés.
 * Un fichier de 6 Gbytes est donc nécessaire.
 * <hr>
 * <b>détermination de la taille de l'arbre</b>
 * <p>pour un arbre à quatre niveau, dans le cas de l'IPC7, avec à chaque niveau, les valeurs suivantes:
 * <pre>
 *                          SECTION      CLASS   SUBCLASS    MAINGROUP
 *  nbr de catégories             8        120        500         4000
 * 
 *                           
 *  nbr de noeuds = 4 + 3*nbsection +2*nbclass+ nbsubclass
 *  soit          = 4 +3*24         +2*120    + 500
 *                = 816 
 *  On prendra 1000 pour être sur de couvrir les variations possibles           
 *</pre>
 * <hr>
 * <b>détermination du total des classes de l'arbre</b>
 * <p>pour un arbre à quatre niveau, dans le cas de l'IPC7, avec à chaque niveau, les valeurs suivantes:
 * <pre>
 *                          SECTION      CLASS   SUBCLASS    MAINGROUP
 *  nbr de catégories             8        120        500         4000
 * 
 *                           
 *  nbr de classes = 4*nbmaingroup +3*nbsubclass+ 2*nbclass + nbsection
 *  soit          =  4*4000        +3*500       + 2*240     + 8
 *                =  17988 
 *  On prendra 20000 pour être sur de couvrir les variations possibles           
 *</pre>
 * <hr>
 * <b>Exemple de programme pour construire un arbre de réseaux</b>
 * <pre>
 * <hr>
 * <b>Exemple de programme pour construire un arbre de réseaux</b>
 * <pre>
 *   // chargement de l'indexeur
 *
 *        IdxStructure id=new IdxStructure();
 *        id.createComponent("F:/JG/IDXNEW/gigaversion/");
 *        String idxFN="EFGIndex.idx";
 *        id.indexMFL=false;
 *        id.withDocBag=true;
 *        id.IO.loadindexdoc(idxFN);  // charge l'indexer
 *        id.Statistic.global();
 *
 *   // construction des catégories et définition du jeu d'apprentissage
 *
 *        String fntrain="F:/JG/IDXNEW/gigaversion/EFGtrainMAX.cat";
 *        String fntest ="F:/JG/IDXNEW/gigaversion/EFGempty.cat";
 *        NNBottomGroup BootGroup=new NNBottomGroup(id.Indexer,fntrain,fntest,NNOne.CAT_MAINGROUP,false,false);
 *
 *
 *   // génération de l'arbre de réseaux de neurones
 *
 *        String nnfile="EFG.mnn";
 *        NNBuildTree.init(
 *           BootGroup,                 //catégories et documents d'apprentissage
 *           id,                        //indexeur
 *           true,                      // sauver l'arbre
 *           NNBuildTree.MODE_1347,     // stratégie de construction
 *           1000,                      // nbr de réseaux
 *           22000,                     // somme de toutes les catégories de tous les réseaux
 *           1.06f,                     // alpha
 *           300.0f,300.0f,             // niveaux d'histeresis
 *           4,1000000,                 // min et max occurence
 *           3,                         // nbre de choix retenu
 *           5,                         // nbre de cycle d'apprentissage
 *           nnfile                     // fichier pour mémoriser l'arbre.
 *        );
 * </pre>
 */

public class NNBuildTreeOver {
    
    static boolean verbose=false; // no comment
    static IdxStructure id;
    
    static float alpha=1.03f;
    static float deltamin=300;
    static float deltamax=300;
    static int minocc=4;
    static int maxnbocc=50000;
    static int nfirst=4;
    static int repeatK=5;
    
    static boolean save=true;
    /** Stratégie de construction de l'arbre (3 choix: SECTION,CLASS,SUBBLASSP)
     */
    public static final int MODE_134=0;
    /** Stratégie de construction de l'arbre (2 choix: SECTION,SUBBLASS)
     */
    public static final int MODE_104=1;
    /** Stratégie de construction de l'arbre (2 choix: CLASS,SUBBLASS)
     */
    public static final int MODE_034=2;
    /** Stratégie de construction de l'arbre (4 choix: SECTION,CLASS,SUBBLASS,MAINGROUP)
     */
    public static final int MODE_1347=10;
    /** Stratégie de construction de l'arbre (pour les tests seulement Perfect Sect + 2 choix  (calcul sur Mode_134): SECTION,CLASS,SUBBLASS)
     */
    public static final int MODE_P34=3;
    /** Stratégie de construction de l'arbre (pour les tests seulement Perfect Sect + 1 choix  (calcul sur Mode_104): SECTION,SUBBLASS)
     */
    public static final int MODE_P04=4;
    /** Stratégie de construction de l'arbre (1 choix (un seul réseau!): SECTION)
     */
    public static final int MODE_1=14;
    /** Stratégie de construction de l'arbre (1 choix (un seul réseau!): MAINGROUP)
     */
    public static final int MODE_4=16;
    /** Stratégie de construction de l'arbre (1 choix (un seul réseau!): MAINGROUP)
     */
    public static final int MODE_7=15;
    
    static NNManyOver MM;
    static NNBottomGroup BootGroup;
    
    /** Création de la structure statique de génération de l'arbre.
     */
    public NNBuildTreeOver(){}
    
    /** initialise et construit l'arbre de réseaux de neurones.
     * @param _BootGroup Groupe de catégories et jeu de documents
     * @param _id Indexeur initialier avec le corpus (sac de mots)
     * @param _save true -> finaliser et sauver l'arbre de réseaux de neurones
     * false -> ne pas le faire (utile pour les tests seulement)
     * @param mode stratégie de construction de l'arbre (voir constante MODE_nnn)
     * @param MAXNN nombre maximum de noeuds (réseaux de neurones) de l'arbre
     * @param MAXCLASS somme de toutes les classes de tous les noeuds de l'arbre
     * @param _alpha facteur de promotion/démotion de Winnow-U
     * @param _deltamin zone négative d'histerésis de Winnow-U
     * @param _deltamax zone positive d'histerésis de Winnow-U
     * @param _minocc nombre mininum d'occurences dans les documents d'apprentissage d'um mot pour être retenu dans le réseau de neurones.
     * @param _maxnbocc nombre maximum d'occurences dans les documents d'apprentissage d'um mot pour être retenu dans le réseau de neurones.
     * @param _nfirst nombre de choix pris en compte (pour les tests seulement)
     * @param _repeatK nombre de cycle d'apprentissage
     * @param nnfile Nom du fichier de résultat pour stocker l'arbre du réseau de neurones
     */
    public static void init(String sign, NNBottomGroup _BootGroup, IdxStructure _id, boolean _save, int mode,
    int MAXNN, int MAXCLASS,
    float _alpha,float _deltamin,float _deltamax, int _minocc,int _maxnbocc,
    int _nfirst, int _repeatK,
    String nnfile,
    boolean overSampling,
    int overSize
    
    ){
        id=_id;
        BootGroup=_BootGroup;
        save=_save;
        
        alpha=_alpha;
        deltamin=_deltamin;
        deltamax=_deltamax;
        minocc=_minocc;
        maxnbocc=_maxnbocc;
        nfirst=_nfirst;
        repeatK=_repeatK;
        
        NNOneN_OverSampling.GLOBALMINOCC=minocc;
        //NNOneN_OverSampling.INMEMORY=false;
        NNOneN_OverSampling.setOverSampling(overSampling);
        NNOneN_OverSampling.setMaxOver(overSize);
        NNOneN_OverSampling.init(sign, BootGroup,id,NNOneN_OverSampling.NORMALISED,NNOneN_OverSampling.SDF_SQUARE); // initialise le réseau de neurone ...
        MM=new NNManyOver("WIPO alfa",  NNManyOver.MODE_LEARN, nnfile,
        MAXNN,MAXCLASS );
        if (MODE_134==mode) buildList_134();
        if (MODE_104==mode) buildList_104();
        if (MODE_034==mode) buildList_034();
        if (MODE_1347==mode) buildList_1347();
        if (MODE_1==mode) buildList_1();
       if (MODE_4==mode) buildList_4();
        if (MODE_7==mode) buildList_7();
        if (save) MM.save();
    }
    
    static void addNN(int grouplength, String prefixgroup){
        NNOneN_OverSampling.TrainWinnow(grouplength,prefixgroup,repeatK,1000,alpha,minocc,maxnbocc,deltamin,deltamax,false,false,100);
        //NNOne.testWinnow(false);
        if (verbose) NNOneN_OverSampling.testWinnow4(false,false,nfirst);
        if (verbose) NNOneN_OverSampling.testWinnow4Multi(false,nfirst);
        MM.RecordNN();
    }
    
    static void buildList_134(){
        addNN(1,"");
        addNN(3,"");
        addNN(4,"");
        listGroup(1,3);
        listGroup(3,4);
    }
    
    static void buildList_104(){
        addNN(1,"");
        //addNN(3,"");
        //addNN(4,"");
        listGroup(1,4);
    }
    
    static void buildList_034(){
        addNN(3,"");
        //addNN(3,"");
        //addNN(4,"");
        listGroup(3,4);
    }
    
    static void buildList_1347(){
       addNN(7,"");
        addNN(4,"");
        addNN(3,"");
        addNN(1,"");
        listGroup(1,3);
        listGroup(1,4);
        listGroup(1,7);
        listGroup(3,4);
        listGroup(3,7);
        listGroup(4,7);
    }
    
    static void buildList_1(){
        addNN(1,"");
    }
    static void buildList_4(){
        addNN(4,"");
    }
    static void buildList_7(){
        addNN(7,"");
    }
    
    static void listGroup(int fromlevel, int tolevel){
        System.out.println("-------------from:"+fromlevel+" to:"+tolevel+" -------------------------------");
        Hashtable groupinv;
        int maxgroup;
        
        RandomizeDoc.init(id, BootGroup, "" ); // reset all preceding computation, get all doc
        NNLocalGroup ActiveList=new NNLocalGroup(BootGroup, id, fromlevel,false);
        groupinv=ActiveList.groupinv;   // save document class
        maxgroup=ActiveList.maxgroup;
        for (int i=0;i<maxgroup;i++){
            addNN(tolevel,(String)groupinv.get(new Integer(i)));
            
        }
        
        
    }
}
