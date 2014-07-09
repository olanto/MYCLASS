package isi.jg.util;

/** 
 * Gestion de timer en nano seconde.
 * Une classe pour d�clencher un chronom�tre et pour mesurer facilement l'efficacit� du code.
 * Par exemple: 
 * <pre> 
 * Timer t1 = new Timer("section A du code");  // le chrono a d�marrer!
 *    ...section A ... 
 * t1.stop(); // affiche le temps en milliseconde ...
 * </pre> 
 * On peut r�utiliser le m�me chronom�tre plusieurs fois, avec la m�thode restart()
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
public class TimerNano {

    private long start;
    private String activity;
    /**
     * cr�e un chronom�tre. Et puis le d�marre et affiche dans 
     * la console le commentaire associ�
     * 
     * @param s le commentaire associ� au chrono. 
     */

    /* valeur en nanosecondes d'un start/stop sur une machine donn�es */
    long EmptyOnThisComputer = 0; // 2500 nano 0=pas de compensation du timer

    public TimerNano(String s, boolean silent) {
        activity = s;
        if (!silent) {
            System.out.println("START: " + activity);
        }
        start = System.nanoTime();
    }

    /**
     * stope le chronom�tre. Et affiche dans la console le commentaire associ�
     * et le temps mesur� en milliseconde
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
     * red�marre le chronom�tre. Et affiche dans la console le commentaire associ�
     * @param s le commentaire associ� avec le chronom�tre
     */
    public void restart(String s, boolean silent) {
        activity = s;
        if (!silent) {
            System.out.println("START: " + activity);
        }
        start = System.nanoTime();
    }
}
