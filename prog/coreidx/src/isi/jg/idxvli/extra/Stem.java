package isi.jg.idxvli.extra;

import java.lang.reflect.Method;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.util.Messages.*;
import java.util.*;
import net.sf.snowball.*;

/**
 * Cette classe wrap les stemmer de snowball.
 *
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
 * Cette classe wrap les stemmer de snowball.
 *
 *Snowball - License (package net.sf.snowball)
 *
 * All the software given out on this Snowball site is covered by the BSD License (see http://www.opensource.org/licenses/bsd-license.html ), with Copyright (c) 2001, Dr Martin Porter, and (for the Java developments) Copyright (c) 2002, Richard Boulton.
 *
 * Essentially, all this means is that you can do what you like with the code, except claim another Copyright for it, or claim that it is issued under a different license. The software is also issued without warranties, which means that if anyone suffers through its use, they cannot come back and sue you. You also have to alert anyone to whom you give the Snowball software to the fact that it is covered by the BSD license.
 *
 * We have not bothered to insert the licensing arrangement into the text of the Snowball software
 *
 */
public class Stem {

    private String lang = "french";
    private Class stemClass;
    private SnowballProgram stemmer;
    private Method stemMethod;
    private Object[] emptyArgs = new Object[0];
    private Hashtable<String, String> InMemory;
    private int countInMemory = 0;

    public Stem(String _lang) {
        lang = _lang;
        String stemName = "net.sf.snowball.ext." + lang + "Stemmer";
        System.out.println("Stemmer.init:" + _lang);
        countInMemory = 0;
        InMemory = new Hashtable<String, String>();
        try {
            stemClass = Class.forName(stemName);
            stemmer = (SnowballProgram) stemClass.newInstance();
            stemMethod = stemClass.getMethod("stem", new Class[0]);
        } catch (Exception e) {
            System.err.println("Error during Stemmer.init (check language):" + stemName);
            e.printStackTrace();
        }

    }

    public String stemmingOfW(String src) {
        try {
            String stemInMemory = InMemory.get(src);
            if (stemInMemory != null) { // dans le cache
                return stemInMemory;
            } else { // pas dans le cache
                stemmer.setCurrent(src);
                stemMethod.invoke(stemmer, emptyArgs);
                String res = stemmer.getCurrent();
                if (countInMemory > STEM_CACHE_COUNT) { // dépassement de capacité
                    countInMemory = 0;
                    InMemory = new Hashtable<String, String>();
                    msg("reset stemming cache:" + lang);
                }
                InMemory.put(src, res);
                countInMemory++;
                return res;
            }
        } catch (Exception e) {
            System.err.println("Error during Stemmer.stemmingOfWt (check language)");
            e.printStackTrace();
        }
        return src;
    }
}
