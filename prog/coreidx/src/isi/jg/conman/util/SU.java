/* Lazy hypertext view system
Copyright (C) 2002-2003,  ISI Research Group, CUI, University of Geneva
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
You can also find the GNU GPL at http://www.gnu.org/copyleft/gpl.html
You can contact the ISI research group at http://cui.unige.ch/isi
 */
package isi.jg.conman.util;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.net.*;
import java.lang.reflect.*;

class SU { // strings utilities

    static final String hrefPrefix = "<a href=\"ns?";

    static String replaceHrefs(String s) {
        int ix = 0, ixf;

        while ((ix = s.indexOf(hrefPrefix, ix)) > -1) {
            ixf = s.indexOf("\">", ix);
            String uParameterPart = s.substring(ix, ixf);
            //System.out.println("first ix:"+ix+" ixf:"+ixf+" s:"+s);
            s =
                    s.substring(0, ix) + getParameters(uParameterPart) + s.substring(ixf, s.length());
            ix = ixf;

        }
        return s;
    }

    static String getParameters(String s) {
        int ix = 0, ixf = 0;

        while ((ix = s.indexOf("&amp;u=", ix)) > -1) {
            ix = ix + 7;
            if ((ixf = s.indexOf("&amp;u=", ix)) < 0) {
                break;
            }
//            System.out.println("in ix:"+ix+" ixf:"+ixf+" s:"+s);
            String uParameterPart = s.substring(ix, ixf);
            s = s.substring(0, ix) + URLUTF8Encoder.encode(uParameterPart) + s.substring(ixf, s.length());

            ix = ixf;

        }
        //System.out.println("out ix:"+ix+" ixf:"+ixf+" s:"+s);
        s = s.substring(0, ix) + URLUTF8Encoder.encode(s.substring(ix, s.length()));

        return s;
    }

    /************************************
     *
     *  DOUBLE QUOTE
     */
    static String doubleQuotes(String s) {
        int ix = 0;
        while ((ix = s.indexOf("'", ix)) > -1) {
            s = s.substring(0, ix) + "''" + s.substring(ix + 1, s.length());
            ix += 2;
        }
        return s;

    }

    /************************************
     *
     *  For update ....
     */
    static String cleanValue(String encodeType, String s) {
        int ix = 0;
        if (encodeType.equals("XML")) {
            while ((ix = s.indexOf("&", ix)) > -1) {
                s = s.substring(0, ix) + "&amp;" + s.substring(ix + 1, s.length());
                ix += 4;
            }
            ix = 0;
            while ((ix = s.indexOf("<", ix)) > -1) {
                s = s.substring(0, ix) + "&lt;" + s.substring(ix + 1, s.length());
                ix += 3;
            }
        }
        ix = 0;
        while ((ix = s.indexOf("'", ix)) > -1) {
            s = s.substring(0, ix) + "''" + s.substring(ix + 1, s.length());
            ix += 3;
        }

        return s;

    }

    /************************************
     *
     *  REPLACE PARAMETERS
     */
    static String replaceParameters(String s, String[] params) {
        int nbparam = 0;
        String paramVal = "";

        int ix = 0, ixf, ixx;

        if (params != null) {
            nbparam = params.length;
        }

        while ((ix = s.indexOf("<<??param-", ix)) > -1) {
            int ipara = 0;
            ixf = s.indexOf("//??>>", ix);
            // System.out.println("replacing "+s.substring(ix, ixf)+" at "+ix+"--"+ixf);

            ixx = ix + 10; // length of "<<??param-"
            while (ixx < ixf) {
                ipara = ipara * 10 + (s.charAt(ixx) - '0');
                ixx++;
            }
            if (ipara < nbparam) {
                paramVal = doubleQuotes(params[ipara]);
            } else {
                paramVal = " ";
            }

            s =
                    s.substring(0, ix) + "'" + paramVal + "'" + s.substring(ixf + 6, s.length());
        }
        return s;
    }
//    /************************************
//     *
//     *  REPLACE PARAMETERS
//     */
//    static String replaceSystemParameters(String s,HttpSession session, Hashtable text, String projectid) {
//        
//        int ix = 0, ixf, ixx;
//        String replacevalue="";
//        
//        while ((ix = s.indexOf(NodeServer.SystParamPrefix, ix)) > -1) {
//            int ipara = 0;
//            ixf = s.indexOf(NodeServer.SystParamSuffix, ix);
//            ixx = ix + 2; // length of "[["
//            String var=s.substring(ixx,ixf);
//            if (NodeServer.verboseReplace) System.out.println("replacing "+s.substring(ix, ixf)+" at "+ix+"--"+ixf+" var:"+var);
//            if (!var.substring(0,1).equals("?")){  // not a project variable
//                if (!var.substring(0,1).equals("!")){  // not a request variable
//                    replacevalue=(String) (session.getAttribute(s.substring(ixx,ixf)));
//                }
//                else {replacevalue =NodeServer.SystParamPrefix+var+NodeServer.SystParamSuffix; // a request variable dont modify now
//                }
//            }
//            else {
//              String userlang=((String)(session.getAttribute("LANG")));
//              if (var.substring(1,2).equals("!")){  // project system variable jg 31.3.2003
//                    userlang=NodeServer.defaultLang;
//                }
// 
//              String keytxt=projectid+","+userlang+","+var.substring(1, var.length());
//              if (NodeServer.verboseReplace) System.out.println("look for "+keytxt);
//              String value=(String)text.get(keytxt);
//            if (value==null) {// try SHARE
//                keytxt="SHARE"+","+((String)(session.getAttribute("STYLE")))+","+var.substring(1, var.length());
//                if (NodeServer.verboseReplace) System.out.println("look for "+keytxt);
//                value=(String)text.get(keytxt);
//                if (value==null) {
//                    value="no definition for: "+var;}
//            }
//            replacevalue=value;
//            }
//            s = s.substring(0, ix)
//            + replacevalue
//            + s.substring(ixf + 2, s.length()); // 2 length ]]
//            ix=ixx;
//        }
//        return s;
//    }
//    
//    static void replaceQueryParameters(HttpSession session, String[] u) {
//        
//        if (u==null) return;
//        for (int i=0 ;i<u.length ;i++){
//            int ix = 0, ixf, ixx;
//            String replacevalue="";
//            String s=u[i];
//            while ((ix = s.indexOf(NodeServer.SystParamPrefix, ix)) > -1) {
//                int ipara = 0;
//                ixf = s.indexOf(NodeServer.SystParamSuffix, ix);
//                ixx = ix + 2; // length of "[[!"
//                String var=s.substring(ixx,ixf);
//                replacevalue=(String) (session.getAttribute(s.substring(ixx,ixf)));
//                if (NodeServer.verboseReplace) System.out.println("replacing "+s.substring(ix, ixf)+" at "+ix+"--"+ixf+" var:"+var+" ->:"+replacevalue);
//                s = s.substring(0, ix)
//                + replacevalue
//                + s.substring(ixf + 2, s.length()); // 2 length ]]
//                ix=ixx;
//            }
//            u[i]= s;
//        }
//    }
//    
//    
//    static String concatParameters(String[] params) {
//        int nbparam = 0;
//        String s = "";
//        
//        int ix = 0, ixf, ixx;
//        
//        if (params != null) {
//            nbparam = params.length;
//            for (int i = 0; i < nbparam; i++)
//                s += "|" + params[i];
//        }
//        
//        return s;
//    }
//    
}
