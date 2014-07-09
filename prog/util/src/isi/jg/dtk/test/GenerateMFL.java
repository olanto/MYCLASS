package isi.jg.dtk.test;

import java.io.*;
import java.util.*;
import isi.jg.util.Timer;
import isi.jg.dtk.*;

/**
 * permet de générer des familles de documents synthétiques à partir d'un automate de Markov.
 * <p>
 * La famille de documents synthétique est au format MFL, on choisit la langue, l'ordre de l'automate,
 * le nombre de documents, la taille des documents
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
public class GenerateMFL {

    /**
     * permet d'effectuer le test de cette classe
     * @param args pas utilisés
     */
    public static void main(String args[]) {
        int tokensize = 2;
        MAutomata french = new MAutomata("C:/JG/gigaversion/TrainingDTK/allFrench.txt", 8200000, tokensize);
        Timer t1 = new Timer("generate:");
        //  generate(french,"C:/JG/gigaversion/isi/jg/dtk/test_","a",872000/tokensize,20);
        generate(french, "C:/JG/gigaversion/TrainingDTK/test_", "f", 10000 / tokensize, 10000);
        t1.stop();
    }

    /**
     * génération automatique d'un document mfl
     * @param a l'automate de Markov entrainé avec une langue donnée
     * @param filename fichier mfl à générer
     * @param prefix à ajouter au nom du fichier mfl, pour la génération d'un ensemble de mfl
     * @param size nbr de token générés, la taille = size * tokensize, oû tokensize est l'ordre de l'automate
     * @param nbdoc nbr de documents dans le fichier mfl
     */
    public static void generate(MAutomata a, String filename, String prefix, int size, int nbdoc) {

        try {
            FileWriter out = new FileWriter(filename + prefix + ".mfl");
            for (int i = 0; i < nbdoc; i++) {
                if (i % 1000 == 0) {
                    System.out.println(i);
                }
                out.write("#####" + prefix + i + "#####\n");
                out.write(a.generateText(size) + "\n");
            }
            out.write("#####ENDOFFILE#####\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("IO error ExportEntry");
            e.printStackTrace();
        }


    }
}
