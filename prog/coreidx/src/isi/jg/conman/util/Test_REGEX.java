/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.conman.util;

import static isi.jg.util.Messages.*;
import java.util.regex.*;

/**
 * 
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class Test_REGEX {

    private static Pattern p;
    private static Matcher m;

    public static void main(String[] args) {

        String s = "ceci     est, une source de chaleur.";

        test(s, "est");
        test(s, "ce");
        test(s, "c.");
        test(s, "r.e");
        test("Ceci est cela", "[cC]e");
        test("Ceci est cela", "[cC]*[ia]");
        test(";vertu;verte;vertus;vert;vertueux;vertueuse;", ";vertu *");
        test(";vertu;verte;vertus;vert;vertueux;vertueuse;", ";vertue *");
        split(s, " ");
        split("NEAR(bnf,java)", "[\\s\\.+~*,\\)\\(]");

    }

    public static void test(String s, String r) {

        p = Pattern.compile(r);
        m = p.matcher(s);

        msg("----- pattern:" + r + " apply on:" + s);
        while (m.find()) {
            msg("found [" + m.start() + ".." + m.end() + "] " + s.substring(m.start(), m.end()));

        }
    }

    public static void split(String s, String r) {

        p = Pattern.compile(r);

        msg("----- split pattern:" + r + " apply on:" + s);
        String[] item = p.split(s);
        for (int i = 0; i < item.length; i++) {
            msg("split " + i + " : " + item[i]);
        }
    }
}
