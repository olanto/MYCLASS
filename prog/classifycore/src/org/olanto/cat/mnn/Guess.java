/**********
    Copyright © 2003-2014 Olanto Foundation Geneva

   This file is part of myCLASS.

   myLCASS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    myCAT is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with myCAT.  If not, see <http://www.gnu.org/licenses/>.

**********/

package org.olanto.cat.mnn;



import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.reflect.*;
import org.olanto.util.Timer;

/** Objet pour retourner un choix du catégoriseur.
  */

public class Guess{
    
    /** Nom de la catégorie choisie
     */    
    public String categorie="Not defined";
    int catid=-1;
    /** poids de l'évalution de ce choix
     */    
    public double weight=0;
    
    /** constructeur pour créer un choix du catégoriseur
     */    
    public Guess(){};
    
    Guess(int _catid, double _weight){
        categorie="not translate";
        catid=_catid;
        weight=_weight;
    }
    
    Guess(String _categorie, int _catid, double _weight){
        categorie=_categorie;
        catid=_catid;
        weight=_weight;
    }
    
    
} // end class


