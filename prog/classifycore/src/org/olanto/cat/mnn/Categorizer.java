package org.olanto.cat.mnn;


import org.olanto.idxvli.extra.DocBag;
import org.olanto.idxvli.IdxStructure;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.olanto.util.Timer;


/**
 * Classificateur interactif de documents.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * toute autre utilisation est sujette à autorisation
 * <hr>
 * *
 */
public class Categorizer  {
    
    private static final int notFind=IdxStructure.notFind;
    private static  int MAXCLASS=25000;//460; // in term of disk block of 4k or 8k ...
    private static  int MAXFEATURE=2000; // 1000=753, 2000=275, 4000=89
    private static  int STARTLEVEL=16;  // start with document having startlevel occurence
    private static  int FREECACHE=4;  // ask for 1/freecache position on MAXFEATURE
    
    //public static final boolean FULLLOAD=true;
    //public static final boolean NOFULLLOAD=false;
    
    private static boolean verbose=true; // no comment
    private static boolean verbosecache=false; // no comment
    
    private static String fileName;
    private String NNName;
    
    private Hashtable NNT;  // to store index on a NN exemple C12Q is 33 (NN)
    private int lastNNT=0;
    
    // global for all NN
    //Hashtable Finaldocgroup, Finalgroupinv, Finalgroup;
    private static int maxused;
    private static int[] wordAtIdx;
    private static int[] wordFreq;
    private static int[] lengthcompress;
    private static long[] idxcompress;
    
    private static float[] sdf=new float[DocBag.MAXOCCINDOC];  // weighting of feature
    private static float[] alfaPn;     // puissance de alfa pour ositive Winnow compact
    private static float[] alfaNn;     // puissance de alfa pour ositive Winnow compact
    
    // for each NN
    private Hashtable[] groupinv;
    private int[] maxgroup;
    private byte[][] worduse;
    private byte[][] nnc;   /// could be optimise with a cache ...
    private int[] firstmnn;          // offset for a net
    private byte[][] compressfeature; //
    
    // random MNN
    private static RandomAccessFile cmnn;
    private static int lastfreemnn;  // next free position
    private static boolean fullload=false;
    private static int activeFeature=0;
    private static int totclear=0;
    private static int totincache=0;
   private  static int totnotincache=0;
    private static final  boolean nowrapboolean=true;
    private static Deflater def=new Deflater(9,nowrapboolean);
    private static Inflater inf=new Inflater(nowrapboolean);
    
