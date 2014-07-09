package isi.jg.cat;



import isi.jg.idxvli.*;
import isi.jg.idxvli.extra.*;
import isi.jg.util.Timer;
import static isi.jg.idxvli.IdxConstant.*;

/** classe pour générer un réseau de neurone par apprentissage et pour le tester.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée au groupe ISI
 * toute autre utilisation est sujette à autorisation
 *
*
 * <hr>
 * <b>principe de fonctionnement</b>
 * basé sur l'apprentissage et sur les réseaux de neurones
 *
 */

public class NNOneUni {
    
    /** niveau 1 de catégorisation
     */
    public static final int CAT_SECTION=1;
    /** niveau 2 de catégorisation
     */
    public static final int CAT_CLASS=3;
    /** niveau 3 de catégorisation
     */
    public static final int CAT_SUBCLASS=4;
    /** niveau 4 de catégorisation
     */
    public static final int CAT_MAINGROUP=7;
    
    /** pondération unitaire des termes
     */
    public static final int SDF_ONE=0;
    /** pondération racine carrée de la fréquence du terme dans le document
     */
    public static final int SDF_SQUARE=1;
    /** pondération fréquence du terme dans le document
     */
    public static final int SDF_N=2;
    /** pondération log de la fréquence du terme dans le document
     */
    public  static final int SDF_LN=3;
    /** pondération puissance de la fréquence du terme dans le document
     */
    public  static final int SDF_POWER=4;
    /** puissance associée à SDF_POWER */
    public  static  double POWER=0.5;
    
    static int method=0; // method for sdf
    
    /* learn on multi-class */
    /** apprentissage uniquement sur la catégorie principale du document
     */
    public static final boolean LEARNMULTIGROUP=true;
    /** apprentissage sur les catégories secondaires du document
     */
    public static final boolean LEARNMONOGROUP=false;
    static boolean learntype=true;
    
    /* normalised feature weigthing*/
    
    /** normalisation des pondérations
     */
    public static final boolean NORMALISED=true;
    /** pas de normalisation des pondérations
     */
    public static final boolean UNNORMALISED=false;
    static boolean normalisedFeature=false;
    
    
    /* flag for system */
    /** mode montrant les résultats intermédiares de l'apprentissage
     */
    public static  boolean VERBOSE=false; /* show training result ... */
    /** chargement des documents en mémoire (pour accélére l'apprentissage)
     */
    public static  boolean INMEMORY=true; /* keep DocBag in memory if big enough */
    /** montrer les détails des tests
     */
    static final int nfirst=3;  
    

    static final int W_USED=4;
    static final int W_OPEN=3;
    static final int W_DISC=2;
    static final int W_FILT=1;



    
    static IdxStructure glue;  // global variable
    //static IdxIndexer Indexer; // global variable
    
    
    /** seuil minimum pour le minimum d'occurence d'un terme dans le corpus selectionné
     */
    public static  int GLOBALMINOCC=2; // inferior limit for minocc
    
    static  int maxtrain=0;  // define 0..maxtrain -> the set of training documents
    static int lastdoc;      // define maxtrain..lastdoc -> the set of testing documents
    
    static int lasttesttraindoc; // define 0..lasttesttraindoc -> the subset of training documents
    static int lasttestdoc;      // define lasttesttraindoc..lasttestdoc -> the subset of testing documents
    static int lastword;
    
    static double[][] nnc;     // neuron net positive Winnow compact
    
    static double[] cumul;     // the result of a evaluation of net neuron
    
    static  int maxgroup=0; // nb of group
    static  int maxused=0;  // nb of active feature
    
    static  int[] wordusetrain;  // set of words used in the training docs
    static  int[] worduse;       // set of words used as feature (after filtering)
//    static  int[] wordAtIdx;      // map for compacting words indexind
    static  int[] wordFreq;       // map for compacting words freq
    static  int[] wordOcctrain;   // occ for training doc
    static int[] docbag;          // active docbag (for train, correct, and evaluation)
    static int[][] alldocbag;    //  docbag if in memmory
    
    static float add=1.06f,qlevel=1000,deltamin,deltamax,qlevelminusdelta,qlevelplusdelta;
    static float startwith;

    static int minocc,maxocc;
          static long avg;  // modif 30.1.2006
    
    static NNBottomGroup BootGroup;
    static NNLocalGroup  ActiveGroup;
    static int categorylevel;
    static  String prefix;
    
    /** si 0 = tout le document est considéré comme un exemple sinon on prend nW termes de l'exemple
     */
    public static int nW=0; // if 0 = trainset is all the doc else get only nW terms
    
