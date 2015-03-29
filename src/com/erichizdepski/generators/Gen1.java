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
        ampOffset = 2 * transfer(mSynthController.getAmplitude());
		
		
		for (int i = 0; i < length; i++)
        {
			modPhase += modIncr;
			theta += ((carIncr + mSynthController.sin2(modPhase + modOffset)) % twopi);
			
			theta = Math.abs(theta);
            
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
	public short[] getStereoSamples(int length) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.erichizdepski.generators.SoundGenerator#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Trying for scary FM";
	}

}
