package isi.jg.idxvli;

/**
 * Une classe pour d�finir la notion de terme.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 * Une classe pour d�finir la notion de terme. Cette classe doit �tre impl�ment�e pour chaque application
 */
public interface TokenDefinition {

    /**
     * Cherche le symbole suivant.
     * @param a le parseur courant
     */
    public void next(DoParse a);

    /** normalise le mot.
     * @param id l'indexeur de r�f�rence
     * @param w le mot � normaliser
     * @return un mot normalis�
     */
    public String normaliseWord(IdxStructure id, String w);
}
