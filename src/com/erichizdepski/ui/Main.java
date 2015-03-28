package com.erichizdepski.ui;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {
	
	
	private MainController controller;
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			       @Override
			       public void handle(WindowEvent e) {
			    	  stop(); 
			          
			       }
			    });
			
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			setUserAgentStylesheet(Application.STYLESHEET_MODENA);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			controller = (MainController)loader.getController();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void stop() {
		
		if (controller.shutdown())
		{
			System.out.println("synth shutdown");
		}
		else
		{
			System.out.println("synth failed to hutdown");
		}
		
		Platform.exit();
        System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
		
	}
	
	


}
