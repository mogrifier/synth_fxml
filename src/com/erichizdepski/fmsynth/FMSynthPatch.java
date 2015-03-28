/*
 * FMSynthPatch.java
 *
 * Created on June 26, 2005, 7:21 AM
 */

package com.erichizdepski.fmsynth;

import javax.sound.midi.*;
import java.io.*;
/**
 * A Java Bean describing the parameters of an audio program.
 *
 * @author Erich Izdepski
 */
public class FMSynthPatch implements Serializable
{
    private double carrierFreq = 440; //up to 10000?
    private double modFreq = 220; //ratio to freq
    private double modIndex = .73;  //0 to 1
    private double modIndexMax = 1; //range 0 to 1; 1ow is best for music
    private double amplitude = 0.5; // 0 to 1
    private double freqRatio = 0.5; // 5 to .05
    private String name = null;
    private LFO modlfo = new LFO(LFO.SAW, 0, 0);  
    private FxWrapper fx = new FxWrapper(FX.CHEB2,  0);
    public static FMSynthPatch PATCH = new FMSynthPatch(1, .66);
    
    public FMSynthPatch()
    {
        
    }
    
    public FMSynthPatch(double ratio, double mi)
    {
        //uses mostly default values to start
        this.setFreqRatio(ratio);
        this.setModIndex(mi);
    }  
    
    
    public FMSynthPatch(double cf, double mf, double ratio, double mi, 
                double amp, double max)
    {
        this.setCarrierFreq(cf);
        this.setModFreq(mf);
        this.setFreqRatio(ratio);
        this.setModIndex(mi);
        this.setAmplitude(amp);
        this.setModIndexMax(max);
    }

    //need write object and read object?? can't recall.
    
    
    public double getCarrierFreq()
    {
        return carrierFreq;
    }

    public void setCarrierFreq(double carrierFreq)
    {
        this.carrierFreq = carrierFreq;
    }

    public double getModFreq()
    {
        return modFreq;
    }

    public void setModFreq(double modFreq)
    {
        this.modFreq = modFreq;
    }

    public double getModIndex()
    {
        return modIndex;
    }

    public void setModIndex(double modIndex)
    {
        this.modIndex = modIndex;
    }

    public double getAmplitude()
    {
        return amplitude;
    }

    public void setAmplitude(double amplitude)
    {
        this.amplitude = amplitude;
    }
    

    public double getFreqRatio()
    {
        return freqRatio;
    }

    public void setFreqRatio(double freqRatio)
    {
        this.freqRatio = freqRatio;
    }    

    public double getModIndexMax()
    {
        return modIndexMax;
    }

    public void setModIndexMax(double modIndexMax)
    {
        this.modIndexMax = modIndexMax;
    }

    public String toString()
    {
        //FIXME include new params
        return "\n\tfreq= " + this.carrierFreq + "\n\tmodIndex=" + this.modIndex +
                "\n\tratio=" + this.freqRatio + "\n\tmax mod=" + this.modIndexMax
                + "\n\tamp =" + this.amplitude;
    }

    public LFO getModlfo()
    {
        return modlfo;
    }

    public void setModlfo(LFO modlfo)
    {
        this.modlfo = modlfo;
    }

    public FxWrapper getFx()
    {
        return fx;
    }

    public void setFx(FxWrapper fx)
    {
        this.fx = fx;
    }

 

}
