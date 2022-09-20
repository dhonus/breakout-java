package com.java2.hon0102;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

/*
*
[ ] měla architekturu klient-server, v kombinaci s architekturou REST;
*
[x] obsahovala perzistentní data, ukládaná pomocí JPA;
[X] vytvářela logy jednak do souboru a jednak na konzoli, použijte log4j2;
[x] byla zde řešena problematika souběhu pomocí CompletableFuture;
[X] podporovala vícejazyčnost;
[X] použití lombok-u;
[x] bude práce s datumem a časem nebo s "money";
[x] používaly se streamy a lambda výrazy;
[-] byla sestavitelná pomocí nástroje maven a sestavením vznikne spustitelný jar nebo jeho obdoba.
*
* */

public class Main extends Application {

	public static void main(String[] args) { launch(args); }

	private static final Logger logger = LogManager.getLogger(Main.class);

	private static ResourceBundle bundle;

	private Controller controller;
	private Stage primaryStage;

	@FXML
	private Canvas breakoutCanvas;

	@Override
	public void start(Stage primaryStage) {
		try{
			if (System.getProperty("user.language") != null && System.getProperty("user.country") != null) {
				Locale.setDefault(new Locale(System.getProperty("user.language"),
						System.getProperty("user.country")));
			} else {
				Locale.setDefault(new Locale("en", "US"));
			}
			ResourceBundle bundle = ResourceBundle.getBundle("Locale");

			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("breakout.fxml"), bundle);

			BorderPane root = loader.load();
			Scene scene = new Scene(root);
			this.primaryStage = primaryStage;
			this.primaryStage.setScene(scene);
			this.primaryStage.resizableProperty().set(true);
			this.primaryStage.setTitle("Breakout - Java 2");
			this.primaryStage.show();
			this.controller = new Controller();
			controller = loader.getController();
			controller.load();
			logger.log(Level.INFO,"the game has started");

			// Exit program when main window is closed
			primaryStage.setOnCloseRequest(this::exitProgram);
		} catch (Exception e) {
			e.printStackTrace();
		};
	}

	private void exitProgram(WindowEvent evt) {
		controller.stop();
		System.exit(0);
	}
}
