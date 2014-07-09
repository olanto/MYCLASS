package org.olanto.cat.util;



import org.olanto.idxvli.IdxStructure;
import java.io.*;
import java.util.*;



/**
 * Une classe pour d�terminer les cat�gories et les jeux d'apprentissage et de test.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limit�e au groupe ISI
 * toute autre utilisation est sujette � autorisation
 *
 *
 * <hr>
 * <b>Codification des classes</b>
 * <p>Les codes des classes sont d�termin�s par une longueur fixe, par exemple pour l'IPC7:
 * <br>NNOne.CAT_SECTION=1, A
 * <br>NNOne.CAT_CLASS=3, A01
 * <br>NNOne.CAT_SUBCLASS=4, A01B
 * <br>NNOne.CAT_MAINGROUP=7, A01B001
 *
 * <hr>
 * <b>Catalogue des documents</b>
 * <p>Les cataloques ont le format suivant:
 * <br>catalogue= {ID_DOC CODE_CAT_PRINCIPAL {CODE_CAT_SECONDAIRE} NOUVELLE_LIGNE}
 * <p> par exemple pour l'IPC7:
 * <pre>
 * WO942870119941222 A01B00116
 * WO942484119941110 A01B00118
 * EP057393919931215 A01B00118
 * EP053128719930317 A01B00100 B65G00100 A01D00900 E01H00502
 * EP053205719930317 A01B00100
 * WO922020919921126 A01B00102 A01B00100
 * EP051520219921125 A01B00114
 * </pre>
 *
 * <hr>
 * <b>Principe de fonctionnement</b>
 * <p>Le catalogue d'apprentissage d�termine enti�rement les cat�gories de travail.
 * Une cat�gorie est s�lectionn�e si et seulement si elle est cit�e comme exemple en cat�gorie
 * principale pour au moins un document d'apprentissage.
 * * <p> par exemple pour l'IPC7 en consid�rant que le catalogue ci-dessus est celui d'apprentissage, nous avons:
 * <br>A01B00100 est une cat�gorie retenue
 * <br>A01D00900 n'est pas retenue (appara�t seulement dans les classes secondaires)
 *
 * <p>En utilisant, un catalogue � un niveau donn�, il est toujours possible de travailler avec les niveaux
 * recouvert par ce niveau. Par exemple, si les cat�gories sont d�termin�es au niveau CLASS, il est possible
 * de travailler au niveau SECTION aussi.
 */

public class NNBottomGroup{
    
    Hashtable group,groupinv;
    BufferedReader in;
    int bottomgrouplength=0;
    int  maxbottomgroup=0;
    String docname;
    boolean verbose=true;
    boolean showerror=true;
    IdxStructure Indexer;
    public byte[] doctype;
    int[][] docgroup;
    int[][] inclass; // contient la r�partition des catalogues
    
    public static final byte TESTDOC=-1;
    public static final byte TRAINDOC=1;
    static final byte NOTUSEDDOC=0;
    static final String SEPARATOR="\t";
    
    /** contructeur par d�faut
     */
    public NNBottomGroup(){}
    /** construit les cat�gories du classifieur et d�termine les documents d'apprentissage et de  test
     * @param _Indexer Indexeur contenant les libell�s des documents
     * @param trainfilename catalogue d'apprentissage
     * @param testfilename catalogue de test
     * @param _bottomgrouplength niveau maximum de construction des cat�gories
     * <br>NNOne.CAT_SECTION
     * <br>NNOne.CAT_CLASS
     * <br>NNOne.CAT_SUBCLASS
     * <br>NNOne.CAT_MAINGROUP
     * @param _verbose affiche les d�tails de la construction des cat�gories
     * @param _showerror affiche les erreurs du catalogue (fichiers et cat�gories erronn�e)
     */
    public NNBottomGroup(IdxStructure _Indexer, String trainfilename, String testfilename, int _bottomgrouplength, boolean _verbose, boolean _showerror){
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
        try{
            in = new BufferedReader(new FileReader(trainfilename));
            getNextFirst();
            in.close();
        } catch (Exception e) {
            System.err.println("ERROR IN open file:"+trainfilename);
            e.printStackTrace();
        }
        try{
            in = new BufferedReader(new FileReader(trainfilename));
            getNext(TRAINDOC);
            in.close();
        } catch (Exception e) {
            System.err.println("ERROR IN open file:"+trainfilename);
            e.printStackTrace();
        }
        try{
            in = new BufferedReader(new FileReader(testfilename));
            getNext(TESTDOC);
            in.close();
        } catch (Exception e) {
            System.err.println("ERROR IN open file:"+trainfilename);
            e.printStackTrace();
        }
        repartitionTX();
    }
    
