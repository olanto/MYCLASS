package isi.jg.profil;

import java.io.*;
import java.text.*;
import isi.jg.idxvli.*;
import isi.jg.idxvli.extra.*;
import static isi.jg.idxvli.IdxConstant.*;


/**
 * Une classe pour définir les token (une définition alpha numérique).
 * <p>
 * <b>JG-2008
* <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
  *
 *
 *<p>
 *
 */


public class TokenIndexing implements TokenDefinition {
    
    static final int EOF = -1;

    
    /** Crée une attache
     */
    public TokenIndexing() {
    }
    
        /**
     * Cherche le symbole suivant.
     * définition symbole= commence avec une lettre (au sens unicode)
     * et n'est pas dans un tag <> (peut occasioner une erreur d'indexation dans les mauvais html)
     * s'arrête quand on rencontre autre chose qu'une lettre (au sens unicode)
     */

    public  final void next(DoParse a) {
        int c=0;
        char r;
        try {
            while (!(Character.isLetter((char)c))&&(c!=EOF)){  // skip non letter
                if ((char)c=='<'){ // skip tag
                    while ((c!=EOF)&&((char)c!='>')){c=a.in.read();a.poschar++;}
                }
                c=a.in.read();
                a.poschar++;
            }
            a.cw.setLength(0);
            r= (char)c;
            while ((Character.isLetter(r)
            // ||Character.isDigit(r)
            //     ||(char)c=='-'
            )&&(c!=EOF)){  // get word
                a.cw.append(r);
                c=a.in.read();
                a.poschar++;
                r= (char)c;
            }
        } catch (Exception e) {e.printStackTrace();}
        a.EOFflag=(c==EOF);
    }
    
     
    /** normalise le mot. actuellement<br>
     * - mis en minuscule<br>
     * - tronquer à la longueur fixée.<br>
     * - lemmatiser si le stemming est actif.<br>
     * @param id l'indexeur de référence
     * @param w le mot à normaliser
     * @return un mot normalisé
     */
    public final  String normaliseWord(IdxStructure id, String w){
        //   System.out.print(w+"-->");
        w = w.toLowerCase();
        if (w.length() > WORD_MAXLENGTH)
            w = w.substring(0, WORD_MAXLENGTH);
        if (WORD_USE_STEMMER){
            w=Stemmer.stemmingOfW(w);
        }
        //   System.out.println(w);
        return w;
    } 
    
}