package isi.jg.tag2mfl;


import java.io.*;
import java.util.*;
import java.sql.*;



public class TaggedFile {
    
    
    static BufferedReader in;
    static final boolean verbose=false;
    static StringBuffer news;
    static String fulltext;
    static int idx;
    static String w="";

    TaggedFile(String fname){
        try {
            in = new BufferedReader(new FileReader(fname));
        }
        catch (Exception e) {System.err.println("IO error during TaggedFile= file");}
    }
    
    
    public void getNext(){
        news=new StringBuffer("");
        try {
                          w=in.readLine();
                           while (w!=null){
                                //System.out.println("in news:"+w);
                                news.append(w+"\n");
                                w=in.readLine();
                            }
            }
        catch (Exception e) {System.err.println("IO error during read TaggedFile :"+w);
        e.printStackTrace();}
    fulltext=news.toString();
    idx=0;
    }
    
    public String getrecord(){
            int begrec=fulltext.indexOf("<DOC>",idx);
            int endrec=fulltext.indexOf("</DOC>",idx);
            //System.out.println(" begrec:"+begrec+" endrec:"+endrec+" idx:"+idx);
            if (begrec!=-1 & endrec!=-1){
               String rec=fulltext.substring(begrec,endrec+6);
               idx=endrec+6;
               return rec;
            }
            else return null;
    }


    
}