    /** Initialise un catégoriseur associé avec un fichier contenant son réseau de neurones
     *
     * @param _fileName Fichier du réseau de neurones
     * @param _fullload true -> le réseau de neurone est chargé en mémoire
     * false -> les neurones sont chargées une à une
     * @param _MAXCLASS Nombre de classe maximum (ce nombre est déterminé à la construction du réseau de neurones)
     * @param _MAXFEATURE taille du cache des neurones (2000)
     *
     * @param _STARTLEVEL niveau de fréquence pour initialiser le nettoyage du cache (16)
     * @param _FREECACHE  fraction du cache à récupérer (1/n) (4)
     */    
    public Categorizer(
    
    String _fileName,
    boolean _fullload,
    int _MAXCLASS,
    int _MAXFEATURE,
    int _STARTLEVEL,
    int _FREECACHE
    ) {
        fileName=_fileName;
        fullload=_fullload;
        MAXCLASS=_MAXCLASS;
        MAXFEATURE=_MAXFEATURE;
        STARTLEVEL=_STARTLEVEL;
        FREECACHE=_FREECACHE;
    System.out.println("Parameters for Categorization"+
           "\nfile:"+fileName+        
           "\nfull load:"+fullload+        
           "\ntotal max class:"+MAXCLASS+        
           "\nfeature cache size:"+MAXFEATURE+        
           "\nstart level:"+STARTLEVEL+        
           "\nfree cache:"+FREECACHE);        
        load();
        try {
            cmnn = new RandomAccessFile(fileName+".cmnn", "r");
        } catch (Exception e) {
            System.err.println("IO error in open MNN file");
            e.printStackTrace();
        }
        nnc=new byte[maxused][];
        if (fullload) fullLoadCompress();  // charge toutes les features compressée
    }
    
    
    private void loadFeature(int j) {
        System.out.println("load feature:"+j);
        if (nnc[j]==null){  // to be loaded
            totnotincache++;
            try {
                byte[] feature;
                if (fullload){ // la feature compressée est déja en mémoire
                    feature=compressfeature[j];
                }
                else{// read from disk
                        cmnn.seek(idxcompress[j]);
                    feature=new byte[lengthcompress[j]];
                    cmnn.read(feature, 0, lengthcompress[j]);
                }
                inf.setInput(feature);
                byte[] byteidx=new byte[MAXCLASS];
                inf.inflate(byteidx);
                inf.reset();
                nnc[j]=byteidx;
                activeFeature++;
                
            } catch (Exception e) {
                System.err.println("IO error in loadFeature");
                e.printStackTrace();
            }
        }
        else {  // already in cache
            totincache++;
        }
    }
    
    
    private void fullLoadCompress() {
        compressfeature=new byte[maxused][];
        Timer t1=new Timer("full load of Compress MNN file");
        try {
            for(int j=0;j < maxused ;j++){ // init all net
                //System.out.println(j+" compress load "+idxcompress[j]+" : "+lengthcompress[j]);
                cmnn.seek(idxcompress[j]);
                compressfeature[j]=new byte[lengthcompress[j]];
                cmnn.read(compressfeature[j], 0, lengthcompress[j]);
            }
        } catch (Exception e) {
            System.err.println("IO error in fullLoadCompress");
            e.printStackTrace();
        }
        t1.stop();
    }
    
    
    /** affiche des informations sur l'utilisation du cache
     */    
    public static void finalstatistic() {
        System.out.println("maxused:"+maxused+"totnotincache:"+totnotincache+" totincache:"+totincache);
    }
    
    private void resetFeatureALL() {
        if (fullload&&activeFeature>MAXFEATURE) {
            for(int j=0;j < maxused ;j++){ // clear all feature
                nnc[j]=null;
                
            }
            activeFeature=0;
            totclear++;
            System.out.println("totclear:"+totclear);
        }
    }
    
