package isi.jg.idxvli.word.test;

/* générique test pour les différentes implémenations remplacer le nom de l'implémentation .... */
import isi.jg.util.Timer;
import java.rmi.*;
import isi.jg.idxvli.word.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;

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
 *
 *
 */
public class TestStringRepository_Open {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://jg4/TERMDICT");

            System.out.println("access to serveur");

            if (r instanceof DictionnaryService) {
                DictionnaryService is = ((DictionnaryService) r);
                String s = is.getInformation();
                System.out.println("chaîne renvoyée = " + s);
                msg("open");
                is.open(implementationMode.XL, readWriteMode.rw, "C:/JG/VLI_RW/data/dictio", "test");
                msg("is opend now");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
