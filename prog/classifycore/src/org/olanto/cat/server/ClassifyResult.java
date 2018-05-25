/**********
    Copyright © 2003-2018 Olanto Foundation Geneva

   This file is part of myCLASS.

   myCLASS is free software: you can redistribute it and/or modify
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

package org.olanto.cat.server;

import java.io.*;

/**
 * Classe stockant les  résultats d'une classification.
 * <p>
 *<p>
 */
public class ClassifyResult implements Serializable {

    /* les codes des résultats */
    private String[] category;

    public ClassifyResult(String[] category) {
        this.category = category;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(getCategory());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        category = (String[]) in.readObject();
    }

    /**
     * @return the category
     */
    public String[] getCategory() {
        return category;
    }

    /**
     * only for debug
     */
    public void show() {
        if (category != null) {
            for (int i = 0; i < category.length; i++) {
                System.out.println(i + " -> " + category[i]);
            }
        } else {
            System.out.println("category is null!");
        }
    }
}
