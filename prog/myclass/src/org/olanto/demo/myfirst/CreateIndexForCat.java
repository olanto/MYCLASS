/**
 * ********
 * Copyright © 2003-2018 Olanto Foundation Geneva
 *
 * This file is part of myCLASS.
 *
 * myCLASS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.demo.myfirst;

import org.olanto.cat.util.SenseOS;
import org.olanto.idxvli.IdxStructure;
import org.olanto.util.Timer;

/**
 * indexing the training corpus
 */
public class CreateIndexForCat {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new IdxStructure("NEW", new ConfigurationForCat());

        id.indexdirOnlyCount(SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/alpha");// indexation du dossier 
            
            System.out.println("MAXOCCINDOC:"+org.olanto.idxvli.extra.DocBag.MAXOCCINDOC);
        

        id.flushIndexDoc();  //  vide les buffers       

        Timer t2 = new Timer("doc bag build");
        id.indexdirBuildDocBag(SenseOS.getMYCLASS_ROOT() + "MYCLASS_MODEL/sample/alpha");// indexation du dossier 
        t2.stop();
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global();
        id.close();
        t1.stop();

    }
}