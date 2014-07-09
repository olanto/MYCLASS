package isi.jg.conman.util;

import java.rmi.*;
import static isi.jg.util.Messages.*;
import java.util.regex.*;

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
 */
public class UtilitiesOnHTML {

    static final String END_MARK = "$$$$$$$$";
    static final boolean verbose = false;

    public static String getTitle(String html) {
        // try from title
        String title = extract(html, "<title", "</title>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc title : " + title);
            }
            return title;
        }
        // try from title
        title = extract(html, "<TITLE", "</TITLE>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc TITLE : " + title);
            }
            return title;
        }
        // try from h1
        title = extract(html, "<h1", "</h1>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc h1 : " + title);
            }
            return title;
        }
        // try from h1
        title = extract(html, "<H1", "</H1>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc H1 : " + title);
            }
            return title;
        }
        // try from h2
        title = extract(html, "<h2", "</h2>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc h2 : " + title);
            }
            return title;
        }
        // try from h2
        title = extract(html, "<H2", "</H2>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc H2 : " + title);
            }
            return title;
        }
        // try from h3
        title = extract(html, "<h3", "</h3>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc h3 : " + title);
            }
            return title;
        }
        // try from h3
        title = extract(html, "<H3", "</H3>") + END_MARK;
        title = extract(title, ">", END_MARK);
        if (title != null) {
            if (verbose) {
                msg("doc H3 : " + title);
            }
            return title;
        }
        msg("no title try to extract from txt");
        String onlytxt = html2txt(html);
        String first = onlytxt.substring(0, Math.min(64, onlytxt.length())) + "...";
        msg("deduce title:" + first);
        return first;
    }

    static public String extract(String s, String start, String end) {
        //System.out.println(" start:"+start+" end:"+end);
        int begrec = s.indexOf(start);
        int endrec = s.indexOf(end, begrec + start.length());
        //System.out.println(" begrec:"+begrec+" endrec:"+endrec);
        if (begrec != -1 & endrec != -1) {
            String rec = s.substring(begrec + start.length(), endrec);
            return rec;
        } else {
            return null;
        }

    }

    
    

       ; 
            
           public static String html2txt(String html) {
        StringBuilder res=new StringBuilder();
        int poschar=0;
        int end = html.length();
        try {
            while (poschar < end) {
                while (html.charAt(poschar) == '<') { // skip tag
                    while ((poschar < end) && (html.charAt(poschar) != '>')) {
                        poschar++;
                    }
                    poschar++;
                    if (!(poschar < end)) {
                        return clean(res.toString());
                    } // c'est fini'
                }
                res.append(html.charAt(poschar));
                poschar++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clean(res.toString());
    }

    public static String clean(String txt) {
        txt = txt.replace('\r', ' ');
        txt = txt.replace('\n', ' ');
        txt = HTMLEntities.unhtmlentities(txt);
        while (txt.indexOf("  ") != -1) {
            txt = txt.replace("  ", " ");
        }
        txt = txt.trim();
        return txt;
    }
}
