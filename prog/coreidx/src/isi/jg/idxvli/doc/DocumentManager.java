package isi.jg.idxvli.doc;

import java.util.*;
import isi.jg.idxvli.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 * Comportements d'un gestionnaire de r�f�rence des documents.
 * <p>
 * Un gestionnaire de documents ce type impl�mente:<br>
 * - la cr�ation du gestionnaire<br>
 * - l'ouverture du gestionnaire<br>
 * - la fermeture du gestionnaire<br>
 * - la recherche d'une r�f�rence<br>
 * - la recherche d'une r�f�rence<br>
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
public interface DocumentManager {

    /** nom de la propi�t� invalide (les documents effac�s)*/
    public static final String INVALID_NAME = "/:/:/";
    /** date invalide (les documents effac�s)*/
    public static final long INVALID_DATE = -1;

    /**
     * cr�e un gestionnaire  de 2^maxSize, � l'endroit indiqu� par le path.
     * @param implementation cette classe poss�de plusieurs option d'impl�mentation (FAST,BIG)
     * @param keepLanguage option pour m�moriser les propri�t�s de language du document (YES,NO)
     * @param keepCollection option pour m�moriser les propri�t�s de collection du document (YES,NO)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @param maxSize nbre de r�f�rence maximum m�moris�es = 2^maxSize
     * @param maxLengthSizeOfName taille maximum des noms des r�f�rences des documents = nbr de byte UTF8
     * @return un gestionnaire de documents
     */
    public DocumentManager create(implementationMode implementation, LanguageMode keepLanguage, CollectionMode keepCollection,
            String path, String file, int maxSize, int maxLengthSizeOfName);

    /**
     * ouvre le gestionnaire � l'endroit indiqu� par le path et le file. Normalement les modes implemenation, keepLanguage,
     * keepCollection doivent �tre les m�mes que ceux utilis�s lors de la cr�ation.
     * @param implementation cette classe poss�de plusieurs option d'impl�mentation (FAST,BIG)
     * @param keepLanguage option pour m�moriser les propri�t�s de language du document (YES,NO)
     * @param keepCollection option pour m�moriser les propri�t�s de collection du document (YES,NO)
     * @param updatingMode mode mise � jour (INCREMENTAL, DIFFERENTIAL)
     * @param path dossier contenant les fichiers
     * @param file nom racine des fichiers
     * @return un gestionnaire de documents
     */
    public DocumentManager open(implementationMode implementation, LanguageMode keepLanguage, CollectionMode keepCollection,
            IdxMode updatingMode, String path, String file);

    /**  ferme le gestionnaire   (et sauve les modifications*/
    public void close();

    /**
     * ajoute un document au gestionnaire retourne le num�ro du document, retourne EMPTY s'il y a une erreur,
     * retourne son id s'il existe d�ja
     * @param refDoc nom du document
     * @return le num�ro attribu� au document
     */
    public int put(String refDoc);

    /**
     * cherche le num�ro du document, retourne EMPTY s'il n'est pas dans le dictionnaire
     * @param refDoc nom du document
     * @return le num�ro du document
     */
    public int get(String refDoc);

    /**
     * cherche le nom du document associ� � un identifiant. Retourne NOTINTHIS s'il n'est pas dans le dictionnaire
     * @param i num�ro du document
     * @return le nom du document
     */
    public String get(int i);

    /**
     * retourne le nbr de documents m�moris�s dans le gestionnaire. Tous confondus, valides et invalides
     * @return le nbr de documents m�moris�s
     */
    public int getCount();

    /**
     * enregistre la date pour le document i
     * @param i num�ro du document
     * @param date la date pour le document i
     */
    public void setDate(int i, long date);

    /**
     * demande  la date pour le document i
     * @param i num�ro du document
     * @return la date pour le document i
     */
    public long getDate(int i);

    /**
     * enregistre la taille pour le document i
     * @param i num�ro du document
     * @param size taille du document
     */
    public void setSize(int i, int size);

    /**
     * Retourne la taille pour le document i
     * @param i num�ro du document
     * @return la taille pour le document i
     */
    public int getSize(int i);

    /**
     * enregistre la propri�t� pour le document i
     * @param i num�ro du document
     * @param propertie propir�t�
     */
    public void setPropertie(int i, String propertie);

    /**
     * �limine la propri�t� pour le document i
     * @param i num�ro du document
     * @param propertie propir�t�
     */
    public void clearPropertie(int i, String propertie);

    /**
     * Retourne la propri�t� pour le document i
     * @param i num�ro du document
     * @param propertie propri�t�
     * @return la valeur de cette propi�t�
     */
    public boolean getPropertie(int i, String propertie);

    /**
     * V�rifie si le document est � indexer (nom diff�rent et date diff�rente)
     * @param fname nom du document
     * @param date date du document
     * @return true si le document est � indexer
     */
    public boolean IndexThisDocument(String fname, long date);

    /**
     * rend invalide le document i
     * @param i num�ro du document � invalider
     */
    public void invalid(int i);

    /**
     * Test si le document i  est valide
     * @param i num�ro du document � tester
     * @return true si le document est valide
     */
    public boolean isValid(int i);

    /**
     * propage l'invalidit� des documents dans les propri�t�s
     */
    public void propagateInvalididy();

    /**
     * Retourne le nombre de document valides r�f�renc�s
     * @return le nombre de document valides r�f�renc�s
     */
    /**
     *  cherche toutes les documents poss�dant une propri�t�s
     * @param properties nom de la propri�t�
     * @return masque sur l'ensemble du corpus
     */
    public SetOfBits satisfyThisProperty(String properties);

    /**
     *  r�cup�re le dictionnaire de propri�t�s
     * @return liste des propri�t�s actives
     */
    public List<String> getDictionnary();

    /**
     *  r�cup�re le dictionnaire de propri�t�s ayant un certain pr�fix (COLECT., LANG.)
     * @param prefix pr�fixe des propri�t�s
     * @return liste des propri�t�s actives
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
