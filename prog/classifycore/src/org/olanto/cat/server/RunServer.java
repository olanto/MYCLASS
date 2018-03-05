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

package org.olanto.cat.server;

import java.rmi.*;
import static org.olanto.util.Messages.*;

/**
 *
 * <p>
 *<p>
 *
 * -Xmx1000m -Djava.rmi.server.codebase="file:///c:/JG/prog/classifycore/dist/classifycore.jar"  -Djava.security.policy="c:/ JG/prog/classifycore /rmi.policy"
 */
public class RunServer {

    public static void main(String[] args) {


        try {
            System.out.println("initialisation du classifier ...");

            ClassifyService_BASIC classifyobj = new ClassifyService_BASIC();
            classifyobj.init();
            System.out.println("Enregistrement du serveur");
            String name = "rmi://localhost/C1";
            System.out.println("name:" + name);
            Naming.rebind(name, classifyobj);
            System.out.println("Server classifier manager started");
        } catch (Exception e) {
            error("Server classifier manager", e);
        }


    }
}
