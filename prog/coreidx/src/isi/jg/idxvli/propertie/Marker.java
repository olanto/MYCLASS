package isi.jg.idxvli.propertie;

import java.util.*;
import java.io.*;
import isi.jg.idxvli.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import static isi.jg.idxvli.IdxConstant.*;
import isi.jg.dtk.*;

/**
 * Permet de checher les propriétés associés à des objets.
 * <p>
 * Par exemple, les propriétés de langue ou de collection associées à des documents
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
 * */
public class Marker implements MarkerManager {

    static DetectLang detectLang;
    static CollectionRules collections;

    public Marker(String rootlangfile, String collectClassFile) {
        initLanguageDetector(rootlangfile);
        collections = new CollectionRules(collectClassFile);
    }

    private void initLanguageDetector(String fileroot) {

        detectLang = new DetectLang(4);

        detectLang.trainLang("FRE", fileroot + "French.txt");
        detectLang.trainLang("ENG", fileroot + "English.txt");
        detectLang.trainLang("GER",fileroot+"German.txt");
        detectLang.trainLang("SPA",fileroot+"Spanish.txt");
//        msg("initialization of Language assign for : FRE; ENG; GER; SPA");


    }

    /**
     *  retourne la langue d'un document
     * @param txt échantillon du document (1000 caractères par exemple)
     * @return code de la langue (FR,EN,...)
     */
    public String getLanguage(String txt) {
        return detectLang.langOfText(txt);
    }

    /**
     *  retourne la collection d'un document
     * @param name nom du document (url, ...)
     * @return code de la collection (INFORMATIQUE,MEDECINE,...)
     */
    public String[] getCollectionFromName(String name) {
        return collections.eval(name);

    }
}


