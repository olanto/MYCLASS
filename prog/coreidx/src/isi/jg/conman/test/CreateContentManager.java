package isi.jg.conman.test;

import isi.jg.conman.*;
import isi.jg.util.Timer;

/**
 * *
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Test de content manager, cr�ation d'un nouveau content manager
 */
public class CreateContentManager {

    private static ContentStructure id;
    private static Timer t1 = new Timer("global time");

    public static void main(String[] args) {

        id = new ContentStructure("NEW", new ConfigurationContentManager());


        // id.Statistic.global();
        id.close();
        t1.stop();

    }
}
