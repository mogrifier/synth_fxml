/**
 * 
 */
package com.erichizdepski.generators;

import com.erichizdepski.fmsynth.FX;
import com.erichizdepski.fmsynth.utils.AudioUtils;

/**
 * @author erich
 *
 */
public class Gen4 extends AbstractSoundGenerator {

	/**
	 * 
	 */
	public Gen4() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.erichizdepski.generators.AbstractSoundGenerator#getMonoSamples(int)
	 */
	@Override
	public short[] getMonoSamples(int length) {
		
		samples = new short[length];
		
		carIncr = twopi * mSynthController.getCarrierFreq() * mSynthController.getSampleInterval();
        modIncr = twopi * mSynthController.getModFreq() * mSynthController.getSampleInterval();
        modOffset = 2 * Math.acos(mSynthController.getModIndex());
        ampOffset = 2 * Math.acos(.9);  //vary from 0 to 1
		
		
		for (int i = 0; i < length; i++)
        {
			modPhase += modIncr;
			theta += ((carIncr + mSynthController.sin2(modPhase + modOffset)
					+  mSynthController.sin2(modPhase - modOffset)) % twopi);
			
            //good formula - spectrum is not noisy. Math.sin(theta + ampOffset)  OR Math.sin(theta - ampOffset)
			//noisy formula -   Math.sin(theta + ampOffset) +  Math.sin(theta - ampOffset)
			
			//original formula was causing noise- just to high an input ALL THE TIME to sin function
			//this formula makes a more spectrally rich sound
            samples[i] = AudioUtils.scaleToShort(1,  (Math.sin(theta + ampOffset) + Math.sin(theta - ampOffset)) *.4);
    }
		  
		  return samples;
		
	}

	@Override
	public String getDescription() {
		return "Rich FM";
	}

}
