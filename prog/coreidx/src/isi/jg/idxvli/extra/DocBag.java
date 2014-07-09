package isi.jg.idxvli.extra;

import java.util.*;
import isi.jg.idxvli.*;

/**
 * Une classe pour gérer des sacs de mots représentant un document.
 *
 * L'implémentation spécifie que les sacs de mots sont représentés par un vecteur d'entier
 * chaque entier représentant le mot et le nombre d'occurences
 * le vecteur est trié par ordre de grandeur.
 *
 *
 * @author Jacques Guyot
 * copyright Jacques Guyot 2003
 * @version 1.1
 *
 * modification 9.4.2005
 * modifié pour ne plus prendre en compte l'indexeur, on compte les mots lors de la créatio du docbag
 * *
 */
public class DocBag {

    // public static final int MAXOCCINDOC=1000; // ->> max word = 2'000'000
    /** Nombre maximum d'occurence dans un document d'un terme */
    public static final int MAXOCCINDOC = 100; // ->> max word = 20'000'000

    public static final int MAXTERMINDOC = 200000;
    static Random gen = new Random(13);  // THE TEST

    private static int[] listOfWord;
    private static int[] bagOfWord;
    private static int NbWords;
    private static int differentWords;
    static IdxStructure glue;

    DocBag() {
    }

    /** initialise la création de sacs de mots (uniquement pour l'indexeur)
     */
    public static void init(IdxStructure id) {
        glue = id;
        listOfWord = new int[MAXTERMINDOC];//

        bagOfWord = new int[MAXTERMINDOC];//

        NbWords = 0;
    }

    /** initialise le sac pour un nouveau document (uniquement pour l'indexeur)
     */
    public static void reset() {
        NbWords = 0;
    }

    /**
     * ajoute un mot au sac (uniquement pour l'indexeur)
     * @param w mot à ajouter
     */
    public static void addWord(int w) {
        listOfWord[NbWords] = glue.idxtrans.getWordId(w);  // on recherche la vrai valeur du terme (si cache)

        NbWords++;
    }

    /** retourne le sac de mots compacté (uniquement pour l'indexeur.
     * @return le sac de mots compacté
     */
    public static int[] compact() {

        java.util.Arrays.sort(listOfWord, 0, NbWords);
        differentWords = -1;
        int currentWord = -1;
        for (int i = 0; i < NbWords; i++) {
            if (listOfWord[i] != currentWord) {
                differentWords++;
                bagOfWord[differentWords] = listOfWord[i] * MAXOCCINDOC + 1;
                currentWord = listOfWord[i];
            } else {
                bagOfWord[differentWords]++; // attention on fait pas de test si un terme apparait plus du maxocc

            }
        }
        differentWords++;

        int[] res = new int[differentWords];
        System.arraycopy(bagOfWord, 0, res, 0, differentWords);

        return res;
    }

    /** extrait un sous ensemble de termes d'un docbag
     * @param nW taille du sous ensemble
     * @param docbag source de l'extraction
     * @return nouveau docbag de longueur (maxnW)
     *
     */
    public static int[] getTerms(int nW, int[] docbag) {

        if (docbag.length <= nW) {
            return docbag;  // si petit document alors on le retourne immédiatement

        }
        int ix = gen.nextInt(docbag.length - nW);  //tire un emplacement dans le document

        int[] res = new int[nW];
        for (int i = 0; i < nW; i++) {
            res[i] = docbag[i + ix];
        }
        return res;
    }

    /** retourne le nombre de mots actuellement dans le sac
     * @return le nombre de mots actuellement dans le sac
     */
    public static int getDifferentWords() {
        return differentWords;
    }
}