package isi.jg.idxvli.util;

import java.util.*;
import java.io.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

/**
 * Implémente les opérations de base sur un vecteur de bit de taille fixe n*32.
 * <p>
 * Cette classe est 3 fois plus rapide pour les get que BitSet de Sun (pas de test ?). Les
 * opérations sont 30% plus lente (sans doute l'utilisation des int à la place des long).
 * La classe permet de récupérer le vecteur zipé.
 * <p>
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class SetOfBits implements Serializable {

    /** effectue une opération sur tous les bits (voir and, or ) */
    public static final int ALL = -1;
    /** mask pour chaque bit d'un int */
    static final int[] mask = {1, 1 << 1, 1 << 2, 1 << 3, 1 << 4, 1 << 5, 1 << 6, 1 << 7, 1 << 8, 1 << 9,
        1 << 10, 1 << 11, 1 << 12, 1 << 13, 1 << 14, 1 << 15, 1 << 16, 1 << 17, 1 << 18, 1 << 19,
        1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27, 1 << 28, 1 << 29,
        1 << 30, 1 << 31
    };
    /** nbre de bits dans un int */
    private final int INT_LENGTH = 32;
    /** taille du vecteur de bits */
    private int cardinality = 0;
    /** taille interne du vecteur */
    private int internalSize;
    /** vecteur de int représentant les bits du vecteur */
    private int[] binaryInt;

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(binaryInt);
        out.writeInt(cardinality);
        out.writeInt(internalSize);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        binaryInt = (int[]) in.readObject();
        cardinality = in.readInt();
        internalSize = in.readInt();
    }

    /**
     * crée un vecteur vide (false),
     * ne gère pas les tailles inférieur non divisible par 32
     * @param nbits taille du vecteur de bits
     */
    public SetOfBits(int nbits) {
        if (nbits % INT_LENGTH != 0) {
            error("BitArray_Basic size must be a multiple of 32");
        }
        cardinality = nbits;
        internalSize = cardinality / INT_LENGTH;
        binaryInt = new int[internalSize];
    }

    /**
     * crée un vecteur à partir des bit d'un vecteur de int
     * @param binaryInt les bits des ints deviennent ceux du vecteur de bit
     */
    public SetOfBits(int[] binaryInt) {
        cardinality = binaryInt.length;
        this.binaryInt = binaryInt;
    }

    /** crée un vecteur à partir d'un zip dont les nombre de bits était initialement nbits
     * @param bZip zip sous forme de byte
     *  @param nbits taille du vecteur de bits
     */
    public SetOfBits(byte[] bZip, int nbits) {
        cardinality = nbits;
        internalSize = cardinality / INT_LENGTH;
        binaryInt = decompress(bZip, internalSize);
    }

    /** retourne une copie du vecteur stockant les bits
     *  @return les bits
     */
    public int[] getIntStructure() {

        return binaryInt.clone();
    }

    /**
     * mets à jour la position bitIndex avec la valeur value
     * @param bitIndex position à mettre à jour
     * @param value valeur de la mise à jour
     */
    public final void set(int bitIndex, boolean value) {
        //msg("bitIntdex:"+bitIndex);
        //msg("binaryInt.length:"+binaryInt.length);
        if (value) {
            binaryInt[bitIndex / INT_LENGTH] |= mask[bitIndex % INT_LENGTH];
        } // set bit
        else {
            binaryInt[bitIndex / INT_LENGTH] &= ~mask[bitIndex % INT_LENGTH];
        }
    }

    /**
     * cherche la valeur du bit à la position bitIndex
     * @param bitIndex position recherchée
     * @return valeur de la position
     */
    public final boolean get(int bitIndex) {
        return (binaryInt[bitIndex / INT_LENGTH] & mask[bitIndex % INT_LENGTH]) == mask[bitIndex % INT_LENGTH];
    }

    /**
     * retourne le vecteur compressé
     * @return zip du vecteur de bits
     */
    public final byte[] getZip() {
        return compress(binaryInt);
    }

    /**
     * retourne la taille du vecteur
     * @return taille du vecteur de bits
     */
    public final int length() {
        return cardinality;
    }

    /**
     * convertit les nbits en nInts
     * @param nbits taille en bits
     * @return taille en représentation interne
     */
    private final int bitsToInts(int nbits) {
        if (nbits != ALL) {
            return (1 + nbits / INT_LENGTH);
        } else {
            return internalSize;
        }
    }

    /**
     * this = this AND operand
     * @param operand pour effectuer l'opération
     * @param firstbits l'opération est restreinte aux premiers bits (ALL=tous)
     */
    public final void and(SetOfBits operand, int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            binaryInt[i] &= operand.binaryInt[i];
        }
    }

    /**
     * this = this OR operand
     * @param operand pour effectuer l'opération
     * @param firstbits l'opération est restreinte aux premiers bits (ALL=tous)
     */
    public final void or(SetOfBits operand, int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            binaryInt[i] |= operand.binaryInt[i];
        }
    }

    /**
     * this = NOT (this)
     * @param firstbits l'opération est restreinte aux premiers bits (ALL=tous)
     */
    public final void not(int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            binaryInt[i] = ~binaryInt[i];
        }
    }

    /**
     * true if all false
     * @param firstbits l'opération est restreinte aux premiers bits (ALL=tous)
     * return true if all false
     */
    public final boolean allFalse(int firstbits) {
        int first = bitsToInts(firstbits);
        for (int i = 0; i < first; i++) {
            if (binaryInt[i] != 0) {
                return false;
            }
        }
        return true;
    }
}

