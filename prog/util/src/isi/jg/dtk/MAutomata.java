package isi.jg.dtk;

import java.io.*;
import java.util.Hashtable;
import isi.jg.util.Timer;

/**
 * Une classe pour construire un automate de Markov.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2004
Copyright (C) 2005,  ISI Research Group, CUI, University of Geneva
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
You can also find the GNU GPL at http://www.gnu.org/copyleft/gpl.html
You can contact the ISI research group at http://cui.unige.ch/isi
 *
 */
public class MAutomata {

    /** timer pour les tests de performance */
    private static Timer t1;
    /** flag d'impression pour les tests */
    private static boolean verbose = false;
    /** flag d'impression pour les tests */
    // variable de l'automate
    /* si true, on applique une normalisation des caractères du training, actuellement lowercase() */
    private boolean normalise = false;
    /** nbr de token pour l'apprentissage */
    private long sampleSize = 1000000;
    /** ordre de l'automate 1  char-char, 2 bigram-bigram, etc */
    private int tokenSize = 3;
    /** fichier de d'apprentissage */
    private FileReader in;
    /** détermine si l'on est en train de contruire l'automate (true), sinon on est en consultation */
    private boolean buildMode = true;
    /** structure de l'automate , un n-gram est associé à un noeud */
    private Hashtable<String, MNode> automata = new Hashtable<String, MNode>();
    /** nbr de noeud déja activé */
    private int totalNode = 0;

    /**
     * création d'un automate à partir d'un fichier
     * @param trainingFile fichier contenant le texte pour construire l'automate
     * @param _sampleSize taille de l'échantillon
     * @param _tokenSize taille des tokens
     */
    public MAutomata(String trainingFile, int _sampleSize, int _tokenSize) {
        sampleSize = _sampleSize;
        tokenSize = _tokenSize;
        char rec[] = new char[tokenSize];
        t1 = new Timer("train:" + trainingFile + " on:" + sampleSize + " token size:" + tokenSize);
        String s;
        MNode lasti = new MNode(".");  // start node
        automata.put(".", lasti);
        totalNode = 1;
        try {
            System.out.println("open file");
            in = new FileReader(trainingFile);
            long sample = sampleSize;
            while (in.ready()) {
                in.read(rec, 0, tokenSize);
                s = new String(rec);
                if (isNormalise()) {
                    s = normalise(s);
                }
                lasti.addLink(s);
                lasti = get_ref(s);
                //   System.out.print (s);
                if (--sample == 0) {
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        buildMode = false;
        t1.stop();
    }

    /**
     * création d'un automate à partir d'un fichier
     * @param trainingFile Fichier contenant le texte pour créer l'automate
     */
    public MAutomata(String trainingFile) {
        char rec[] = new char[tokenSize];
        t1 = new Timer("train:" + trainingFile + " on:" + sampleSize + " token size:" + tokenSize);
        String s;
        MNode lasti = new MNode(".");  // start node
        automata.put(".", lasti);
        totalNode = 1;
        try {
            System.out.println("open file");
            in = new FileReader(trainingFile);
            long sample = sampleSize;
            while (in.ready()) {
                in.read(rec, 0, tokenSize);
                s = new String(rec);
                if (isNormalise()) {
                    s = normalise(s);
                }
                lasti.addLink(s);
                lasti = get_ref(s);
                //   System.out.print (s);
                if (--sample == 0) {
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        buildMode = false;
        t1.stop();
    }

    private String normalise(String w) {
        return w.toLowerCase();
    }

    private MNode get_ref(String c) {
        MNode node = automata.get(c);
        if (node != null) {// il existe déja
            return node;
        } else {
            if (buildMode) {// c'est un nouveau
                node = new MNode(c);
                automata.put(c, node);
                totalNode++;
                return node;
            } else {
                return null;
            }
        }
    }

    /**
     * évalue la probabilité d'appartenance à cette automate
     * @param s texte à évaluer
     * @return probabilité (exp(x))
     */
    public double probOfSentence(String s) {
        if (verbose) {
            t1 = new Timer("probOfSentence");
        }
        if (isNormalise()) {
            s = normalise(s);
        }
        double t = 0.0;
        MNode lasti = automata.get(".");
        String from = "", to = ".";
        for (int i = 0; i < s.length() - tokenSize; i += tokenSize) {
            from = to;
            to = s.substring(i, i + tokenSize);
            if (lasti != null) {
                t += lasti.probOfNext(to);
            //System.out.println(from+"->"+to+"/"+lasti.probOfNext(to));
            } else {
                t += -0;
            } // mark unknow
            lasti = automata.get(to);
        }
        if (verbose) {
            t1.stop();
        }
        return t;
    }

    /**
     * génération d'une phrase à partir de l'automate
     * @param size taille de la phrase
     * @return phrase générée
     */
    public String generateText(int size) {
        StringBuffer res = new StringBuffer(size);
        MNode lasti = automata.get(".");
        for (int i = 0; i < size; i++) {
            String s = lasti.getnext();
            res.append(s);
            lasti = get_ref(s);
        }
        return res.toString();
    }

    /** Retourne le nombre de noeuds de cette automate de Markov 
     * @return le nombre de noeuds de cette automate de Markov
     */
    public int getTotalNode() {
        return totalNode;
    }

    /** Retourne la hashtable de cette automate de Markov (uniquement pour les tests et debug)
     * @return la hashtable de cette automate de Markov (uniquement pour les tests et debug)
     */
    public Hashtable<String, MNode> getAutomata() {
        return automata;
    }

    /** Retourne le switch indiquant si la normalisation est active
     * @return le switch indiquant si la normalisation est active
     */
    public boolean isNormalise() {
        return normalise;
    }

    /** active ou désactive la normalisation
     * @param aNormalise false=texte brut, true=texte normalisé
     */
    public void setNormalise(boolean aNormalise) {
        normalise = aNormalise;
    }
}
