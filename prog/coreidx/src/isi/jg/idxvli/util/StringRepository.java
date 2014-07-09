package isi.jg.idxvli.util;

import java.util.*;

/**
 *
 *
 *  Comportements d'un gestionaire de mots.
 *  les mots sont ajoutés séquentiellement par rapport au id retourné
 *
 *  <P>exemple de code pour un test d'une implémentation de l'interface.
 *
 *  <pre>
 *  import isi.jg.idxvli.*;
 *
 * public class TestStringTable{
 *     static StringRepository o;
 *     public static void main(String[] args)   {
 *         String s;
 *         int i;
 *         o=(new StringTable()).create("C:/JG/VLI_RW/objsto",10);
 *         o=o.open("C:/JG/VLI_RW/objsto");
 *         s="voilà";  System.out.println(s+":"+o.put(s));
 *         s="un";     System.out.println(s+":"+o.put(s));
 *         s="test";   System.out.println(s+":"+o.put(s));
 *         s="très";   System.out.println(s+":"+o.put(s));
 *         s="simple"; System.out.println(s+":"+o.put(s));
 *         s="test";System.out.println(s+" est cherché:"+o.get(s));
 *         s="un";System.out.println(s+" est cherché:"+o.get(s));
 *         s="oups";System.out.println(s+" est cherché:"+o.get(s));
 *         i=2;System.out.println(i+" est cherché:"+o.get(i));
 *         i=0;System.out.println(i+" est cherché:"+o.get(i));
 *         i=99;System.out.println(i+" est cherché:"+o.get(i));
 *         o.close();
 *         System.out.println("open again ...");
 *         o=o.open("C:/JG/VLI_RW/objsto");
 *         s="simple";System.out.println(s+" est cherché:"+o.get(s));
 *         o.printStatistic();
 *         o.close();
 *    }
 * }
 * </pre>
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
 */
public interface StringRepository {

    /**  valeur retournée si on ne trouve pas un mot dans le dictionnaire */
    public static final int EMPTY = -1;
    /**  mot retourné si on ne trouve pas de mot associé à une valeur */
    public static final String NOTINTHIS = null;

    /**  crée une word table de la taille 2^_maxSize par défaut à l'endroit indiqué par le path, (maximum=2^31),
    avec des string de longueur max _lengthString*/
    public StringRepository create(String _path, String _name, int _maxSize, int _lengthString);

    /**  ouvre un gestionnaire de mots  à l'endroit indiqué par le path */
    public StringRepository open(String _path, String _name);

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public void close();

    /**  ajoute un terme au gestionnaire retourne le numéro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja
     */
    public int put(String w);

    /**  cherche le numéro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public int get(String w);

    /**  cherche le terme associé à un numéro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public String get(int i);

    /**  retourne le nbr de mots dans le dictionnaire */
    public int getCount();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne  les statistiques */
    public String getStatistic();

    /**  modifier la valeur du string, (sttention!) utilisé pour invalider une entrée du dictionnaire  */
    public void modify(int i, String newValue);
}
