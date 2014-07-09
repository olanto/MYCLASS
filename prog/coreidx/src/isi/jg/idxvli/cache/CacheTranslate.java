package isi.jg.idxvli.cache;

/**
 * Gestionnaire des translations entre les WordId et les CacheID.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
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
     * retourne le CacheId associé à un WordID
     * @param wordId word
     * @return CacheId
     */
    public int getCacheId(int wordId);

    /**
     * retourne le retourne WordID associé à un CacheId
     * @param CacheId interne au cache
     * @return WordID
     */
    public int getWordId(int CacheId);

    /** efface toutes les associations */
    public void resetCache();

    /**
     * retourne la capacité du gestionnaire
     * @return la capacité du gestionnaire
     */
    public int capacity();

    /**
     * retourne le nombre d'association enregistrées
     * @return nombre d'association
     */
    public int allocate();
}
