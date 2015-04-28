package com.erichizdepski.generators;

import com.erichizdepski.fmsynth.utils.AudioUtils;

public class Gen1 extends AbstractSoundGenerator {

	

	@Override
	public short[] getMonoSamples(int length) {
		
		samples = new short[length];

		
		
		for (int i = 0; i < length; i++)
        {
			lfoValue = mSynthController.getModlfo().getLFOValue();
			totalFreq = mSynthController.getCarrierFreq();
			if (mSynthController.getAssignLFO() == LFO_TO_PITCH)
            {
                totalFreq +=  lfoValue;
            }
			
			carIncr = twopi * totalFreq * mSynthController.getSampleInterval();
	        modIncr = twopi * mSynthController.getModFreq() * mSynthController.getSampleInterval();
	        
            totalMod = mSynthController.getModIndex();// + mSynthController.getModWheel();
            if (mSynthController.getAssignLFO() == LFO_TO_MODINDEX)
            {
            	//produce pretty good effect most of the time
            	
            	//control range of returned value
                totalMod = (totalMod + lfoValue/50.0d) %1;
            }
            
          //vary from 0 to 1
	        modOffset = 2 * Math.acos(totalMod);
	        
	        
	        /*
	         * Observed that modulating amplitude has little todo with volume. Adds noise to and
	         * causes loss of pitch. 
	         * NOT REAL USEFUL
	         * 
	         * Applying to carIncr or modIncr makes it go totally SciFi (50's) -- yoyo effect on pitch
	         */
	        
            if (mSynthController.getAssignLFO() == LFO_TO_AMPLITUDE)
            {
            	//control range of returned value
            	modIncr = (modIncr + lfoValue/50.0d);
            }
            
	        ampOffset = 2 * Math.acos(amp);  //vary from 0 to 1
			buzz = mSynthController.getBuzz();
			
			
			
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
