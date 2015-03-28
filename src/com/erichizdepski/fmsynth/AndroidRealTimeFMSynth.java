/*
 * FMSynth.java
 *
 */
package com.erichizdepski.fmsynth;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.erichizdepski.fmsynth.utils.AudioUtils;
/**
 * This is the FM Synthesizer sample generation engine. It is a mono, 16-bit,
 * real-time synthesis engine. Use for experimenting with new sound gen techniques.
 *
 * @author  Erich Izdepski
 */
public class AndroidRealTimeFMSynth extends Thread implements Constants
{
    
    private boolean DEBUG = true;
    private double carrierFreq;
    private double modFreq;
    private double modIndex;
    private double amplitude;
    private double modIndexMax = 1;
    private double freqRatio;
    private FxWrapper fx = new FxWrapper(0, 0);
        
    private int pitchBend = 0;
    private double modWheel = 0;
    private LFO modlfo = new LFO(LFO.SINE, 16, 10); //default

    //for tracking what the lfo is modulating
    public static int LFO_TO_PITCH = 2;
    public static int LFO_TO_AMPLITUDE = 1;
    public static int LFO_TO_MODINDEX = 0;
    private int assignLFO = LFO_TO_MODINDEX;
    
    private double sampleInterval = (double)1/44100;
    private double max;
    private double min;
    
    //to get a proper complete cyclic waveform, you must set the buffer to the sample interval inverse
    
    public static int BUFFER_SIZE = 6000;// 8192;//affects responsiveness to real-time changes-
    //smaller buffer means more responsive but not a complete cycle of the sound

    
    public static double twopi = Math.PI * 2;
    boolean alive = false;
    
    PipedInputStream playback = null;
    PipedOutputStream outflow = null;
    FMSynthPatch patch = null;
       
    
    //waveforms
    final int SINE = 0;
    
    final int SAW = 1;
    
    final int TRIANGLE = 2;
    
    final int SINE2 = 3;
    
    final int SAMPLE = 4;
    
    //good pulsating tones
    final int PROP = 5;
    
    //real good wave interference and strong sounds- non-integer freqRatio are best-
    //more interesting than simple SINE
    final int CYCLOID = 6;
    
    final int POLY = 7;
    
    int index = 0;
    byte[] sampleTable;
    
    /** Creates a new instance of FMSynth */
    public AndroidRealTimeFMSynth(FMSynthPatch patch)
        
    {
        setPatch(patch);
        
        DataInputStream input = null;
        
        //set up pipes
        try
        {
            playback = new PipedInputStream();
            outflow = new PipedOutputStream();
            playback.connect(outflow); //connecting one half is enough
            
            //FIXME - wav is little endian, aif is big endian (from sound recorder)
            File yaya = new File("C://Users//erich//Desktop//Sound3.aif");
            boolean found = yaya.exists();
            input = new DataInputStream(new FileInputStream(yaya));
            input.skipBytes(100);
            
            //read all the bytes in- convert to short later
            sampleTable = new byte[65000];
            input.read(sampleTable, 0, 65000);
            
           
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
        	
            try
            {
                if (input != null) input.close();
            }
            catch (IOException e)
            {}
            
        }

    }
    
