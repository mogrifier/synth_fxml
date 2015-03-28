/*
 * FMSynth.java
 *
 */
package com.erichizdepski.fmsynth;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import com.erichizdepski.fmsynth.utils.AudioUtils;
import com.erichizdepski.generators.AbstractSoundGenerator;
import com.erichizdepski.generators.SoundGenerator;
/**
 * This is the FM Synthesizer sample generation engine. It is a mono, 16-bit,
 * real-time synthesis engine. Use for experimenting with new sound gen techniques.
 *
 * @author  Erich Izdepski
 */
public class RealTimeFMSynth extends Thread implements Constants
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

    private double sampleInterval = (double)1/44100;

    //for tracking what the lfo is modulating
    protected int assignLFO = LFO_TO_MODINDEX;
    //to get a proper complete cyclic waveform, you must set the buffer to the sample interval inverse
    
    public static int BUFFER_SIZE = 65535;//affects responsiveness to real-time changes-
    //smaller buffer means more responsive but not a complete cycle of the sound

    boolean alive = false;
    PipedInputStream playback = null;
    PipedOutputStream outflow = null;
    FMSynthPatch patch = null;
       
    int index = 0;
    byte[] sampleTable;
    
    private ArrayList<AbstractSoundGenerator> family = new ArrayList<AbstractSoundGenerator> ();
	private int selectedGen = 0;

    
    /** Creates a new instance of FMSynth */
    public RealTimeFMSynth(FMSynthPatch patch)
        
    {
        setPatch(patch);
        DataInputStream input = null;
        
        //set up pipes
        try
        {
            playback = new PipedInputStream();
            outflow = new PipedOutputStream();
            playback.connect(outflow); //connecting one half is enough
            
            //FIXME
            input = new DataInputStream(new FileInputStream((new File("C:\\Users\\erich\\Documents\\androidapps\\ephonic\\yaya_deci300.aif"))));
            input.skipBytes(100);
            //build sample table
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[8192];
            int x = 0;
            while ((x = input.read(b)) > -1)
            {
            	baos.write(b);
            }
            
            sampleTable = baos.toByteArray();

            //instantiate all the SoundGenerators by naming convention
            String[] generators = AbstractSoundGenerator.getAllGenerators();
            
            for (int i = 0; i < generators.length; i++)
            {
            	AbstractSoundGenerator gen = (AbstractSoundGenerator) (Class.forName(generators[i])).newInstance();
            	gen.setmSynthController(this);
            	family.add(gen);
            	System.out.println("instantiated " + generators[i]);
            }
            
            
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally
        {
        	/*
            try
            {
               // if (input != null) input.close();
            }
            catch (IOException e)
            {}
            */
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
        RealTimeFMSynth mysynth = new RealTimeFMSynth(FMSynthPatch.PATCH);         
        
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
    	short[] samples = null;
    	SoundGenerator engine = family.get(selectedGen);
    	
        while(alive)
        {
        	
        	//choose with  SoundGenerator to use
        	
        	
        	samples = engine.getMonoSamples(8192);
        	
            
            try
            {
                outflow.write(AudioUtils.shortToByte(samples));
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
        //System.out.println(value);
        
        //do some math function based on waveform selected
        switch(waveform)
        {
            case SINE:
            {
                return Math.sin(value);
            }
            case SAW:
            {
                //range bind to -1 to + 1
                
                //this produces a pulsing sound- not a saw?
                return value/(twopi + 2.1);
                
                //return Math.sin(value);
            }
            case PROP:
            {
                return Math.sqrt(4 * Math.cos(2 * value));
            }
            case CYCLOID:
            {
                return 1 - Math.cos(value);
            }
            case TRIANGLE:
            {
                return Math.sin(value);
            }
            case SINE2:
            {
                return Math.sin(value) * this.getModIndexMax(); 
            }
            case POLY:
            {
                //pretty limited- need better function to spread out the values usefully
                return Math.PI * value + Math.pow(value, 2); 
            }
            case SAMPLE:
            {
                //should cycle through the table
                index++;
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
        debug("mod index= " + modIndex);
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
        debug("lfo assigned to " + assignLFO);
    }
    
    
    //linear ( y = -pi/2 * x + pi/2) replacement for acos
    private double transfer(double x)
    {
        return -1.57 * x + 1.57;
    }

	/**
	 * @return the fx
	 */
	public FxWrapper getFx() {
		return fx;
	}

	/**
	 * @param fx the fx to set
	 */
	public void setFx(FxWrapper fx) {
		this.fx = fx;
	}
}
