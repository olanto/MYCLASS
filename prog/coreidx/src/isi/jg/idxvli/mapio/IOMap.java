package isi.jg.idxvli.mapio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * gère une structure de fichiers directement mappé en mémoire.
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
 * gère des fichiers à travers les buffers.
 *
 */
public interface IOMap {

    /**
     * retourne un gestionnaire de buffer des IO avec des valuer par défaut.
     * @param channel canal associé au fichier
     * @param _RW (lecture/écriture)
     * @throws java.io.IOException exections levées
     * @return un gestionnaire de buffer
     */
    public IOMap get(FileChannel channel, readWriteMode _RW) throws IOException;

    /**
     * retourne un gestionnaire de buffer des IO, spécifiant les paramètres du mapping.
     * @param _channel canal associé au fichier
     * @param _RW (lecture/écriture)
     * @param _slice2n taille des buffers=2^_slice2n
     * @param _maxLength taille du fichier en bytes
     * @throws java.io.IOException exections levées
     * @return un gestionnaire de buffer
     */
    public IOMap get(FileChannel _channel, readWriteMode _RW,
            int _slice2n, long _maxLength) throws IOException;

    /**
     * ferme le gestionnaire et force les mises à jour si nécessaire.
     * @throws java.io.IOException exections levées
     */
    public void close() throws IOException;

    /**
     *  lire ces bytes à cette position.
     * @param data valeurs
     * @param pos position
     * @throws java.io.IOException exections levées
     */
    public void read(byte[] data, long pos) throws IOException;

    /**
     * écrire ces bytes à cette position
     * @param data valeurs
     * @param pos position
     * @throws java.io.IOException exections levées
     */
    public void write(byte[] data, long pos) throws IOException;

    /**
     *  lire un int à cette position
     * @param pos position
     * @throws java.io.IOException exections levées
     * @return valeur lue
     */
    public int readInt(long pos) throws IOException;

    /**
     * écrire un int à cette position
     * @param data valeurs
     * @param pos position
     * @throws java.io.IOException exections levées
     */
    public void writeInt(int data, long pos) throws IOException;

    /**
     *  lire un long à cette position
     * @param pos position
     * @throws java.io.IOException exections levées
     * @return valeur lue
     */
    public long readLong(long pos) throws IOException;

    /**
     * écrire un long à cette position
     * @param data valeur
     * @param pos position
     * @throws java.io.IOException exections levées
     */
    public void writeLong(long data, long pos) throws IOException;

    /** 
     * taille maximum possible du fichier.
     * @throws java.io.IOException exections levées
     * @return max
     */
    public long getMaxLength() throws IOException;
}
