/*
 * FMInstrument.java
 *
 * Created on June 24, 2005, 1:32 PM
 */

package com.erichizdepski.fmsynth;

import javax.sound.midi.*;

/**
 * Just a class for storing how sound programs are stored. An instrument is 
 * stored in a patch (bank, progran #). 1:1 with patch.
 * @author  Erich Izdepski
 */
public class FMInstrument extends javax.sound.midi.Instrument
{
    private Patch program = null;
    private FMSynthPatch patch = null;
    private Soundbank bank = null;
    private String name = null;
    
    public FMInstrument(Soundbank soundbank, Patch program, String name, FMSynthPatch patch)
    {
        super (soundbank, program, name, FMSynthPatch.class); 
        this.bank = soundbank;
        this.program = program;
        this.patch = patch;
        this.setName(name);
    }
    
    /** Creates a new instance of FMInstrument */
    private FMInstrument(Soundbank soundbank, Patch program, String name, Class dataClass)
    {
        super (soundbank, program, name, dataClass); 
    }
    
    //returns an FMSynthPatch object holding the settings for this instrument
    public Object getData()
    {
        return patch;
    }

    
    public javax.sound.midi.Patch getPatch()
    {
        return program;
    }
    
    
    public Class getDataClass()
    {
        return FMSynthPatch.class;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
