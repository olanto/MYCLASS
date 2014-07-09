package isi.jg.idxvli;

import static isi.jg.util.Messages.*;
import java.io.*;

/**
 * Une classe pour d�clarer les types �num�r�s.
 * <p>
 * <b>JG-2008
 * <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limit�e aux ayant droit
 * <p>l'utilisation de cette classe n�cessite une autorisation explicite de son auteur
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
        /**On ajoute des documents (ou �ventuellement on remplace des documents*/
        INCREMENTAL, /**On ajoute des documents, remplace et �limine les documents absents */
        

    
    

      DIFFERENTIAL 

        ,
        /**interrogation de l'index, pas de mise � jour */
        QUERY
    }

    
    

       

        ;
        /** Mode Mix. */
        
        
        
        
    

    
    

    public static  enum ContinueMode {

        /** en lecture et en �criture simultan� */
        MIX,
        /** en lecture ou en �criture */
        ALT
    }

    
    

       

        ;
        /** Mode d'ouverture des objets */
        
    

    
    

    public static  enum readWriteMode {

        /**lecture seulement */
        r,
        /**lecture/�criture */
        rw
    }

    
    

       

        ;
        /** strat�gie d'impl�mentation */
        
        
    

    
    

    public static  enum implementationMode {

        /**privil�gie la rapidit� et les noms des fichiers sont des num�ros (pour le content manager) */
        DIRECT,
        /**privil�gie les volumes de grande taille */
        FAST, /**privil�gie les volumes de grande taille */
        

    
    

      BIG 

        ,
        /**privil�gie les volumes de grande taille avec un acc�s disque au string */
        XL,
        /**privil�gie les volumes de tr�s grande taille */
        XXL
    }

    
    

       

        ;
        /** conserve des propri�t�s de langage */
        
    

    
    

public static enum LanguageMode {
        /**oui*/
        YES,
        /**non*/
        NO
    };
    
    /** conserve des propri�t�s de collection */
    public static enum CollectionMode {
        /**oui*/
        YES,
        /**non*/
        NO
    };
    /** la compression est activ�e */
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
        
        /** �tat d'un verrouillage  */
        public static enum StateMark {
            /** verrouiller */
            LOCK,
            /** d�verrouiller */
            UNLOCK  };
            
            /** type d'utilisation de l'index d'un mot  */
            public static enum UsageMark {
                /** de base (sans les positions) */
                BASIC,
                /** complet (avec les positions) */
                FULL,
                /** pas utilis�*/
                UNUSED  };
                
                /** type de stockage de l'index */
                public static enum PassMode {
                    /** en une passe, on indexe et on stocke simultan�ment */
                    ONE,
                    /** en une passe, on indexe et on stocke successivement */
                    TWO  };
                    
}
