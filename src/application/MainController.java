package application;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

public class MainController {

	
	@FXML
	private Slider modIndex, carrierFreq, lfoDepth, lfoRate;
	
	@FXML
	private ChoiceBox<String> frequencyRatio, lfoType;
	
	
	@FXML
	private void initialize()
	{
		
		System.out.println("initializing...");
		
		lfoType.getSelectionModel().selectFirst();
		frequencyRatio.getSelectionModel().selectFirst();

		// Handle Slider value change events.
		modIndex.valueProperty().addListener((observable, oldValue, newValue) -> {
		    System.out.println(observable.toString());
		});
	
		
		carrierFreq.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());
		});
		
		
		lfoDepth.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		
		lfoRate.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		
		lfoType.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		frequencyRatio.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(observable.toString());;
		});
		
		
		
	}
	

}
