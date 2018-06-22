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

package org.olanto.demo.myfirst;

import static org.olanto.util.Messages.msg;

/**
 B60G17/0162 
 * 
 The invention is a suspension for motor vehicles and the like which adjustably controls the wheel height of the vehicle relative to the chassis during turns. Sensors convert vehicle turn angle and velocity information into electrical signals for initiating pressure changes in selected expansible springs for adjustably offsetting the centrifugal force occurring during turns.
 * 
 */
public class ApplicationText2Class {
    
        public static void main(String[] args) {


        msg("init clssifier ...");
        Classify.init();
        
        String patentAbstract="The invention is a suspension for motor vehicles and the like which adjustably controls the wheel height of the vehicle relative to the chassis during turns. Sensors convert vehicle turn angle and velocity information into electrical signals for initiating pressure changes in selected expansible springs for adjustably offsetting the centrifugal force occurring during turns.\n";
        
       //  System.out.println(Classify.advise1(patentAbstract));
      System.out.println(Classify.advise3(patentAbstract));
      
      System.out.println(Classify.advise(patentAbstract, "", "1"));
      System.out.println(Classify.advise(patentAbstract, "", "3"));
      System.out.println(Classify.advise(patentAbstract, "", "4"));
      System.out.println(Classify.advise(patentAbstract, "", "7"));
      System.out.println(Classify.advise(patentAbstract, "", "1"));

      System.out.println(Classify.advise(patentAbstract, "B", "7"));
      System.out.println(Classify.advise(patentAbstract, "B60", "7"));
      System.out.println(Classify.advise(patentAbstract, "B60G", "7"));
   
        msg("end ...");
    }

    
}
