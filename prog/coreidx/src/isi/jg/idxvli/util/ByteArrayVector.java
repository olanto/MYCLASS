package isi.jg.idxvli.util;

import java.util.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 *
 *
 *  Comportements d'un vecteur de byte[fixedArraySize].
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
public interface ByteArrayVector {

    /**  crée un vecteur 2^_maxSize par défaut à l'endroit indiqué par le path, (maximum=2^31), objet=byte[fixedArraySize] */
    public ByteArrayVector create(String _path, String _file, int _maxSize, int _fixedArraySize);

    /**  ouvre un vecteur à l'endroit indiqué par le path */
    public ByteArrayVector open(String _path, String _file, readWriteMode _RW);

    /**  ferme un gestionnaire de vecteurs  (et sauve les modifications*/
    public void close();

    /** mets à jour la position pos avec la valeur val */
    public void set(int pos, byte[] val);

    /** élimine le vecteur a la position pos */
    public void clear(int pos);

    /**  cherche la valeur à la position pos  */
    public byte[] get(int pos);

    /**  cherche la valeur à la position pos, la ième valeur   */
    public byte get(int pos, int i);

    /**  retourne la taille du vecteur */
    public int length();

    /**  retourne la taille du vecteur à la position pos*/
    public int length(int pos);

    /**  retourne la taille maximum des vecteurs stocké*/
    public int maxUsedlength();

    /**  imprime des statistiques */
    public void printStatistic();
}
