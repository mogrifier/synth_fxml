package com.erichizdepski.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

import com.erichizdepski.fmsynth.FMSynthPatch;
import com.erichizdepski.fmsynth.Player;
import com.erichizdepski.fmsynth.RealTimeFMSynth;

public class MainController {
	
	@FXML
	private Slider modIndex, carrierFreq, lfoDepth, lfoRate;
	
	@FXML
	private ChoiceBox<String> frequencyRatio, lfoType;

	private RealTimeFMSynth synth;

	private Player player;
	

	@FXML
	private void initialize()
	{
		
		System.out.println("initializing...");
		
		
		lfoType.getSelectionModel().selectFirst();
		frequencyRatio.getSelectionModel().selectFirst();

		// Handle Slider value change events.
		modIndex.valueProperty().addListener((observable, oldValue, newValue) -> {
		    System.out.println(observable.toString());
		    synth.setModIndex(newValue.doubleValue());
		});
	
		
		carrierFreq.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());
			synth.setCarrierFreq(newValue.doubleValue());
		});
		
		
		lfoDepth.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		
		lfoRate.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		
		//handle ChoiceBox value change events
		lfoType.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		frequencyRatio.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		
		
		
		//create FM Synth
        synth = new RealTimeFMSynth(new FMSynthPatch());         
        player = new Player(synth.getAudioStream(), RealTimeFMSynth.BUFFER_SIZE);
        
        //use two threads- one for generating audio, one for playing back
        synth.setAlive(true);
        synth.start();
        player.setAlive(true);
        player.start();
		
		System.out.println("initialization complete");
	}
	

	/*
	 * Properly clean up the threads, else the synth can keep playing.
	 * @return boolean True means it shutdown. False would be real bad.
	 */
	public boolean shutdown()
    {
        synth.setAlive(false);
        player.setAlive(false);
        return true;
    }
}
