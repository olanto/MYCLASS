package isi.jg.util;

/** 
 * Gestion de timer en milli seconde.
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
public class Timer {

    private long start;
    private String activity;

    /**
     * crée un chronomètre. Et puis le démarre et affiche dans 
     * la console le commentaire associé
     * 
     * @param s le commentaire associé au chrono. 
     */
    public Timer(String s) {
        activity = s;
        start = System.currentTimeMillis();
        System.out.println("START: " + activity);
    }

    /**
     * crée un chronomètre. Et puis le démarre et affiche dans 
     * la console le commentaire associé
     * 
     * @param s le commentaire associé au chrono. 
     */
    public Timer(String s, boolean silent) {
        activity = s;
        start = System.currentTimeMillis();
    }

    /**
     * stope le chronomètre. Et affiche dans la console le commentaire associé
     * et le temps mesuré en milliseconde
     * 
     */
    public void stop() {
        start = System.currentTimeMillis() - start;
        System.out.println("STOP: " + activity + " - " + start + " ms");
    }

    /**
     * stope le chronomètre. Et affiche dans la console le commentaire associé
     * et le temps mesuré en milliseconde
     * 
     */
    public long getstop() {
        return System.currentTimeMillis() - start;
    }

    /**
     * redémarre le chronomètre. Et affiche dans la console le commentaire associé
     * @param s le commentaire associé avec le chronomètre
     */
    public void restart(String s) {
        activity = s;
        start = System.currentTimeMillis();
        System.out.println("START: " + activity);
    }
}
