/**********
    Copyright � 2003-2018 Olanto Foundation Geneva

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

import java.util.*;

/**
 * gestion des messages (console, erreur, etc)
 *
 */
public class Messages {

    public final static void msg(String s) { // pour faciliter les io
        System.out.println(s);
    }

    public final static void msgnoln(String s) { // pour faciliter les io
        System.out.print(s);
    }

    public final static void error(String s) { // pour faciliter les io
        msg("**** application ***** " + s);
    }

    public final static void error_fatal(String s) { // pour faciliter les io
        msg("**** application ***** " + s);
        System.exit(1);
    }

    public final static void error(String s, Exception e) { // pour faciliter les io
        msg("**** Exception ***** " + s);
        e.printStackTrace();
    }

    public final static void sep(String s) { // pour faciliter les io
        for (int i = 0; i < 80; i++) {
            System.out.print(s);
        }
        System.out.println();
    }

    /** afficher le contenu du vecteur
     * @param p vecteur
     */
    public final static void showList(List<String> p) {
        if (p != null) {
            int l = p.size();
            for (int i = 0; i < l; i++) {
                msgnoln(p.get(i) + ", ");
            }
            msg(" Length=" + l);
        } else {
            msg("Is null");
        }
    }

    /** afficher le contenu du vecteur
     * @param p vecteur
     */
    public final static void showVector(int[] p) {
        if (p != null) {
            int l = p.length;
            for (int i = 0; i < l; i++) {
                msgnoln(p[i] + ",");
            }
            msg("Length=" + l);
        } else {
            msg("Is null");
        }
    }

    /** afficher le contenu du vecteur
     * @param p vecteur
     */
    public final static void showVector(float[] p) {
        if (p != null) {
            int l = p.length;
            for (int i = 0; i < l; i++) {
                msgnoln(p[i] + ",");
            }
            msg("Length=" + l);
        } else {
            msg("Is null");
        }
    }

    /** afficher le contenu du vecteur
     * @param p vecteur
     */
    public final static void showVector(String[] p) {
        if (p != null) {
            int l = p.length;
            for (int i = 0; i < l; i++) {
                msgnoln(p[i] + ",");
            }
            msg("Length=" + l);
        } else {
            msg("Is null");
        }
    }

    /** afficher le contenu du vecteur
     * @param p vecteur
     */
    public final static void showVector(byte[] p) {
        if (p != null) {
            int l = p.length;
            for (int i = 0; i < l; i++) {
                msgnoln(p[i] + ",");
            }
            msg("Length=" + l);
        } else {
            msg("Is null");
        }
    }

    public final static String cleanValue(String s) {  // éliminie les & et x pour être xml compatible
        int ix = 0;
        while ((ix = s.indexOf("&", ix)) > -1) {
            s = s.substring(0, ix) + "&amp;" + s.substring(ix + 1, s.length());
            ix += 4;
        }
        ix = 0;
        while ((ix = s.indexOf("<", ix)) > -1) {
            s = s.substring(0, ix) + "&lt;" + s.substring(ix + 1, s.length());
            ix += 3;
        }
        return s;

    }
}
