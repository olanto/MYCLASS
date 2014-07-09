package isi.jg.txt2mfl;

import java.io.*;
import java.util.*;
import java.sql.*;



public class LoadTXT {

  
  static BufferedReader in;

  LoadTXT(String fname){
  try {
     in = new BufferedReader(new FileReader(fname)); 
     }
     catch (Exception e) {System.err.println("IO error during open txt file");}
  }
  
  public String getNext(){
     StringBuffer txt=new StringBuffer("");
  try {
     String w=in.readLine();
     while (w!=null){
         txt.append(w);
         txt.append("\n");
         w=in.readLine();
     }
     return txt.toString();
      }
     catch (Exception e) {System.err.println("IO error during read TXT file");}
    return txt.toString();
  }

}



