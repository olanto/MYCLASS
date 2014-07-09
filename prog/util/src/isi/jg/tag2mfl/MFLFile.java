package isi.jg.tag2mfl;

import java.io.*;
import java.util.*;
import java.sql.*;



public class MFLFile{
    
    static FileWriter out;
    
    MFLFile(String filename){
           try{
               out= new FileWriter(filename);
               } catch (Exception e) {
           			System.err.println("ERROR IN init file:"+filename);
           		}
    }

    	public  void writeInDir(String filename,String news){
           try{
              out.write("\n#####"+filename+"#####\n");
              out.write(news);
                } catch (Exception e) {
           			System.err.println("ERROR IN writeInDir:"+filename);
           		}
           }

      public  void writeInDir(String filenameAndCat){
           try{
              out.write(filenameAndCat+"\n");
                } catch (Exception e) {
           			System.err.println("ERROR IN writeInDir:"+filenameAndCat);
           		}
           }
 
     	public  void close(){
           try{
              out.write("\n#####ENDOFFILE#####\n");
               out.flush();
                out.close();
                } catch (Exception e) {
           			System.err.println("ERROR IN close file:");
           		}
           }
   
    
}





