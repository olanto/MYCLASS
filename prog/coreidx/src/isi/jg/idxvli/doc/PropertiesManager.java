package isi.jg.idxvli.doc;

import java.util.*;
import isi.jg.idxvli.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 *  Comportements d'un gestionnaire de propiétés des documents.
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
public interface PropertiesManager {

    /**
     *  crée un gestionnaire  de 2^_maxPropertie par défaut à l'endroit indiqué par le path, (maximum=2^31), objet=bit[2^_maxDoc]
     * @param _typeDocImplementation (FAST BIG)
     * @param _path directoire
     * @param _file nom racine des fichier
     * @param _maxPropertie 2^_maxPropertie
     * @param _lengthString longueur max d'une propriété
     * @param _maxDoc maximum de documents
     * @return gestionnaire de propriétés
     */
    public PropertiesManager create(implementationMode _typeDocImplementation, String _path, String _file, int _maxPropertie, int _lengthString, int _maxDoc);

    /**
     *  ouvre le gestionnaire à l'endroit indiqué par le path
     * @param _typeDocImplementation (FAST BIG)
     * @param _path directoire
     * @param _file nom racine des fichier
     * @param _RW (lecture/lect+écriture)
     * @return gestionnaire de propriétés
     */
    public PropertiesManager open(implementationMode _typeDocImplementation, String _path, String _file, readWriteMode _RW);

    /**  ferme le gestionnaire (et sauve les modifications*/
    public void close();

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
     *  ajoute une propiété pour un document
     * @param properties nom de la propriété
     * @param doc document id
     */
    public void put(String properties, int doc);

    /**
     *  élimine une propiété pour un document
     * @param properties nom de la propriété
     * @param doc document id
     */
    public void clear(String properties, int doc);

    /**
     *  ajoute une propiété pour tous les documents avec un masque extérieur
     * @param properties nom de la propriété
     * @param corpus masque
     */
    public void put(String properties, SetOfBits corpus);

    /**
     *  vérifie si le document possède la propriété
     * @param properties nom de la propriété
     * @param doc document id
     * @return true=la propriété est active pour ce document
     */
    public boolean get(String properties, int doc);

    /**
     *  cherche toutes les valeurs d'une propriétés
     * @param properties nom de la propriété
     * @return masque sur l'ensemble du corpus
     */
    public SetOfBits get(String properties);

    /**
     *  retourne le nbr de propriétés actuellement gérées dans le gestionnaire
     * @return nbr de propriétés
     */
    public int getCount();

    /**  imprime des statistiques */
    public void printStatistic();

    /**  imprime des statistiques */
    public String getStatistic();
}
