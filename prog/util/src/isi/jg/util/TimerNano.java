package isi.jg.util;

/** 
 * Gestion de timer en nano seconde.
 * Une classe pour déclencher un chronomètre et pour mesurer facilement l'efficacité du code.
 * Par exemple: 
 * <pre> 
 * Timer t1 = new Timer("section A du code");  // le chrono a démarrer!
 *    ...section A ... 
 * t1.stop(); // affiche le temps en milliseconde ...
 * </pre> 
 * On peut réutiliser le même chronomètre plusieurs fois, avec la méthode restart()
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
public class TimerNano {

    private long start;
    private String activity;
    /**
     * crée un chronomètre. Et puis le démarre et affiche dans 
     * la console le commentaire associé
     * 
     * @param s le commentaire associé au chrono. 
     */

    /* valeur en nanosecondes d'un start/stop sur une machine données */
    long EmptyOnThisComputer = 0; // 2500 nano 0=pas de compensation du timer

    public TimerNano(String s, boolean silent) {
        activity = s;
        if (!silent) {
            System.out.println("START: " + activity);
        }
        start = System.nanoTime();
    }

    /**
     * stope le chronomètre. Et affiche dans la console le commentaire associé
     * et le temps mesuré en milliseconde
     * 
     */
    public long stop(boolean silent) {
        start = System.nanoTime() - start - EmptyOnThisComputer;
        if (!silent) {
            System.out.println("STOP: " + activity + " - " + start / 1000 + " us");
        }
        return start / 1000;
    }

    /**
     * redémarre le chronomètre. Et affiche dans la console le commentaire associé
     * @param s le commentaire associé avec le chronomètre
     */
    public void restart(String s, boolean silent) {
        activity = s;
        if (!silent) {
            System.out.println("START: " + activity);
        }
        start = System.nanoTime();
    }
}