    NNOneUni(){};
    /** initialisation du réseau. Le réseau est initialisé avec un groupe de document pour l'apprentissage et pour les tests.
     * L'initialisation se fait une fois et plusieurs apprentissages et tests consécutifs sont possibles.
     *
     * @param _BootGroup les groupes de documents d'apprentissage et de test.
     * @param _glue la structure d'index permettant de décoder les noms des documents et les termes indexés.
     * @param _normalised normalisation ({NORMALISED} ou {UNNORMALISED}).
     * @param _method méthode de pondération des neurones (SDF_ONE, SDF_SQUARE, SDF_N, SDF_LN).
     *
     * NORMALISED et SDF_SQUARE donne les meilleurs résultats (dans les corpus testés)
     */
    public static void init(NNBottomGroup _BootGroup, IdxStructure _glue, boolean _normalised, int _method){
        glue=_glue;
        BootGroup=_BootGroup;
        lastdoc=glue.lastRecordedDoc;
        lastword=glue.lastRecordedWord;
        normalisedFeature=_normalised;
        method=_method;
        if (INMEMORY) {
            alldocbag=new int[lastdoc][];
            for(int d=1;d<lastdoc;d++){
                alldocbag[d]=glue.getBagOfDoc(d);
            }
        }
//        initSdf(method);
        avgLength();
        System.out.println("GLOBALMINOCC: "+GLOBALMINOCC+" , MAX features:"+maxused);
    }
    
    static double sdf(int i){
        if (method==SDF_ONE){
            return 1;
        } else
            if (method==SDF_N){
                return i;
            } else
                if (method==SDF_LN){
                    return Math.log(i+1);
                } else
                    if (method==SDF_SQUARE){
                        return Math.sqrt(i);
                        } else if (method==SDF_POWER){
                        return Math.pow(i,POWER);
                    } else  System.out.println("ERROR in sdf:"+method);
        return 1;
    }
    
    static void computeWinnow(int d){
        computeWinnowPosCompact(d);
        normalisedFeature();
    }
    
    
    static void correctWinnow(int[] group){
        correctWinnowPosCompact(group);
    }
    
    static void initWinnow(){
        worduse=new int[lastword];
        for(int j=0;j<lastword;j++){
                worduse[j]=wordusetrain[j]; // init from training doc
                if (worduse[j]==W_OPEN){
                    if(wordOcctrain[j]>=minocc&&wordOcctrain[j]<=maxocc){
                        ; // ok
                    }
                    else worduse[j]=0x1;
            }
        }
        initWinnowPosCompact();
    }
    
    /* positive winnow and balanced implementation */
    
    static void computeWinnowPosCompact(int d){
        cumul=new double[maxgroup];
        if (INMEMORY){
            docbag=alldocbag[d];}
        else {
            docbag=glue.getBagOfDoc(d);}
        if (nW!=0) docbag=DocBag.getTerms(nW,docbag);
        // showVector(docbag);
        for (int i=0;i<docbag.length;i++){
            int iiii=docbag[i]/DocBag.MAXOCCINDOC;  // with indirection
            if (worduse[iiii]>=W_OPEN){// if not discard
                double wsdf=sdf(docbag[i]%DocBag.MAXOCCINDOC); // eval feature weight
                for(int j=0;j<maxgroup;j++){
                        cumul[j]+=nnc[iiii][j]*wsdf;
               }
            }
        }
    }
    
    static void computeWinnowPosCompactOnVector(int[] freedoc){
        cumul=new double[maxgroup];
        docbag=freedoc;
        // showVector(docbag);
        for (int i=0;i<docbag.length;i++){
            int iiii=docbag[i]/DocBag.MAXOCCINDOC;  // with indirection
            if (worduse[iiii]>=W_OPEN){// if not discard
                double wsdf=sdf(docbag[i]%DocBag.MAXOCCINDOC); // eval feature weight
                for(int j=0;j<maxgroup;j++){
                        cumul[j]+=nnc[iiii][j]*wsdf;
                }
            }
        }
        //showVector(cumul);
        //showVector(nnc[wordAtIdx[docbag[0]/DocBag.MAXOCCINDOC]]);
    }
    
    
    static void correctWinnowPosCompact(int[] group){
        //showVector(cumul);
        for(int j=0;j<maxgroup;j++){
            if(cumul[j]>qlevelminusdelta){ // predict in the group
                if(!inGroup(group,j)){// minimize if not ingroup
                    for (int i=0;i<docbag.length;i++){
                        int iiii=docbag[i]/DocBag.MAXOCCINDOC;  // with indirection
                        if (worduse[iiii]>=W_OPEN){// if not discard
                            nnc[iiii][j]/=add;
                        }
                    }
                }
            }
            if(cumul[j]<qlevelplusdelta) { // not in the group
                if(inGroup(group,j)){// reinforce
                    for (int i=0;i<docbag.length;i++){
                        int iiii=docbag[i]/DocBag.MAXOCCINDOC;  // with indirection
                        if (worduse[iiii]>=W_OPEN){// if not discard
                            nnc[iiii][j]*=add;
                            // if  (DISCARDPROCESS) wordpmistake[iiii]++;
                        }
                    }
                }
            }
        }
    }
    
