package isi.jg.conman.test;

import isi.jg.conman.*;
import isi.jg.util.Timer;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.conman.ContentConstant.*;

/**
 * Une classe pour initialiser les constantes.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 * Une classe pour initialiser les constantes. Cette classe doit être implémentée pour chaque application
 */
public class ConfigurationContentManager implements ContentInit {

    /** crée l'attache de cette classe.
     */
    public ConfigurationContentManager() {
    }

    /** initialisation permanante des constantes.
     * Ces constantes choisies définitivement pour toute la durée de la vie de l'index.
     */
    public void InitPermanent() {

        COMLOG_FILE = "C:/JG/VLI_RW/data/javaora/common.log";

        DETLOG_FILE = "C:/JG/VLI_RW/data/javaora/detail.log";


        DOC_MAXBIT = 18;
        DOC_MAX = (int) Math.pow(2, DOC_MAXBIT);  // recalcule

        DOC_IMPLEMENTATION = implementationMode.FAST;
        OBJ_IMPLEMENTATION = implementationMode.FAST;

        /** nbre d'object storage actif = 2^OBJ_PW2 */
        OBJ_PW2 = 0;  ///0=>1,1=>2,2=>4,3=>8,4=>16
        OBJ_NB = (int) Math.pow(2, OBJ_PW2);  ///0=>1,1=>2,2=>4,


        /** taille maximum des noms de documents */
        DOC_SIZE_NAME = 256;
    }

    /** initialisation des constantes de configuration (modifiable).
     * Ces constantes choisies définitivement pour toute la durée de la vie du processus.
     */
    public void InitConfiguration() {

        // les directoire
        String root = "c:/JG/VLI_RW/data/javaora";
        String root0 = "c:/JG/VLI_RW/data/javaora/conman0";
//        String root1="c:/JG/gigaversion/data/conman1";
//        String root2="c:/JG/gigaversion/data/conman2";
//        String root3="c:/JG/gigaversion/data/conman3";
        COMMON_ROOT = root;
        DOC_ROOT = root;
        SetObjectStoreRoot(root0, 0);
//        SetObjectStoreRoot(root1,1);
//        SetObjectStoreRoot(root2,2);
//        SetObjectStoreRoot(root3,3);

    // paramètre de fonctionnement
    }
}



