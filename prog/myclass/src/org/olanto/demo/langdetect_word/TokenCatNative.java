/**
 * ********
 * Copyright © 2003-2018 Olanto Foundation Geneva
 *
 * This file is part of myCLASS.
 *
 * myCLASS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * myCAT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with myCAT. If not, see <http://www.gnu.org/licenses/>.
 *
 *********
 */
package org.olanto.demo.langdetect_word;

import org.olanto.idxvli.DoParse;
import static org.olanto.idxvli.IdxConstant.*;
import org.olanto.idxvli.IdxStructure;
import org.olanto.idxvli.TokenDefinition;
import org.olanto.idxvli.extra.Stemmer;

/**
 * to define TOKEN to parse training corpus
 *
 *
 */
public class TokenCatNative implements TokenDefinition {

    static final int EOF = -1;


    public TokenCatNative() {
    }

    /**
     * find next token
     * start with a letter (unicode), contains digit, -
     * not inside < > (html)
     * 

     */
    public final void next(DoParse a) {
        int c = 0;
        char r;
        try {
            while (!(Character.isLetter((char) c)) && (c != EOF)) {  // skip non letter
//                if ((char) c == '<') { // skip tag
//                    while ((c != EOF) && ((char) c != '>')) {
//                        c = a.in.read();
//                        a.poschar++;
//                    }
//                }
                c = a.in.read();
                a.poschar++;
            }
            a.cw.setLength(0);
            r = (char) c;
            while ((Character.isLetter(r)
                    //|| Character.isDigit(r)
                    || (char) c == '-') && (c != EOF)) {  // get word
                a.cw.append(r);
                c = a.in.read();
                a.poschar++;
                r = (char) c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        a.EOFflag = (c == EOF);
    }

    /**
     * normalised word. 
     *  lower case
     *  trunc if >maxlength
     *  get a stem if actived
     * @param id indexer
     * @param w word to be normalised
     * @return normalised word
     */
    @Override
    public final String normaliseWord(IdxStructure id, String w) {
        //System.out.print(w+"-->");
        
        w = w.toLowerCase();
        if (w.length() > WORD_MAXLENGTH) {
            w = w.substring(0, WORD_MAXLENGTH);
        }
        if (WORD_USE_STEMMER) {
            w = Stemmer.stemmingOfW(w);
        }
        //   System.out.println(w);
        return w;
    }
}