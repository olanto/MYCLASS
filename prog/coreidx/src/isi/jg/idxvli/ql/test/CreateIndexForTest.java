package isi.jg.idxvli.ql.test;

import isi.jg.idxvli.*;
import isi.jg.util.Timer;

/** Test de l'indexeur, cr�ation d'un nouvel index
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 */
public class CreateIndexForTest {

    private static IdxStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new IdxStructure("NEW", new ConfigurationQL());
        //id.indexdir("C:/OMC_corpus/BISD/S02");// indexation du dossier sp�cifi�
        //id.indexdir("C:/OMC_corpus/");// indexation du dossier sp�cifi�
        id.indexdir("C:/AAA/RECIPE/DOC/");// indexation du dossier sp�cifi�
        id.flushIndexDoc();  //  vide les buffers       
        id.Statistic.global();
        id.close();
        t1.stop();

    }
}
