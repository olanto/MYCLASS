package isi.jg.conman;

import isi.jg.idxvli.*;
import static isi.jg.util.Messages.*;

/**
 * Une classe pour indexer les contents manager.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class ContentIndex {

    ContentStructure cm;
    IdxStructure id;

    public ContentIndex(ContentStructure cm, IdxStructure id) {
        this.cm = cm;
        this.id = id;
    }

    /** Affiche dans la console des statistiques sur l'indexeur.
     */
    public void indexAll() {

        for (int i = 0; i < cm.lastdoc; i++) {
            if (cm.isIndexable(i)) {
                String name = cm.getFileNameForDocument(i);
                String content = cm.getStringContent(i);
                msg("index " + i + " :" + name + " length:" + content.length());
                id.indexThisContent(name, content);
                cm.setIndexed(i);
            }
        }

    }
}
