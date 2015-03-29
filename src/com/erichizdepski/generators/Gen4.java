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
        ampOffset = 2 * transfer(mSynthController.getAmplitude());
		
		
		for (int i = 0; i < length; i++)
        {
			modPhase += modIncr;
			theta += ((carIncr + mSynthController.sin2(modPhase + modOffset)) % twopi);
			
            
            
            //samples[i] = AudioUtils.scaleToShort(Math.PI, getWaveValue(mid, SINE) * (amp));
            
            
           //original formula
            
            //samples[i] = AudioUtils.scaleToShort(getWaveValue((theta + ampOffset), SINE) 
            //    + getWaveValue((theta - ampOffset), SINE));
             
          //amplitude = 0 to pi
          //this corrects amplitude behavior- I don't notice any other change
          //samples[i] = AudioUtils.scaleToShort(Math.PI, Math.sin(theta) * getAmplitude());
            samples[i] = AudioUtils.scaleToShort(1, Math.sin(theta + ampOffset) + Math.sin(theta - ampOffset));
    }
		  
		  return samples;
		
	}

	@Override
	public String getDescription() {
		return "Simple FM tone generator";
	}

}
