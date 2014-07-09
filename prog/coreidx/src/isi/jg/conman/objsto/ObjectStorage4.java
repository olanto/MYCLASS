/*
 * ObjectStorage.java
 *
 * Created on 29. juin 2004, 14:06
 */
package isi.jg.conman.objsto;

import static isi.jg.idxvli.IdxEnum.*;

/**  Comportements d'un gestionaire d'objets.
 *
 * version 2.2
 * seul append et read sur les int sont conserv�s
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
 */
public interface ObjectStorage4 {

    /**  cr�e un ObjectStorage de taille 2^maxSize � l'endroit indiqu� par le path,
     *  ObjectStore ne prend en consid�ration que des objets de la minBigSize  exprim� (2^n) en byte,
     *  Exemple create("ici",20,32) cr�e 2^20 id avec un stockage minimum de 32 bytes au premier niveau soit 8 entiers,
     *  les objets plus petits (16) sont stock� dans une structure simplifi�e
     *
     *  modification realSize et StoredSize  //21-11-2005
     */
    public ObjectStorage4 create(implementationMode implementation, String path, int maxSize, int minBigSize);

    /**  ouvre un ObjectStorage  � l'endroit indiqu� par le path */
    public ObjectStorage4 open(implementationMode implementation, String path, readWriteMode _RW);

    /**  ferme un ObjectStorage  (et sauve les modifications*/
    public void close();

    /**  �crit des bytes a cet objet, si 0 = OK , l'identifiant est impos� de l'exterieur pour le premier
    realLength indique la longeur r�el de l'objet sans compression, l'objet doit d�j? ?tre compress� si l'on veut le compresser*/
    public int write(byte[] b, int user, int realLength);

    /**  retourne l'objet stock� completement,si null = erreur*/
    public byte[] read(int user);

    /**  retourne l'objet stock� partiellement de from � to,si null = erreur*/
    public byte[] read(int user, int from, int to);

    /**  retourne la taille stock�e de l'objet*/
    public int storedSize(int user);

    /**  retourne la taille r�el de l'objet sans compression*/
    public int realSize(int user);

    /**  lib�re un id (ceux vus par les utilisateurs)*/
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
