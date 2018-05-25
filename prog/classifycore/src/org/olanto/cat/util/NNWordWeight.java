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

package org.olanto.cat.util;



import org.olanto.idxvli.IdxStructure;

/** classe pour stocker la pondération du réseau pour une classe et pour un document
 *
*/



public class NNWordWeight{
    
    int[] wordOfDoc;
    float[] weightOfWord;
    int lastused,doc,group;
    
    public NNWordWeight(int maxlength,int _doc, int _group){
        wordOfDoc= new int[maxlength];
        weightOfWord=new float[maxlength];
        lastused=0;
        doc=_doc;
        group=_group;
    }
    
    public void add(int word, float weight){
        wordOfDoc[lastused]=word;
        weightOfWord[lastused]=weight;
        lastused++;
    }
    
    public void displayXML(IdxStructure glue, int maxkw){
        maxkw=Math.min(lastused,maxkw);
        for(int j=0;j<maxkw;j++){
            float maxw=weightOfWord[j];
            int maxidx=j;
            for(int i=j+1;i<lastused;i++){
                if (maxw<weightOfWord[i]){
                    maxw=weightOfWord[i];
                    maxidx=i;
                }
            }
            float temp=weightOfWord[maxidx];weightOfWord[maxidx]=weightOfWord[j];weightOfWord[j]=temp;
            int w=wordOfDoc[maxidx];wordOfDoc[maxidx]=wordOfDoc[j];wordOfDoc[j]=w;
        }
        for(int j=0;j<maxkw;j++){
            System.out.println("<MR:keywords>"+glue.getStringforW(wordOfDoc[j])+"</MR:keywords>");
            //System.out.println(doc+","+group+","+glue.getTermOfW(wordOfDoc[j])+","+((int)weightOfWord[j]));
        }
    }
    
    public void displayTXT(IdxStructure glue, int maxkw){
        maxkw=Math.min(lastused,maxkw);
        for(int j=0;j<maxkw;j++){
            float maxw=weightOfWord[j];
            int maxidx=j;
            for(int i=j+1;i<lastused;i++){
                if (maxw<weightOfWord[i]){
                    maxw=weightOfWord[i];
                    maxidx=i;
                }
            }
            float temp=weightOfWord[maxidx];weightOfWord[maxidx]=weightOfWord[j];weightOfWord[j]=temp;
            int w=wordOfDoc[maxidx];wordOfDoc[maxidx]=wordOfDoc[j];wordOfDoc[j]=w;
        }
        for(int j=0;j<maxkw;j++){
            System.out.print(","+glue.getStringforW(wordOfDoc[j]));
        }
    }
    
    public void displayTXTDetail(IdxStructure glue, int maxkw){
        maxkw=Math.min(lastused,maxkw);
        for(int j=0;j<maxkw;j++){
            float maxw=weightOfWord[j];
            int maxidx=j;
            for(int i=j+1;i<lastused;i++){
                if (maxw<weightOfWord[i]){
                    maxw=weightOfWord[i];
                    maxidx=i;
                }
            }
            float temp=weightOfWord[maxidx];weightOfWord[maxidx]=weightOfWord[j];weightOfWord[j]=temp;
            int w=wordOfDoc[maxidx];wordOfDoc[maxidx]=wordOfDoc[j];wordOfDoc[j]=w;
        }
        for(int j=0;j<maxkw;j++){
            System.out.println(glue.getStringforW(wordOfDoc[j])+
                    ", "+glue.getOccCorpusOfW(wordOfDoc[j])+
                    ", "+weightOfWord[j]
                    );
        }
    }
    
    public void display(IdxStructure glue){
        for(int i=0;i<lastused;i++){
            System.out.println(doc+","+group+","+glue.getStringforW(wordOfDoc[i])+","+((int)weightOfWord[i]));
        }
    }
    
} // end class


