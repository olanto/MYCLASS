package isi.jg.profil;
import isi.jg.idxvli.*;
import isi.jg.idxvli.knn.*;
import isi.jg.util.Timer;
import java.io.*;
import java.util.*;
import static isi.jg.util.Messages.*;
import static isi.jg.idxvli.IdxEnum.*;
import isi.jg.util.TimerNano;


/**
 * *
 * <p>
 * <b>JG-2008
* <p>author: Jacques Guyot
 * <p>copyright Jacques Guyot 2008
 * <p>l'utilisation de cette classe est strictement limitée aux ayant droit
 * <p>l'utilisation de cette classe nécessite une autorisation explicite de son auteur
 * JG-2008</b>
 *<p>
  *
 *
 * Test de l'indexeur, mode query
 */
public class TESTKNNMode_1{
    
    
    private static IdxStructure id;
    private static Timer t1=new Timer("global time");
    
    private static KNNManager KNN;
    
    /**
     * application de test
     * @param args sans
     */
    public static void main(String[] args)   {
        
        id=new IdxStructure("QUERY");
        // création de la racine de l'indexation
        id.createComponent(new Configuration());
        
        // charge l'index (si il n'existe pas alors il est vide)
        id.loadIndexDoc();
        
        id.Statistic.global();
        
        Timer t2=new Timer("init KNN");
        
        //for (int i=100000;i<101000;i++) msg(i+" : "+id.getOccOfW(i)); // test la fonction de longueur
        
        KNN=new TFxIDF_ONE();
        KNN.initialize(id,    // Indexeur
                5,     // Min occurence d'un mot dans le corpus (nbr de documents)
                50,    // Max en o/oo d'apparition dans le corpus (par mille!)
                true,   // montre les détails
                1,        // formule IDF (1,2)
                1       // formule TF (1,2,3) toujours 1
                );
        t2.stop();
        int ntop=100;
//        test1("telecomunication wifi computer",10);
//        test1("The broadcast function provided by  the  MPCP  binding  is  limited  to  the "+
//                "subnetwork in which it exists. It may form part of  a  multicast  (selective "+
//                "broadcast) function within a larger (containing) subnetwork. "+
//                "Four types of Multipoint connections Broadcast, Merge, Composite,  and  Full "+
//                "Multipoint are shown in Figure  I.1  using  a  MultiPoint  Connection  Point "+
//                "(MPCP). The MPCP denotes the Root  of  the  Multipoint  connection  for  the "+
//                "Broadcast, Merge, and Composite  types,  where  the  Connection  Point  (CP) "+
//                "denotes the leaf. For the Full Multipoint connection,  the  MPCP  denotes  a "+
//                "hybrid Root/Leaf. Note that the directionality refers only  to  the  traffic "+
//                "flow, the OAM flow are for further study(see ITU-T Rec. I.610)."
//                ,ntop);
//        
//        test1("The present invention relates to an arrangement for a hand tool (1), for example a spade, a pitchfork or a shovel. The tool comprises a shaft (2), which at one of its ends is attached to a tool component (3) and at its other end is executed with a handle (4). The invention is characterized in that the hand tool component (3) is attached to the shaft (2) in such a way that its principal direction of longitudinal extension (5) forms an angle (v1) of the order of 140°-150° with the shaft (2) at the attachment. The length of the shaft (2) is of the order of 1-1.5 m, and is executed at a point approximately half way along its length with a bend (6), so that the part (2a) of the shaft (2) between the handle (4) and the bend (6) forms an angle (v2) with the part (2b) of the shaft (2) situated between the bend (6) and the tool attachment. The last-mentioned angle (v2) is selected so that the first-mentioned part (2a) of the shaft (2) is approximately parallel with the aforementioned principal direction of longitudinal extension of the tool component (3)"
//                ,ntop);
        msg("le meuble");
        test1("A table is a form of furniture composed of a horizontal surface supported by a base, usually four legs. It is often used to hold objects or food at a convenient or comfortable height when sitting. Generic tables are typically meant for combined use with chairs. Unlike many earlier table designs, today's tables usually do not have drawers. A table specifically intended for working is a desk. Some tables have hinged extensions of the table top called drop leaves, while others can be extended with removable sections called leaves."
          ,ntop);
        msg("l'information");
        test1("A table is both a mode of visual communication and a means of arranging data. The use of tables is pervasive throughout all communication, research and data analysis. Tables appear in print media, handwritten notes, computer software, architectural ornamentation, traffic signs and many other places. The precise conventions and terminology for describing tables varies depending on the context. Moreover, tables differ significantly in variety, structure, flexibility, notation, representation and use."
        ,ntop);
      msg("la base de données");
        test1("In relational databases, SQL databases, and flat file databases, a table is a set of data elements (values) that is organized using a model of horizontal rows and vertical columns. The columns are identified by name, and the rows are identified by the values appearing in a particular column subset which has been identified as a candidate key. Table is the lay term for relation. A table has a specified number of columns but can have any number of rows. Besides the actual data rows, tables generally have associated with them some meta-information, such as constraints on the table or on the values within particular columns."
       ,ntop);
    }
    
    private static void test(String s,int nTop){
        TimerNano t1=new TimerNano("KNN: "+s+" "+nTop,false);
        KNN.showKNN(KNN.getKNN(s,nTop));
        t1.stop(false);
    }
    private static void test1(String s,int nTop){
        TimerNano t1=new TimerNano("KNN: "+s+" "+nTop,false);
        float[] sim=KNN.getSimilarity(s);
        showSIM(sim,0.002f);
        showDIST(sim);
        KNN.showKNNWithName(KNN.getKNN(s,nTop));
        t1.stop(false);
    }
    
    
    public final static  void showSIM(float[] p,float min) {
        if (p!=null){
            int l = p.length;
            int notnull=0;
            for (int i = 0; i < l; i++)
                if (p[i]>=min) {
                msgnoln(p[i] + "\n");
                notnull++;
                }
            msg("Length=" + l +" notnull=" + notnull);
        } else msg("Is null");
    }
    
    public final static  void showDIST(float[] p) {
        if (p!=null){
            int l = p.length;
            int not1=0;
            int not2=0;
            int not3=0;
            int not4=0;
            int not5=0;
            int not6=0;
            for (int i = 0; i < l; i++)
                if (p[i]>0.1) not1++;
                else if (p[i]>0.01) not2++;
                else if (p[i]>0.001) not3++;
                else if (p[i]>0.0001) not4++;
                else if (p[i]>0.00001) not5++;
                else not6++;
            msg("Length=" + l +
                    "\n> 0.1 =" + not1+
                    "\n> 0.01 =" + not2+
                    "\n> 0.001 =" + not3+
                    "\n> 0.0001 =" + not4+
                    "\n> 0.00001 =" + not5+
                    "\n<= 0.00001 =" + not6
                    );
        } else msg("Is null");
    }
    
    
}