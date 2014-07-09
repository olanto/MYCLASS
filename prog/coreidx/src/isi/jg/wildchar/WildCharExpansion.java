package isi.jg.wildchar;

import isi.jg.util.Timer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static isi.jg.util.Messages.*;

/**
 * Une classe pour définir les token (une définition alpha numérique).
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *<p>
 *
 */

public class WildCharExpansion {

    static String target;

    public static void main(String[] args) {
        target = readFromFile("C:/SIMPLE/dict/ENorganisation.dic");
        //target=readFromFile("C:/SIMPLE/dict/fr.dic");
        //expand("\n.*col.*\n");
        //expand("\ncol.*\n");
        expand("global*");
        expand("*inge*");
        expand("*x*y*z");
        expand("att*");
    }

    public static void init(String fileName) {
        target = readFromFile(fileName);
    }

    public static Vector<String> Getxpand(String regex) {
        regex = regex.replaceAll("\\*", ".*");
        regex = "\n" + regex + "\n";

        Pattern pattern = Pattern.compile(regex);
        // Get a Matcher based on the target string. 
        Matcher matcher = pattern.matcher(target);

        Vector<String> result = new Vector<String>();
        while (matcher.find()) {
            String match = matcher.group();
            result.add(match.substring(1, match.length() - 1));
        }
        return result;
    }

    public static void expand(String regex) {
        // Compile the regex. 
        System.out.println("---------- look for :" + regex);
        regex = regex.replaceAll("\\*", ".*");
        regex = "\n" + regex + "\n";
        //System.out.println("---------- look for :"+regex);
        Timer t1 = new Timer("---------- look for :" + regex);

        Pattern pattern = Pattern.compile(regex);
        // Get a Matcher based on the target string. 
        Matcher matcher = pattern.matcher(target);

        // Find all the matches. 
        int count = 0;
        while (matcher.find()) {
            count++;
            System.out.print(matcher.group().substring(1));
//            System.out.println("Found a match: " + matcher.group());
//            System.out.println("Start position: " + matcher.start());
//            System.out.println("End position: " + matcher.end());
        }
        System.out.println("found:" + count);
        t1.stop();
    }

    private static String readFromFile(String fname) {
        StringBuffer b = new StringBuffer("\n");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "ISO-8859-1");

            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                b.append(w + "\n");
                w = in.readLine();
            }
        } catch (Exception e) {
            error("readFromFile", e);
        }
        return b.toString();
    }
}