    static void initWinnowPosCompact(){
        nnc=new double[lastword][maxgroup];
        for(int j=0;j<lastword;j++){
            for(int i=0;i<maxgroup;i++){
                nnc[j][i]=startwith;
            }
        }
         
    }
    
    
    static void normalisedFeature(){
        if (normalisedFeature){
            float normalised=0;
            for (int i=0;i<docbag.length;i++){  // compute normalisation for this doc
                int iiii=docbag[i]/DocBag.MAXOCCINDOC;  // with indirection
                if (worduse[iiii]>=W_OPEN){// if not discard
                    normalised+=sdf(docbag[i]%DocBag.MAXOCCINDOC);
                }
            }
            for(int j=0;j<maxgroup;j++){  // normalised
                cumul[j]/=normalised;
            }
        }
    }
    
    
    static boolean testDocOK(int d){ // selecting doc for training
        int g=ActiveGroup.getMainGroup(d);
        return (g!=NOT_FOUND)&&ActiveGroup.groupSize(d)>=1; //==1 monogroup
    }
    
    static boolean testDocOK2(int d){ // selecting doc for testing
        int g=ActiveGroup.getMainGroup(d);
        return (g!=NOT_FOUND)&&ActiveGroup.groupSize(d)>=1; //==1 monogroup
    }
    
    
    static void featureWinnow(String title){
        // showVector(docbag);
        int totused=0,totopen=0,totdisc=0,totfilt=0;
        for (int i=0;i<lastword;i++){
            if(worduse[i]==W_USED)totused++;
            if(worduse[i]==W_OPEN)totopen++;
            if(worduse[i]==W_DISC)totdisc++;
            if(worduse[i]==W_FILT)totfilt++;
        }
        System.out.println(title+" used:"+totused+", open:"+totopen+", discarded:"+totdisc+", filtred:"+totfilt);
    }
    
    static boolean inGroup(int[] group,int g){
        int max;
        if  (learntype)
            max=group.length; // learn multi
        else
            max=1; // learn main only
        for(int i=0;i<max;i++){
            if (group[i]==g) return true;
        }
        return false;
    }
    
    
    static void avgLength(){
        Timer t1=new Timer("avgLength()");
        int minlength=999999999,maxlength=-1;
        int nbdoc=0,totdoc=0;
        int[] doclength=new int[lastdoc];
        wordusetrain=new int[lastword];  // 8=used in train 4=used training 2=discard 1=TF is too low
        wordOcctrain=new int[lastword];  // occ in train Set
        for(int id=1;id<lastdoc;id++){ // use train vocabulary
            //  for(int id=maxtrain;id<lastdoc;id++){ // use test vocabulary
            if (BootGroup.doctype[id]==NNBottomGroup.TRAINDOC){ // only training doc
                if (INMEMORY){
                    docbag=alldocbag[id];}
                else {
                    docbag=glue.getBagOfDoc(id);}
                for (int n=0;n<docbag.length;n++){
                    //System.out.println(id+","+n+","+docbag[n]+","+DocBag.MAXOCCINDOC);
                    wordusetrain[docbag[n]/DocBag.MAXOCCINDOC]=W_OPEN;
                    wordOcctrain[docbag[n]/DocBag.MAXOCCINDOC]++;
                }
                nbdoc++;totdoc+=docbag.length;
                doclength[id]=docbag.length;
                if (docbag.length<minlength)minlength=docbag.length;
                if (docbag.length>maxlength)maxlength=docbag.length;
            }
        }
        avg=totdoc/nbdoc;
        System.out.println("#doc:"+nbdoc+", avg:"+avg+", min:"+minlength+", max:"+maxlength);
        
        maxused=0;
        for(int j=0;j<lastword;j++){// translate
            if (wordOcctrain[j]>=GLOBALMINOCC){
                maxused++;}
        }
        wordFreq= new int[lastword];
        for(int j=0;j<lastword;j++){// compute freq
                wordFreq[j]=glue.getOccOfW(j); // document appearance
        }
        
        if (normalisedFeature){
       //     startwith=qlevel; } // normalisation dans le calcul
            startwith=qlevel; } // normalisation dans le calcul
        else{
            startwith=qlevel/(float)avg;}  // moyenne des longueurs des doc
        if (VERBOSE) System.out.println("startwith:"+startwith);
        t1.stop();
        
    }
    
