package com.erichizdepski.generators;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import com.erichizdepski.fmsynth.Constants;
import com.erichizdepski.fmsynth.RealTimeFMSynth;

public abstract class AbstractSoundGenerator implements SoundGenerator, Constants {

	protected double carIncr = 0; //phase step due to carrier frequency
	protected double modPhase = 0; //phase of the modulator
	protected double modIncr = 0; //phase rate of change == from frequency
	protected double modOffset = 0; //phase offset for scaling modulator
	protected double theta = 0; //phase of output
	protected double ampOffset = 0; //phase offset for scaling output
	protected double mix = 0;
	protected double totalMod = 0;
	protected double totalFreq = 0;
	protected short[] samples = null; //8192 samples = .1 sec of audio      
	protected double lfoValue = 0;
	protected double amp = 0;
	protected RealTimeFMSynth mSynthController;

    
    public AbstractSoundGenerator()
    {
    }
    
    public static String[] getAllGenerators()
    {
        String srcRoot =  System.getProperty("user.dir") + File.separator + "src" + File.separator;
        
        //go down the correct package limb
        String genRoot = srcRoot + "com" + File.separator + "erichizdepski" + File.separator + "generators";
        
        File home = new File(genRoot);
        if(home.exists())
        {
        	System.out.println("found generator source home= " + home.getAbsolutePath());
        }
        
        String[] generators = home.list(new FilterGenNames());
        //remove .java from names
        for (int i = 0; i < generators.length; i++)
        {
        	generators[i] = "com.erichizdepski.generators." + generators[i].substring(0, generators[i].length() - 5);
        }
        
        
		return generators;
    }
    
    
    public String getName()
    {
    	return this.getClass().getName();
    }
	

	@Override
	public short[] getStereoSamples(int length) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the mSynthController
	 */
	public RealTimeFMSynth getmSynthController() {
		return mSynthController;
	}

	/**
	 * @param mSynthController the mSynthController to set
	 */
	public void setmSynthController(RealTimeFMSynth mSynthController) {
		this.mSynthController = mSynthController;
	}

	
    //linear ( y = -pi/2 * x + pi/2) replacement for acos
    public double transfer(double x)
    {
    	if (x < -1) x = -1;
    	if (x > 1) x = 1;
        return -1.57 * x + 1.57;
    }
    
    //linear ( y = -pi/2 * x + pi/2) replacement for atan  y = mx + b
    public double transfer2(double x)
    {
    	if (x < -5) x = -5;
    	if (x > 5) x = 5;
        return -Math.PI/10 * x + 1.57;
    }
	
}
