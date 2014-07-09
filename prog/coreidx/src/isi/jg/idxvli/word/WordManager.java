package isi.jg.idxvli.word;

import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * 
 * Comportements d'un gestionnaire de dictionnaire.
 * <p>
 * Un gestionnaire de documents ce type implémente:<br>
 * - la création du gestionnaire<br>
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
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * 
 */
public interface WordManager {

    /**
     * crée un gestionnaire  de 2^maxSize, à l'endroit indiqué par le path.
     * @param implementation cette classe possède plusieurs option d'implémentation (FAST,BIG)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @param maxSize nbre de référence maximum mémorisées = 2^maxSize
     * @param maxLengthSizeOfName taille maximum des mots = nbr de byte UTF8
     * @return un gestionnaire de mots
     */
    public WordManager create(implementationMode implementation,
            String path, String file, int maxSize, int maxLengthSizeOfName);

    /**
     * ouvre le gestionnaire à l'endroit indiqué par le path et le file. Normalement les modes implemenation, keepLanguage,
     * keepCollection doivent être les mêmes que ceux utilisés lors de la création.
     * @param implementation cette classe possède plusieurs option d'implémentation (FAST,BIG)
     * @param RW mode lecture/écriture (rw,r)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @return un gestionnaire de mots
     */
    public WordManager open(implementationMode implementation,
            readWriteMode RW, String path, String file);

    /**  ferme le gestionnaire   (et sauve les modifications*/
    public void close();

    /**
     * ajoute un mot au gestionnaire retourne le numéro du mot, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja
     * @param word mot
     * @return le numéro attribué au mot
     */
    public int put(String word);

    /**
     * cherche le numéro du mot, retourne EMPTY s'il n'est pas dans le dictionnaire
     * @param word mot
     * @return le numéro du mot
     */
    public int get(String word);

    /**
     * cherche le mot associé à un identifiant. Retourne NOTINTHIS s'il n'est pas dans le dictionnaire
     * @param i numéro du mot
     * @return le nom du mot
     */
    public String get(int i);

    /**
     * retourne le nbr de mots mémorisés dans le gestionnaire.
     * @return le nbr de mots mémorisés
     */
    public int getCount();

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic();
}
