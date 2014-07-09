package isi.jg.idxvli.ql;

import isi.jg.idxvli.*;
import isi.jg.conman.server.*;
import isi.jg.idxvli.server.*;

/**
 * Comportement d'un ex�cuteur de requ�te.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 *
 *
 * Comportement d'un ex�cuteur de requ�te.
 */
public interface QLManager {

    /**
     * retourne la liste des documents valides correspondants � la requ�te, (null) si erreur.
     * @param request requ�te
     * @param id indexeur de r�f�rence
     * @return la liste des documents valides
     */
    public int[] get(String request, IdxStructure id);
    
    public QLResultAndRank getMore(String request, IdxStructure id);

    public QLResultNice get(IdxStructure id, ContentService cs, String request, int start, int size);
  
    public QLResultNice get(IdxStructure id, ContentService cs, String request1, String request2, int start, int size1, int size2);

    public QLResultNice get(IdxStructure id, ContentService cs, String request, String properties, int start, int size);

    public QLResultNice get(IdxStructure id, ContentService cs, String request, String properties, String profile, int start, int size);
}
