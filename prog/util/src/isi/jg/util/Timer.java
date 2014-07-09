package isi.jg.util;

/** 
 * Gestion de timer en milli seconde.
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
public class Timer {

    private long start;
    private String activity;

    /**
     * cr�e un chronom�tre. Et puis le d�marre et affiche dans 
     * la console le commentaire associ�
     * 
     * @param s le commentaire associ� au chrono. 
     */
    public Timer(String s) {
        activity = s;
        start = System.currentTimeMillis();
        System.out.println("START: " + activity);
    }

    /**
     * cr�e un chronom�tre. Et puis le d�marre et affiche dans 
     * la console le commentaire associ�
     * 
     * @param s le commentaire associ� au chrono. 
     */
    public Timer(String s, boolean silent) {
        activity = s;
        start = System.currentTimeMillis();
    }

    /**
     * stope le chronom�tre. Et affiche dans la console le commentaire associ�
     * et le temps mesur� en milliseconde
     * 
     */
    public void stop() {
        start = System.currentTimeMillis() - start;
        System.out.println("STOP: " + activity + " - " + start + " ms");
    }

    /**
     * stope le chronom�tre. Et affiche dans la console le commentaire associ�
     * et le temps mesur� en milliseconde
     * 
     */
    public long getstop() {
        return System.currentTimeMillis() - start;
    }

    /**
     * red�marre le chronom�tre. Et affiche dans la console le commentaire associ�
     * @param s le commentaire associ� avec le chronom�tre
     */
    public void restart(String s) {
        activity = s;
        start = System.currentTimeMillis();
        System.out.println("START: " + activity);
    }
}
