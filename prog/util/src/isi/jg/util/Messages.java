package isi.jg.util;

import java.util.*;

/**
 * gestion des messages (console, erreur, etc)
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
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
