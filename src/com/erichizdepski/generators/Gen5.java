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
public class Gen5 extends AbstractSoundGenerator {

	double mod1, mod2, cf, mf1, mf2, s, t;
	
	/**
	 * FM Piano from page 59 of Foundation sof Computer Music.
	 */
	public Gen5() {
	}

	/* (non-Javadoc)
	 * @see com.erichizdepski.generators.AbstractSoundGenerator#getMonoSamples(int)
	 */
	@Override
	public short[] getMonoSamples(int length) {
		samples = new short[length];
        
        cf = mSynthController.getCarrierFreq();
        mf1 = cf;
        mf2 = 4 * cf;
        mod1 = (17 * (8 - Math.log(cf))) / Math.pow(Math.log(cf), 2);
        mod2 = (20 * (8 - Math.log(cf))) / cf;
		s = cf/200;
		t = 1d/8192d; //mSynthController.getSampleInterval();
		
		//TODO have sound now but the samples pop too much. bad beat frequency
		for (int i = 0; i < length; i++)
        {
			theta = t * (twopi * cf + mod1 * Math.sin(twopi * (mf1 + s)) + mod2 * Math.sin(twopi * (mf2 + s)));
			t += mSynthController.getSampleInterval();
			//TODO need an amplitude function
            samples[i] = AudioUtils.scaleToShort(1, Math.sin(theta));
    }
		  
		  return samples;
		
	}

	/* (non-Javadoc)
	 * @see com.erichizdepski.generators.SoundGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return "FM Piano";
	}

}
