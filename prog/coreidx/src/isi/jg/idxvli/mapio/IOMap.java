package isi.jg.idxvli.mapio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * g�re une structure de fichiers directement mapp� en m�moire.
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
 * g�re des fichiers � travers les buffers.
 *
 */
public interface IOMap {

    /**
     * retourne un gestionnaire de buffer des IO avec des valuer par d�faut.
     * @param channel canal associ� au fichier
     * @param _RW (lecture/�criture)
     * @throws java.io.IOException exections lev�es
     * @return un gestionnaire de buffer
     */
    public IOMap get(FileChannel channel, readWriteMode _RW) throws IOException;

    /**
     * retourne un gestionnaire de buffer des IO, sp�cifiant les param�tres du mapping.
     * @param _channel canal associ� au fichier
     * @param _RW (lecture/�criture)
     * @param _slice2n taille des buffers=2^_slice2n
     * @param _maxLength taille du fichier en bytes
     * @throws java.io.IOException exections lev�es
     * @return un gestionnaire de buffer
     */
    public IOMap get(FileChannel _channel, readWriteMode _RW,
            int _slice2n, long _maxLength) throws IOException;

    /**
     * ferme le gestionnaire et force les mises � jour si n�cessaire.
     * @throws java.io.IOException exections lev�es
     */
    public void close() throws IOException;

    /**
     *  lire ces bytes � cette position.
     * @param data valeurs
     * @param pos position
     * @throws java.io.IOException exections lev�es
     */
    public void read(byte[] data, long pos) throws IOException;

    /**
     * �crire ces bytes � cette position
     * @param data valeurs
     * @param pos position
     * @throws java.io.IOException exections lev�es
     */
    public void write(byte[] data, long pos) throws IOException;

    /**
     *  lire un int � cette position
     * @param pos position
     * @throws java.io.IOException exections lev�es
     * @return valeur lue
     */
    public int readInt(long pos) throws IOException;

    /**
     * �crire un int � cette position
     * @param data valeurs
     * @param pos position
     * @throws java.io.IOException exections lev�es
     */
    public void writeInt(int data, long pos) throws IOException;

    /**
     *  lire un long � cette position
     * @param pos position
     * @throws java.io.IOException exections lev�es
     * @return valeur lue
     */
    public long readLong(long pos) throws IOException;

    /**
     * �crire un long � cette position
     * @param data valeur
     * @param pos position
     * @throws java.io.IOException exections lev�es
     */
    public void writeLong(long data, long pos) throws IOException;

    /** 
     * taille maximum possible du fichier.
     * @throws java.io.IOException exections lev�es
     * @return max
     */
    public long getMaxLength() throws IOException;
}
