/*
 * Constants.java
 *
 * Created on July 29, 2005, 10:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.erichizdepski.fmsynth;

/**
 *
 * @author Owner
 */
public interface Constants
{
	/*
    public static final int SINE = 2;
    public static final int SAW = 1;
    public static final int TRI = 4;
    public static final int SQUARE = 3;
    */
    
    
    //waveforms
    public static final int SINE = 0;
    
    public static final int SAW = 1;
    
    public static final int TRIANGLE = 2;
    
    public static final int SINE2 = 3;
    
    public static final int SAMPLE = 4;
    
    //good pulsating tones
    public static final int PROP = 5;
    
    //real good wave interference and strong sounds- non-integer freqRatio are best-
    //more interesting than simple SINE
    public static final int CYCLOID = 6;
    
    public static final int POLY = 7;
    
    public static final int NOISE = 8;
    public static final int SQUARE = 9;
    
    
    public static int AHz = 440;
    //0 based
    public static int A_MIDI = 69;
    
    //fx name/values
    public static int MOBIUS = 1;
    public static int SHAPER = 2;
    public static int CHEB2 = 0; 
    public static int SHAPER2 = 3;
    public static int RFILTER = 4;
    
    public static final double twopi = 6.283;
    
    public static int LFO_TO_PITCH = 2;
    public static int LFO_TO_AMPLITUDE = 1;
    public static int LFO_TO_MODINDEX = 0;
}