    public void setPatch(FMSynthPatch patch)
    {
        debug(patch.toString());
        this.patch = patch;
        this.carrierFreq = patch.getCarrierFreq();
        this.modFreq = patch.getModFreq();
        this.modIndex = patch.getModIndex();
        this.amplitude = patch.getAmplitude();
        this.setFreqRatio(patch.getFreqRatio());
        this.modIndexMax = patch.getModIndexMax();
        this.pitchBend = 0;
        this.modWheel = 0;
        this.modlfo = patch.getModlfo();
        this.fx = patch.getFx();
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        //now just wire up GUI
        AndroidRealTimeFMSynth mysynth = new AndroidRealTimeFMSynth(FMSynthPatch.PATCH);         
        
        //use two threads- one for generating audio, one for playing back
        mysynth.setAlive(true);
        mysynth.start();
        Player player = new Player(mysynth.getAudioStream(), BUFFER_SIZE);
        player.setAlive(true);
        player.start();
        
        try
        {
            Thread.sleep(2500);
        }
        catch (InterruptedException e)
        {}
            
        player.setAlive(false);
        mysynth.setAlive(false);
        
        System.exit(0);
    }
    

   
    public void setAlive(boolean status)
    {
        alive = status;
    }
         
    
    public void run()
    {
        //assumes radians are in use
        double carIncr = 0; //phase step due to carrier frequency
        double modPhase = 0; //phase of the modulator
        double modIncr = 0; //phase rate of change == from frequency
        double modOffset = 0; //phase offset for scaling modulator
        double theta = 0; //phase of output
        double ampOffset = 0; //phase offset for scaling output
        double mid = 0;
        double totalMod = 0;
        double totalFreq = 0;
        
        short[] samples = new short[BUFFER_SIZE]; //8192 samples = .1 sec of audio
                
        double lfoValue = 0;
        double amp = 0;
        
        while(alive)
        {
            //reset
            //theta = 0;
            
            for (int i = 0; i < BUFFER_SIZE; i++)
            {
                    //get values plus control wheel offsets
                    
                    //lfoValue = getModlfo().getLFOValue();

                    //changed to add mod to pitch
                    totalFreq = this.getCarrierFreq() + this.getPitchBend();
                    if (assignLFO == LFO_TO_PITCH)
                    {
                    	//scale pitch depth to carrier freq. make depth into a percentage. should only use low values. < 10%
                        totalFreq  += (int) Math.rint(totalFreq * lfoValue/100.0f);
                        //System.out.println("pitch lfo=" + lfoValue);
                    }
                    
                    //debug("total freq=" + Double.toString(totalFreq));
                    
                    //FIXME -n the gui allow user to choose whether to modulate pitch or modindex
                    //pitch mod (vibrato) is producing prettier tones     
                    
                    totalMod = this.getModIndex() + this.getModWheel();
                    if (assignLFO == LFO_TO_MODINDEX)
                    {
                        totalMod += lfoValue;
                    }
                    
                    
                    //range checks
                    totalMod = (totalMod % 1) ; //keep 0 to 1-- supposed to be a fraction
                   
                    //range checks
                    totalFreq = (totalFreq < 0) ? 0 : totalFreq;
                                    
                    //the amount of the increment controls how quickly a cycle completes,
                    //hence it is the frequency. This value gives accurate pitch for sine waves.
                    carIncr = twopi * totalFreq * getSampleInterval();
                    
                    //if let constant, we don't get music. scale to carrier frequency for now;
                    modIncr = twopi * getModFreq() * getSampleInterval();
                    
                    /*
                     * inputs to acos to keep it in range are roughly -57 to 57 degrees which
                     * is -1 to +1 radians!!
                     */
                    
                    modOffset = 2 * Math.acos(getModIndex()); //(totalMod); //result is 0 to pi
                    ampOffset = 2 * Math.acos(getAmplitude() % 1); //result is 0 to pi
                    
                    
                    //modOffset = 2 * transfer(totalMod); //result is 0 to pi
                    //FIXME why is the function input modulo 1??
                    //ampOffset = 2 * transfer(this.getAmplitude() % 1); //result is 0 to pi  
                    
                    
                    modPhase+= modIncr;
                    
                    //puts theta in range -2pi to + 2pi
                    //theta = (theta + carIncr   + getWaveValue(modPhase - modOffset, SINE)) ;
                    
                    //nice- vary by fixed plus small variable component
                    //theta = (theta + carIncr +  getWaveValue(modPhase - modOffset, SQUARE));
                    
                    
                   // theta =  (theta + carIncr + getWaveValue(modPhase + modOffset, SQUARE) 
                    //        + getWaveValue(modPhase - modOffset, SQUARE)) % twopi;
                    
                    
                    
                    //pitch control by ration of modulator to carrier
                    //theta = (theta +  getWaveValue(modPhase - modOffset, SINE));
                    
                    
                  
                    
                    //just a fuzzy tonal buzz
                   // theta = (theta +  getWaveValue(modPhase + modOffset, SAW));
                    
                    //less fuzzy than square
                   // theta = (theta +  getWaveValue(modPhase + modOffset, TRIANGLE));
                    
                    //this makes frequency too high
                   // theta = (theta + carIncr + getWaveValue( modOffset, TRIANGLE));
                    
                    
                    //perfect and smooth but boring
                    //theta+= carIncr;

                    //write to the outflow pipe
                    //store up some data in a buffer for writing- use short for 16 bit audio.
                    
                    //remove ampoffset
                    //samples[i] = AudioUtils.scaleToShort(getWaveValue(theta + ampOffset, SAW) 
                       // + getWaveValue(theta - ampOffset, SAW));
                    
                    /*
                    samples[i] = (short)getWaveValue(theta , SAMPLE);
                       */
                    
                    /*
                    samples[i] = AudioUtils.scaleToShort(twopi, getWaveValue(theta, SINE) * getAmplitude());
                     */
                    
                    //value return is 0 - 3. fix for scaling???
                    /*
                    samples[i] = AudioUtils.scaleToShort(getWaveValue(theta + ampOffset, CYCLOID) 
                        + getWaveValue(theta - ampOffset, CYCLOID));       
                    */
                    
                    /*
                    samples[i] = AudioUtils.scaleToShort(getWaveValue(theta + ampOffset, POLY) 
                        + getWaveValue(theta - ampOffset, POLY));     
                    */
                    
                    /*
                     * causes pulsating sound. adds fx in.
                     * 
                    mid = (1 - fx.getAmt()) * theta + fx.getAmt() * FX.applyFx(fx.getName(), theta);
                    
                    amp = getAmplitude();
                    if (assignLFO == LFO_TO_AMPLITUDE)
                    {
                        amp += lfoValue;
                    }
                    
                    samples[i] = AudioUtils.scaleToShort(Math.PI, getWaveValue(mid, SINE));  // * (amp));
                    */
                    
                   //original formula
                    
                    /*
                    samples[i] = AudioUtils.scaleToShort(getWaveValue((theta + ampOffset), SINE) 
                        + getWaveValue((theta - ampOffset), SINE));
                     */
                                  
                  //amplitude = 0 to pi
                  //this corrects amplitude behavior- I don't notice any other change
                 // samples[i] = AudioUtils.scaleToShort(Math.PI, Math.sin(theta) * getAmplitude());
                    
                    
                    // save   samples[i] = AudioUtils.scaleToShort(1, Math.sin(theta));
                    
                    
                    //very interesting- random effects *****************************************************
                    theta = (theta +  getWaveValue(modPhase - modOffset, SINE));
                    
                    
                    samples[i] = AudioUtils.scaleToShort(1, Math.sin(theta));
                    
                  
                }
            
            try
            {
                //outflow.write(AudioUtils.shortToByte(samples));
                
                //for sample table, use bytes directly
                outflow.write(sampleTable);
            }
            catch (IOException e)
            {
                //just means other end is dead
                //e.printStackTrace();
            }
            
            
        }
            
           
        }
        
        
            
    
   public double getWaveValue(double value, int waveform)
   {
      // System.out.println(value);
	   //value = value % Math.PI; no good
       
       //do some math function based on waveform selected
       switch(waveform)
       {
           case SINE:
           {
               return Math.sin(value);
           }
           case SAW:
           {
               if (value <= Math.PI)
               {
               	//scale to 0 to 1;
               	return value/Math.PI;
               }
               //else
               return ((value - Math.PI)/Math.PI - 1f);
           }
           case TRIANGLE:
           {

	           	value = value % twopi;
	           	if (value <= (Math.PI/2f))
	           	{
	           		return value/(Math.PI / 2f);
	           	}
	           	else if (value > (1.5f * Math.PI))
	           	{
	           		return ((value - (1.5f * Math.PI))/ (Math.PI/2)) - 1f;
	           	}
	           	//else
	           	return 1f - ((value - (Math.PI * 0.5f)) / Math.PI);
           }
           case SQUARE:
           {
	           	//100% duty cycle square wave
	        	   value = value % twopi;
	        	   
	        	   //control duty cycle %
	        	   double duty =.43;
	        	   
	           	if (value <= (Math.PI * duty))
	           	{
	           		return 1;
	           	}
	           	else if (value >= ((2f - duty) * Math.PI))
	           	{
	           		return -1;
	           	}
	           	//else
	           		return 0;
           }
           case PROP:
           { 
               return Math.sqrt(4 * Math.cos(2 * value));
           }
           case CYCLOID:
           {
               return 1 - Math.cos(value);
           }
           case POLY:
           {
               //pretty limited- need better function to spread out the values usefully
               return Math.PI * value + Math.pow(value, 2); 
           }
           case SAMPLE:
           {
        	   //how should value be used? speed through sample table??
        	   
               //should cycle through the table
               index += (int)Math.abs(value);
               if (index > sampleTable.length -1)
               {
                   index = 0;
               }
               
               return sampleTable[index] ;
           }
           default:
           {
               return Math.sin(value);
           }
       }
   }
    
