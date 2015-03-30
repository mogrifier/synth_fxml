package com.erichizdepski.generators;

import com.erichizdepski.fmsynth.FX;
import com.erichizdepski.fmsynth.utils.AudioUtils;

public class Gen3 extends AbstractSoundGenerator {

	private double frqRad, modValue, modIncr, carIncr, modAmp, modPhase, carPhase;

	@Override
	public short[] getMonoSamples(int length) {
		
		samples = new short[length];
		
		frqRad = twopi * mSynthController.getSampleInterval();
		modIncr = frqRad * mSynthController.getModFreq();
		carIncr = frqRad * mSynthController.getCarrierFreq();
		modAmp = frqRad * 100;
		modPhase = 0;
		carPhase = 0;

		for (int i = 0; i < length; i++)
      	{
		    samples[i] = AudioUtils.scaleToShort(1, Math.sin(carPhase));
		    modValue = modAmp * Math.sin(modPhase);
		    carPhase += (carIncr + modValue);
		    modPhase += modIncr;
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
		return "Thin FM";
	}

}
