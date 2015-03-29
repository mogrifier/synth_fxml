package com.erichizdepski.ui;

import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private ChoiceBox<String> frequencyRatio, lfoType, generator;

	private RealTimeFMSynth synth;
	private Player player;
	private final static Logger LOGGER = Logger.getLogger(MainController.class.getName()); 
	

	@FXML
	private void initialize()
	{
		//Register UI Event Listeners
		 
		// Handle Slider value change events.
		modIndex.valueProperty().addListener((observable, oldValue, newValue) -> {
		    synth.setModIndex(newValue.doubleValue());
		});
	
		
		carrierFreq.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setCarrierFreq(newValue.doubleValue());
		});
		
		
		lfoDepth.valueProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info(observable.toString());
		});
		
		
		lfoRate.valueProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info(observable.toString());
		});
		
		
		//handle ChoiceBox value change events
		lfoType.valueProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info(observable.toString());
		});
		
		frequencyRatio.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setFreqRatio(Double.parseDouble(newValue));
		});
		
		
		generator.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setGenerator(generator.getSelectionModel().getSelectedIndex());
		});
		
		
		
		LOGGER.info("initializing...");
		//create FM Synth
        synth = new RealTimeFMSynth(new FMSynthPatch());         
        player = new Player(synth.getAudioStream(), RealTimeFMSynth.BUFFER_SIZE);
        
		//initialize the choice boxes
		lfoType.getSelectionModel().selectFirst();
		frequencyRatio.getSelectionModel().selectFirst();
		
		//dynamically load the generator choices
		 List<String> descriptions = synth.getGeneratorDescriptions();
		 ObservableList<String> observableList = FXCollections.observableList(descriptions);
		 //populate the UI choice list
		 generator.setItems(observableList);
		 generator.getSelectionModel().selectFirst();
		 
        
        //use two threads- one for generating audio, one for playing back
        synth.setAlive(true);
        synth.start();
        player.setAlive(true);
        player.start();
		        
		LOGGER.info("initialization complete");
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