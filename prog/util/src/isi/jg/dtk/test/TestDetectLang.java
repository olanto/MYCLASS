package isi.jg.dtk.test;

import java.io.*;
import isi.jg.dtk.*;

/**
 * permet de tester les performances d'un automatique pour la reconnaissance de la langue d'un document.
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
public class TestDetectLang {

    /**
     * permet d'effectuer le test de cette classe
     * @param args pas utilisés
     */
    public static void main(String args[]) {

        DetectLang d = new DetectLang(4);

        d.trainLang("FRE", "C:/JG/VLI_RW/TrainingDTK/French.txt");
        d.trainLang("ENG", "C:/JG/VLI_RW/TrainingDTK/English.txt");
        d.trainLang("GER", "C:/JG/VLI_RW/TrainingDTK/German.txt");
        d.trainLang("SPA", "C:/JG/VLI_RW/TrainingDTK/Spanish.txt");

        String r = readFromFile("C:/JG/VLI_RW/TrainingDTK/L-333_FR.TXT");

        for (int seg = 16; seg < 1025; seg *= 2) {
            int count = 0;
            int countOK = 0;

            for (int i = 0; i < r.length() - seg; i += seg) {
                if (d.langOfText(r.substring(i, i + seg)).equals("FRE")) {
                    countOK++;
                }
                count++;
            }
            System.out.println("seg:" + seg + ": count:" + count + ": countOK:" + countOK);
        }

    }

    /**
     * lecture d'un fichier
     * @param fname ficher à lire
     * @return contenu du fichier
     */
    private static String readFromFile(String fname) {
        StringBuffer b = new StringBuffer("");
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String w = in.readLine();
            while (w != null) {
                //System.out.println(w);
                b.append(w);
                w = in.readLine();
            }
        } catch (Exception e) {
            System.err.println("IO error in SetOfWords");
        }
        return b.toString();
    }
}
