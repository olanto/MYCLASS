package isi.jg.idxvli.doc;

import isi.jg.idxvli.util.*;
import isi.jg.idxvli.*;
import java.util.List;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxConstant.*;
import static isi.jg.idxvli.IdxEnum.*;

/**
 * gestionnaire de documents.
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2004
 * <p>l'utilisation de cette classe est strictement limit�e au groupe ISI et � MetaRead
 * dans le cadre de l'�tude pr�liminaire pour le parlement europ�en
 * <p>
 * gestionnaire de documents. cette version est all�g�e pour ne pas tenir compte des dates, des properties, des ...
 * seul les noms des documents sont conserv�s et donc le mode IdxMode.INCREMENTAL;
 */


public class Documents1_ligth implements DocumentManager{
    
    
    
    private StringRepository documentName;
        /** structure contenant les documents non-indexables*/
    private SetOfWords dontIndexThisDocuments;

    
    private IdxMode updatingMode=IdxMode.INCREMENTAL;
//    private LanguageMode keepLanguage;
//    private CollectionMode keepCollection;
    
    /** cr�er une nouvelle instance de repository pour effectuer les create, open*/
    public Documents1_ligth() {}
    
    /**  cr�e un gestionnaire de documents (la taille et la longueur) � l'endroit indiqu� par le path */
    public final DocumentManager create(implementationMode _ManagerImplementation, LanguageMode _keepLanguage,  CollectionMode _keepCollection,
    String _path, String _idxName, int _maxSize, int _lengthString) {
        return (new Documents1_ligth(_ManagerImplementation,_keepLanguage, _keepCollection,
        _path,_idxName, _maxSize,_lengthString));
    }
    
    /**  ouvre un gestionnaire de documents  � l'endroit indiqu� par le _path */
    public final DocumentManager open(implementationMode _ManagerImplementation, LanguageMode _keepLanguage,  CollectionMode _keepCollection,
    IdxMode _updatingMode, String _path, String _idxName) {
        return (new Documents1_ligth(_ManagerImplementation,_keepLanguage, _keepCollection,
        _updatingMode, _path,_idxName));
    }
    
    /**  ferme un gestionnaire de mots  (et sauve les modifications*/
    public final void close(){
        msg("mode close:"+updatingMode.name());
        documentName.close();
      //  }
        msg("--- DocumentManager is closed now ");
    }
    
    /** cr�er une nouvelle instance de DocumentManager � partir des donn�es existantes*/
    private Documents1_ligth(implementationMode _ManagerImplementation, LanguageMode _keepLanguage,  CollectionMode _keepCollection,
    IdxMode _updatingMode, String _pathName, String _idxName) {  // recharge un gestionnaire
                if (updatingMode==IdxMode.DIFFERENTIAL){
            error("Documents1_ligth must be in INCREMENTAL mode");
        }
updatingMode=_updatingMode;
         switch(_ManagerImplementation) {
            case FAST:
                documentName=(new StringTable_HomeHash_InMemory()).open(_pathName,_idxName+"_name");
                break;
            case BIG:
                documentName=(new StringTable_HomeHash_OnDisk_DIO()).open(_pathName,_idxName+"_name");
                break;
            case XL:
                documentName=(new StringTable_OnDisk_WithCache_MapIO_XL()).open(_pathName,_idxName+"_name");
                break;
            case XXL:
                documentName=(new StringTable_OnDisk_WithCache_XXL()).open(_pathName,_idxName+"_name");
                break;
        } 
         msg("load excluding list of documents:"+IDX_DONTINDEXTHISDOC);
         dontIndexThisDocuments = new SetOfWords(IDX_DONTINDEXTHISDOC);

    }
    /** cr�er une nouvelle instance de Document Manager*/
    private Documents1_ligth(implementationMode _ManagerImplementation, LanguageMode _keepLanguage,  CollectionMode _keepCollection,
    String _pathName, String _idxName, int _maxSize, int _lengthString){
        switch(_ManagerImplementation) {
            case FAST:
                documentName=(new StringTable_HomeHash_InMemory()).create(_pathName,_idxName+"_name",_maxSize+1,-1); // on double la taille pour les collisions
                break;
            case BIG:
                documentName=(new StringTable_HomeHash_OnDisk_DIO()).create(_pathName,_idxName+"_name",_maxSize+1,_lengthString);
                break;
            case XL:
                documentName=(new StringTable_OnDisk_WithCache_MapIO_XL()).create(_pathName,_idxName+"_name",_maxSize,_lengthString);  // sauf pour celui-ci on accepte plus de collision
                break;
            case XXL:
                documentName=(new StringTable_OnDisk_WithCache_XXL()).create(_pathName,_idxName+"_name",_maxSize+1,_lengthString);
                // doit �tre remplac� par une implementation sur disque
                break;
        }
    }
    
    
    
