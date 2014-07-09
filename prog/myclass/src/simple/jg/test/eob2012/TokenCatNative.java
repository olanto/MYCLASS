package simple.jg.test.eob2012;

import org.olanto.idxvli.extra.Stemmer;
import org.olanto.idxvli.IdxStructure;
import org.olanto.idxvli.TokenDefinition;
import org.olanto.idxvli.DoParse;
import isi.jg.deploy.frende.*;
import java.io.*;
import java.text.*;
import static org.olanto.idxvli.IdxConstant.*;


/**
 * Une classe pour d�finir les token (une d�finition alpha num�rique).
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2004
 * <p>l'utilisation de cette classe est strictement limit�e au groupe ISI et � MetaRead
 * dans le cadre de l'�tude pr�liminaire pour le parlement europ�en
 *
 *<p>
 *
 */


public class TokenCatNative implements TokenDefinition {
    
    static final int EOF = -1;

    
    /** Cr�e une attache
     */
    public TokenCatNative() {
    }
    
        /**
     * Cherche le symbole suivant.
     * d�finition symbole= commence avec une lettre (au sens unicode)
     * et n'est pas dans un tag <> (peut occasioner une erreur d'indexation dans les mauvais html)
     * s'arr�te quand on rencontre autre chose qu'une lettre (au sens unicode)
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
             ||Character.isDigit(r)
                 ||(char)c=='-'
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
     * - tronquer � la longueur fix�e.<br>
     * - lemmatiser si le stemming est actif.<br>
     * @param id l'indexeur de r�f�rence
     * @param w le mot � normaliser
     * @return un mot normalis�
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