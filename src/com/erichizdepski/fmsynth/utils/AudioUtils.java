/*
 * AudioUtils.java
 *
 * Created on June 12, 2005, 7:06 AM
 */

package com.erichizdepski.fmsynth.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.erichizdepski.fmsynth.Constants;

/**
 *
 * @author Erich
 */
public class AudioUtils
{
    public static AudioFormat MONO_WAV_CD = 
            new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
            44100, 16, 1, 2, 44100, true);
    
    public static AudioFormat STEREO_WAV_CD = 
            new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
            44100, 16, 2, 2, 44100, true);
    
    
    
    public static double convertHzToRadians(double hz)
    {
    	//1 Hz = 2pi rad/s = 6.2831853 rad/s
    	return Constants.twopi * hz;
    }
    
    
    //FIXME must read wav file data and reverse each byte pair. Change to process a byte buffer.
    public static short readLittleEndianShort(DataInputStream input) throws IOException
    {
        byte[] bytes = new byte[2];
        //read 2 bytes
        input.read(bytes);
        //reverse the bytes
        byte b1 = bytes[0];
        byte b2 = bytes[1];
        bytes[0] = b2;
        bytes[1] = b1;
        //reverse and re-read
        DataInputStream bais = new DataInputStream(new ByteArrayInputStream(bytes));
        return bais.readShort();
    }
    
    
    public static byte[] shortToByte(short[] data)
    {
        byte lsb;
        byte msb;
        
        byte[] output = new byte[data.length * 2];
        
        int index = 0;
        for (int i = 0; i < data.length; i++)
        {
            //get lsb and msb
            msb = (byte)(data[i] >>> 8); 
            lsb = (byte)(data[i] - (msb << 8));
            
            output[index++] = msb;
            output[index++] = lsb;
        }
        
        return output;
    }
    
    /**
     * This casts and rescales short data to a double in range -1 to 1.
     * @param data
     * @return
     */
    
    public static double[] shortToDouble(short[] data)
    {
        
        double[] output = new double[data.length];
        
        int index = 0;
        for (int i = 0; i < data.length; i++)
        {
        	//simple brute force casting
            output[index++] = scaleToDouble(data[i]);
        }
        
        return output;
    }
    
    
    /*
     * specific to functions that range from -1 to 1 (trig)
     */
    public static double scaleToDouble(short raw)
    {
    	//put short in range 0 - 65535; output a double from -1 to 1
    	//recall Short.MIN_VALUE is negative
    	double output = (((raw - Short.MIN_VALUE)/65535.0) * 2.0) -1.0;
    	return output;
    }
    
    /*
     * specific to functions that range from -1 to 1 (trig)
     */
    public static short scaleToShort(double raw)
    {

    	double rounded = ((raw + 1.0d)/2.0d)*65535 + Short.MIN_VALUE;
    	
        //raw should be in range -2 to +2 max- convert to range of a short
        //old wrong way double rounded = ((raw + 2)/4 * 65535) - Short.MIN_VALUE;
        
        return (short)rounded;
    }
    
    /*
     * Range is max of absolute value of that raw can take.
     */
    public static short scaleToShort(double range, double raw)
    {
        //raw should be in range - range to + range - convert to range of a short
    	double rounded = ((raw + range)/(2 * range) * 65535) + Short.MIN_VALUE;
      
    	
        return (short)rounded;
    }    
    
    public static byte scaleToByte(double raw)
    {
        //raw should be in range -2 to +2 max- convert to range of a short
        double rounded = ((raw + 2)/4 * 255) - Byte.MIN_VALUE;
        
        return (byte)rounded;
    }

    public static byte scaleToByte(int range, double raw)
    {
        //raw should be in range -range to + range max- convert to range of a short
        double rounded = ((raw + range)/(range * 2) * 255) - Byte.MIN_VALUE;
        
        return (byte)rounded;
    }  
        
    /**
     * For writing mono PCM wav files (CD quality) Key point to this method is that it
    * operates on an array of 16-bit audio samples (mono or stereo).
     */
    public static boolean writeMonoAudioFile (short[] data, String fileName)
    {
        return writeAudioFile(data, fileName, MONO_WAV_CD);
    }
    
    /**
     * For writing stereo PCM wav files (CD quality). Key point to this method is that it
    * operates on an array of 16-bit audio samples (mono or stereo).
     */
    public static boolean writeStereoAudioFile (short[] data, String fileName)
    {
        return writeAudioFile(data, fileName, STEREO_WAV_CD);
    }
    
   /**
    * For use when other format is desired. Key point to this method is that it
    * operates on an array of 16-bit audio samples (mono or stereo).
    *
    */
    public static boolean writeAudioFile(short[] data, String fileName, AudioFormat format)
    {
        //convert short[] to byte[]
        byte[] byteSamples = shortToByte(data);
                       
        AudioInputStream stream = null;
                
        // For encodings like PCM, a frame consists of the set of samples for all 
        //channels at a given point in time, and so the size of a frame (in bytes) 
        //is always equal to the size of a sample (in bytes) times the number of channels. 
      
        try
        {
            stream = new AudioInputStream(new ByteArrayInputStream(byteSamples), format, data.length);
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File(fileName));
            System.out.println("generated file " + fileName);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        //only happens if exception occurred
        return false;
    }
    
    /*
     * Opens raw resources bundled with the app. These need to be wav files.
     * 
     */
    public static byte[] readWav(File wavFile, String raw) throws IOException
    {
        //open wav file and read first 36 bytes
   	 DataInputStream input = new DataInputStream(new FileInputStream(wavFile)); 
   	          
        input.skip(36);
        //should be the word 'data'
        byte[] format = new byte[4];
        input.read(format);
        //System.out.println(new String(format));
        
        int dataSize = AudioUtils.readLittleEndianInt(input);
        //read size of size in subchunk2
        //System.out.println("size of data subchunk " + dataSize);
        
        byte[] buffer = new byte[dataSize];
        input.read(buffer, 0, dataSize);
        
        return buffer;
        
    }
    
    
    
    /*
     * Opens raw resources bundled with the app. These need to be wav files.
     * 
     */
    public static byte[] readWav(File wavFile) throws IOException
    {
        //open wav file and read first 36 bytes
   	 DataInputStream input = new DataInputStream(new FileInputStream(wavFile)); 
   	          
        input.skip(36);
        //should be the word 'data'
        byte[] format = new byte[4];
        input.read(format);
        //System.out.println(new String(format));
        
        int dataSize = AudioUtils.readLittleEndianInt(input);
        //read size of size in subchunk2
        //System.out.println("size of data subchunk " + dataSize);
        
        byte[] buffer = new byte[dataSize];
        input.read(buffer, 0, dataSize);
        
        return buffer;
        
    }
    
    
    
    public static int readLittleEndianInt(DataInputStream input) throws IOException
    {
        byte[] bytes = new byte[4];
        //read 4 bytes
        input.read(bytes);
        //reverse
        byte b1 = bytes[0];
        byte b2 = bytes[1];
        byte b3 = bytes[2];
        byte b4 = bytes[3];
        
        bytes[0] = b4;
        bytes[1] = b3;
        bytes[2] = b2;
        bytes[3] = b1;
        
        //reverse and re-read
        DataInputStream bais = new DataInputStream(new ByteArrayInputStream(bytes));
        return bais.readInt();
    }
    
 
    /**
     * Read in a wav file, possibly with data after the data subchunk and write
     * the data (without the extra non-audio data) to a new file. No format changes 
     * are but the header may be re-written. The new header will be the standard
     * wave 44 byte header.
     *
     * @param File the file to copy. It is written to the same directory with
     * "copy of" prepended to the name.
     * @return boolean true if the operation sucseeds, false otherwise
     * @exception IllegalArgumentException if a non-wav file is provided.
     */
    public static boolean wavStrip(File file)
    {
        //identify the file type being read- should be wav
        AudioFileFormat fileFormat = null;
        
        try
        {
            fileFormat = AudioSystem.getAudioFileFormat(file);

            if (fileFormat.getType() != AudioFileFormat.Type.WAVE)
            {
                throw new IllegalArgumentException(file.getName() + " is not a wav file!");
            }

            AudioFormat format = fileFormat.getFormat();
            //length in frames * size of frame in bytes
            int length = fileFormat.getFrameLength() * format.getFrameSize();
            //read file data (true audio data) to array and create inputstream of it
            byte[] audioData = new byte[length];
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            //System.out.println("frame length " + fileFormat.getFrameLength());
            //System.out.println("file has " + stream.available() + " bytes available");
            //System.out.println("file length of audio should be " + length);

            //write the file with possibly a new header and no extra data
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, 
                new File(file.getParent() + File.separator + "copy of " + file.getName()));

            return true;
        }
        catch (UnsupportedAudioFileException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Performs wavStrip on all wav files in the given directory.
     */
    public static void wavStripDirectory(File directory)
    {
       // if (!directory.isFile())
       // {
       //     throw new IllegalArgumentException(directory.getName() + " is not a directory!");
       // }
        
        //iterate over all *.wav files; uses anonymous class to select .wav files
        File[] files = directory.listFiles(new FilenameFilter()
            {
                public boolean accept(File f, String s)
                {
                    return s.toLowerCase().endsWith(".wav");
                }
            }
        );
        
        boolean status = true;
        for (int i = 0; i < files.length; i++)
        {
            if (!wavStrip(files[i]))
            {
                //indicates failure for the file
                System.out.println("operation failed on " + files[i].getPath());
            }
        }
        
    }
    

    public static void main(String args[])
    {
        //wavStrip(new File("C:\\music\\Artist\\Album\\Track 01.wav"));
        File file = new File(args[0]);
        
        if (file.isDirectory())
        {
            //process all in the directory given
            wavStripDirectory(file);
        }
        else
        {
            wavStrip(file);
        }
        
    }
    
}