    public  void repartitionTX(){
        int tottrain=0,tottest=0;
        inclass=new int[maxbottomgroup][2];  // 0 = train 1= test
        for(int id=1;id<Indexer.lastRecordedDoc;id++){
            int g=getMainGroup(id);
            if (g!=-1){
                if (doctype[id]==TRAINDOC){
                    inclass[g][0]++;
                    tottrain++;
                } else {
                    inclass[g][0]++;
                    tottest++;
                }
            }
        }
        if (verbose){
            System.out.println("REPARTITION IN CATALOG");
            System.out.println("======================");
            System.out.println("total train:, "+tottrain);
            System.out.println("total test:, "+tottest);
            System.out.println("\ngroup,train,test");
            for(int i=0;i<maxbottomgroup;i++){
                System.out.println(getgroupName(i)+","+inclass[i][0]+","+inclass[i][1]);
            }
            System.out.println();
        }
    }
    
    
    public int[] getGroup(int d){
        return docgroup[d];
    }
    
    /** nombre de cat�gories
     * @return nbre total de cat�gorie
     */
    public int getMaxGroup(){
        return maxbottomgroup;
    }
    
    /** cherche les groupes principaux de tous les documents
     * @return vecteur des groupes
     */
    public int[] getCorpusMainGroup(){
        int[] docgrp=new int[Indexer.lastRecordedDoc];
        for (int i=1;i<Indexer.lastRecordedDoc;i++)
            docgrp[i]=getMainGroup(i);
        return docgrp;
    }
    
    public boolean inThisGroup(int d, String prefix, byte type){
        //System.out.println(s);
        if (doctype[d]!=type) {
            return false;}
        //System.out.println(Indexer.getFileNameForDocument(d)+" MainGroup:"+getMainGroup(d));
        //System.out.println("MainGroup:"+getgroupName(getMainGroup(d)));
        if (prefix.equals(""))
            return true; // no prefix
        else return (getgroupName(getMainGroup(d))).startsWith(prefix);
    }
    
    
    /** retourne le num�ro de la cat�gorie principale du document
     * @param d num�ro du document dont on recherche la cat�gorie
     * @return num�ro cat�gorie du document
     */
    public int getMainGroup(int d){
        if (docgroup[d]==null) {
            //System.out.println("error in getMainGroup:"+d);
            return -1;}
        //System.out.println("get main group:"+s+"  -> "+res[0]);
        return docgroup[d][0];
    }
    
    /** retourne le nom de la cat�gorie principale du document
     * @param d num�ro du document dont on recherche la cat�gorie
     * @return nom cat�gorie du document
     */
    public String getMainGroupName(int d){
        return getgroupName(getMainGroup(d));
    }
    
    /** retourne le nom de la cat�gorie principale du document (pour un ensemble
     * @param docs num�ro des documents dont on recherche la cat�gorie
     * @return ensemble des noms des cat�gorie des documents
     */
    public String[] getMainGroupName(int[] docs){
        String[] res=new String[docs.length];
        for (int i=0;i<docs.length;i++)res[i]=getgroupName(getMainGroup(docs[i]));
        return res;
    }
    /** garde dans le catalogue que les documents ayant une seule classe (pour les tests)
     */
    public void keepOnlyMonoClass(){
        for(int i=0;i<Indexer.lastRecordedDoc;i++){
            if (groupSize(i)>1) docgroup[i]=null; //�limine les documents ayant plusieurs classes pour l'apprentissage'
        }
    }
    
    public int groupSize(int d){
        //System.out.println(s);
        if (docgroup[d]==null) {
            //System.out.println("error in groupSize:"+d);
            return 0;}
        return docgroup[d].length;
    }
    
    /** retourne le num�ro d'une cat�gorie
     * @param s nom dont on recherche le num�ro
     * @return num�ro d'un groupe
     */
    
    public int getgroup(String s){
        if (s.length()<bottomgrouplength) {     // normal 7 maingroup
            if (showerror) System.out.println("("+s+") bad class:"+docname);
            return-1;}
        String radical=s.substring(0,bottomgrouplength);
        Integer n = (Integer) group.get(radical);
        if (n == null) {return-1;}
        return   n.intValue();
    }
    /** retourne le nom d'une cat�gorie
     * @param n le num�ro de la cat�gorie
     * @return le nom de la ct�gorie
     */
    
    
    public String getgroupName(int n){
        return   (String)groupinv.get(new Integer(n));
    }
    
