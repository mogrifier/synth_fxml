package com.erichizdepski.generators;

public interface SoundGenerator {

	public short[] getMonoSamples(int length);
	
	
	public short[] getStereoSamples(int length);
	
	
}