   private void resetFeature(){
        if (fullload&&activeFeature>MAXFEATURE) {
            int level=STARTLEVEL;
            int cleared=0;
            while (cleared< (MAXFEATURE/FREECACHE)){
                for(int j=0;j < maxused ;j++){ // clear all feature at this freq
                    if (nnc[j]!=null&&wordFreq[j]<=level){
                        nnc[j]=null;
                        cleared++;
                    }
                }
                if (verbosecache) System.out.println("startlevel:"+level+" cleared:"+cleared);
                level*=2;
            }
            activeFeature-=cleared;
            totclear++;
            if (verbosecache) System.out.println("totclear:"+totclear);
        }
    }
    
    
   private void load() {
        try {
            FileInputStream istream = new FileInputStream(fileName);
            ObjectInputStream p = new ObjectInputStream(istream);
            NNName = (String) p.readObject();
            NNT = (Hashtable) p.readObject();
            lastNNT = p.readInt();
            wordAtIdx = (int[]) p.readObject();
            for (int i=0; i<wordAtIdx.length;i++)System.out.println(i+" "+wordAtIdx[i]);
            wordFreq = (int[]) p.readObject();
            idxcompress = (long[]) p.readObject();
            lengthcompress = (int[]) p.readObject();
            maxused = p.readInt();
            sdf = (float[]) p.readObject();
            alfaPn = (float[]) p.readObject();
            alfaNn = (float[]) p.readObject();
            groupinv = (Hashtable[]) p.readObject();
            maxgroup = (int[]) p.readObject();
            worduse = (byte[][]) p.readObject();
            lastfreemnn = p.readInt();
            firstmnn = (int[]) p.readObject();
            System.err.println("end of NNmany load:"+NNName);
            istream.close();
        } catch (Exception e) {
            System.err.println("IO error in load NN");
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    private synchronized final float[]  computeWinnowPosCompact(int[] docbag, int NNidx){
//        System.out.println("NNidx: "+NNidx);
//        System.out.println("maxgroup[NNidx]: "+maxgroup[NNidx]);
        resetFeature();
        float[] cumul=new float[maxgroup[NNidx]];
        //showVector(docbag);
        for (int i=0;i<docbag.length;i++){
//            System.out.println("feature: "+i+", value: "+docbag[i]);
            int iiii=wordAtIdx[docbag[i]/DocBag.MAXOCCINDOC];  // with indirection
 //           System.out.println("iiii: "+iiii);
            if (iiii!=notFind&&worduse[NNidx][iiii]>=0x4){// if not discard
 //               System.out.println("process iiii: "+iiii);
                loadFeature(iiii); // charge la feature
                float wsdf=sdf[docbag[i]%DocBag.MAXOCCINDOC]; // eval feature weight
                for(int j=0;j<maxgroup[NNidx];j++){
                    //System.out.println("nnc[NNidx][iiii][j]: "+nnc[NNidx][iiii][j]);
                    if (nnc[iiii][firstmnn[NNidx]+j]<0){// negative
                        //System.out.println("alfaNn[-nnc[NNidx][iiii][j]]: "+alfaNn[-nnc[NNidx][iiii][j]]);
                        
                        cumul[j]+=alfaNn[-nnc[iiii][firstmnn[NNidx]+j]]*wsdf;
                    }
                    else {// positive
                        cumul[j]+=alfaPn[nnc[iiii][firstmnn[NNidx]+j]]*wsdf;
                    }
                }
            }
        }
        float normalised=0;
        for (int i=0;i<docbag.length;i++){  // compute normalisation for this doc
            int iiii=wordAtIdx[docbag[i]/DocBag.MAXOCCINDOC];  // with indirection
            if (iiii!=notFind&&worduse[NNidx][iiii]>=0x4){// if not discard
                normalised+=sdf[docbag[i]%DocBag.MAXOCCINDOC];
            }
        }
        for(int j=0;j<maxgroup[NNidx];j++){
            cumul[j]/=normalised;
        }
//        for (int i=0;i<cumul.length;i++)System.out.println(cumul[i]);
        return cumul;
    }
    
    /** demande la classification d'un document
     * @param clue Indice sur la classification, H par exemple.
     * @param tolevel Niveau de classification demandé (1,3,4,7)
     * @param dogbag Document à classifier (sac de mots)
     * @param maxchoice Nombre de choix à proposer
     * @return liste des catégories proposées
     */    
    public String[] classifyDocument(String clue, String tolevel, int[] dogbag, int maxchoice){
        int net=((Integer)NNT.get(tolevel+"."+clue)).intValue();  // get the NN
        int[] choice=topGroupN(computeWinnowPosCompact(dogbag,net), maxchoice);
        int maxfound=0;  // look for max cat found
        for (int i=0; i<maxchoice;i++){
            if (choice[i]==notFind) break;
            else maxfound++;
        }
        if (maxfound==0) return null; // nothing ...
        String[] guess=new String[maxfound];
        for (int i=0; i<maxfound;i++){
            guess[i]=(String) groupinv[net].get(new Integer(choice[i]));
        }
        return guess;
    }
    
    /** demande la classification d'un document
     * @param clue Indice sur la classification, H par exemple.
     * @param tolevel Niveau de classification demandé (1,3,4,7)
     * @param dogbag Document à classifier (sac de mots)
     * @param maxchoice Nombre de choix à proposer
     * @return liste des choix proposés
     */    
    public Guess[] classifyDocumentAndPonderate(String clue, String tolevel, int[] dogbag, int maxchoice){
        System.out.println("guess for:"+tolevel+"."+clue);
        int net=((Integer)NNT.get(tolevel+"."+clue)).intValue();  // get the NN
        System.out.println("net:"+net);
        System.out.println("docbag.length:"+dogbag.length);
       Guess[] choice=topGuessN(computeWinnowPosCompact(dogbag,net), maxchoice);
     
       for (int i=0; i<maxchoice;i++){
        System.out.println("guess: "+i+", "+choice[i].categorie);
       }
            int maxfound=0;  // look for max cat found
        for (int i=0; i<maxchoice;i++){
            if (choice[i].catid==notFind) break;
            else maxfound++;
        }
        if (maxfound==0) return null; // nothing ...
        Guess[] guess=new Guess[maxfound];
        for (int i=0; i<maxfound;i++){
            guess[i]=new Guess((String) groupinv[net].get(new Integer(choice[i].catid)),choice[i].catid,choice[i].weight);
        }
        return guess;
    }
    
    
    
    /** Demande la classification d'un document en suivant une stratégie de parcours de l'arbre de décision.
     * (pour les tests/recherches uniquement)
     * @param dogbag Document à classifier
     * @return la catégorie choisie
     */    
    public String computeWinnowTree_1(int[] dogbag){
        int netA=((Integer)NNT.get("1.")).intValue();  // root NN
        int choiceA=topGroup(computeWinnowPosCompact(dogbag,netA));
        String levelB=(String) groupinv[netA].get(new Integer(choiceA));
        return levelB;
    }
    /** Demande la classification d'un document en suivant une stratégie de parcours de l'arbre de décision.
     * (pour les tests/recherches uniquement)
     * @param dogbag Document à classifier
     * @return la catégorie choisie
     */    
     public String computeWinnowTree_134(int[] dogbag){
        int netA=((Integer)NNT.get("1.")).intValue();  // root NN
        int choiceA=topGroup(computeWinnowPosCompact(dogbag,netA));
        String levelB=(String) groupinv[netA].get(new Integer(choiceA));
        int netB=((Integer)NNT.get("3."+levelB)).intValue();
        int choiceB=topGroup(computeWinnowPosCompact(dogbag,netB));
        String levelC=(String) groupinv[netB].get(new Integer(choiceB));
        int netC=((Integer)NNT.get("4."+levelC)).intValue();
        int choiceC=topGroup(computeWinnowPosCompact(dogbag,netC));
        String levelD=(String) groupinv[netC].get(new Integer(choiceC));
        //System.out.println("level: "+levelB+"-> "+levelC+"-> "+levelD);
        return levelD;
    }
    /** Demande la classification d'un document en suivant une stratégie de parcours de l'arbre de décision.
     * (pour les tests/recherches uniquement)
     * @param dogbag Document à classifier
     * @return la catégorie choisie
     */    
     public String computeWinnowTree_104(int[] dogbag){
        int netA=((Integer)NNT.get("1.")).intValue();  // root NN
        int choiceA=topGroup(computeWinnowPosCompact(dogbag,netA));
        String levelB=(String) groupinv[netA].get(new Integer(choiceA));
        int netB=((Integer)NNT.get("4."+levelB)).intValue();
        int choiceB=topGroup(computeWinnowPosCompact(dogbag,netB));
        String levelC=(String) groupinv[netB].get(new Integer(choiceB));
        //System.out.println("level: "+levelB+"-> "+levelC);
        return levelC;
    }
    /** Demande la classification d'un document en suivant une stratégie de parcours de l'arbre de décision.
     * (pour les tests/recherches uniquement)
     * @param dogbag Document à classifier
     * @return la catégorie choisie
     */    
     public String computeWinnowTree_034(int[] dogbag){
        int netA=((Integer)NNT.get("3.")).intValue();  // root NN
        int choiceA=topGroup(computeWinnowPosCompact(dogbag,netA));
        String levelB=(String) groupinv[netA].get(new Integer(choiceA));
        int netB=((Integer)NNT.get("4."+levelB)).intValue();
        int choiceB=topGroup(computeWinnowPosCompact(dogbag,netB));
        String levelC=(String) groupinv[netB].get(new Integer(choiceB));
        //System.out.println("level: "+levelB+"-> "+levelC);
        return levelC;
    }
    /** Demande la classification d'un document en suivant une stratégie de parcours de l'arbre de décision.
     * (pour les tests/recherches uniquement)
     * @param dogbag Document à classifier
     * @return la catégorie choisie
     */    
     public String computeWinnowTree_P34(int[] dogbag,String levelB){
        int netB=((Integer)NNT.get("3."+levelB)).intValue();
        int choiceB=topGroup(computeWinnowPosCompact(dogbag,netB));
        String levelC=(String) groupinv[netB].get(new Integer(choiceB));
        int netC=((Integer)NNT.get("4."+levelC)).intValue();
        int choiceC=topGroup(computeWinnowPosCompact(dogbag,netC));
        String levelD=(String) groupinv[netC].get(new Integer(choiceC));
        //System.out.println("level: "+levelB+"-> "+levelC+"-> "+levelD);
        return levelD;
    }
    /** Demande la classification d'un document en suivant une stratégie de parcours de l'arbre de décision.
     * (pour les tests/recherches uniquement)
     * @param dogbag Document à classifier
     * @return la catégorie choisie
     */    
     public String computeWinnowTree_P04(int[] dogbag,String levelB){
        int netB=((Integer)NNT.get("4."+levelB)).intValue();
        int choiceB=topGroup(computeWinnowPosCompact(dogbag,netB));
        String levelC=(String) groupinv[netB].get(new Integer(choiceB));
        //System.out.println("level: "+levelB+"-> "+levelC);
        return levelC;
    }
    
    private static int topGroup(float[] cumul){   // 0=group //1=score
        float max=0;
        int imax=0;
        for (int i = 0; i < cumul.length; i++) { // for each group
            if (max<cumul[i]) {max=cumul[i];imax=i;}
        }
        if (max==0) return -1;
        return imax;
    }
    
    private static int[] topGroupN(float[] cumul, int n){
        
        // cherche les groupe smax
        int[] res=new int[n];
        for (int j=0;j<n;j++){res[j]=notFind;} // init res
        for (int j=0;j<n;j++){
            float max=0;
            int imax=0;
            for (int i = 0; i < cumul.length; i++) { // for each group
                if (max<cumul[i]) {
                    int k=notFind;
                    for (k=0;k<j;k++){
                        if (res[k]==i) break; }
                    if ((k!=notFind)&&(res[k]!=i)){
                        max=cumul[i];imax=i;}
                }
            }
            if (max==0) return res; // partial result
            res[j]=imax; // new one
        }
        return res;
    }
    private static Guess[] topGuessN(float[] cumul, int n){
        
        // cherche les groupe smax
        Guess[] res=new Guess[n];
        for (int j=0;j<n;j++){res[j]=new Guess();} // init res
        for (int j=0;j<n;j++){
            float max=0;
            int imax=0;
            for (int i = 0; i < cumul.length; i++) { // for each group
                if (max<cumul[i]) {
                    int k=notFind;
                    for (k=0;k<j;k++){
                        if (res[k].catid==i) break; }
                    if ((k!=notFind)&&(res[k].catid!=i)){
                        max=cumul[i];imax=i;}
                }
            }
            if (max==0) return res; // partial result
            res[j].catid=imax; // new one
            res[j].weight=cumul[imax];
        }
        return res;
    }
    
    
}
