package org.olanto.cat.mnn;



import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.*;
import org.olanto.util.Timer;

/** Objet pour retourner un choix du catégoriseur.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2009
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * toute autre utilisation est sujette à autorisation
 * <hr>
 */

public class Guess{
    
    /** Nom de la catégorie choisie
     */    
    public String categorie="Not defined";
    int catid=-1;
    /** poids de l'évalution de ce choix
     */    
    public double weight=0;
    
    /** constructeur pour créer un choix du catégoriseur
     */    
    public Guess(){};
    
    Guess(int _catid, double _weight){
        categorie="not translate";
        catid=_catid;
        weight=_weight;
    }
    
    Guess(String _categorie, int _catid, double _weight){
        categorie=_categorie;
        catid=_catid;
        weight=_weight;
    }
    
    
} // end class


