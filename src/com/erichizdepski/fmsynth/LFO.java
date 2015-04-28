/*
 * LFO.java
 *
 * Created on July 18, 2005, 3:06 PM
 */

package com.erichizdepski.fmsynth;

/**
 * This class provides an LFO for use in creating vibrato, tremolo, and modulation 
 * index variations (OR ANYTHING ELSE). The LFO waveform is selectable along with
 * rate (Hz) and depth ( 0 to x).
 *
 * @author Erich Izdepski
 */
public class LFO implements Constants
{
    //use static wavetables designed for use at 441000 Hz- these use about 1.3 MB RAM
    private static double SINE_TABLE[] = new double[44100];
    private static double SAW_TABLE[] = new double[44100];
    private static double TRI_TABLE[] = new double[44100];
    private static double SQUARE_TABLE[] = new double[44100];
    private static double NOISE_TABLE[] = new double[44100];
    //this is the step for an LFO at a rate of 1Hz.
    private static double step = (Math.PI * 2)/44100;
    //static intializer for whole class
    static
    {
        //all wavetable values are in the range -1 to 1
        
        //SINE wave
        for (int i = 0; i < 44100; i++)
        {
            SINE_TABLE[i] = Math.sin(i * step);
        }
        
        //NOISE
        for (int i = 0; i < 44100; i++)
        {
            NOISE_TABLE[i] = (Math.random() * 2) -1;
        }
        
        
        //for quarter waves like tri, saw
        double incr = (double)1/11025;
        
        //SAW wave
        double val = -1;
        for (int i = 0; i < 22050; i ++)
        {
            SAW_TABLE[i] = (val += incr);
        }
        val = -1;
        for (int i = 22050; i < 44100; i++)
        {
            SAW_TABLE[i] = (val += incr);
        }
        
        //TRI wave- tabel data is not-rectified, but this is fixed when data is called for
        val = 0;
        for (int i = 0; i < 11025; i ++)
        {
            TRI_TABLE[i] = (val += incr);
        }
        for (int i = 11025; i < 33075; i ++)
        {
            TRI_TABLE[i] = (val -= incr);
        }
        for (int i = 33075; i < 44100; i ++)
        {
            TRI_TABLE[i] = (val += incr);
        }
        
        
        //SQAURE wave
        for (int i = 0; i < 22050; i++)
        {
            //using 100% duty cycle
            SQUARE_TABLE[i] = 1;
        }
        for (int i = 22050; i < 44100; i++)
        {
            //using 100% duty cycle
            SQUARE_TABLE[i] = -1;
        }
    }
        
    //this determines how much of the LFO amplitude is used- may be a %??
    private int depth = 0;
    //this is the LFO frequency in Hz
    private int rate = 0; 
    private double morphBalance = 1;
    //where in wavetable we currently are
    private int tablePos = 0;
    
    //what tyope of waveform is used for LFO
    private int type;
    //for morphing between LFO waveforms
    private int type2 = -1;
    private boolean transition = false;
    
    
    /** Creates a new instance of LFO */
    public LFO(int type, int rate, int depth)
    {
        //one of SINE,SAW,TRI,SQUARE
       // if (type < 0 || type > 4)
         //   throw new IllegalArgumentException("invalid LFO type");
        
        this.setType(type);
        this.rate = rate;
        this.depth = depth;
    }
    
    public LFO(int type1, int type2, int rate, int depth)
    {
        //one of SINE,SAW,TRI,SQUARE
       // if (type1 < 0 || type1 > 4)
      //      throw new IllegalArgumentException("invalid LFO type");
     //   if (type2 < 0 || type2 > 4)
       //     throw new IllegalArgumentException("invalid LFO type");
     //   
        this.setType(type1);
        this.type2 = type2;
        this.rate = rate;
        this.depth = depth;
    }
    
    
    public double getMorphLFOValue()
    {
        //FIXME- use balance variable like a mixer!! combine values from each
        //table type in use and change balance to control which is dominant.
        
        //this is not very smooth-may be better to modulate the rate with LFO
        double t1 = getLFOValue(type);
        tablePos -= rate; //set it back so same table positions are read
        double t2 = getLFOValue(type2);
        
        //apply balance
        return ((1 - morphBalance) * t1 + (morphBalance * t2)) * depth/100;
        
        
    }
    
    /*
     * This is the method to call to get a value in the range of -1 to -1 for 
     * the set rate.
     */ 
    public double getLFOValue()
    {
        return getLFOValue(getType());
    }
    

    private double getLFOValue(int lfoType)
    {
        if (depth == 0 || rate == 0)
        {
            return 0;
        }
        
        if (tablePos >= 44100)
        {
            tablePos = 0;
        }
        
        double val = 0;
        
        switch (lfoType)
        {
            case SINE:
            {
                val = SINE_TABLE[tablePos];
                break;
            }
            case SAW:
            {
                val = SAW_TABLE[tablePos];
                break;
            }
            case TRIANGLE: 
            {
                //let's retify and check result- this sounds better than non-rectified
                val = Math.abs(TRI_TABLE[tablePos]);
                break;
            }
            case SQUARE:
            {
                val = SQUARE_TABLE[tablePos];
                break;
            }
            case NOISE:
            {
                val = NOISE_TABLE[tablePos];
                break;
            }
        }
        
        //add in random rate variance- could be from other LFO
        
        tablePos +=rate; // (rate + ((int)Math.floor(Math.random() * 10) -5));

        return val * depth/10.0d;
    }
    
    
    public void reset()
    {
        tablePos = 0;
    }
    

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
        System.out.println("lfo depth=" + depth);
    }

    public int getRate()
    {
        return rate;
    }

    public void setRate(int rate)
    {
        this.rate = rate;
        System.out.println("lfo rate=" +rate);
    }
    
    
    public static void main(String[] args)
    {
        //dump the tables
        for (int i = 0; i < 44100; i++)
        {
           System.out.println(TRI_TABLE[i] );
        }
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
        System.out.println("lfo type=" +type);
    }
    
    public String toString()
    {
        return "type =" + type + "  rate =" + rate + "  depth =" + depth;
    }
}
