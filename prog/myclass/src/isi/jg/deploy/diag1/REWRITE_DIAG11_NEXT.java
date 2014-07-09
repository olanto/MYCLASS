package isi.jg.deploy.diag1;

import isi.jg.deploy.demo.alpha.SomeConstant;
import isi.jg.idxvli.*;
import isi.jg.idxvli.ql.QueryOperator;
import isi.jg.util.Timer;
import static isi.jg.util.Messages.*;
import java.io.*;


/**
 * TOTAL 4,40 countrealnext:1002428
STOP: read and rewrite - 233000 ms
BUILD SUCCESSFUL (total time: 4 minutes 4 seconds)

TOTAL countrealnext:2753028
STOP: read and rewrite - 2393656 ms
BUILD SUCCESSFUL (total time: 39 minutes 59 seconds)
 *
 *
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2006
 * @version 1.1
 *
 * permet de g�n�rer une collection avec 2 dimensions
 */
public class REWRITE_DIAG11_NEXT{
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        
        id=new IdxStructure("QUERY",new ConfigurationForRewrite());
        id.Statistic.global();
        
        t1 =new Timer("read and rewrite");
        
        rewritemflf(SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/rewrite/alpha_DIAG1_brut_4_40.mflf"
                ,4,1000);
        
        t1.stop();
    }
    
    private static final void rewritemflf(String f,int minocc, int maxocc) {
        // no mfl no stemming no moreinfo
        //String IDX_MFL_ENCODING="ISO-8859-1";
       String IDX_MFL_ENCODING="UTF-8";
        try {
            OutputStreamWriter out= new OutputStreamWriter(new FileOutputStream(f),IDX_MFL_ENCODING);
   int countrealnext=0;
   int countnonext=0;
            for(int i=0;i<id.lastRecordedDoc;i++){
                if (i%1000==0)msg("doc rewrite:"+i+", countrealnext:"+countrealnext+", countnonext:"+countnonext);
                String title=id.getFileNameForDocument(i);
                out.write("\n#####"+title+"#####\n");
                int[] seq=id.getSeqOfDoc(i);
                if(seq.length>0){
                    int nbocc=id.getOccOfW(seq[0]);
                    if(nbocc>=minocc && nbocc*1000/id.lastRecordedDoc < maxocc){ // terme atteind le seuil
                        out.write(" "+id.getStringforW(seq[0]));
                    }
                    for (int j=1;j<seq.length;j++){
                        nbocc=id.getOccOfW(seq[j]);
                        if(nbocc>=minocc && nbocc*1000/id.lastRecordedDoc < maxocc){ // terme atteind le seuil
                            out.write(" "+id.getStringforW(seq[j]));
                            nbocc=id.getOccOfW(seq[j-1]);
                            if(nbocc>=minocc && nbocc*1000/id.lastRecordedDoc < maxocc){ // terme-1 atteind le seuil
                                String sj=id.getStringforW(seq[j]); // cherche les termes
                                String sk=id.getStringforW(seq[j-1]);
                               // int[] and=QueryOperator.getDocforWandW(id,sk,sj).doc;
                               int[] next=QueryOperator.getDocforWnextW(id,sk,sj).doc;
                                if (next!=null&&next.length>=minocc){ // la paire existe dans un nombre suffisant de document
                                    out.write(" "+id.getStringforW(seq[j-1])+"zz"+id.getStringforW(seq[j]));
                                countrealnext++;
                                }
                                else countnonext++;
                            }
                        }
                    }
                }
            }
   msg("TOTAL countrealnext:"+countrealnext+", countnonext:"+countnonext);
            out.close();
        } catch (IOException e) {
            error("IO error",e);
        }
    }
    
    
}


