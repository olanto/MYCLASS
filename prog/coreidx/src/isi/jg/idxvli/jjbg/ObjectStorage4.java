/*
 * ObjectStorage.java
 *
 * Created on 29. juin 2004, 14:06
 */
package isi.jg.idxvli.jjbg;

import static isi.jg.idxvli.IdxEnum.*;

/**  Comportements d'un gestionaire d'objets.
 *
 * version 2.2
 * seul append et read sur les int sont conservés
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
public interface ObjectStorage4 {

    /**  crée un ObjectStorage de taille 2^maxSize à l'endroit indiqué par le path,
     *  ObjectStore ne prend en considération que des objets de la minBigSize  exprimé (2^n) en byte,
     *  Exemple create("ici",20,32) crée 2^20 id avec un stockage minimum de 32 bytes au premier niveau soit 8 entiers,
     *  les objets plus petits (16) sont stocké dans une structure simplifiée
     *
     *  modification realSize et StoredSize  //21-11-2005
     */
    public ObjectStorage4 create(implementationMode implementation, String path, int maxSize, int minBigSize);

    /**  ouvre un ObjectStorage  à l'endroit indiqué par le path */
    public ObjectStorage4 open(implementationMode implementation, String path, readWriteMode _RW);

    /**  ferme un ObjectStorage  (et sauve les modifications*/
    public void close();

    /**  ajoute des int a cet objet, si 0 = OK , l'identifiant est imposé de l'exterieur pour le premier append,
    l'objet sera compressé si nécessaire */
    public int append(int[] b, int user, int to);

    /**  ajoute des bytes a cet objet, si 0 = OK , l'identifiant est imposé de l'exterieur pour le premier append
    realLength indique la longeur réel de l'objet sans compression, l'objet doit déj? ?tre compressé si l'on veut le compresser*/
    public int append(byte[] b, int user, int realLength);

    /**  retourne l'objet stocké completement,si null = erreur*/
    public int[] readInt(int user);

    /**  retourne la taille stockée de l'objet*/
    public int storedSize(int user);

    /**  retourne la taille réel de l'objet sans compression*/
    public int realSize(int user);

    /**  libère un id (ceux vus par les utilisateurs)*/
    public void releaseId(int user);

    /**  imprime les statistiques */
    public void printStatistic();

    /**  imprime les statistiques */
    public void resetStatistic();

    /**  imprime les info sur un id (USER) */
    public void printNiceId(int user);

    /**  utilisation en byte de l'espace disque (sans l'overhead) */
    public long getSpace();
}
