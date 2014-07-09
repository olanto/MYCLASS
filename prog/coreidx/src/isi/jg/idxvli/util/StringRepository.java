package isi.jg.idxvli.util;

import java.util.*;

/**
 *
 *
 *  Comportements d'un gestionaire de mots.
 *  les mots sont ajout�s s�quentiellement par rapport au id retourn�
 *
 *  <P>exemple de code pour un test d'une impl�mentation de l'interface.
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
 *         s="voil�";  System.out.println(s+":"+o.put(s));
 *         s="un";     System.out.println(s+":"+o.put(s));
 *         s="test";   System.out.println(s+":"+o.put(s));
 *         s="tr�s";   System.out.println(s+":"+o.put(s));
 *         s="simple"; System.out.println(s+":"+o.put(s));
 *         s="test";System.out.println(s+" est cherch�:"+o.get(s));
 *         s="un";System.out.println(s+" est cherch�:"+o.get(s));
 *         s="oups";System.out.println(s+" est cherch�:"+o.get(s));
 *         i=2;System.out.println(i+" est cherch�:"+o.get(i));
 *         i=0;System.out.println(i+" est cherch�:"+o.get(i));
 *         i=99;System.out.println(i+" est cherch�:"+o.get(i));
 *         o.close();
 *         System.out.println("open again ...");
 *         o=o.open("C:/JG/VLI_RW/objsto");
 *         s="simple";System.out.println(s+" est cherch�:"+o.get(s));
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
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public interface StringRepository {

    /**  valeur retourn�e si on ne trouve pas un mot dans le dictionnaire */
    public static final int EMPTY = -1;
    /**  mot retourn� si on ne trouve pas de mot associ� � une valeur */
    public static final String NOTINTHIS = null;

    /**  cr�e une word table de la taille 2^_maxSize par d�faut � l'endroit indiqu� par le path, (maximum=2^31),
    avec des string de longueur max _lengthString*/
    public StringRepository create(String _path, String _name, int _maxSize, int _lengthString);

    /**  ouvre un gestionnaire de mots  � l'endroit indiqu� par le path */
    public StringRepository open(String _path, String _name);

    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public void close();

    /**  ajoute un terme au gestionnaire retourne le num�ro du terme, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe d�ja
     */
    public int put(String w);

    /**  cherche le num�ro du terme, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public int get(String w);

    /**  cherche le terme associ� � un num�ro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public String get(int i);

    /**  retourne le nbr de mots dans le dictionnaire */
    public int getCount();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  retourne  les statistiques */
    public String getStatistic();

    /**  modifier la valeur du string, (sttention!) utilis� pour invalider une entr�e du dictionnaire  */
    public void modify(int i, String newValue);
}
