package isi.jg.idxvli.cache;

/**
 * Gestionnaire des translations entre les WordId et les CacheID.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Gestionnaire des translations entre les WordId et les CacheID.
 */
public interface CacheTranslate {

    /**
     * enregistre un nouveau WordId, retourne le CacheId
     * @param wordId word
     * @return CacheId
     */
    public int registerCacheId(int wordId);

    /**
     * retourne le CacheId associ� � un WordID
     * @param wordId word
     * @return CacheId
     */
    public int getCacheId(int wordId);

    /**
     * retourne le retourne WordID associ� � un CacheId
     * @param CacheId interne au cache
     * @return WordID
     */
    public int getWordId(int CacheId);

    /** efface toutes les associations */
    public void resetCache();

    /**
     * retourne la capacit� du gestionnaire
     * @return la capacit� du gestionnaire
     */
    public int capacity();

    /**
     * retourne le nombre d'association enregistr�es
     * @return nombre d'association
     */
    public int allocate();
}
