package com.erichizdepski.generators;

import com.erichizdepski.fmsynth.FX;
import com.erichizdepski.fmsynth.RealTimeFMSynth;
import com.erichizdepski.fmsynth.utils.AudioUtils;

public class Gen2 extends AbstractSoundGenerator {

	private double cf, cf_rad, mf, mf_rad, d, modIndex, t;

	@Override
	public short[] getMonoSamples(int length) {
		
		samples = new short[length];

		carIncr = twopi * mSynthController.getCarrierFreq() * mSynthController.getSampleInterval();
        modIncr = twopi * mSynthController.getModFreq() * mSynthController.getSampleInterval();
        modOffset = 2 * Math.acos(mSynthController.getModIndex());
        ampOffset = 2 * transfer(mSynthController.getAmplitude());
		
        //no idea what is a good value
        d = 8000;
        cf = mSynthController.getCarrierFreq();
        mf = 300000; //mSynthController.getModFreq();
        
        cf_rad = AudioUtils.convertHzToRadians(cf);
        mf_rad = AudioUtils.convertHzToRadians(mf);
        
        modIndex = d/mf;
        t = mSynthController.getSampleInterval();
        
		  for (int i = 0; i < length; i++)
          {
			  theta = Math.sin(cf_rad * t + modIndex * Math.sin(mf_rad * t));
			  t += mSynthController.getSampleInterval();
              samples[i] = AudioUtils.scaleToShort(1, theta);
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
		return "Simple chowning";
	}


}
