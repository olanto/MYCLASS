/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myLCASS is free software: you can redistribute it and/or modify
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

package org.olanto.cat.inbox;

import org.olanto.cat.mnn.Categorizer;
import org.olanto.cat.mnn.Guess;
import org.olanto.idxvli.DoParse;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;



/**
 *
 * Test du catégoriseur
 */

class ClassifierRequest{
    int[] docbag;
    int nbchoice;
    String language;
    
    ClassifierRequest(int[] _docbag, int _nbchoice, String _language){
        docbag=_docbag;
        nbchoice=_nbchoice;
        language=_language;
    }
}  
    
    public class Classify{
        
        static Categorizer MM;
        
        private static IdxStructure id;
        private static Timer t1=new Timer("global time");
        
        
        public Classify(){}
        
        /**
         * application de test
         * @param args sans
         */
        public static String advise(String p)   {
            
            init();
             ClassifierRequest rq=parse(p,"3","EN");
            return guess(rq,"","8");
            
        }
        
        
        
        
        public static ClassifierRequest parse(String s, String nbguess, String lang){
            System.out.println("ok Classifier.parse");
            DoParse a = new DoParse(s, id.dontIndexThis);
            int[] requestDB=a.getDocBag(id); // get the docBag od the request
            ClassifierRequest rq=new ClassifierRequest(requestDB,Integer.parseInt(nbguess),lang);
            return rq;
        }
        
        public static String guess(ClassifierRequest rq,String from, String to){
            String res="\nok Classifier.guess";
            Guess[] guess=MM.classifyDocumentAndPonderate(from, to, rq.docbag, rq.nbchoice);
            if (guess==null){
                res+="\nNo guess !";
            } else{
                for (int i=0;i<guess.length;i++){
                    res+="\n  choice:"+(i+1)+", "+guess[i].categorie+", "+guess[i].weight;
                }
            }
            return res;
        }
        
        
       public static void reset(){  // init class
           id=null;
       }
         
        
        
        public static void init(){  // init class
            if (id==null){
                id=new IdxStructure("QUERY",new ConfigurationCat());
                // charge l'index (si il n'existe pas alors il est vide)
                id.loadIndexDoc();
                id.Statistic.global();
                String nnfile=COMMON_ROOT+"CLASSIFIER.mnn";
                MM=new Categorizer(nnfile,
                        false, // chargement du réseau complet
                        100, // maxclass
                        2000,  // cache size
                        16,    // start level
                        4      // free cache
                        );
            }
            
        }
        
    }