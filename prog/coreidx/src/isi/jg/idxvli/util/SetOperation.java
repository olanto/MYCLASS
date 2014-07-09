package isi.jg.idxvli.util;

import java.io.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.BytesAndFiles.*;

/**
 * Classe gérant des opérations logiques basiques.
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
 * Classe gérant les accès de bas niveau dans les fichiers à accès aléatoire
 */
public class SetOperation {

    /**
     * filtre un vecteur avec un bitset
     * @return vecteur de documents filtré
     * @param filter (true=on garde les true, false on garde les false)
     * @param res un vecteur de documents
     * @param sob une propriété
     */
    public final static int[] filtering(int[] res, SetOfBits sob, boolean filter) {
        if (res == null) {
            //msg ("filtering: res is empty");
            return res;  // rien à filtrer
        }
        if (sob == null) {
            //msg ("filtering: sob is null");
            return res;  // pas de filtrage tout passe
        }
        int[] DD = new int[res.length];
        int last = 0;
        for (int i = 0; i < res.length; i++) {
            //msg("filter doc:"+res[i]+"="+sob.get(res[i]));
            if (sob.get(res[i]) == filter) { // il possède la propriété
                DD[last] = res[i];
                last++;
            }
        }
        if (last == DD.length) {
            return DD;
        } // ils possèdent tous la propriété
        return copyVector(last, DD); // ajuste la taille
    }

    /** calcul l'intersection de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param r1 vecteur1
     * @param r2 vecteur2
     * @return intersection de 1 et 2
     */
    public static int[] and(int[] r1, int[] r2) { // migrate
        if (r2 == null || r1 == null) {
            return new int[0];
        }
        int id = 0;
        int il1 = r1.length;
        int il2 = r2.length;
        int wc1 = 0;
        int wc2 = 0;
        int doc[] = new int[Math.min(il1, il2)];
        while (true) { // merge sort  r1 and r2 must be ordered !!!!!
            if (wc1 >= il1) {
                break;
            }
            if (wc2 >= il2) {
                break;
            }
            // System.out.println(wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
            if (r1[wc1] == r2[wc2]) { // and ok
                doc[id] = r1[wc1];// the first operand stay the reference for occurrences
                wc1++;
                wc2++;
                id++;
            } else if (r1[wc1] < r2[wc2]) {
                wc1++;
            } else {
                wc2++;
            }
        }
        return copyVector(id, doc);
    }

    /** calcul l'union de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param r1 vecteur1
     * @param r2 vecteur2
     * @return intersection de 1 et 2
     */
    public static int[] or(int[] r1, int[] r2) { // migrate
        if (r2 == null && r1 == null) {
            return new int[0];
        }
        if (r2 == null) {
            return r1;
        }
        if (r1 == null) {
            return r2;
        }
        int id = 0;
        int il1 = r1.length;
        int il2 = r2.length;
        int wc1 = 0;
        int wc2 = 0;
        int doc[] = new int[il1 + il2];
        while (true) { // merge sort  r1 and r2 must be ordered !!!!!
            if (wc1 >= il1) {
                break;
            }
            if (wc2 >= il2) {
                break;
            }
            // System.out.println(wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
            if (r1[wc1] == r2[wc2]) { // and ok
                doc[id] = r1[wc1];// the first operand stay the reference for occurrences
                wc1++;
                wc2++;
                id++;
            } else if (r1[wc1] < r2[wc2]) {
                doc[id] = r1[wc1];
                wc1++;
                id++;
            } else {
                doc[id] = r2[wc2];
                wc2++;
                id++;
            }
        }
        if (wc1 < il1) {  // copie la fin de r1
            for (int i = wc1; i < il1; i++) {
                doc[id] = r1[i];
                id++;
            }
        }
        if (wc2 < il2) {  // copie la fin de r2
            for (int i = wc2; i < il2; i++) {
                doc[id] = r2[i];
                id++;
            }
        }
        return copyVector(id, doc);
    }

