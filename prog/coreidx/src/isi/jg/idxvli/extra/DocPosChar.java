package isi.jg.idxvli.extra;

import java.util.*;
import java.io.*;
import static isi.jg.idxvli.util.SetOperation.*;
import static isi.jg.idxvli.ql.QueryOperator.*;
import static isi.jg.idxvli.IdxConstant.*;
import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;

import isi.jg.idxvli.IdxEnum.*;

/** Une classe pour stocker la séquence des positions des termes dans un document sous forme d'un
 * vecteur d'entier.
 *  Lors de l'indexation, il faut utiliser, un mode qui active la création
 *  des positions de terme -> docMore = true;
 *l'indexeur construit alors automatiquement les vecteurs des positions de termes.
 *après coup, on peut recharger la séquence avec getPosOfDoc dans IdxStructure
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
public class DocPosChar {

    // pour stocker la positions des termes
    private static int lastposchar = 0;
    private static int[] seqOfPosChar = new int[DOC_MAXOCCLENGTH];

    DocPosChar() {
    }

    /** initialise la création des positions (uniquement pour l'indexeur)
     */
    public static void init() {
        seqOfPosChar = new int[DOC_MAXOCCLENGTH];  // pour la création des positions
    }

    /** initialise des positions un nouveau document (uniquement pour l'indexeur)
     */
    public static void reset() {
        lastposchar = 0; // prépare un nouveau document
    }

    /**
     * ajoute une position (uniquement pour l'indexeur)
     * @param w position à ajouter
     */
    public static void addWord(int w) {
        seqOfPosChar[lastposchar] = w;
        lastposchar++;
    }

    /** retourne les positions compactées (uniquement pour l'indexeur.
     * @return les positions compactées
     */
    public static int[] compact() {
        int[] res = new int[lastposchar];
        System.arraycopy(seqOfPosChar, 0, res, 0, lastposchar);
        return res;
    }

    /** extrait le texte correspondant à la première occurence de w dans le document
     * @param d référence du document
     * @param glue indexeur de référence
     * @param w terme dont on cherche le contexte
     * @param contextSize largeur du context
     * @return texte extrait du document
     */
    public static String extractForW(int d, IdxStructure glue, String w, int contextSize) {
        String plaintext = "not found";
        int[] doc = getDocforW(glue, w, RankingMode.NO).doc; // cherche le vecteur de document
        int iw = glue.getIntForW(w);
        if (doc != null) {
            int n = getIdxOfValue(doc, doc.length, d); // cherche la position du document
            if (n != -1) {
                int[] pos = glue.getWposition(iw, n); // load position for word iw , n th documents
                plaintext = extract(d, glue, pos[0], contextSize);
            }
        }
        return plaintext;
    }

    /** extrait l'interval correspondant à la première occurence de w dans le document
     * @param d référence du document
     * @param glue indexeur de référence
     * @param w terme dont on cherche le contexte
     * @param contextSize largeur du context
     * @return texte extrait du document
     */
    public static FromTo extractIntervalForW(int d, IdxStructure glue, String w, int contextSize) {
        boolean verbose = false;
        // doit ^tre attentivement revue ... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // pour travailler directement dans le cache et non pas sur des copies !!!!!!!!!!!!!!!!
        // cela marche bien avec le txt, ou les strings (pas UTF-8 en natif)
        int byteFactor = 1;  // =2 pour UTF-16 pour utf8 on doit bricoler ...
        int[] doc = getDocforW(glue, w, RankingMode.NO).doc; // cherche le vecteur de document
        if (verbose) {
            showVector(doc);
        }
        int iw = glue.getIntForW(w);
        if (doc != null) {
            int n = getIdxOfValue(doc, doc.length, d); // cherche la position du document
            if (verbose) {
                msg("position in vector:" + n);
            }
            if (n != -1) {
                glue.indexread.lockForFull(iw);
                int[] pos = glue.getWposition(iw, n); // load position for word iw , n th documents
                if (verbose) {
                    msg("position vector:");
                }
                if (verbose) {
                    showVector(pos);
                }
                int at = pos[0];  // la première occurence
                int[] posChar = glue.getPosCharOfDoc(d);  // charge le vecteur des positions
                if (verbose) {
                    msg("posChar vector:");
                }
                if (verbose) {
                    showVector(posChar);
                }
                int from = Math.max(0, at - contextSize) * byteFactor;
                int to = Math.min(posChar.length - 2, at + contextSize) * byteFactor;   // -2 car la dernière position est à zéro bug ??
                from = posChar[from];  //en terme de caractère
                to = posChar[to]; //en terme de caractère
                glue.indexread.unlock(iw);
                return new FromTo(from, to);
            }
        }
        return null;
    }

    /** extrait le texte correspondant une position dans le document
     * @param d référence du document
     * @param glue indexeur de référence
     * @param at position à extraire
     * @param contextSize largeur du context
     * @return texte extrait du document
     */
    public static String extract(int d, IdxStructure glue, int at, int contextSize) {

        // cela marche bien avec le txt, pour le html, il faut éliminer les balises ...
        int byteFactor = 1;  // =2 pour UTF-16 pour utf8 on doit bricoler ...
        String encoding = "ISO-8859-1";
        String plaintext = "error during extraction";
        String f = glue.getFileNameForDocument(d);
        // byte Factor and encoding doivent être des propiétés du document !!!
        // actuellement on les considère fixe pour tout le corpus.
        int[] posChar = glue.getPosCharOfDoc(d);
        int from = Math.max(0, at - contextSize) * byteFactor;
        int to = Math.min(posChar.length - 2, at + contextSize) * byteFactor;   // -2 car la dernière position est à zéro bug ??
        //System.out.println("doc length:"+posChar.length+" idx from:"+from+", idx to:"+to);
        from = posChar[from];
        to = posChar[to];
        //System.out.println("pos from:"+from+", pos to:"+to);
        int lengthextract = to - from;
        // cas du txt ou autres
        try {
            RandomAccessFile refdoc = new RandomAccessFile(f, "r");
            //System.out.println("REF DOC"+f+":encoding "+encoding+":factor "+byteFactor);
            refdoc.seek(from); // position the cursor

            byte[] byteidx = new byte[lengthextract];
            refdoc.read(byteidx, 0, lengthextract);
            plaintext = new String(byteidx, encoding);
            plaintext = "<![CDATA[" + plaintext + "]]>";
            refdoc.close();

        } catch (Exception e) {
            System.err.println("IO error during open file:" + f);
            e.printStackTrace();
        }
        return plaintext;
    }
}
