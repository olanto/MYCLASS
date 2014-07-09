package isi.jg.txt2mfl;

import java.io.*;
import java.util.*;
import java.sql.*;


class ExportMFL{
    
    static FileWriter out ;
    
    
    public static void init(String filename) {
        
        try{
            out= new FileWriter(filename+".mfl");
        } catch (Exception e) {
            System.err.println("IO error open mfl");
            e.printStackTrace();
        }
        System.out.println("open "+filename);
    }
     
    public static void close() {
        
        try{
            out.write("#####ENDOFFILE#####\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("IO error close mfl");
            e.printStackTrace();
        }
    }
        
    public synchronized static void addFile(String filename, String s){
        try{
            out.write("#####"+filename+"#####\n");
            out.write(s);
            out.write("\n");
        } catch (Exception e) {
            System.err.println("IO error addFile");
             e.printStackTrace();
       }
        
    }
}