    public PipedInputStream getAudioStream()
    {
        return playback;
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
        //debug("carrier freq= " + carrierFreq);
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
        //debug("modfreq= " + modFreq);
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
    

    /** Getter for property sampleInterval.
     * @return Value of property sampleInterval.
     *
     */
    public double getSampleInterval()
    {
        return sampleInterval;
    }
    
    public double sin2(double rads)
    {
       return Math.sin(rads) * this.getModIndexMax(); 
    }

    public double getModIndexMax()
    {
        return modIndexMax;
    }

    public void setModIndexMax(double modIndexMax)
    {
        this.modIndexMax = modIndexMax;
    }

    public int getPitchBend()
    {
        return pitchBend;
    }

    public void setPitchBend(int pitchBend)
    {
        this.pitchBend = pitchBend;
    }

    public double getModWheel()
    {
        //debug("modwheel= " + modWheel);
        return modWheel;
    }

    public void setModWheel(double modWheel)
    {
        this.modWheel = modWheel;
    }
    
public FMSynthPatch getCurrentPatch()
{
    //FIXME- inclued all usefula params
    
    //FIXME assignLFO is not part of patch- should use generic technique- reflection - to load and save
    //that way, subsequent additions to gui/patch are handled automatically!!!
    
    //get what is currently playing
    return new FMSynthPatch(this.getCarrierFreq(), this.getModFreq(), 
            this.getFreqRatio(), this.getModIndex(), 
            this.getModIndexMax(), this.getAmplitude());
                
}

    public double getFreqRatio()
    {
        return freqRatio;
    }

    public void setFreqRatio(double freqRatio)
    {
        this.freqRatio = freqRatio;
    }

    public void setModlfo(LFO modlfo)
    {
        this.modlfo = modlfo;
    }

    public LFO getModlfo()
    {
        return modlfo;
    }


   private void debug(String msg)
   {
       if (DEBUG)
       {
           System.out.println(msg);
       }
       
   }

    public int getAssignLFO()
    {
        return assignLFO;
    }

    public void setAssignLFO(int assignLFO)
    {
        this.assignLFO = assignLFO;
    }
    
    
    //linear ( y = -pi/2 * x + pi/2) replacement for acos
    private double transfer(double x)
    {
        return -1.57 * x + 1.57;
    }
}