    /**  ajoute un document au gestionnaire retourne le num�ro du docuemnt*/
    public final int put(String d){
        //msg("add this:"+d);
        int id=documentName.put(d);
        return id;
    }
    
    /**  cherche le num�ro du document, retourne EMPTY s'il n'est pas dans le dictionnaire  */
    public final int get(String d){
        return documentName.get(d);
    }
    
    /**  cherche le document associ� � un num�ro, retourne NOTINTHIS s'il n'est pas dans le dictionnaire*/
    public final String get(int i){
        return documentName.get(i);
    }
    
    /**  imprime des statistiques */
    public final void printStatistic(){
        msg("------------------------------------------------------------");
        msg("- DOCS TABLE STAT                                         -");
        msg("------------------------------------------------------------");
        msg("mode:"+updatingMode.name());
        msg("REFERENCE STRUCTURE:");
        documentName.printStatistic();
        msg("");
     }
    
    /**  retourne le nbr de mots dans le dictionnaire */
    public final int getCount(){
        return documentName.getCount();
    }
    
    /**  enregistre la date pour le document i */
    public final void setDate(int i,long date){
        // rien faire documentDate.set(i,date);
    }
    
    /**  demande  la date pour le document i */
    public final long getDate(int i){
        return -1; // rien faire
    }
    
    /**  enregistre la taille pour le document i */
    public final void setSize(int i,int size){
        // rien faire documentSize.set(i,size);
    }
    
    /**  demande  la taille pour le document i */
    public final int getSize(int i){
        return -1; // rien faire
    }
            /**
     * enregistre la propi�t� pour le document i
     * @param i num�ro du document
     * @param properties propir�t�
     */
    public void setPropertie(int i, String properties){
        // rien faire documentProperties.put(properties,i);
    }
           /**
     * �limine la propi�t� pour le document i
     * @param i num�ro du document
     * @param properties propir�t�
     */
    public void clearPropertie(int i, String properties){
       // rien faire documentProperties.clear(properties,i);
    }
    
    /**
     * Retourne la taille pour le document i
     * @param i num�ro du document
      * @param properties propri�t�
    * @return la valeur de cette propi�t�
     */
    public boolean getPropertie(int i, String properties){
        return false;// rien faire documentProperties.get(properties,i);
    }

    
    /**  verifie si le document est � indexer (nom diff�rent, date diff�rente */
    public final boolean IndexThisDocument(String fname, long date){
//        msg("test:"+fname);
        if (dontIndexThisDocuments.check(fname))return false; // ne pas indexer
        int id=get(fname);  // cherche si le document est d�j� index�
        if (id==StringRepository.EMPTY) return true; // un nouveau document
         return false; // cas le document est identique,
    }
    
    /**  rend invalide le document i */
    public final void invalid(int i){
// rien faire
    }
    
    /**  test si le document i  est valide */
    public final boolean isValid(int i){
        return true ;// rien faire !documentInvalid.get(i);
    }
    
    /**  le nombre de document valides*/
    public final int countValid(){
        return documentName.getCount();
    }
    
    
     
        /**  cherche toutes les documents poss�dant une propri�t�s*/
    public final SetOfBits satisfyThisProperty(String properties){
          return null; // rien faire    
    }

     /**
     *  r�cup�re le dictionnaire de propri�t�s
     * @return liste des propri�t�s actives
     */
    public List<String> getDictionnary() {
        return null;
    }

    /**
     *  r�cup�re le dictionnaire de propri�t�s ayant un certain pr�fix (COLECT., LANG.)
     * @param prefix pr�fixe des propri�t�s
     * @return liste des propri�t�s actives
     */
    public List<String> getDictionnary(String prefix) {
        return null;
    }

        /**
     * propage l'invalidit� des documents dans les propri�t�s
     */
    public void propagateInvalididy() {
          // pour �tre conforme � l'interface
          // pas de protection sp�ciale pour la concurrence
    }

}


