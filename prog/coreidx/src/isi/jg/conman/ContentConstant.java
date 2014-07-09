package isi.jg.conman;

/**
 * Une classe pour déclarer des constants.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class ContentConstant extends isi.jg.idxvli.IdxConstant {

    public static final String SEPARATOR = "<::::....---->";  // une chaine improbable (sauf dans ce programme!)
    /*************************************************************************************/
    /** path de la racine des fichiers communs */
    public static String COMMON_ROOT = "C:/JG/VLI_RW/data/conman";
    /** racine des nom des fichiers  */
    public static String currentf = "rootctm";
    /* DOCUMENT *************************************************************************************/
    /** taille maximum d'un document*/
    public static final int DOC_MAXOCCLENGTH = 2000000;
    /** encodage des contenus */
    public static String CONTENT_ENCODING = "UTF-8";  //;"ISO-8859-1"
    /*************************************************************************************/
}
