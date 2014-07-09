/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package isi.jg.deploy.demo.alpha;

import static org.olanto.util.Messages.*;
/**
 *
 * @author jg
 */
public class TESTClassify {

       public static void main(String[] args)   {
             Classify.init();
             // mc="A01B003421"
             String toBeClassify="A valve assembly (20, 20') for automatically reversing an actuator"
+"(1, 2),in particular a double-acting cylinder, which is operated"
+"by a pressure medium. The valve assembly comprises a first and a second"
+"port (21, 22), it being possible for each of the first and second ports"
+"(21, 22) to be selectively connected to a supply for a pressure medium"
+"while the other can be connected to a reservoir, and a third and fourth"
+"port (23, 24), which can be connected to the actuator (1, 2). Furthermore,"
+"a reversing valve (25) is provided, which is indirectly operated by pressure"
+"medium. The reversing valve (25) comprises a sliding body (26) which is"
+"able to adopt a first and a second position. The position of the reversing"
+"valve (25) is set by means of a first and a second pilot valve (40, 50)."
+"There is a back-pressure setting means (67, 70) between the third"
+"port (23) and the reversing valve (25), which back-pressure setting"
+"means (67, 70) allows pressure medium to flow from the third port (23)"
+"to the reversing valve (25), generating a back pressure at the third port"
+"(23).";
             msg(Classify.advise(toBeClassify));
       }
}
