package isi.jg.idxvli.ref;

import java.io.*;
import java.util.*;
import static isi.jg.idxvli.ql.QueryOperator.*;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;

/**
 * Une classe pour référencer un string (pour la version de référence).
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
 * to do:
 * - l'utilisation de SparseBitSet[] peut etre remplacée par des opérations sur les vecteurs 
 *   de documents directement
 */
public class IdxReference {

    public static final int MaxIndexedW = 1000000;
    public static final int NotIndexed = -1;
    static final int MaxMark = 3;
    static final int NoMark = -1;
    static int currentMark = 0;
    static int seqmax = 6;   // borne du seq
    static int seqn = 6 - 1;   // borne pour les boucles
    public int[] idxW;
    public String[] word;
    private SparseBitSet[] doc;
    public int lastscan;
    protected int lastcp;
    private IdxStructure glue;
    private int[] idxcp;
    private int[] cpW;
    private int[] begM,  endM,  docM;
    private int minlength;

    public IdxReference(IdxStructure _glue, String s, int min) {
        Timer timing = new Timer("--------------------------------Scan String");
        glue = _glue;
        lastscan = 0;
        minlength = min;
        idxW = new int[MaxIndexedW];
        word = new String[MaxIndexedW];
        Timer t = new Timer("Parsing");
        DoParse a = new DoParse(new StringReader(s), glue.dontIndexThis);
        a.scanString(glue, this);
        t.stop();
        t = new Timer("Compacting");
        compact();
        t.stop();
        t = new Timer("Compute Sequence");
        computeSeq3();
        t.stop();
        t = new Timer("Marking");
        markString();
        t.stop();
        t = new Timer("Parsing");
        timing.stop();
    //this.print();
    }

    private final void newMark() {
        currentMark++;
        if (currentMark == MaxMark) {
            currentMark = 0;
        }
    }

    private final void compact() {
        int l;
        lastcp = 0;
        idxcp = new int[lastscan];
        cpW = new int[lastscan];
        for (int i = 0; i < lastscan; i++) {
            if (idxW[i] != NotIndexed) {
                idxcp[i] = lastcp;
                cpW[lastcp] = idxW[i];
                lastcp++;
            } else {
                idxcp[i] = NoMark;
            }
        }
        System.out.println(lastscan + " compact " + lastcp);
    }

    private final void computeSeq3() {
        int[] resD;
        int count = 0;
        Date start = new Date();
        doc = new SparseBitSet[lastscan];
        for (int i = 0; i < lastscan; i++) {
            doc[i] = new SparseBitSet();
        }
        for (int i = 0; i < lastcp - seqn; i++) {
            //		resD=getDocforWseqW3(glue,cpW[i],cpW[i+1],cpW[i+2]);
            count++;
            if (count % 100 == 0) {
                Date end = new Date();
                System.out.println(count + ":" + (end.getTime() - start.getTime()));
                start = new Date();
            }
            resD = getDocforWseqW6(glue, cpW[i], cpW[i + 1], cpW[i + 2], cpW[i + 3], cpW[i + 4], cpW[i + 5]);
            //id.showVector(resD);
            int l = resD.length;
            for (int j = 0; j < l; j++) {
                doc[i].insertbit(resD[j]);
            }
        }
    }

    private final void markString() {
        begM = new int[lastscan];
        for (int i = 0; i < lastscan; i++) {
            begM[i] = NoMark;
        }
        endM = new int[lastscan + 1]; // for the last !
        for (int i = 0; i < lastscan; i++) {
            endM[i] = NoMark;
        }
        docM = new int[lastscan];
        int maxmarked = -1;
        int mark;
        int markdoc = 0;
        for (int i = 0; i < lastcp - seqn; i++) {
            SparseBitSet b = doc[i];
            if (b.notEmpty()) { // ok look for the next
                mark = i;
                for (int j = i; j < lastcp - seqn; j++) {
                    b = b.and(doc[j]);
                    if (b.notEmpty()) {
                        mark = j;
                        b.resetcursor();
                        markdoc = b.getNextPos();
                    } else {
                        break;
                    } // not in the same doc
                }
                if ((mark > maxmarked) && ((mark - i) >= minlength - seqmax)) { // not included in a bigger ref
                    maxmarked = mark; // new max
                    newMark();
                    begM[i] = currentMark;
                    endM[maxmarked + seqmax] = currentMark;
                    b.resetcursor();
                    docM[i] = markdoc; // get the firs ref (if many)
                }
            }// if
        }// for
    }

    public final String getXML() {
        String s = "<P_small>\n";
        for (int i = 0; i < lastscan; i++) {
            if (idxcp[i] != NoMark) {
                if (endM[idxcp[i]] != NoMark) {
                    s += "<ICON><COMMENT>click on the open tag</COMMENT><IMG>end" + endM[idxcp[i]] + "</IMG></ICON>\n";
                } // if
                if (begM[idxcp[i]] != NoMark) {
                    String fn = glue.getFileNameForDocument(docM[idxcp[i]]);
                    s += "<URL target=\"document\" href=\"" + fn + "\">" + fn.substring(12) + "</URL>" +
                            "<ICON><COMMENT>" + fn.substring(12) + "</COMMENT><IMG>beg" + begM[idxcp[i]] + "</IMG></ICON>";
                } //if
            } //if
            s += word[i] + " ";
        } // for
        if (endM[idxcp[lastscan - 1] + 1] != NoMark) {
            s += "<ICON><COMMENT>click on the open tag</COMMENT><IMG>end" + endM[idxcp[lastscan - 1] + 1] + "</IMG></ICON>\n";
        } // if
        s += "</P_small>\n";
        return s;
    }

    private final void print() {
        System.out.println("Scanned String lenght" + lastscan);
        for (int i = 0; i < lastscan; i++) {
            System.out.println(i + "/" + word[i] + "/" + idxW[i] + "/" + idxcp[i] + "/" + begM[i] + "/" + endM[i] + "/" + docM[i]);
            doc[i].print();
        }
    }
}
