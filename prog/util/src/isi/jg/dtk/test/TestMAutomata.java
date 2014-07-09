package isi.jg.dtk.test;

import java.io.*;
import java.util.*;
import isi.jg.util.Timer;
import isi.jg.dtk.*;

/**
 * permet de tester les différentes fonctions des automates de Markov.
 * <p>
 * <p>
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
public class TestMAutomata {

    /**
     * permet d'effectuer le test de cette classe
     * @param args pas utilisés
     */
    public static void main(String args[]) {
        MAutomata french = new MAutomata("C:/JG/gigaversion/TrainingDTK/French.txt");
        test(french);
        MAutomata english = new MAutomata("C:/JG/gigaversion/TrainingDTK/English.txt");
        test(english);
    }

    /**
     * test d'un automate
     * @param a automate à tester
     */
    private static void test(MAutomata a) {

        // for (Enumeration<String> i = automata.keys(); i.hasMoreElements() ;){automata.get(i.nextElement()).show();}

        System.out.println("tot n-gram:" + a.getTotalNode());
        int sum = 0;
        for (Enumeration<String> i = a.getAutomata().keys(); i.hasMoreElements();) {
            sum += a.getAutomata().get(i.nextElement()).getNbLinks() + 1;
        }
        System.out.println("sum freq:" + sum);

        System.out.println("generate Texte:" + a.generateText(1000));

        System.out.println();
        System.out.println(a.getTotalNode());
        String s1 = "Ceci est une phrase un peu plus longue.";
        String s2 = "Provides the classes necessary to create an applet and the.";
        String s3 = "According to a major industry study issued earlier this year.";
        String s4 = "Il aime les pommes et les poires qui viennent du jardin.";
        String s5 = "pere serre les terres";
        String s6 = "pere aime la terre";
        System.out.println(s1 + " : " + a.probOfSentence(s1));
        System.out.println(s2 + " : " + a.probOfSentence(s2));
        System.out.println(s3 + " : " + a.probOfSentence(s3));
        System.out.println(s4 + " : " + a.probOfSentence(s4));
        System.out.println(s5 + " : " + a.probOfSentence(s5));
        System.out.println(s6 + " : " + a.probOfSentence(s6));
    }
}
