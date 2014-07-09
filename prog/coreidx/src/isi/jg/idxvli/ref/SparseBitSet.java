package isi.jg.idxvli.ref;

import java.io.*;

/**
 * gestion simple d'un ensemble épars de bit.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * <p>
 */
class SparseBitSet implements Serializable {

    private static final int EOSBS = -1;
    private static int cursor;
    private static int cursor2;
    private int[] delta;

    //SparseBitSet() {}
    protected final void resetcursor() {
        cursor = 0;
    }

    protected final int getNextPos() { // one by one
        if (delta == null) {
            return EOSBS;
        } else {
            if (cursor == delta.length) {
                return EOSBS;
            } else {
                int position = delta[cursor];
                cursor++;
                return position;
            }
        }
    }

    protected final void resetcursor2() {
        cursor2 = 0;
    }

    protected final int getNextPos2() { // one by one
        if (delta == null) {
            return EOSBS;
        } else {
            if (cursor2 == delta.length) {
                return EOSBS;
            } else {
                int position = delta[cursor2];
                cursor2++;
                return position;
            }
        }
    }

    protected final int length() { // only one by one
        if (delta == null) {
            return 0;
        }
        return delta.length;
    }

    protected final void addbit(int position) { // must be ordered
        if (delta == null) {
            delta = new int[1];
            delta[0] = position;
        } else {
            int l = delta.length;
            int[] it = new int[l + 1];
            System.arraycopy(delta, 0, it, 0, l);
            delta = it;
            delta[l] = position;
        }
    }

    protected final void insertbit(int position) { // 
        if (delta == null) {
            delta = new int[1];
            delta[0] = position;
        } else {
            //System.out.print("before copy: ");showVector(delta);
            int l = delta.length;
            int i = 0;
            for (i = 0; i < l; i++) {
                if (delta[i] >= position) {
                    break;
                }
            }

            if ((i == l) || (delta[i] != position)) {
                int[] it = new int[l + 1];
                //System.out.println("insert: "+position+" i: "+i+" l: "+l);
                System.arraycopy(delta, 0, it, 0, i); // copy first part
                System.arraycopy(delta, i, it, i + 1, l - i); // copy second part      
                delta = it;
                //System.out.print("after copy: ");showVector(delta);
                if (i == 0) {
                    delta[0] = position;
                } // if the first
                else {
                    if (i == l) {
                        delta[i] = position;
                    } // if  the last
                    else {
                        delta[i] = position;
                    }
                }
            } // already set
        //System.out.print("end insert: ");showVector(delta);

        }
    }

    protected final static void showVector(int[] p) {
        int l = p.length;
        for (int i = 0; i < l; i++) {
            System.out.print(p[i] + ",");
        }
        System.out.println();
    }

    protected final SparseBitSet and(SparseBitSet p) {
        SparseBitSet res = new SparseBitSet();
        int wc1 = 0, wc2 = 0, il1, il2;
        if ((p.delta == null) | (this.delta == null)) {
            return res;
        } else {
            il1 = this.delta.length;
            il2 = p.delta.length;
            while (true) { // merge sort  must be ordered !!!!!
                // System.out.println(wc1+", "+this.delta[wc1]+", "+wc2+", "+p.delta[wc2]);
                if (this.delta[wc1] == p.delta[wc2]) { // and ok
                    res.addbit(this.delta[wc1]);
                    wc1++;
                    if (wc1 >= il1) {
                        break;
                    }
                    wc2++;
                    if (wc2 >= il2) {
                        break;
                    }
                } else if (this.delta[wc1] < p.delta[wc2]) {
                    wc1++;
                    if (wc1 >= il1) {
                        break;
                    }
                } else {
                    wc2++;
                    if (wc2 >= il2) {
                        break;
                    }
                }
            } // while
        } // else
        return res;
    }

    protected final boolean notEmpty() {
        if (delta != null) {
            return true;
        }
        return false;
    }

    protected final boolean intersect(SparseBitSet p) { // true if one bit in the and
        int wc1 = 0, wc2 = 0, il1, il2;
        if ((p.delta == null) | (this.delta == null)) {
            return false;
        } else {
            il1 = this.delta.length;
            il2 = p.delta.length;
            while (true) { // merge sort  must be ordered !!!!!
                // System.out.println(wc1+", "+this.delta[wc1]+", "+wc2+", "+p.delta[wc2]);
                if (this.delta[wc1] == p.delta[wc2]) { // and ok
                    return true;
                } else if (this.delta[wc1] < p.delta[wc2]) {
                    wc1++;
                    if (wc1 >= il1) {
                        break;
                    }
                } else {
                    wc2++;
                    if (wc2 >= il2) {
                        break;
                    }
                }
            } // while
        } // else
        return false;
    }

    protected final void print() { // must be ordered
        if (delta == null) {
            System.out.println("SBS is empty");
        } else {
            System.out.print("SBS = ");
            int l = delta.length;
            for (int i = 0; i < l; i++) {
                System.out.print(delta[i] + " ");
            }
            System.out.print("\n");
        }
    }
}
