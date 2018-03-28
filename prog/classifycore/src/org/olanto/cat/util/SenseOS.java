/**
 * ********
 * Copyright � 2003-2018 Olanto Foundation Geneva
 *
 * This file is part of myCLASS.
 *
 * myLCASS is free software: you can redistribute it and/or modify it under the
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
package org.olanto.cat.util;

import java.util.Map;
import static org.olanto.cat.util.Messages.*;

/**
 * Gestion de l'OS et des dépendances d'installation
 */
public class SenseOS {

    public static final String WINDOWS_FAMILIES = "WINDOWS_FAMILIES";
    public static final String UNIX_FAMILIES = "UNIX_FAMILIES";
    private static String OS_TYPE = null;
    private static String MYCLASS_HOME = null;
    private static String MYCLASS_ROOT = null;
    private static Map<String, String> env;

    /**
     * permet d'effectuer le test de cette classe
     *
     * @param args pas utilisés
     */
    public static void main(String args[]) {

        msg("MYCLASS_ROOT:" + getMYCLASS_ROOT());
        msg("OS_TYPE:" + getOS_TYPE());
        msg("MYCLASS_HOME:" + getMYCLASS_HOME());
        msg("NUMBER_OF_PROCESSORS:" + getENV("NUMBER_OF_PROCESSORS"));
    }

    /**
     * @return the OS_TYPE windows ou unix
     */
    public static String getOS_TYPE() {
        if (OS_TYPE == null) {//
            String runningOS = System.getProperty("os.name");
            msg("running OS:" + runningOS);
            if (runningOS.startsWith("Window")) {
                OS_TYPE = WINDOWS_FAMILIES;
            } else {  // pour le moment tous les autres sont des Unix !!!
                OS_TYPE = UNIX_FAMILIES;
            }
            env = System.getenv();
        }
        return OS_TYPE;
    }

    /**
     * @return the OS_TYPE windows ou unix
     */
    public static String getMYCLASS_ROOT() {
        if (MYCLASS_ROOT == null) {
            if (OS_TYPE == null) {//
                getOS_TYPE();
            }
            if (OS_TYPE.equals(WINDOWS_FAMILIES)) {
                MYCLASS_ROOT = "C:/";
            } else {
                MYCLASS_ROOT = "/home/";
            }
        }
        return MYCLASS_ROOT;
    }

    /**
     * permet de forcer le type de OS
     *
     * @param aOS_TYPE the OS_TYPE to set
     */
    public static void setOS_TYPE(String aOS_TYPE) {
        OS_TYPE = aOS_TYPE;
    }

    /**
     * retourne le home de myclass
     *
     * @return the MYCLASS_HOME
     */
    public static String getMYCLASS_HOME() {
        if (MYCLASS_HOME == null) {// par encore initialisée
            if (env == null) { // init env
                getOS_TYPE();
            }
            String res = env.get("MYCLASS_HOME");
            if (res == null) { // pas défini
                MYCLASS_HOME = "! Error you Need to add a MYCLASS_HOME variable";
            } else {
                MYCLASS_HOME = res;
            }
        }
        return MYCLASS_HOME;
    }

    /**
     * retourne la valeur d'une variable de l'environnement
     *
     * @return AT_HOMEthe MYC
     */
    public static String getENV(String envName) {
        return env.get(envName);
    }

    /**
     * permet de forcer le home de myCAT
     *
     * @param aMYCAT_HOME the aMYCAT_HOME to set
     */
    public static void setMYCLASS_HOME(String HOME) {
        MYCLASS_HOME = HOME;
    }

    /**
     * @param aMYCLASS_ROOT the MYCLASS_ROOT to set
     */
    public static void setMYCLASS_ROOT(String aMYCLASS_ROOT) {
        MYCLASS_ROOT = aMYCLASS_ROOT;
    }
}