    /** calcul la différence de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param r1 vecteur1
     * @param r2 vecteur2
     * @return intersection de 1 et 2
     */
    public static int[] minus(int[] r1, int[] r2) { // migrate
        if (r2 == null && r1 == null) {
            return new int[0];
        }
        if (r2 == null) {
            return r1;
        }
        if (r1 == null) {
            return new int[0];
        }
        int id = 0;
        int il1 = r1.length;
        int il2 = r2.length;
        int wc1 = 0;
        int wc2 = 0;
        int doc[] = new int[il1];
        while (true) { // merge sort  r1 and r2 must be ordered !!!!!
            if (wc1 >= il1) {
                break;
            }
            if (wc2 >= il2) {
                break;
            }
            // System.out.println(wc1+", "+r1[wc1]+", "+wc2+", "+r2[wc2]);
            if (r1[wc1] == r2[wc2]) { // ne pas copier
                wc1++;
                wc2++;
            } else if (r1[wc1] < r2[wc2]) {
                doc[id] = r1[wc1];
                wc1++;
                id++;
            } else {
                wc2++;
            }
        }
        if (wc1 < il1) {  // copie la fin de r1
            for (int i = wc1; i < il1; i++) {
                doc[id] = r1[i];
                id++;
            }
        }
        return copyVector(id, doc);
    }

    /** calcul l'intersection de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param r1 vecteur1
     * @param r2 vecteur2
     * @return intersection de 1 et 2
     */
    public final static int[] andVector(int[] r1, int[] r2) {
        return andVector(r1, r1.length, r2, r2.length);
    }

    /** calcul l'intersection de deux vecteurs. (de documents, par exemple), les vecteurs doivent être ordonnés.
     * @param r1 vecteur1
     * @param il1 longueur de l'opération sur vecteur1
     * @param r2 vecteur2
     * @param il2  longueur de l'opération sur vecteur2
     * @return intersection de 1 et 2
     */
    public final static int[] andVector(int[] r1, int il1, int[] r2, int il2) {
        int id = 0;
        int doc[] = new int[Math.min(il1, il2)];
        if (il1 <= il2) // r1<r2
        {
            id = andVector2(r1, 0, il1, r2, 0, il2, doc, id);
        } else // permute r1,r2
        {
            id = andVector2(r2, 0, il2, r1, 0, il1, doc, id);
        }
        return copyVector(id, doc);
    }

    private final static int andVector2(int[] r1, int wc1, int il1, int[] r2, int wc2, int il2, int[] doc, int id) {
        boolean verbose = true;
        /* debug and */  // if(verbose)msg(id+" r1:"+wc1+","+il1+" r2:"+wc2+","+il2);
        if (wc1 >= il1 || wc2 >= il2) {
            /* debug and */ // if(verbose)msg("end >= id:"+id);
            return id;
        }
        if (wc1 == il1 - 1) { // r1.length=1
            int ref2 = getValue(r2, wc2, il2, r1[wc1]);
            if (ref2 == -1) {
                /* debug and */ // if(verbose)msg("end 1 -----------------");
                return id;
            }
            if (r1[wc1] == r2[ref2]) { // and ok
                doc[id] = r1[wc1];// the first operand stay the reference for occurrences
                id++;
                /* debug and */ // if(verbose)msg("end 1= id:"+id+" >> "+r1[wc1]+"("+wc1+","+ref2+")");
                return id;
            } else {
                /* debug and */ // if(verbose)msg("end 1 id:"+id);
                return id;
            }
        } else { // plus de 1 élément
            int halfr1 = (wc1 + il1) / 2;  // calcul l'élément milieu
            int halfr2 = getValue(r2, wc2, il2, r1[halfr1]);
            if (halfr2 == -1) {
                /* debug and */ // if(verbose)msg("cut -1 id:"+id);
                /* debug and */ // if(verbose)msg("eliminine:"+halfr1);
                /* debug and */ // if(verbose)msg(wc1+", "+halfr1+" / "+ wc2+", "+ il2);
                id = andVector2(r1, wc1, halfr1, r2, wc2, il2, doc, id);
                /* debug and */ // if(verbose)msg(halfr1+", "+il1+" / "+ wc2+", "+ il2);
                id = andVector2(r1, halfr1 + 1, il1, r2, wc2, il2, doc, id);
                return id;
            }
            if (r1[halfr1] == r2[halfr2]) { // and ok
                /* debug and */ // if(verbose)msg("eliminine:"+halfr1);
                /* debug and */ // if(verbose)msg("cut = id:"+id+" >> "+r1[halfr1]+"("+halfr1+","+halfr2+")");
                /* debug and */ // if(verbose)msg("prepare cut = "+halfr1+"="+r1[halfr1]+" / "+halfr2+"="+r2[halfr2]);
                /* debug and */ // if(verbose)msg(wc1+", "+halfr1+" / "+ wc2+", "+ halfr2);
                id = andVector2(r1, wc1, halfr1, r2, wc2, halfr2, doc, id);
                doc[id] = r1[halfr1];// the first operand stay the reference for occurrences
                id++;
                /* debug and */ // if(verbose)msg("prepare cut = "+halfr1+"="+r1[halfr1]+" / "+halfr2+"="+r2[halfr2]);
                /* debug and */ // if(verbose)msg((halfr1+1)+", "+il1+" / "+ (halfr2+1)+", "+ il2);
                id = andVector2(r1, halfr1 + 1, il1, r2, halfr2 + 1, il2, doc, id);
                return id;
            } else {
                /* debug and */ // if(verbose)msg("eliminine:"+halfr1);
                /* debug and */ // if(verbose)msg("prepare cut != "+halfr1+"="+r1[halfr1]+" / "+halfr2+"="+r2[halfr2]);
                /* debug and */ // if(verbose)msg(wc1+", "+halfr1+" / "+ wc2+", "+ (halfr2+1));
                id = andVector2(r1, wc1, halfr1, r2, wc2, halfr2 + 1, doc, id);
                /* debug and */ // if(verbose)msg("prepare cut != "+halfr1+"="+r1[halfr1]+" / "+halfr2+"="+r2[halfr2]);
                /* debug and */ // if(verbose)msg((halfr1+1)+", "+il1+" / "+ halfr2+", "+ il2);
                id = andVector2(r1, halfr1 + 1, il1, r2, halfr2, il2, doc, id);
                return id;
            }
        }
    }

