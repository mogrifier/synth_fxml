/*
 * FX.java
 *
 * Created on July 16, 2005, 12:12 AM
 */

package com.erichizdepski.fmsynth;

/**
 *
 * @author Owner
 */
public class FX implements Constants
{

    public static double applyFx(int fxName, double val)
    {
        switch (fxName)
        {
            case CHEB2:
            {
                return cheb2(val);
            }
            case MOBIUS:
            {
                return mobius(val);
            }
            case SHAPER:
            {
                return shaper(val);
            }
            case SHAPER2:
            {
                return waveshaper2(val);
            }
            case RFILTER:
            {
                return resonantFilter(val);
            }
        }
        
        //like a default
        return val;
    }
    
    
    /* Calculate the chebyshev values for the input up to Nth 0rder. Generally
     * very good. Must weight the returned values to get the sound desired. 
     * Ignore [0]- it is always 1.
     *
     */
    public static double[] chubbySynth(int order, double x)
    {
        double[] values = new double[order + 1];
        values[0] = 1;
        values[1] = x;
        
        for (int i = 2; i <= order; i++)
        {
            values[i] = 2 * x * values[i - 1] - values[i - 2];
        }
        
        return values;
    }
    
    /*
     * A really good effect when the c:m ratio is less than 1. Use mod wheel
     * to 'tune in' the best sounds. Output range is .33 to 2 for inputs -1 to +1.
     *
     */
    public static double mobius(double x)
    {
        return ((double)(2 + x)/(2 - x));
    }
    
    /*
     * Good fuzz.
     */
    public static double shaper(double input)
    {
        return  (input/2 + Math.pow(input/2, 3)) / 2;
    }
    
    
    public static double cheb2(double input)
    {
        return 2 * Math.pow(input/2, 2) -1;  //0 to 1
    }
    
    
    /*
     * 
     *
     */
    public static double foldbackDistortion(double in, double threshold)
    {
        if (in > threshold || in < -threshold)
        {
            in = Math.abs(Math.abs(((in - threshold) % threshold*4)) - threshold*2) - threshold;
        }

        return in;
    }
    
    
    /*
     * Produces a slight signal boost, not unlike increasing the amplitude.
     * Useless?
     *
     */
    public static double resonantFilter(double sample)
    {
//for resonant filter feedback
      double f = .7;
    double q = .9;
    double fb = q + q/(1.0 - f);
        
        
        //set feedback amount given f and q between 0 and 1
        double buf0 = 0;
        double buf1 = 0;
        
        buf0 = buf0 + f * (sample - buf0 + fb * (buf0 - buf1));
        buf1 = buf1 + f * (buf0 - buf1);
        return buf1;
    }
    
        /**
     * This one is good. It can thicken certain sounds, like a chorus or mild
     * distortion. 
     * negative values for amount cut the sound, positive values boost it.
         *
         * use with saw mod lfo of rate 1 depth 2 to give very good sound.
     */
    public static double waveshaper2(double sample)
    {
        //amount should be in [-1..1]
        double amount = .4;
        double x = sample/2;
        double k = 2 * amount/(1 - amount);

        return (1+k)*x/(1+k*Math.abs(x));
    }    
}
