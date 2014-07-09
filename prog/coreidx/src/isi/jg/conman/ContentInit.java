package isi.jg.conman;

/**
 * Une classe pour initialiser les constantes.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 * Une classe pour initialiser les constantes. Cette classe doit �tre impl�ment�e pour chaque application
 */
public interface ContentInit {

    /** initialisation permanante des constantes. 
     * Ces constantes choisies d�finitivement pour toute la dur�e de la vie de l'index.
     */
    public void InitPermanent();

    /** initialisation des constantes de configuration (modifiable). 
     * Ces constantes choisies d�finitivement pour toute la dur�e de la vie du processus.
     */
    public void InitConfiguration();
}
