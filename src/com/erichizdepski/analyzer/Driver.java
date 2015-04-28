package com.erichizdepski.analyzer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.jtransforms.fft.DoubleFFT_1D;

import com.erichizdepski.fmsynth.FMSynthPatch;
import com.erichizdepski.fmsynth.RealTimeFMSynth;
import com.erichizdepski.fmsynth.utils.AudioUtils;
import com.erichizdepski.generators.Gen4;

public class Driver {

	public Driver() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Driver driver = new Driver();
		
		driver.analyze();
		//convert short
		driver.analysis2();
		
	}
	
	
	
	public void analyze() {
		
		double SAMPLERATE = 44100.0;
		double SAMPLES = 16384.0;
		Gen4 richfm = new Gen4();
		RealTimeFMSynth synth = new RealTimeFMSynth(FMSynthPatch.PATCH);
		richfm.setmSynthController(synth);
		
		short[] samples = richfm.getMonoSamples((int)SAMPLES);
		double[] audioData = AudioUtils.shortToDouble(samples);
		
		// we compute the fft of the whole sample
		DoubleFFT_1D fft = new DoubleFFT_1D((int)SAMPLES);

		// we need to initialize a buffer where we store our samples as complex numbers. 
		//first value is the real part, second is the imaginary.
		double[] fftData = new double[(int)SAMPLES * 2];
		for (int i = 0; i < SAMPLES; i++) {
			
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
			for (int i = 2; i < SAMPLES; i += 2) { // we are only looking at the half of the spectrum
				double hz = (i * SAMPLERATE)/(SAMPLES)/2.0;
				
				double vlen = Math.sqrt(fftData[i] * fftData[i] + fftData[i + 1] * fftData[i + 1]);
				if (vlen > 50)
				{
				
					if (Math.abs(fftData[i]) > 0.1 || Math.abs(fftData[i+1]) > 0.1)
					{
						outputStream.write(i + ".\tr:" + Double.toString((Math.abs(fftData[i]) > 0.1 ? fftData[i] : 0)) + " i:" + Double.toString((Math.abs(fftData[i + 1]) > 0.1 ? fftData[i + 1] : 0)) + "\t\t" + hz + "hz\n");
					}
				}
				
				
				// complex numbers -> vectors, so we compute the length of the vector, which is sqrt(realpart^2+imaginarypart^2)
				
				if (max_fftval < vlen) {
					// if this length is bigger than our stored biggest length
					max_fftval = vlen;
					max_i = i;
				}
			}

			double dominantFreq = (max_i * SAMPLERATE)/(SAMPLES)/2.0;
			System.out.println("Dominant frequency: " + dominantFreq + "hz (output.txt line no. " + max_i + ")");

			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//write the wav file and listen
		AudioUtils.writeMonoAudioFile(richfm.getMonoSamples((int)SAMPLES), "./fmout.wav");
	}

	
	public void analysis2()
	{
		double N = 1024;         // size of FFT and sample window
		double Fs = 44100;        // sample rate = 44.1 kHz
		double[] data = new double[(int) N];           // input PCM data buffer
		double[] fft = new double[(int) (N * 2)];        // FFT complex buffer (interleaved real/imag)
		double[] magnitude = new double[ (int) (N / 2)];  // power spectrum
	
		//capture audio in data[] buffer
		
		//apply window function to data[]
	

	
		//perform in-place complex-to-complex FFT on fft[] buffer
		Gen4 richfm = new Gen4();
		RealTimeFMSynth synth = new RealTimeFMSynth(FMSynthPatch.PATCH);
		richfm.setmSynthController(synth);
		
		short[] samples = richfm.getMonoSamples((int)N);
		data = AudioUtils.shortToDouble(samples);
		// copy real input data to complex FFT buffer
		for (int i = 0; i <=  (N - 1); i++)
		{
		  fft[2*i] = data[i];
		  fft[2*i+1] = 0;
		}
		// we compute the fft of the whole sample
		DoubleFFT_1D xform = new DoubleFFT_1D((int)N);
		xform.complexForward(fft);
		
		
		
		// calculate power spectrum (magnitude) values from fft[]
		for (int i = 0 ; i<= ( N / 2 - 1); i++)
		{
		  magnitude[i] = Math.sqrt(fft[2*i] * fft[2*i] +  fft[2*i+1] * fft[2*i+1]);
		}
	
		// find largest peak in power spectrum
		double max_magnitude = Double.MIN_VALUE;
		double max_index = -1.0;
		
		for (int i = 0 ; i<= ( N / 2 - 1); i++)
		{
		  if (magnitude[i] > max_magnitude)
		  {
		    max_magnitude = magnitude[i];
		    max_index = i;
		  }
		}
		// convert index of largest peak to frequency
		double freq = max_index * Fs / N;
					

		System.out.println("analysis 2 *********");
		System.out.println("max power at " + freq);
	}
	
}
