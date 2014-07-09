package isi.jg.idxvli.doc;

import java.util.*;
import isi.jg.idxvli.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 *  Comportements d'un gestionnaire de propi�t�s des documents.
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
public interface PropertiesManager {

    /**
     *  cr�e un gestionnaire  de 2^_maxPropertie par d�faut � l'endroit indiqu� par le path, (maximum=2^31), objet=bit[2^_maxDoc]
     * @param _typeDocImplementation (FAST BIG)
     * @param _path directoire
     * @param _file nom racine des fichier
     * @param _maxPropertie 2^_maxPropertie
     * @param _lengthString longueur max d'une propri�t�
     * @param _maxDoc maximum de documents
     * @return gestionnaire de propri�t�s
     */
    public PropertiesManager create(implementationMode _typeDocImplementation, String _path, String _file, int _maxPropertie, int _lengthString, int _maxDoc);

    /**
     *  ouvre le gestionnaire � l'endroit indiqu� par le path
     * @param _typeDocImplementation (FAST BIG)
     * @param _path directoire
     * @param _file nom racine des fichier
     * @param _RW (lecture/lect+�criture)
     * @return gestionnaire de propri�t�s
     */
    public PropertiesManager open(implementationMode _typeDocImplementation, String _path, String _file, readWriteMode _RW);

    /**  ferme le gestionnaire (et sauve les modifications*/
    public void close();

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
     *  ajoute une propi�t� pour un document
     * @param properties nom de la propri�t�
     * @param doc document id
     */
    public void put(String properties, int doc);

    /**
     *  �limine une propi�t� pour un document
     * @param properties nom de la propri�t�
     * @param doc document id
     */
    public void clear(String properties, int doc);

    /**
     *  ajoute une propi�t� pour tous les documents avec un masque ext�rieur
     * @param properties nom de la propri�t�
     * @param corpus masque
     */
    public void put(String properties, SetOfBits corpus);

    /**
     *  v�rifie si le document poss�de la propri�t�
     * @param properties nom de la propri�t�
     * @param doc document id
     * @return true=la propri�t� est active pour ce document
     */
    public boolean get(String properties, int doc);

    /**
     *  cherche toutes les valeurs d'une propri�t�s
     * @param properties nom de la propri�t�
     * @return masque sur l'ensemble du corpus
     */
    public SetOfBits get(String properties);

    /**
     *  retourne le nbr de propri�t�s actuellement g�r�es dans le gestionnaire
     * @return nbr de propri�t�s
     */
    public int getCount();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  imprime des statistiques */
    public String getStatistic();
}
