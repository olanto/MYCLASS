package isi.jg.idxvli;

import static isi.jg.util.Messages.*;
import java.io.*;

/**
 * Une classe pour déclarer les types énumérés.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
 */
public class IdxEnum {

    /** Mode d'indexation. */
    public static enum RankingMode {

        /**pas de ranking */
        NO,
        /**classique inverse document frequency * term document frequency */
        IDFxTDF,
        /**classique BM25 adapted */
        BM25,
        /**classique BM25 adapted */
        BM25TWICE
    }

    
    

       

        ;
        /** Mode d'indexation. */
        
        
        
    

    
    

    public static  enum IdxMode {

        /**nouvelle indexation */
        NEW,
        /**On ajoute des documents (ou éventuellement on remplace des documents*/
        INCREMENTAL, /**On ajoute des documents, remplace et élimine les documents absents */
        

    
    

      DIFFERENTIAL 

        ,
        /**interrogation de l'index, pas de mise à jour */
        QUERY
    }

    
    

       

        ;
        /** Mode Mix. */
        
        
        
        
    

    
    

    public static  enum ContinueMode {

        /** en lecture et en écriture simultané */
        MIX,
        /** en lecture ou en écriture */
        ALT
    }

    
    

       

        ;
        /** Mode d'ouverture des objets */
        
    

    
    

    public static  enum readWriteMode {

        /**lecture seulement */
        r,
        /**lecture/écriture */
        rw
    }

    
    

       

        ;
        /** stratégie d'implémentation */
        
        
    

    
    

    public static  enum implementationMode {

        /**privilégie la rapidité et les noms des fichiers sont des numéros (pour le content manager) */
        DIRECT,
        /**privilégie les volumes de grande taille */
        FAST, /**privilégie les volumes de grande taille */
        

    
    

      BIG 

        ,
        /**privilégie les volumes de grande taille avec un accès disque au string */
        XL,
        /**privilégie les volumes de très grande taille */
        XXL
    }

    
    

       

        ;
        /** conserve des propriétés de langage */
        
    

    
    

public static enum LanguageMode {
        /**oui*/
        YES,
        /**non*/
        NO
    };
    
    /** conserve des propriétés de collection */
    public static enum CollectionMode {
        /**oui*/
        YES,
        /**non*/
        NO
    };
    /** la compression est activée */
    public static enum Compression {
        /**oui*/
        YES,
        /**non*/
        NO
    };
    
    /** type de mapping pour les fichiers  */
    public static enum MappingMode {
        /** pas de mapping */
        NOMAP,
        /** mapping total  */
        FULL,
        /** mapping partiel  */
        CACHE  };
        
        /** état d'un verrouillage  */
        public static enum StateMark {
            /** verrouiller */
            LOCK,
            /** déverrouiller */
            UNLOCK  };
            
            /** type d'utilisation de l'index d'un mot  */
            public static enum UsageMark {
                /** de base (sans les positions) */
                BASIC,
                /** complet (avec les positions) */
                FULL,
                /** pas utilisé*/
                UNUSED  };
                
                /** type de stockage de l'index */
                public static enum PassMode {
                    /** en une passe, on indexe et on stocke simultanément */
                    ONE,
                    /** en une passe, on indexe et on stocke successivement */
                    TWO  };
                    
}
