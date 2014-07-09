package isi.jg.idxvli.util.test;

/* générique test pour les différentes implémenations remplacer le nom de l'implémentation .... */
import isi.jg.idxvli.util.*;

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
public class TestStringRepository {

    static StringRepository o;

    public static void main(String[] args) {
        String s;
        int i;
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).create("C:/JG/gigaversion/data/objsto", "test", 14, 32);
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open("C:/JG/gigaversion/data/objsto", "test");
        s = "voilà";
        System.out.println(s + ":" + o.put(s));
        s = "un";
        System.out.println(s + ":" + o.put(s));
        s = "test";
        System.out.println(s + ":" + o.put(s));
        s = "très";
        System.out.println(s + ":" + o.put(s));
        s = "simple";
        System.out.println(s + ":" + o.put(s));
        s = "test";
        System.out.println(s + " est cherché:" + o.get(s));
        s = "un";
        System.out.println(s + " est cherché:" + o.get(s));
        s = "oups";
        System.out.println(s + " est cherché:" + o.get(s));
        i = 2;
        System.out.println(i + " est cherché:" + o.get(i));
        i = 0;
        System.out.println(i + " est cherché:" + o.get(i));
        i = 99;
        System.out.println(i + " est cherché:" + o.get(i));
        o.close();
        System.out.println("open again ...");
        o = (new StringTable_HomeHash_OnDisk_DIO_Clue()).open("C:/JG/gigaversion/data/objsto", "test");
        s = "simple";
        System.out.println(s + " est cherché:" + o.get(s));
        o.printStatistic();


        o.close();

//        StringTable_HomeHash_OnDisk_DIO_Clue x=new StringTable_HomeHash_OnDisk_DIO_Clue();
//        System.out.println(x.hdocclue(1,1));

    }
}
