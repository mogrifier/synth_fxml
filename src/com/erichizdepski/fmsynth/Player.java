/*
 * Player.java
 *
 * Created on June 14, 2005, 8:19 PM
 */

package com.erichizdepski.fmsynth;

import java.io.IOException;
import java.io.PipedInputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.erichizdepski.fmsynth.utils.AudioUtils;

    /**
     * This class plays 16-bit, mono sample data using Java Sound.
     *
     * @author Erich Izdepski
     */
public class Player extends Thread
{
    PipedInputStream input = null;
    boolean alive = false; 
    //control data flushing behavior
    boolean discard = false;
    int buffersize = 0;
    
     
     public Player(PipedInputStream input, int buffersize)
     {
         this.input = input;
         this.buffersize = buffersize;
     } 
     
     
     public void setAlive(boolean status)
     {
         this.alive = status;
     }
     
    public void run()
    {
        alive = true;
        //now create a playback piece
        DataLine.Info info = null;
        SourceDataLine line = null;
        
        
        try
        {
            info = new DataLine.Info(SourceDataLine.class, AudioUtils.MONO_WAV_CD);

            line = (SourceDataLine)AudioSystem.getLine(info);

            line.open(AudioUtils.MONO_WAV_CD, buffersize);
            line.start();
            byte[] buffer = new byte[buffersize];
            
            //write data for a while, then quit
            int length = 0;
            
            while(alive)
            {
                //this discards any data- but there is still some junk being played.
                if (discard)
                {
                    //line.stop();
                    line.flush();//??
                }
                else
                {
                    //line.start();
                    length = input.read(buffer);
                    if (length > 0)
                    {
                        //write to the audio line (most likely BUFFER_SIZE bytes)- this should start playback
                        line.write(buffer, 0, length);
                    }
                    /*
         *Exception in thread "Thread-5" java.lang.IllegalArgumentException: illegal len:
         -1
        at com.sun.media.sound.DirectAudioDevice$DirectDL.write(DirectAudioDevice.java:714)
        at fmsynth.Player.run(Player.java:73)
                     */
                    
                    
                }

            }
            
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            line.stop();
            try
            {
                input.close();
            }
            catch (IOException e)
            {}
        }
    }
    
    public void setDiscard(boolean status)
    {
        discard = status;
    }
}
