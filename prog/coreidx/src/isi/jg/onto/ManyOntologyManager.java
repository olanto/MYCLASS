/*
 * Server.java
 *
 * Created on 25. janvier 2005, 14:40
 */
package isi.jg.onto;

import isi.jg.idxvli.*;
import isi.jg.idxvli.extra.*;
import isi.jg.util.TimerNano;
import isi.jg.util.Timer;
import java.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.util.SetOperation.*;
import static java.util.Arrays.*;


/**
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
 */
public class ManyOntologyManager {

    static Hashtable<String, LexicManager> multiOntology;

    public ManyOntologyManager() {

        multiOntology = new Hashtable<String, LexicManager>();
    }

    public void add(String suffix, String rootOntologyFile, String src, String stem) {
        //LexicManager ontology= (new LexicBasic()).create(rootOntologyFile+src+".txt",src,stem);
        LexicManager ontology = (new LexicBetter()).create(rootOntologyFile + src + ".txt", src, stem);
        multiOntology.put(suffix, ontology);
    }

    public LexicManager get(String suffix) {
        return multiOntology.get(suffix);
    }
}

