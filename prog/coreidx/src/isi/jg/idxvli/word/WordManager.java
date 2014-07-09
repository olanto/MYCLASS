package isi.jg.idxvli.word;

import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * 
 * Comportements d'un gestionnaire de dictionnaire.
 * <p>
 * Un gestionnaire de documents ce type impl�mente:<br>
 * - la cr�ation du gestionnaire<br>
 * - l'ouverture du gestionnaire<br>
 * - la fermeture du gestionnaire<br>
 * - la recherche d'un mot<br>
 * - la recherche d'un mot<br>
 * - ...
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
 * 
 */
public interface WordManager {

    /**
     * cr�e un gestionnaire  de 2^maxSize, � l'endroit indiqu� par le path.
     * @param implementation cette classe poss�de plusieurs option d'impl�mentation (FAST,BIG)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @param maxSize nbre de r�f�rence maximum m�moris�es = 2^maxSize
     * @param maxLengthSizeOfName taille maximum des mots = nbr de byte UTF8
     * @return un gestionnaire de mots
     */
    public WordManager create(implementationMode implementation,
            String path, String file, int maxSize, int maxLengthSizeOfName);

    /**
     * ouvre le gestionnaire � l'endroit indiqu� par le path et le file. Normalement les modes implemenation, keepLanguage,
     * keepCollection doivent �tre les m�mes que ceux utilis�s lors de la cr�ation.
     * @param implementation cette classe poss�de plusieurs option d'impl�mentation (FAST,BIG)
     * @param RW mode lecture/�criture (rw,r)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @return un gestionnaire de mots
     */
    public WordManager open(implementationMode implementation,
            readWriteMode RW, String path, String file);

    /**  ferme le gestionnaire   (et sauve les modifications*/
    public void close();

    /**
     * ajoute un mot au gestionnaire retourne le num�ro du mot, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe d�ja
     * @param word mot
     * @return le num�ro attribu� au mot
     */
    public int put(String word);

    /**
     * cherche le num�ro du mot, retourne EMPTY s'il n'est pas dans le dictionnaire
     * @param word mot
     * @return le num�ro du mot
     */
    public int get(String word);

    /**
     * cherche le mot associ� � un identifiant. Retourne NOTINTHIS s'il n'est pas dans le dictionnaire
     * @param i num�ro du mot
     * @return le nom du mot
     */
    public String get(int i);

    /**
     * retourne le nbr de mots m�moris�s dans le gestionnaire.
     * @return le nbr de mots m�moris�s
     */
    public int getCount();

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic();
}
