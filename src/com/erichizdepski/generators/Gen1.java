package com.erichizdepski.generators;

import com.erichizdepski.fmsynth.FX;
import com.erichizdepski.fmsynth.RealTimeFMSynth;
import com.erichizdepski.fmsynth.utils.AudioUtils;

public class Gen1 extends AbstractSoundGenerator {

	

	@Override
	public short[] getMonoSamples(int length) {
		
		samples = new short[length];
		
		  for (int i = 0; i < length; i++)
          {
              //get values plus control wheel offsets
              
              lfoValue = mSynthController.getModlfo().getLFOValue();

              //changed to add mod to pitch
              totalFreq = mSynthController.getCarrierFreq() + mSynthController.getPitchBend();
              if (mSynthController.getAssignLFO() == LFO_TO_PITCH)
              {
                  totalFreq += 10 * lfoValue;
              }
              
              //FIXME -n the gui allow user to choose whether to modulate pitch or modindex
              //pitch mod (vibrato) is producing prettier tones     
              
              totalMod = mSynthController.getModIndex() + mSynthController.getModWheel();
              if (mSynthController.getAssignLFO() == LFO_TO_MODINDEX)
              {
                  totalMod += lfoValue;
              }
              
              
              //range checks
              totalMod = (totalMod % 1) ; //keep 0 to 1-- supposed to be a fraction
              //range checks
              totalFreq = (totalFreq < 0) ? 0 : totalFreq;
                              
              //the amount of the increment controls how quickly a cycle completes,
              //hence it is the frequency
              carIncr = twopi * totalFreq * mSynthController.getSampleInterval();
              
              //if let constant, we don't get music. scale to carrier frequency for now
              modIncr = twopi * mSynthController.getModFreq() * mSynthController.getSampleInterval();
              
              /*
               * inputs to acos to keep it in range are roughly -57 to 57 degrees which
               * is -1 to +1 radians!!
               */
              
              
              
              modOffset = 2 * Math.acos(totalMod); //result is 0 to pi
              ampOffset = 2 * Math.acos(mSynthController.getAmplitude() % 1); //result is 0 to pi
              
              
              //modOffset = 2 * transfer(totalMod); //result is 0 to pi
              //FIXME why is the function input modulo 1??
              //ampOffset = 2 * transfer(this.getAmplitude() % 1); //result is 0 to pi  
              
              
              modPhase = modPhase + modIncr;
              
              //puts theta in range -2pi to + 2pi
              theta = Math.PI +  (theta + carIncr + mSynthController.getWaveValue(modPhase + modOffset, SAW) 
                          + mSynthController.getWaveValue(modPhase - modOffset, SAW)) % Math.PI;

              //write to the outflow pipe
              //store up some data in a buffer for writing- use short for 16 bit audio.
              
              /*
              samples[i] = AudioUtils.scaleToShort(getWaveValue(theta + ampOffset, SAW) 
                  + getWaveValue(theta - ampOffset, SAW));
               */
              
              /*
              samples[i] = (short)getWaveValue(theta , SAMPLE);
                 */
              
              /*
              samples[i] = AudioUtils.scaleToShort(twopi, getWaveValue(theta, SINE) * getAmplitude());
               */
              
              //value return is 0 - 3. fix for scaling???
              /*
              samples[i] = AudioUtils.scaleToShort(getWaveValue(theta + ampOffset, CYCLOID) 
                  + getWaveValue(theta - ampOffset, CYCLOID));       
              */
              
              /*
              samples[i] = AudioUtils.scaleToShort(getWaveValue(theta + ampOffset, POLY) 
                  + getWaveValue(theta - ampOffset, POLY));     
              */
              
              
              //this is a dry/wet mix formula for aplpying FX in real time. 
              mix = (1 - mSynthController.getFx().getAmt()) * theta + mSynthController.getFx().getAmt() * FX.applyFx(mSynthController.getFx().getName(), theta);
              
              amp = mSynthController.getAmplitude();
              if (mSynthController.getAssignLFO() == LFO_TO_AMPLITUDE)
              {
                  amp += lfoValue;
              }
              
              //samples[i] = AudioUtils.scaleToShort(Math.PI, getWaveValue(mid, SINE) * (amp));
              
              
             //original formula
              
              //samples[i] = AudioUtils.scaleToShort(getWaveValue((theta + ampOffset), SINE) 
              //    + getWaveValue((theta - ampOffset), SINE));
               
                            
            //amplitude = 0 to pi
            //this corrects amplitude behavior- I don't notice any other change
            //samples[i] = AudioUtils.scaleToShort(Math.PI, Math.sin(theta) * getAmplitude());
              samples[i] = AudioUtils.scaleToShort(1, Math.sin(mix));
      }
		  
		  return samples;
	}

	@Override
	public short[] getStereoSamples(int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
