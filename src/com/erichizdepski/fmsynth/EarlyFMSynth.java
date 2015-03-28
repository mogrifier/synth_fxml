/*
 * FMSynth.java
 *
 */
package com.erichizdepski.fmsynth;

import com.erichizdepski.fmsynth.utils.AudioUtils;

/**
 * Early version FM Synth. Not real time; the audio samples are written to a 
 * file for playback through a tool such as windows media player.
 *
 * @author  Erich Izdepski
 */
public class EarlyFMSynth
{
    //variable
    private double carrierFreq;
    private double modFreq;
    private double modIndex;
    private double amplitude;
    
    //fixed
    private double sampleInterval;
    private String fileName = null;
    private double max;
    private double min;
    int numSamples;
    short[] samples = null;
    
    private boolean playing = true;
    private static double twopi = Math.PI * 2;
    private static double modIndexMax = 5;
    
    /** Creates a new instance of FMSynth */
    public EarlyFMSynth(double cf, double mf, double mi, double amp, double si, int num, String name)
    {
        this.carrierFreq = cf;
        this.modFreq = mf;
        this.modIndex = mi;
        this.amplitude = amp;
        this.sampleInterval = si;
        this.numSamples = num;
        this.fileName = name;
        samples = new short[num];
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        EarlyFMSynth synth = new EarlyFMSynth(800, 2000, .77, (double)Math.PI/25, 
            (double)1/41000, 100000, "c:\\fmsynth.wav");
        
        synth.synthesize();
    }
    
    /** Getter for property amplitude.
     * @return Value of property amplitude.
     *
     */
    public double getAmplitude()
    {
        return amplitude;
    }    
    
    /** Setter for property amplitude.
     * @param amplitude New value of property amplitude.
     *
     */
    public void setAmplitude(double amplitude)
    {
        this.amplitude = amplitude;
    }
    
    /** Getter for property carrierFreq.
     * @return Value of property carrierFreq.
     *
     */
    public double getCarrierFreq()
    {
        return carrierFreq;
    }
    
    /** Setter for property carrierFreq.
     * @param carrierFreq New value of property carrierFreq.
     *
     */
    public void setCarrierFreq(double carrierFreq)
    {
        this.carrierFreq = carrierFreq;
    }
    
    /** Getter for property modFreq.
     * @return Value of property modFreq.
     *
     */
    public double getModFreq()
    {
        return modFreq;
    }
    
    /** Setter for property modFreq.
     * @param modFreq New value of property modFreq.
     *
     */
    public void setModFreq(double modFreq)
    {
        this.modFreq = modFreq;
    }
    
    /** Getter for property modIndex.
     * @return Value of property modIndex.
     *
     */
    public double getModIndex()
    {
        return modIndex;
    }
    
    /** Setter for property modIndex.
     * @param modIndex New value of property modIndex.
     *
     */
    public void setModIndex(double modIndex)
    {
        this.modIndex = modIndex;
    }
    
    /** Getter for property numSamples.
     * @return Value of property numSamples.
     *
     */
    public int getNumSamples()
    {
        return numSamples;
    }
    
    /** Setter for property numSamples.
     * @param numSamples New value of property numSamples.
     *
     */
    public void setNumSamples(int numSamples)
    {
        this.numSamples = numSamples;
    }
    
    /** Getter for property sampleInterval.
     * @return Value of property sampleInterval.
     *
     */
    public double getSampleInterval()
    {
        return sampleInterval;
    }
    
    /** Setter for property sampleInterval.
     * @param sampleInterval New value of property sampleInterval.
     *
     */
    public void setSampleInterval(double sampleInterval)
    {
        this.sampleInterval = sampleInterval;
    }

       
    public void synthesize()
    {
        //assumes radians are in use
        double carIncr; //phase step due to carrier frequency
        double modPhase; //phase of the modulator
        double modIncr; //step due to modulation
        double modOffset; //phase offset for scaling modulator
        double theta; //phase of output
        double ampOffset; //phase offset for scaling output
        
        //initialize variables
        carIncr = twopi * this.getCarrierFreq() * this.getSampleInterval();
        modIncr = twopi * this.getModFreq() * this.getSampleInterval();
        modOffset = 2 * Math.acos(this.getModIndex());
        ampOffset = 2 * Math.acos(this.getAmplitude());
        System.out.println(ampOffset);
        theta = 0;
        modPhase = 0;
        
        for (int n = 0; n < this.getNumSamples(); n++)
        {
            modPhase = modPhase + modIncr;
            theta = (theta + carIncr + sin2(modPhase + modOffset) 
                        + sin2(modPhase - modOffset)) % twopi;
            //System.out.println(theta);
            //store the data- convert it to a short (for 16-bit audio)
            samples[n] = scaleToShort(Math.sin(theta + ampOffset) + Math.sin(theta - ampOffset));
        }
        
        System.out.println(max);
        System.out.println(min);
        
        //write the array of raw 16-bit audio data to a file as a series of bytes (MSB, LSB?)
        AudioUtils.writeMonoAudioFile(samples, this.getFileName());
    }
    
 
    private double sin2(double rads)
    {
       return Math.sin(rads) * this.modIndexMax; 
    }
    
    
    private short scaleToShort(double raw)
    {
        if (raw > max)
        {
            max = raw;
        }
        
        if (raw < min)
        {
            min = raw;
        }

        //raw should be in range -2 to +2 max- convert to range of a short
        double rounded = ((raw + 2)/4 * 65535) - Short.MIN_VALUE;
        
        return (short)rounded;
    }
    
    /** Getter for property fileName.
     * @return Value of property fileName.
     *
     */
    public java.lang.String getFileName()
    {
        return fileName;
    }
    
    /** Setter for property fileName.
     * @param fileName New value of property fileName.
     *
     */
    public void setFileName(java.lang.String fileName)
    {
        this.fileName = fileName;
    }
    
}
