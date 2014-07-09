/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.swabunga.spell.examples;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/** This class shows an example of how to use the spell checking capability.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class JG_test  {
    
//ma
    private static String dictFile = "C:/JG/VLI_RW/dict/fr.dic";
    private static String phonetFile = "C:/JG/VLI_RW/dict/phonet.fr";
    
    private static SpellChecker spellCheck = null;
    
    
    public JG_test() {
        SpellDictionary dictionary=null;
        try {
            dictionary = new SpellDictionaryHashMap(new File(dictFile), new File(phonetFile));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        spellCheck = new SpellChecker(dictionary);
        
        test("frase");
        test("faclulté");
        test("faKulté");
        test("jauli");
        test("filozofie");
        test("filozofi");
        test("fisik");
        test("ortograf");
        test("démocrassie");
        test("cui");
        test("cour");
        test("benzineb");
        test("benzined");
        
        
    }
    
    void test(String t){
        Timer t1=new Timer("check:"+t);
        if(!spellCheck.isCorrect(t)){
            List suggest=spellCheck.getSuggestions(t,0);
            System.out.println("nb suggestion:"+suggest.size());
            for (Iterator suggestedWord = suggest.iterator(); suggestedWord.hasNext();) {
                System.out.print(" "+suggestedWord.next()); 
            }
            System.out.println();
        }
        else {System.out.println("ok:"+t);
                    List suggest=spellCheck.getSuggestions(t,0);
            System.out.println("nb suggestion:"+suggest.size());
            for (Iterator suggestedWord = suggest.iterator(); suggestedWord.hasNext();) {
                System.out.print(" "+suggestedWord.next()); 
            }
            System.out.println();
        }
        t1.stop();
    }


public static void main(String[] args) {
    new JG_test();
}
}
