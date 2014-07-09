/**********
    Copyright © 2003-2014 Olanto Foundation Geneva

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
package org.olanto.cat;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * manage the properties
 */
public class GetProp {


    private static Properties prop;

    public static String getSignature(String _fileName){
               openProperties(_fileName);
        return  prop.getProperty("SIGNATURE", "NO-SIGNATURE");

    }

     /**
     * charge la configuration depuis un fichier de properties
     * @param fileName nom du fichier
     */
    public static void openProperties(String _fileName) {
        String fileName = _fileName;
        FileInputStream f = null;
        try {
            f = new FileInputStream(fileName);
        } catch (Exception e) {
            System.err.println("cannot find properties file:" + fileName);
            System.exit(0);
        }
        try {
            prop = new Properties();
            prop.loadFromXML(f);
        } catch (Exception e) {
            System.err.println("errors in properties file:" + fileName);
            System.exit(0);
        }
        //msg("properties from: " + fileName);
        prop.list(System.out);
    }

}
