/*
 * MultiFM.java
 *
 * Created on June 15, 2005, 9:33 PM
 */

package com.erichizdepski.fmsynth;

  
    /** 
     * A test piece for additive synthesis. It creates multiple instances of a
     * synth engine for experimentation.
     *
     * @author Erich Izdepski
     */
public class MultiFM
{


    
    public static void main(String[] args)
    {
        FMSynthControlSurface[] synths = new FMSynthControlSurface[6];
        
        for (int i = 0; i < synths.length; i++)
        {
            synths[i].main(null);
        }
    }
    
}
