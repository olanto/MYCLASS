package isi.jg.onto;

import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;


/**
 * 
 * Comportements d'un gestionnaire de lexiques.
 * <p>
 * Un gestionnaire de lexiques ce type impl�mente:<br>
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
public interface LexicManager {

    /**
     * cr�e le gestionnaire avec le fichier donn�
     * @param file nom du fichier
     * @return un gestionnaire de lexiques
     */
    public LexicManager create(String file, String lang, String stemName);

    /**
     * ajoute un mot au gestionnaire 
     * retourne son id s'il existe d�ja
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
     * cherche les mots associ�s � un identifiant de concepts.
     * @param i num�ro du concepts
     * @return la liste des termes
     */
    public Terms get(int i);

    /**
     * cherche les mots sans stemming associ�s � un identifiant de concepts.
     * @param i num�ro du concepts
     * @return la liste des termes
     */
    public Terms getw(int i);

    /**  imprime des statistiques  du gestionnaire de mots*/
    public void printStatistic();
}