    /** apprentissage du réseau de neurone.
     * Le réseau est spécifié par son niveau de classification et par un éventuel sous ensemble à classifier.
     * @param _categorylevel niveau de classification.
     * @param _prefix sous ensemble à classifier.
     * @param repeatK facteur de répétition de l'apprentissage (4, par exemple)
     * @param _qlevel valeur d'initialisation des neurones (1000, par exemple).
     * @param _add facteur de promotion (1.06 par exemple).
     * @param _minocc nombre minimum d'occurences du terme dans le corpus pour être sélectionné (3, par exemple).
     * @param _maxocc nombre maximum d'occurences du terme dans le corpus pour être sélectionné (1000, par exemple).
     * @param _deltamin frontière épaisse (en dessous du seuil) (300, par exemple).
     * @param _deltamax frontière épaisse (en dessus du seuil) (300, par exemple).
     * @param verbosetrain montre les détails du groupe et de la répartition des documents de test et d'apprentissage.
     * @param testtrain effectuer le test avec les documents d'apprentissage
     * @param trainpart pourcentage pris pour l'apprentissage, le reste est pour le test
     *
     * NORMALISED et SDF_SQUARE donne les meilleurs résultats (dans les corpus testés)
     */
    public static void TrainWinnow(int _categorylevel, String _prefix, int repeatK,float _qlevel, float _add, int _minocc, int _maxocc,
    float _deltamin,float _deltamax, boolean verbosetrain,
    boolean testtrain, int trainpart){
        // init global
        
        prefix=_prefix;
        categorylevel=_categorylevel;
        System.out.println(categorylevel+"."+prefix);
        RandomizeDoc.init(glue,BootGroup, prefix );
        
        if (testtrain){ // on utilise une partie du train set pour les tests
            lasttesttraindoc=(trainpart* RandomizeDoc.lasttraindoc)/100;  // trainpart %
            lasttestdoc=RandomizeDoc.lasttraindoc;
            maxtrain=lasttesttraindoc;
            
        }
        else{ // on utilise le catalogue de test pour les tests
            lasttesttraindoc=RandomizeDoc.lasttraindoc;  // 100 %
            maxtrain=RandomizeDoc.lasttraindoc;
            lasttestdoc=RandomizeDoc.lasttestdoc;
        }
        
        System.out.println("Train 0.."+lasttesttraindoc+" Test .."+lasttestdoc);
        
        
        ActiveGroup=new NNLocalGroup(BootGroup, glue, categorylevel, verbosetrain);
        
        
        maxgroup=ActiveGroup.maxgroup;
        System.out.println("Active group:"+maxgroup);
        
        if (verbosetrain) showRepartitionTX();
        
        add=_add;
        
        
        if (_minocc>=GLOBALMINOCC){minocc=_minocc;}
        else{System.out.println("minocc>=GLOBALMINOCC initialise to:"+GLOBALMINOCC);minocc=GLOBALMINOCC;}
        maxocc=_maxocc;
        qlevel=_qlevel;
        deltamin=_deltamin;
        deltamax=_deltamax;
        qlevelminusdelta=qlevel-deltamin;
        qlevelplusdelta=qlevel+deltamax;
        
        Timer t1=new Timer("TrainWinnow");
        //MemoryEconomizer.usedMemory("before initWinnow");
        initWinnow();
        //MemoryEconomizer.usedMemory("After initWinnow");
        
        if (VERBOSE) featureWinnow("filter ");
        //
        int maxepoch=1;
        int epochsize=maxtrain/maxepoch;
        for(int cycle=0;cycle<1;cycle++){
            for(int epoch=0;epoch<maxepoch;epoch++){
                int epochstart=epoch*epochsize;
                int epochstop=epochstart+epochsize;
                for(int k=0;k<repeatK;k++){
                    for(int id=epochstart;id<epochstop;id++){
                        int d=RandomizeDoc.rand[id];
                        if (testDocOK(d)){
                            int[] g=ActiveGroup.getGroup(d );
                            computeWinnow(d);
                            correctWinnow(g);
                        }
                    }
                    if (VERBOSE) {System.out.println("rep: "+k);testWinnow4(false,false,nfirst);}
                }
            }
        }
        t1.stop();
    }
    
