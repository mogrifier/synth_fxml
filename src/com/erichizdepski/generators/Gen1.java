package com.erichizdepski.generators;

import com.erichizdepski.fmsynth.FX;
import com.erichizdepski.fmsynth.RealTimeFMSynth;
import com.erichizdepski.fmsynth.utils.AudioUtils;

public class Gen1 extends AbstractSoundGenerator {

	

	@Override
	public short[] getMonoSamples(int length) {
		
		samples = new short[length];
		carIncr = twopi * mSynthController.getCarrierFreq() * mSynthController.getSampleInterval();
        modIncr = twopi * mSynthController.getModFreq() * mSynthController.getSampleInterval();
        modOffset = 2 * Math.acos(mSynthController.getModIndex());
        ampOffset = 2 * Math.acos(.9);  //vary from 0 to 1
		buzz = mSynthController.getBuzz();
		
		for (int i = 0; i < length; i++)
        {
			modPhase += modIncr;
			theta += ((carIncr + mSynthController.sin2(modPhase + modOffset)
					+  mSynthController.sin2(modPhase - modOffset)) % twopi);
			
            //good formula - spectrum is not noisy. Math.sin(theta + ampOffset)  OR Math.sin(theta - ampOffset)
			//noisy formula -   Math.sin(theta + ampOffset) +  Math.sin(theta - ampOffset)
			
			//original formula was causing noise- just to high an input ALL THE TIME to sin function
			//this formula makes a more spectrally rich sound- reedy or buzzy
			samples[i] = AudioUtils.scaleToShort(1,  (Math.sin(theta + ampOffset) + Math.sin(theta - ampOffset)) * buzz);
        }
		  
		  return samples;
	}

	@Override
	public short[] getStereoSamples(int length) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.erichizdepski.generators.SoundGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Buzzy FM";
	}

}
