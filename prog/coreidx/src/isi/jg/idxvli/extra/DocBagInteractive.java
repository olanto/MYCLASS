package isi.jg.idxvli.extra;

import static isi.jg.idxvli.IdxConstant.*;

import isi.jg.idxvli.*;

/**
 * Une classe pour effectuer la classification des documents.
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
public class DocBagInteractive {

    IdxStructure glue;
    static final int MAXOCCINDOC = DocBag.MAXOCCINDOC; // get the same max
    int[] bagOfWords = new int[WORD_MAX];  //this must be optimize for many users .... !!!!!!! (too big)
    int differentWords;

    /**
     * crée un sac pour une saisie depuis l'interface.
     * @param _glue indexeur associé
     */
    public DocBagInteractive(IdxStructure _glue) {
        glue = _glue;
        differentWords = 0;
        for (int i = 0; i < WORD_MAX; i++) {
            bagOfWords[i] = NOT_FOUND;
        }
    }

    /**
     * ajoute un mot au sac (uniquement pour l'indexeur)
     * @param w mot à ajouter
     */
    public void addWord(int w) {
        if (bagOfWords[w] == NOT_FOUND) {
            differentWords++;
            bagOfWords[w] = w * MAXOCCINDOC + 1;
        } else {
            if ((bagOfWords[w] % MAXOCCINDOC) < MAXOCCINDOC - 1) {
                bagOfWords[w]++;
            }
        }
    }

    /** retourne le sac de mots compacté (uniquement pour l'indexeur. Les indices ne sont pris que dans l'intervalle de lecture
     * @return le sac de mots compacté
     */
    public int[] compact() {
        int nextempty = 0;
        for (int i = 0; i < glue.lastUpdatedWord; i++) {  //
            if (bagOfWords[i] != NOT_FOUND) {
                bagOfWords[nextempty] = bagOfWords[i];
                nextempty++;
            }
        }
        int[] res = new int[nextempty];
        System.arraycopy(bagOfWords, 0, res, 0, nextempty);
        return res;
    }

    /** retourne le nombre de mots actuellement dans le sac
     * @return le nombre de mots actuellement dans le sac
     */
    public int getDifferentWords() {
        return differentWords;
    }
}
