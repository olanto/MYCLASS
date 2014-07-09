package isi.jg.onto;

import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;


/**
 * 
 * Comportements d'un gestionnaire de lexiques.
 * <p>
 * Un gestionnaire de lexiques ce type implémente:<br>
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
public interface LexicManager {

    /**
     * crée le gestionnaire avec le fichier donné
     * @param file nom du fichier
     * @return un gestionnaire de lexiques
     */
    public LexicManager create(String file, String lang, String stemName);

    /**
     * ajoute un mot au gestionnaire 
     * retourne son id s'il existe déja
     * @param word mot
     * @param id id
     */
    public void put(String word, int id);

    /**
     * cherche les concepts du mot, retourne NULL s'il n'est pas dans le dictionnaire
     * @param word mot
     * @return un vecteur de concepts
     */
    public Concepts get(String word);

    /**
     * cherche les concepts du mot, retourne NULL s'il n'est pas dans le dictionnaire
     * @param word mot (sans stemming)
     * @param stem lemme de word 
     * @return un vecteur de concepts
     */
    public Concepts get(String word, String stem);

    /**
     * cherche les mots associés à un identifiant de concepts.
     * @param i numéro du concepts
     * @return la liste des termes
     */
    public Terms get(int i);

    /**
     * cherche les mots sans stemming associés à un identifiant de concepts.
     * @param i numéro du concepts
     * @return la liste des termes
     */
    public Terms getw(int i);

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic();
}
