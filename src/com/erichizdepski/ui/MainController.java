package com.erichizdepski.ui;

import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

import com.erichizdepski.fmsynth.FMSynthPatch;
import com.erichizdepski.fmsynth.Player;
import com.erichizdepski.fmsynth.RealTimeFMSynth;

public class MainController {
	
	//UI controls
	@FXML
	private Slider modIndex, carrierFreq, lfoDepth, lfoRate, continuousCM, modFreq;
	
	@FXML
	private ToggleButton buzz;
	
	@FXML
	private ChoiceBox<String> frequencyRatio, lfoType, generator, lfoAssignment;
	
	@FXML
	private Button saveSample;

	private boolean buzzToggle = false;
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
		
		modFreq.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setModFreq(newValue.doubleValue());
		});
		
		
		continuousCM.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setFreqRatio(newValue.doubleValue() / 10.0d);
		});
		
		
		lfoDepth.valueProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info(observable.toString());
			synth.setLfoDepth(newValue.intValue());
		});
		
		
		lfoRate.valueProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info(observable.toString());
			synth.setLfoRate(newValue.intValue());
		});
		
		
		//handle ChoiceBox value change events
		lfoType.valueProperty().addListener((observable, oldValue, newValue) -> {
			LOGGER.info(observable.toString());
			
			synth.setLfoType(lfoType.getSelectionModel().getSelectedIndex());
			
		});
		
		frequencyRatio.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setFreqRatio(Double.parseDouble(newValue));
		});
		
		
		generator.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setGenerator(generator.getSelectionModel().getSelectedIndex());
		});
		
		
		lfoAssignment.valueProperty().addListener((observable, oldValue, newValue) -> {
			synth.setLfoAssignment(lfoAssignment.getSelectionModel().getSelectedIndex());
		});
		
		
		
		saveSample.setOnAction((event) -> {
		    // Button was clicked, do something...
		    synth.saveSample();
		});
		
		buzz.setOnAction((event) -> {
			
			if (buzz.isSelected())
			{
				synth.setBuzz(0.9d);
				LOGGER.info("buzz on");
			}
			else
			{
				//buzz off
				synth.setBuzz(0.6d);
				LOGGER.info("buzz off");
			}
		    
		});
		
		
		LOGGER.info("initializing...");
		
		//start with buzz off
		buzz.setSelected(false);
		
		//create FM Synth
        synth = new RealTimeFMSynth(new FMSynthPatch());         
        player = new Player(synth.getAudioStream(), RealTimeFMSynth.BUFFER_SIZE);
        
		//initialize the choice boxes
		lfoType.getSelectionModel().selectFirst();
		
		
		//dynamically load the generator choices
		List<String> descriptions = synth.getGeneratorDescriptions();
		ObservableList<String> descriptionList = FXCollections.observableList(descriptions);
		//populate the UI choice list
		generator.setItems(descriptionList);
		generator.getSelectionModel().selectFirst();
		
		//load the carrier to modulation ratios
		List<String> ratios = synth.getCMRatios();
		ObservableList<String> ratioList = FXCollections.observableList(ratios);
		//populate the UI choice list
		frequencyRatio.setItems(ratioList);
		frequencyRatio.getSelectionModel().selectFirst();
		
		//populate where you can assign the LFO to
		List<String> options = synth.getLfoOptions();
		ObservableList<String> lfoOptions = FXCollections.observableList(options);
		//populate the UI choice list
		lfoAssignment.setItems(lfoOptions);
		lfoAssignment.getSelectionModel().selectFirst();
		
		
		 		 
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
