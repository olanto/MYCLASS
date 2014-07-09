package isi.jg.dtk;

import java.io.*;
import java.util.*;

/**
 * Une classe pour mémoriser les information sur les liens d'un automate de Markov.
 *
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2004
Copyright (C) 2005,  ISI Research Group, CUI, University of Geneva
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
 *
 */
class MLink {

    /** libellé de la référence de ce lien */
    String ref;
    /** nombre d'occurrence de ce lien */
    int nbOcc;

    /** ajoute un lien avec un libellé
     * @param reference libellé de la reférence
     */
    MLink(String reference) {
        ref = reference;
        nbOcc++;
    }

    /** ajoute une occurence à ce lien */
    void add() {
        nbOcc++;
    }
}

