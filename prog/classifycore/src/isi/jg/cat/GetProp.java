package isi.jg.cat;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * toute autre utilisation est sujette à autorisation
 * <hr>
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