    private final static int getValue(int[] r, int min, int max, int v) {// search v in vector r of length rl
        //System.out.println("search for :"+v);
        int i;
        max--;
        //System.out.println(" ------ min:"+min+" max:"+max);
        if (min == max) // only one element
        {
            if (r[max] == v) {
                return max;
            } else {
                return -1;
            }
        }  // fait déborder
        while (true) {
            i = (min + max) / 2;  // divise par 2
            //System.out.println("min:"+min+" i:"+i+" max:"+max+"  r[i]:"+r[i]);
            if (r[i] == v) {
                return i;
            }
            if (min + 1 == max) {
                if (r[max] == v) {
                    return max;
                } else if (r[min] < v) {
                    return max;
                } else {
                    return min;
                }
            }
            if (r[i] < v) {
                min = i;
            } else {
                max = i;
            }
        }
    }

    /**
     * calcul la proximité de deux entiers dans les vecteur. (des positions, par exemple), les vecteurs doivent être ordonnés.
     * @param p1 vecteur1 vecteur1
     * @param p2 vecteur2 vecteur2
     * @param nearvalue distance maximale entre les entiers
     * @return entiers satisfaisant la propriété de proximité
     */
    public final static boolean nearTest(int[] p1, int[] p2, int nearvalue) {
        if (p1.length <= p2.length) {
            return nearTest2(p1, p2, nearvalue);
        } else {
            return nearTest2(p2, p1, nearvalue);
        }
    }

    private final static boolean nearTest2(int[] p1, int[] p2, int nearvalue) {
        int pl1 = p1.length;
        int pl2 = p2.length;
        int pc1 = 0;
        int pc2 = 0;
        while (true) { // initialize a new merge sort  r1 and r2 must be ordered !!!!!
            if (pc1 >= pl1 || pc2 >= pl2) {
                return false;
            }
            // msg("  nearTest "+pc1+", "+r1[pc1]+", "+pc2+", "+r2[pc2]);
            if (Math.abs(p1[pc1] - p2[pc2]) < nearvalue) { // near ok
                return true;
            } else if (p1[pc1] < p2[pc2]) {
                pc1++;
            } else {
                pc2++;
            }
        } // while
    }

    /** cherche la indice dans un vecteur ordonné r, de la valeur v, de la position 0 à rl.
     * rl est souvent associé à la longeur de r, cependant cette méthode peut être utilisé avec un vecteur partiellement rempli.
     * @param r  vecteur
     * @param length  limite de la recherche
     * @param v  valeur recherchée
     * @return indice (-1=pas trouvé)
     */
    public final static int getIdxOfValue(int[] r, int length, int v) {// search v in vector r of length rl
        //System.out.println("search for :"+v);
        int i, min = 0, max = length - 1;
        //System.out.println(" ------ min:"+min+" max:"+max);
        if (max == 0) // only one element
        {
            if (r[max] == v) {
                return max;
            } else {
                return -1;
            }
        }
        while (true) {
            i = (min + max) / 2;
            //System.out.println("min:"+min+" i:"+i+" max:"+max+"  r[i]:"+r[i]);
            if (r[i] == v) {
                return i;
            }
            if (min + 1 == max) {
                if (r[max] == v) {
                    return max;
                } else {
                    break;
                }
            }
            ;
            if (r[i] < v) {
                min = i;
            } else {
                max = i;
            }
        }
        return -1;
    }
}
