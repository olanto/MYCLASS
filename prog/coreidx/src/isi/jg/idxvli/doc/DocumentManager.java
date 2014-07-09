package isi.jg.idxvli.doc;

import java.util.*;
import isi.jg.idxvli.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 * Comportements d'un gestionnaire de référence des documents.
 * <p>
 * Un gestionnaire de documents ce type implémente:<br>
 * - la création du gestionnaire<br>
 * - l'ouverture du gestionnaire<br>
 * - la fermeture du gestionnaire<br>
 * - la recherche d'une référence<br>
 * - la recherche d'une référence<br>
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
public interface DocumentManager {

    /** nom de la propiété invalide (les documents effacés)*/
    public static final String INVALID_NAME = "/:/:/";
    /** date invalide (les documents effacés)*/
    public static final long INVALID_DATE = -1;

    /**
     * crée un gestionnaire  de 2^maxSize, à l'endroit indiqué par le path.
     * @param implementation cette classe possède plusieurs option d'implémentation (FAST,BIG)
     * @param keepLanguage option pour mémoriser les propriétés de language du document (YES,NO)
     * @param keepCollection option pour mémoriser les propriétés de collection du document (YES,NO)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @param maxSize nbre de référence maximum mémorisées = 2^maxSize
     * @param maxLengthSizeOfName taille maximum des noms des références des documents = nbr de byte UTF8
     * @return un gestionnaire de documents
     */
    public DocumentManager create(implementationMode implementation, LanguageMode keepLanguage, CollectionMode keepCollection,
            String path, String file, int maxSize, int maxLengthSizeOfName);

    /**
     * ouvre le gestionnaire à l'endroit indiqué par le path et le file. Normalement les modes implemenation, keepLanguage,
     * keepCollection doivent être les mêmes que ceux utilisés lors de la création.
     * @param implementation cette classe possède plusieurs option d'implémentation (FAST,BIG)
     * @param keepLanguage option pour mémoriser les propriétés de language du document (YES,NO)
     * @param keepCollection option pour mémoriser les propriétés de collection du document (YES,NO)
     * @param updatingMode mode mise à jour (INCREMENTAL, DIFFERENTIAL)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @return un gestionnaire de documents
     */
    public DocumentManager open(implementationMode implementation, LanguageMode keepLanguage, CollectionMode keepCollection,
            IdxMode updatingMode, String path, String file);

    /**  ferme le gestionnaire   (et sauve les modifications*/
    public void close();

    /**
     * ajoute un document au gestionnaire retourne le numéro du document, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe déja
     * @param refDoc nom du document
     * @return le numéro attribué au document
     */
    public int put(String refDoc);

    /**
     * cherche le numéro du document, retourne EMPTY s'il n'est pas dans le dictionnaire
     * @param refDoc nom du document
     * @return le numéro du document
     */
    public int get(String refDoc);

    /**
     * cherche le nom du document associé à un identifiant. Retourne NOTINTHIS s'il n'est pas dans le dictionnaire
     * @param i numéro du document
     * @return le nom du document
     */
    public String get(int i);

    /**
     * retourne le nbr de documents mémorisés dans le gestionnaire. Tous confondus, valides et invalides
     * @return le nbr de documents mémorisés
     */
    public int getCount();

    /**
     * enregistre la date pour le document i
     * @param i numéro du document
     * @param date la date pour le document i
     */
    public void setDate(int i, long date);

    /**
     * demande  la date pour le document i
     * @param i numéro du document
     * @return la date pour le document i
     */
    public long getDate(int i);

    /**
     * enregistre la taille pour le document i
     * @param i numéro du document
     * @param size taille du document
     */
    public void setSize(int i, int size);

    /**
     * Retourne la taille pour le document i
     * @param i numéro du document
     * @return la taille pour le document i
     */
    public int getSize(int i);

    /**
     * enregistre la propriété pour le document i
     * @param i numéro du document
     * @param propertie propirété
     */
    public void setPropertie(int i, String propertie);

    /**
     * élimine la propriété pour le document i
     * @param i numéro du document
     * @param propertie propirété
     */
    public void clearPropertie(int i, String propertie);

    /**
     * Retourne la propriété pour le document i
     * @param i numéro du document
     * @param propertie propriété
     * @return la valeur de cette propiété
     */
    public boolean getPropertie(int i, String propertie);

    /**
     * Vérifie si le document est à indexer (nom différent et date différente)
     * @param fname nom du document
     * @param date date du document
     * @return true si le document est à indexer
     */
    public boolean IndexThisDocument(String fname, long date);

    /**
     * rend invalide le document i
     * @param i numéro du document à invalider
     */
    public void invalid(int i);

    /**
     * Test si le document i  est valide
     * @param i numéro du document à tester
     * @return true si le document est valide
     */
    public boolean isValid(int i);

    /**
     * propage l'invalidité des documents dans les propriétés
     */
    public void propagateInvalididy();

    /**
     * Retourne le nombre de document valides référencés
     * @return le nombre de document valides référencés
     */
    /**
     *  cherche toutes les documents possédant une propriétés
     * @param properties nom de la propriété
     * @return masque sur l'ensemble du corpus
     */
    public SetOfBits satisfyThisProperty(String properties);

    /**
     *  récupère le dictionnaire de propriétés
     * @return liste des propriétés actives
     */
    public List<String> getDictionnary();

    /**
     *  récupère le dictionnaire de propriétés ayant un certain préfix (COLECT., LANG.)
     * @param prefix préfixe des propriétés
     * @return liste des propriétés actives
     */
    public List<String> getDictionnary(String prefix);

    /**
     *  retourne le nombre de documents valides
     * @return le nombre de documents valides
     */
    public int countValid();

    /**  imprime des statistiques  du gestionnaire de documents*/
    public void printStatistic();
}
