package isi.jg.idxvli.propertie;

import java.util.*;
import isi.jg.idxvli.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 *  Comportements d'un gestionnaire de marquuer de propi�t�s des documents.
 *
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
public interface MarkerManager {

    /**
     *  retourne la langue d'un document 
     * @param txt �chantillon du document (1000 caract�res par exemple)
     * @return code de la langue (FR,EN,...)
     */
    public String getLanguage(String txt);

    /**
     *  retourne la collection d'un document 
     * @param name nom du document (url, ...)
     * @return code de la collection (INFORMATIQUE,MEDECINE,...)
     */
    public String[] getCollectionFromName(String name);
}