    static int[] topGroup(){   // 0=group //1=score
        double max=0;
        int imax=0;
        int[] res=new int[2];
        res[0]=NOT_FOUND;
        for (int i = 0; i < maxgroup; i++) { // for each group
            if (max<cumul[i]) {max=cumul[i];imax=i;}
        }
        if (max==0) return res;
        res[0]=imax;res[1]=(int)max;
        return res;
    }
    
    
    /** afficher la matrice de confusion
     * @param detail donne les détails pour chaque document
     */
    public static void ConfusionMatrix(boolean detail){
        // init
        Timer t1=new Timer("ConfusionMatrix");
        int totingroup=0,totgoodgroup=0,totbadgroup=0,totnoclassgroup=0;
        int[][] multi=new int[maxgroup][maxgroup];
        int beg,end;
        beg=lasttesttraindoc;end=lasttestdoc;
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            int g=ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)){
                computeWinnow(d);
                int top= topGroup()[0];
                if (detail){
                    int score= topGroup()[1];
                    System.out.println(glue.getFileNameForDocument(d)+" "+ActiveGroup.getgroupName(g)+
                    " "+ActiveGroup.getgroupName(top)+" "+score);
                }
                totingroup++;
                if (top==-1)totnoclassgroup++;
                if (top==g)totgoodgroup++; else totbadgroup++;
                //System.out.println(g+";"+top);
                multi[g][top]++;
            }
        }
        
        int recall=totgoodgroup*1000/(totgoodgroup+totbadgroup);
        int precision=totgoodgroup*1000/(totingroup);
        int error=totbadgroup*1000/(totgoodgroup+totbadgroup);
        int falout=totnoclassgroup*1000/(totingroup);
        System.out.println(qlevel+","+add+","+minocc+","+deltamin+","+deltamax+","+recall+","+precision+","+error+","+falout);
        //t1.stop();
        System.out.println("confusion matrix: (line=real category; colums= prediction)");
        System.out.print(">>predict,");
        for  (int i=0;i<maxgroup;i++){System.out.print(ActiveGroup.getgroupName(i)+",");}// header of cols
        System.out.println();
        for  (int i=0;i<maxgroup;i++){
            System.out.print(ActiveGroup.getgroupName(i)+",");
            for  (int j=0;j<maxgroup;j++){
                System.out.print(multi[i][j]+",");
            }
            System.out.println();
        }
        t1.stop();
    }
    
    /** permet de calculer la répartition des prédictions en fonction de la valeur de prédiction
     * @param maxscore score maximum des calculs
     */
    public static void confidenceLevel(int maxscore){
        // init
        Timer t1=new Timer("confidenceLevel");
        int totingroup=0,totgoodgroup=0,totbadgroup=0,totnoclassgroup=0;
        int[] goodgroup=new int[maxscore];
        int[] badgroup=new int[maxscore];
        int beg,end;
        beg=lasttesttraindoc;end=lasttestdoc;
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            int g=ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)){
                computeWinnow(d);
                int top= topGroup()[0];
                int score= Math.min(maxscore-1,topGroup()[1]);
                totingroup++;
                if (top==-1)totnoclassgroup++;
                if (top==g){
                    totgoodgroup++;
                    goodgroup[score]++;
                } else{
                    totbadgroup++;
                    badgroup[score]++;
                }
            }
        }
        
        int recall=totgoodgroup*1000/(totgoodgroup+totbadgroup);
        int precision=totgoodgroup*1000/(totingroup);
        int error=totbadgroup*1000/(totgoodgroup+totbadgroup);
        int falout=totnoclassgroup*1000/(totingroup);
        System.out.println(qlevel+","+add+","+minocc+","+deltamin+","+deltamax+","+recall+","+precision+","+error+","+falout);
        //t1.stop();
        System.out.println("confidence level : (for top prediction and main category");
        System.out.println("level, good, bad");
        for  (int i=0;i<maxscore;i++){
            System.out.println(i+","+goodgroup[i]+","+badgroup[i]);
        }
        t1.stop();
    }
    
    static int[] topGroupN(int n){
        
        // cherche les groupe smax
        int[] res=new int[n];
        for (int j=0;j<n;j++){res[j]=NOT_FOUND;} // init res
        for (int j=0;j<n;j++){
            double max=0;
            int imax=0;
            for (int i = 0; i < maxgroup; i++) { // for each group
                if (max<cumul[i]) {
                    int k=NOT_FOUND;
                    for (k=0;k<j;k++){
                        if (res[k]==i) break; }
                    if ((k!=NOT_FOUND)&&(res[k]!=i)){
                        max=cumul[i];imax=i;}
                }
            }
            if (max==0) return res; // partial result
            res[j]=imax; // new one
        }
        return res;
    }
    
    
    public static int getIntTopForDocument(int d){ // pour usage interne !! attention au reférence
        computeWinnow(d);
        int[] res=topGroup();
        return res[0];
    }
    
    /** cherche la catégorie principale pour un document
     * @param d numéro du document
     * @return nom de la catégorie principale
     */
    public static String getTopForDocument(int d){
        computeWinnow(d);
        int[] res=topGroup();
        return ActiveGroup.getgroupName(res[0]);
    }
    
    
    /** cherche la catégorie principale d'un ensemble de termes
     * @param db ensembles de termes
     * @return nom de la catégorie principale
     */
    public static String getTopForDocBag(int[] db){
        computeWinnowPosCompactOnVector(db);
        int[] res=topGroup();
        return ActiveGroup.getgroupName(res[0]);
    }
    
    /** cherche n premières catégories d'un ensemble de termes
     * @param db ensembles de termes
     * @param nfirst nombre de catégories à ramener
     * @return liste des catégories
     */
    public static String[] getTopForDocBag(int[] db, int nfirst){
        computeWinnowPosCompactOnVector(db);
        int[] res=topGroupN(nfirst);
        String[] s=new String[res.length];
        for (int i=0;i<res.length;i++) s[i]=ActiveGroup.getgroupName(res[i]);
        return s;
    }
    
    /** cherche n premières catégories d'un texte
     * @param request texte
     * @param nfirst nombre de catégories à ramener
     * @return liste des catégories
     */
    public static String[] getTopForString(String request, int nfirst){
        
        DoParse a = new DoParse(request+".", glue.dontIndexThis);
        int[] db=a.getDocBag(glue); // get the docBag of the request
        //showVector(db);
        computeWinnowPosCompactOnVector(db);
        int[] res=topGroupN(nfirst);
        String[] s=new String[res.length];
        for (int i=0;i<res.length;i++) s[i]=ActiveGroup.getgroupName(res[i]);
        return s;
    }
    
    public static String getList(String[] v){
        String res="";
        if (v==null) return null;
        for (int i=0;i<v.length;i++)res+=v[i]+" ";
        return res;
    }
    
    /** test pour la catégorie principale
     * @param detailclass montre les détails par catégorie
     * @param detaildocument montre les détail par classe
     * @param n nombre de prédictions
     */
    public static void testWinnow4(boolean detailclass,boolean detaildocument, int n){
        // init
        //Timer t1=new Timer("TrainWinnow");
        int totdoc=0;
        int[] totingroup=new int[n];
        int beg,end;
        int[] totclass=new int[maxgroup];
        int[][] inclass=new int[maxgroup][n];
        beg=lasttesttraindoc;end=lasttestdoc;
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            int g=ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)){
                totdoc++;
                totclass[g]++;
                computeWinnow(d);
                int[] top= topGroupN(n);
                for(int k=0;k<n;k++){
                    if (top[k]==g) {
                        totingroup[k]++;
                        inclass[g][k]++;
                        break;
                    }
                }
                if (detaildocument){
                    System.out.print(glue.getFileNameForDocument(d)+" "+ActiveGroup.getgroupName(g));
                    for(int k=0;k<n;k++){ if(top[k]!=NOT_FOUND){
                        System.out.print(" "+ActiveGroup.getgroupName(top[k])+" "+((int)cumul[top[k]]));
                    }
                    }
                    System.out.println();
                }
                
            }
        }
        
        int totgoodgroup=0;
        for(int k=0;k<n;k++){totgoodgroup+=totingroup[k];}
        int totbadgroup=totdoc-totgoodgroup;
        int precision=totgoodgroup*1000/(totdoc);
        int error=totbadgroup*1000/(totdoc);
        System.out.print("Mainclass"+qlevel+","+add+","+minocc+","+deltamin+","+deltamax+","+precision+","+error);
        for(int k=0;k<n;k++){System.out.print(","+totingroup[k]*1000/(totdoc));}
        if (detailclass){
            System.out.println("\ndetail group accuracy");
            System.out.println("\n---------------------");
            System.out.println("\ngroup,in1,in2,in3,...");
            for(int i=0;i<maxgroup;i++){
                System.out.print(ActiveGroup.getgroupName(i)+","+totclass[i]);
                for(int k=0;k<n;k++){System.out.print(","+((float)inclass[i][k]/(float)totclass[i]));}
                System.out.print("\n");
            }
        }
        
        System.out.println();
        
        //t1.stop();
    }
    
    /** test pour les catégories secondaires
     * @param detailclass montre les détails par catégorie
     * @param n  nombre de prédiction */
    public static void testWinnow4Multi(boolean detailclass,int n){
        // init
        //Timer t1=new Timer("TrainWinnow");
        int totdoc=0;
        int[] totingroup=new int[n];
        int beg,end;
        int[] totclass=new int[maxgroup];
        int[][] inclass=new int[maxgroup][n];
        beg=lasttesttraindoc;end=lasttestdoc;
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            if (testDocOK2(d)){
                totdoc++;
                int[] g=ActiveGroup.getGroup(d);
                int gmain=g[0];
                totclass[gmain]++;
                //computeWinnowAndExplain(d);
                computeWinnow(d);
                int[] top= topGroupN(n);
                for(int k=0;k<n;k++){
                    boolean find=false;
                    for (int i=0;i<g.length;i++){
                        if (g[i]==top[k]){find=true;break;}
                    }
                    if (find) {
                        totingroup[k]++;
                        inclass[gmain][k]++;
                        break;
                    }
                }
            }
        }
        
        int totgoodgroup=0;
        for(int k=0;k<n;k++){totgoodgroup+=totingroup[k];}
        int totbadgroup=totdoc-totgoodgroup;
        int precision=totgoodgroup*1000/(totdoc);
        int error=totbadgroup*1000/(totdoc);
        System.out.print("Multiclass"+qlevel+","+add+","+minocc+","+deltamin+","+deltamax+","+precision+","+error);
        for(int k=0;k<n;k++){System.out.print(","+totingroup[k]*1000/(totdoc));}
        if (detailclass){
            System.out.println("\ngroup,in1,in2,in3,...");
            for(int i=0;i<maxgroup;i++){
                System.out.print(ActiveGroup.getgroupName(i)+","+totclass[i]);
                for(int k=0;k<n;k++){System.out.print(","+((float)inclass[i][k]/(float)totclass[i]));}
                System.out.print("\n");
            }
        }
        
        System.out.println();
        
        //t1.stop();
    }
    
    static void showRepartitionTX(){
        int tottrain=0,tottest=0;
        int beg,end;
        int[][] inclass=new int[maxgroup][2];  // 0 = train 1= test
        beg=lasttesttraindoc;end=lasttestdoc;
        for(int id=0;id<beg;id++){
            int d=RandomizeDoc.rand[id];
            if (testDocOK(d)){
                tottrain++;
                int g=ActiveGroup.getMainGroup(d);
                //System.out.println("detail"+id+","+d+","+g);
                inclass[g][0]++;
            }
        }
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            if (testDocOK2(d)){
                tottest++;
                int g=ActiveGroup.getMainGroup(d);
                inclass[g][1]++;
            }
        }
        System.out.println("REPARTITION");
        System.out.println("===========");
        System.out.println("total train:, "+tottrain);
        System.out.println("total test:, "+tottest);
        System.out.println("\ngroup,train,test");
        for(int i=0;i<maxgroup;i++){
            System.out.println(ActiveGroup.getgroupName(i)+","+inclass[i][0]+","+inclass[i][1]);
        }
        System.out.println();
    }
    
    

    
    /** génération XML de mots clé pour les document
     * @param verbosemode montre les détails
     * @param nKW nombre de mots clé
     */
    public static void generateIndex(boolean verbosemode, int nKW){
        // init
        Timer t1=new Timer("generateIndex");
        NNWordWeight ww;
        int beg,end;
        beg=0;end=lasttestdoc;
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            int g=ActiveGroup.getMainGroup(d);
            if (testDocOK2(d)){
                System.out.println("#####"+glue.getFileNameForDocument(d)+"#####"+
                "\n<MR:class id=\""+ActiveGroup.getgroupName(g)+"\">");
                ww=computeWinnowWeight(d,g);
                if (verbosemode) ww.displayXML(glue,nKW);
            }
        }
    }
    
    
    
    static  NNWordWeight computeWinnowWeight(int d, int g){  // pour un document et un groupe
        if (INMEMORY){
            docbag=alldocbag[d];}
        else {
            docbag=glue.getBagOfDoc(d);}
        NNWordWeight res=new NNWordWeight(docbag.length,d,g);
        float wig=0;
        for (int i=0;i<docbag.length;i++){
            int iiii=docbag[i]/DocBag.MAXOCCINDOC;  // with indirection
            if (worduse[iiii]>=W_OPEN){// if not discard
                double wsdf=sdf(docbag[i]%DocBag.MAXOCCINDOC); // eval feature weight
                    wig=(float)(nnc[iiii][g]*wsdf);
                res.add(docbag[i]/DocBag.MAXOCCINDOC,wig);
            }
        }
        return res;
    }
    
    /** génération de mots clé pour les groupes
     * @param nKW nombre de mots clé
     */
    public static void explainGroup(int nKW){
        
        // calcul la répartition des groupes
        int[] inclass=new int[maxgroup];
        for(int id=0;id<lasttestdoc;id++){
            int d=RandomizeDoc.rand[id];
            if (testDocOK(d)){
                int g=ActiveGroup.getMainGroup(d);
                inclass[g]++;
            }
        }
        
        NNWordWeight ww;
        System.out.println("groupe, nbdoc, kw1, kw2, kw3, ...");
        for(int i=0;i<maxgroup;i++){
            System.out.print("---- "+i+" "+ActiveGroup.getgroupName(i)+", nbdoc:"+inclass[i]);
            ww=computeWinnowWeight(i);
            ww.displayTXT(glue,nKW);
            System.out.println();
            
        }
        for(int i=0;i<maxgroup;i++){
            System.out.println("---- "+i+" "+ActiveGroup.getgroupName(i)+", nbdoc:"+inclass[i]);
            ww=computeWinnowWeight(i);
            ww.displayTXTDetail(glue,nKW);
            System.out.println();
            
        }
    }
    
    
    static  NNWordWeight computeWinnowWeight(int g){ // pour un groupe
        NNWordWeight res=new NNWordWeight(lastword,-1,g);
        float wig=0;
        for (int i=0;i<lastword;i++){
            if (worduse[i]>=W_OPEN){// if not discard
                double wsdf=sdf(1); // eval feature weight=
                    wig=(float)(nnc[i][g]*wsdf);
                res.add(i,wig);
                //System.out.println("in group g:"+g+"add w:"+i+" "+glue.getStringforW(i)+" wig:"+wig);
            }
        }
        return res;
    }
    
      /** test (pour le training) pour les catégories secondaires
     * @param detailclass montre les détails par catégorie
     * @param n  nombre de prédiction */
    public static void autoTestWinnow4Multi(boolean detailclass,int n){
        // init
        //Timer t1=new Timer("TrainWinnow");
        int totdoc=0;
        int[] totingroup=new int[n];
        int beg,end;
        int[] totclass=new int[maxgroup];
        int[][] inclass=new int[maxgroup][n];
        beg=0;end=lasttesttraindoc;
        for(int id=beg;id<end;id++){
            int d=RandomizeDoc.rand[id];
            if (testDocOK2(d)){
                totdoc++;
                int[] g=ActiveGroup.getGroup(d);
                int gmain=g[0];
                totclass[gmain]++;
                //computeWinnowAndExplain(d);
                computeWinnow(d);
                int[] top= topGroupN(n);
                for(int k=0;k<n;k++){
                    boolean find=false;
                    for (int i=0;i<g.length;i++){
                        if (g[i]==top[k]){
                            find=true;break;}
                    }
                    if (find) {
                        //System.out.println(glue.getFileNameForDocument(d)+" "+BootGroup.getMainGroupName(d));
                        totingroup[k]++;
                        inclass[gmain][k]++;
                        break;
                    }
                }
            }
        }
        
        int totgoodgroup=0;
        for(int k=0;k<n;k++){totgoodgroup+=totingroup[k];}
        int totbadgroup=totdoc-totgoodgroup;
        int precision=totgoodgroup*1000/(totdoc);
        int error=totbadgroup*1000/(totdoc);
        System.out.print("Multiclass"+qlevel+","+add+","+minocc+","+deltamin+","+deltamax+","+precision+","+error);
        for(int k=0;k<n;k++){System.out.print(","+totingroup[k]*1000/(totdoc));}
        if (detailclass){
            System.out.println("\ngroup,in1,in2,in3,...");
            for(int i=0;i<maxgroup;i++){
                System.out.println(ActiveGroup.getgroupName(i)+","+totclass[i]);
                for(int k=0;k<n;k++){System.out.print(","+((float)inclass[i][k]/(float)totclass[i]));}
                System.out.print("\n");
            }
        }
        
        System.out.println();
        
        //t1.stop();
    }
  
} // end class


