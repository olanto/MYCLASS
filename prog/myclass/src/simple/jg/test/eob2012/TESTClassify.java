/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.jg.test.eob2012;

import isi.jg.deploy.frende.*;
import org.olanto.util.TimerNano;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import static org.olanto.util.Messages.*;
/**
 *
 * @author jg
 */
public class TESTClassify {

       public static void main(String[] args)   {
             Classify.init();
              //mc="A01B73/06F"
             String toBeClassify="Agricultural implement setting mechanism on tractor - has two horizontal frames hinging on implement and on vertical axis"+
"The mechanism sets an agricultural implement in two positions in relation to a tractor, being for a manure distributor, mower, planting or cultivating machine etc. It comprises two horizontal hinging frames, each hinging at one end on the implement. - At the other end the frames swing on a common vertical axis on a supporting frame fixed to the tractor, typically under the action of a double-acting ram. The ram piston rod moves to and fro in a horizontal plane.";
             msg(Classify.advise(toBeClassify,3));
             msg(Classify.advise(toBeClassify,4));
             msg(Classify.advise(toBeClassify,8));

    }

    private static void test(String toBeClassify) {
        msg(Classify.advise(toBeClassify,4));
        msg(Classify.advise(toBeClassify,3));
    }

    private static String readTopic(String fname) {
        msg("load topic");
        String result = "";
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(fname), "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String w = in.readLine();
            while (w != null) {
                result += w;
                w = in.readLine();
            }
        } catch (Exception e) {
            error("IO error in readList", e);
        }
        return result;
    }
}
