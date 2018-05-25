/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myCLASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.demo.diag1;

import org.olanto.idxvli.IdxStructure;
import org.olanto.demo.alpha.SomeConstant;
import org.olanto.idxvli.ql.QueryOperator;
import org.olanto.util.Timer;
import static org.olanto.util.Messages.*;
import java.io.*;


/**
 * *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2006
 * @version 1.1
 *
 * permet de g�n�rer une collection avec 2 dimensions
 */
public class REWRITE_DIAG1{
    
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
        
        rewritemflf(SomeConstant.ROOTDIR+"SIMPLE_CLASS/sample/rewrite/alpha_DIAG1_brut_4_40.mflf",4,40);
        
        t1.stop();
    }
    
    private static final void rewritemflf(String f,int minocc, int maxocc) {
        // no mfl no stemming no moreinfo
        //String IDX_MFL_ENCODING="ISO-8859-1";
       String IDX_MFL_ENCODING="UTF-8";
        try {
            OutputStreamWriter out= new OutputStreamWriter(new FileOutputStream(f),IDX_MFL_ENCODING);
            for(int i=0;i<id.lastRecordedDoc;i++){
                if (i%1000==0)msg("doc rewrite:"+i);
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
                                int[] and=QueryOperator.getDocforWandW(id,sk,sj).doc;
                                if (and!=null&&and.length>=minocc){ // la paire existe dans un nombre suffisant de document
                                    out.write(" "+id.getStringforW(seq[j-1])+"zz"+id.getStringforW(seq[j]));
                                }
                            }
                        }
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            error("IO error",e);
        }
    }
    
    
}