    void add(String s,byte type){
        int fn=s.indexOf(SEPARATOR,0);
        docname=s.substring(0,fn);
        int numdoc=Indexer.getIntForDocument(docname);
        //System.out.println(docname+SEPARATOR+numdoc);
        if (numdoc!=-1){// test if in corpus
            int mcs=fn+1;
            int mce=s.indexOf(SEPARATOR,fn+1);
            String radical=s.substring(mcs,mce).toUpperCase();
            int[] classdoc= new int[1];
            classdoc[0]=getgroup(radical.toUpperCase());
            if (classdoc[0]==-1){
                if(showerror) System.out.println("no bottomgroup in train set for "+docname+" MainGroup:"+radical);
            } else{ // doc have a maingroup in train set
                doctype[numdoc]=type;  //mark this docucment with the type
                int maxclass=1;
                //System.out.println(radical+","+getgroup(radical.toUpperCase()));
                int beg=mce+1,end;
                while (true) {
                    end=s.indexOf(SEPARATOR,beg);
                    if (end==-1) break;
                    int g=getgroup(s.substring(beg,end));
                    boolean find=false;
                    for (int i=0; i<maxclass; i++){
                        if (classdoc[i]==g){find=true;break;}
                    }
                    if (!find&&g!=-1){
                        int[] it = new int[maxclass+1];
                        System.arraycopy(classdoc, 0, it, 0, maxclass);
                        classdoc = it;
                        classdoc[maxclass]=g;
                        maxclass++;
                    }
                    beg=end+1;
                }
                docgroup[numdoc]=classdoc;
                //System.out.println(docname+":"+numdoc+":"+maxclass);
            }
        }
        else {
            System.out.println("Error no doc for :"+docname);
        }
    }
    
    void addFirst(String s){
        //System.out.println("s:"+s+"/");
        int fn=s.indexOf(SEPARATOR,0);
        docname=s.substring(0,fn);
        if (Indexer.getIntForDocument(docname)!=-1){// test if in corpus
            int mcs=fn+1;
            int mce=s.indexOf(SEPARATOR,fn+1);
            String radical=s.substring(mcs,mce).toUpperCase();
try{            radical=radical.substring(0,bottomgrouplength);}
catch(Exception e){
    System.out.println("error in class:"+s+"/");
}
            Integer n = (Integer) group.get(radical);
            if (n == null) {
                group.put(radical,new Integer(maxbottomgroup));
                groupinv.put(new Integer(maxbottomgroup),radical);
                //if (verbose)System.out.println(radical+","+maxbottomgroup);
                maxbottomgroup++;
            }
        }
    }
    
    
    public void getNextFirst(){  // OK
        int max=0;
        try {
            String w=in.readLine();
            while (w!=null){
                addFirst(w+SEPARATOR);  // complete whit a blanck
                w=in.readLine();
                // if (max>200) break; else max++; // only for test
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    
    void getNext(byte type){
        int max=0;
        try {
            String w=in.readLine();
            while (w!=null){
                add(w+SEPARATOR,type);
                w=in.readLine();
                // if (max>200) break; else max++; // only for test
            }
        } catch (Exception e) {e.printStackTrace();}
        System.out.println("maxbottomgroup:"+maxbottomgroup);
    }
    
    public  void showMultiClass(){
        int[][] multi=new int[maxbottomgroup][maxbottomgroup];
        System.out.println("\ntotal doc:"+docgroup.length);
        System.out.println();
        for (int i=0;i<docgroup.length;i++) {
            if (groupSize(i)!=0){
                if (groupSize(i)==1){ // un seul groupe
                    multi[docgroup[i][0]][docgroup[i][0]]++;
                } else{
                    for (int j=0;j<docgroup[i].length-1;j++) {
                        for (int k=j+1;k<docgroup[i].length;k++){
                            multi[docgroup[i][j]][docgroup[i][k]]++;
                            multi[docgroup[i][k]][docgroup[i][j]]++;
                        }
                    }
                }
            }
        }
        System.out.print("MULTI,");
        for  (int i=0;i<maxbottomgroup;i++){
            System.out.print(getgroupName(i)+",");
        }
        System.out.println();
        for  (int i=0;i<maxbottomgroup;i++){
            System.out.print(getgroupName(i)+",");
            for  (int j=0;j<maxbottomgroup;j++){
                System.out.print(multi[i][j]+",");
            }
            System.out.println();
        }
        
    }
    
    
}





