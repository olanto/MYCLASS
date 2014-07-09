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
public class TestStringRepository_Stress_Get_CUIVM {

    public static void main(String[] args) {


        try {

            System.out.println("connect to serveur");

            Remote r = Naming.lookup("rmi://cuivm/TERMDICT");

            System.out.println("access to serveur");

            if (r instanceof DictionnaryService) {
                DictionnaryService is = ((DictionnaryService) r);
                String s = is.getInformation();
                System.out.println("chaîne renvoyée = " + s);
                Timer t0 = new Timer("global get");
                Timer t1 = new Timer("get 0");
                int max=1000000000;
                int delta=10000;
                double maxintable=100000;
                for (int i = max; i > 0; i--) {
                    if (i % delta == 0) {
                        t1.stop();
                        t1 = new Timer("get:" + (i / delta + 1));
                    }
                    int rdn= (int) (Math.random() * maxintable);
                    is.get(String.valueOf(i));
                }
                t0.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
