package org.olanto.cat;

/**
 * Une classe pour effectuer la classification des documents
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée au groupe ISI
 * toute autre utilisation est sujette à autorisation
 *
 * 
 */


import org.olanto.cat.util.RandomizeDoc;
import org.olanto.idxvli.IdxStructure;
import java.io.*;
import java.util.*;

/* local au package */

public class NNLocalGroup{
    
    Hashtable docgroup,group,groupinv;
    int grouplength=0;
    int  maxgroup=0;
    boolean verbose=true;
    IdxStructure Indexer;
    NNBottomGroup BootGroup;
    int[] limitGroup=new int[100];  // limite groupe size pour les différentes longueurs
    
    NNLocalGroup(NNBottomGroup _BootGroup, IdxStructure _Indexer, int _grouplength,  boolean _verbose){
        limitGroup[1]=1;
        limitGroup[3]=1;
        limitGroup[4]=1;
        limitGroup[7]=10;
        limitGroup[8]=10;
        BootGroup=_BootGroup;
        Indexer=_Indexer;
        verbose=_verbose;
        grouplength=_grouplength;
        docgroup=new Hashtable();
        group=new Hashtable();
        groupinv=new Hashtable();
        maxgroup=0;
        getNextFirst();
        getNext();
        System.out.println("maxgroup:"+maxgroup);
    }
    
    int[] getGroup(int d){
        return (int[])docgroup.get(new Integer(d));
    }
    
  /*  public  boolean inThisGroup(String s){
        //System.out.println(s);
        int  [] res =((int[])docgroup.get(s));
        if (res==null) {
            return false;}
        return true;
    }
   */
    
    int getMainGroup(int d){
        int  [] res =((int[])docgroup.get(new Integer(d)));
        if (res==null) {
            //System.out.println("error in main group:"+d);
            return -1;}
        //System.out.println("get main group:"+s+"  -> "+res[0]);
        return res[0];
    }
    
    int groupSize(int d){
        //System.out.println(s);
        int  [] res =((int[])docgroup.get(new Integer(d)));
        if (res==null) {
            //System.out.println("error in main group:"+d);
            return 0;}
        return res.length;
    }
    
    
    int getgroup(String s){
        String radical=s.substring(0,grouplength);
        Integer n = (Integer) group.get(radical);
        if (n == null) {return-1;}
        return   n.intValue();
    }
    String getgroupName(int n){
        return   (String)groupinv.get(new Integer(n));
    }
    
    void add(int d){
         if (BootGroup.groupSize(d)!=0){  // test si existe un groupe
       //System.out.println(s);
        int maxclass=1;
        String radical=BootGroup.getgroupName(BootGroup.getMainGroup(d));
        int[] classdoc= new int[maxclass];
        classdoc[0]=getgroup(radical);
        //System.out.println("doc:"+d+", "+radical+","+getgroup(radical.toUpperCase()));
        int[] dg=BootGroup.getGroup(d); // all the secondary group ...
        for(int j=1;j<dg.length;j++) {
            radical=BootGroup.getgroupName(dg[j]);
            int g=getgroup(radical);
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
        }
        docgroup.put(new Integer(d),classdoc);
        //System.out.println(d+":"+maxclass);
         }
    }
    
    void addFirst(int d){
        if (BootGroup.groupSize(d)!=0){  // test si existe un groupe           
        if (BootGroup.inclass[BootGroup.getMainGroup(d)][0]>=limitGroup[grouplength]){  // test si assez de training dans le groupe
            String radical=BootGroup.getgroupName(BootGroup.getMainGroup(d));
            //System.out.println("doc:"+d+":"+BootGroup.doctype[d]+"-> "+radical);
            radical=radical.substring(0,grouplength);
            Integer n = (Integer) group.get(radical);
            if (n == null) {
                group.put(radical,new Integer(maxgroup));
                groupinv.put(new Integer(maxgroup),radical);
                if (verbose)System.out.println(radical+","+maxgroup);
                maxgroup++;
            }
        }
        }
    }
    
    
    void getNextFirst(){  // OK
        int max=0;
        for(int i=0;i<RandomizeDoc.lasttraindoc;i++){
            addFirst(RandomizeDoc.rand[i]);  // complete whit a blanck
            // if (max>200) break; else max++; // only for test
        }
    }
    
    void getNext(){
        for(int i=0;i<RandomizeDoc.lasttestdoc;i++){  // all document in the scope training+test
            add(RandomizeDoc.rand[i]);  // complete whit a blanck
            // if (max>200) break; else max++; // only for test
        }
    }
    
    void showMultiClass(){
        int[][] multi=new int[maxgroup][maxgroup];
        for (Enumeration e = docgroup.keys() ; e.hasMoreElements() ;) {
            String entry=(String)e.nextElement();
            int[] classdoc=(int[])docgroup.get(entry);
            for (int i=0;i<classdoc.length;i++){
                multi[classdoc[0]][classdoc[i]]++;
            }
        }
        for  (int i=0;i<maxgroup;i++){
            for  (int j=0;j<maxgroup;j++){
                System.out.print(multi[i][j]+",");
            }
            System.out.println();
        }
        
    }
    
    
}





