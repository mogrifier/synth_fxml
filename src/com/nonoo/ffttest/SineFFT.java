/*	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.nonoo.ffttest;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.jtransforms.fft.DoubleFFT_1D;

import com.erichizdepski.fmsynth.utils.AudioUtils;
import com.erichizdepski.generators.Gen4;

// this example generates a sine wave, calculates the fft transform of it and determines the sine wave's frequency by the fft transformed values

public class SineFFT {
	private final static int SAMPLERATE = 44100;

	public static void main(String[] args) {
		int frequency = 3560; // freq of our sine wave
		double lengthInSecs = 1;
		int samplesNum = (int) Math.round(lengthInSecs * SAMPLERATE);

		System.out.println("Samplesnum: " + samplesNum);

		double[] audioData = new double[samplesNum];
		int samplePos = 0;

		// http://en.wikibooks.org/wiki/Sound_Synthesis_Theory/Oscillators_and_Wavetables
		/*
		for (double phase = 0; samplePos < lengthInSecs * SAMPLERATE && samplePos < samplesNum; phase += (2 * Math.PI * frequency) / SAMPLERATE) {
			audioData[samplePos++] = Math.sin(phase);

			if (phase >= 2 * Math.PI)
				phase -= 2 * Math.PI;
		}
		*/
		
		audioData = getMonoSamples(44100);
		
		
		Gen4 richfm = new Gen4();
		
		richfm.getMonoSamples(16384);
		
		
		

		// we compute the fft of the whole sine wave
		DoubleFFT_1D fft = new DoubleFFT_1D(samplesNum);

		// we need to initialize a buffer where we store our samples as complex numbers. first value is the real part, second is the imaginary.
		double[] fftData = new double[samplesNum * 2];
		for (int i = 0; i < samplesNum; i++) {
			// copying audio data to the fft data buffer, imaginary part is 0
			fftData[2 * i] = audioData[i];
			fftData[2 * i + 1] = 0;
		}

		System.out.println("buffer initialized");
		
		// calculating the fft of the data, so we will have spectral power of each frequency component
		// fft resolution (number of bins) is samplesNum, because we initialized with that value
		fft.complexForward(fftData);
		
		System.out.println("fft computed");

		try {
			// writing the values to a txt file
			BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "UTF-8"));

			int max_i = -1;
			double max_fftval = -1;
			for (int i = 0; i < fftData.length; i += 2) { // we are only looking at the half of the spectrum
				double hz = ((i / 2.0) / fftData.length) * SAMPLERATE;
				
				if (Math.abs(fftData[i]) > 0.1 || Math.abs(fftData[i+1]) > 0.1)
				{
					outputStream.write(i + ".\tr:" + Double.toString((Math.abs(fftData[i]) > 0.1 ? fftData[i] : 0)) + " i:" + Double.toString((Math.abs(fftData[i + 1]) > 0.1 ? fftData[i + 1] : 0)) + "\t\t" + hz + "hz\n");
				}
				// complex numbers -> vectors, so we compute the length of the vector, which is sqrt(realpart^2+imaginarypart^2)
				double vlen = Math.sqrt(fftData[i] * fftData[i] + fftData[i + 1] * fftData[i + 1]);

				if (max_fftval < vlen) {
					// if this length is bigger than our stored biggest length
					max_fftval = vlen;
					max_i = i;
				}
			}

			double dominantFreq = ((max_i / 2.0) / fftData.length) * SAMPLERATE;
			System.out.println("Dominant frequency: " + dominantFreq + "hz (output.txt line no. " + max_i + ")");

			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	
	public static double[] getMonoSamples(int length) {
		
		double carIncr = 0; //phase step due to carrier frequency
		double modPhase = 0; //phase of the modulator
		double modIncr = 0; //phase rate of change == from frequency
		double modOffset = 0; //phase offset for scaling modulator
		double theta = 0; //phase of output
		double ampOffset = 0; //phase offset for scaling outp
		double cf;
		double cf_rad;
		double mf;
		double mf_rad;
		double d;
		double modIndex;
		double t = 0;
		double t_inc = 1d/44100d;
		
		double[] samples = new double[length];
		
		cf = 707d;
        mf = 77145d;
		
		carIncr = 6.28 * cf * t_inc;
        modIncr = 6.28 * mf * t_inc;
		
        //no idea what is a good value
        
        
        cf_rad = AudioUtils.convertHzToRadians(cf);
        mf_rad = AudioUtils.convertHzToRadians(mf);
        
        modIndex = 10d;
        
        
		  for (int i = 0; i < length; i++)
          {
			  theta = Math.sin(cf_rad * t) +  Math.sin(mf_rad * t);
			  t += t_inc;
              samples[i] = theta;
          }
		  
		  return samples;
	}
	
	
}
