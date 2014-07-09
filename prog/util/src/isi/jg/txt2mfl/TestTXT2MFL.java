package isi.jg.txt2mfl;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class TestTXT2MFL{
   
   static int idSeq=0;

   public static void main(String[] args)   {

     String FN="C:/JG/uit/doc";
     ExportMFL.init("C:/JG/uit/uit");
     indexdir(FN);
     ExportMFL.close();

   }

  public static void indexdir(String path){
     File f=new File(path);
     if (f.isFile()){
       if (path.endsWith(".txt")) indexdoc(path);
     }
     else {
       String[] lf=f.list();
       int ilf=Array.getLength(lf);
       for (int i=0; i<ilf; i++)
          indexdir(path+"/"+lf[i]);
     }
   }

  public static void indexdoc(String f)   {
     try {
          System.out.println("file:"+f+" from idseq:"+idSeq);
           LoadTXT LF=new LoadTXT(f);

     String txt=LF.getNext();
     
     while (!txt.equals("")){

        //System.out.println(idSeq);
        ExportMFL.addFile(f.substring(14,f.length()),txt);
        idSeq++;   
        txt=LF.getNext();
       }
    } //try
	catch (Exception e) {System.err.println("IO error");
	                     System.out.println(e.toString());}
  }


}



	

