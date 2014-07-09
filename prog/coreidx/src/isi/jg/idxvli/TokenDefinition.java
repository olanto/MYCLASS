package isi.jg.idxvli;

/**
 * Une classe pour définir la notion de terme.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 * Une classe pour définir la notion de terme. Cette classe doit être implémentée pour chaque application
 */
public interface TokenDefinition {

    /**
     * Cherche le symbole suivant.
     * @param a le parseur courant
     */
    public void next(DoParse a);

    /** normalise le mot.
     * @param id l'indexeur de référence
     * @param w le mot à normaliser
     * @return un mot normalisé
     */
    public String normaliseWord(IdxStructure id, String w);
}
