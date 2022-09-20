package com.java2.hon0102;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Controller {

	private static final Logger logger = LogManager.getLogger(Controller.class);

	private boolean started = false;
	private boolean paused = false;
	@FXML
	private Canvas breakoutCanvas;
	@FXML
	private Button right;
	@FXML
	private Button left;
	@FXML
	private Button start;
	@FXML
	private BorderPane borderpane;

	private LinkedList<Score> scores = new LinkedList<>();

	private Game game = new Game(507, 676);

	private AnimationTimer timer;

	private int score = 0;

	public Controller() { }

	private static String removeAccents(String text) {
		return text == null ? null :
				Normalizer.normalize(text, Normalizer.Form.NFD)
						.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}


	public void load() {
		try {
			// load score from file
			read();
			Locale l = new Locale("cs", "CZ");
			if (System.getProperty("user.language") != null && System.getProperty("user.country") != null) {
				Locale.setDefault(new Locale(System.getProperty("user.language"),
						System.getProperty("user.country")));
			} else {
				Locale.setDefault(new Locale("en", "US"));
			}
			ResourceBundle bundle = ResourceBundle.getBundle("Locale");

			// main menu
			GraphicsContext gc = breakoutCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, breakoutCanvas.getWidth(), breakoutCanvas.getHeight());
			gc.setFill(Color.BLACK);
			gc.fillRect(0, 0, breakoutCanvas.getWidth(), breakoutCanvas.getHeight());
			gc.setLineWidth(1);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E, MMM dd yyyy").withLocale(bundle.getLocale());
			// date and time
			try {
				gc.setFont(Font.loadFont(getClass().getResource("font.ttf").toExternalForm(), 24));
				gc.setStroke(Color.WHITE);
				gc.setFill(Color.WHITE);
				String theDate = removeAccents(dtf.format(ZonedDateTime.now()));
				gc.fillText(theDate, game.getWidth()/2 - 24*theDate.length()/2,60);
			} catch (Exception e) {
				logger.log(Level.INFO, "Cannot load the font!");
				e.printStackTrace();
				gc.setFont(new Font("FreeSans", 12));
			}

			// main screen BREAKOUT logo
			try {
				gc.setFont(Font.loadFont(getClass().getResource("font.ttf").toExternalForm(), 50));
				gc.setStroke(Color.WHITE);
				gc.setFill(Color.WHITE);
				gc.fillText("BREAKOUT", breakoutCanvas.getWidth() / 2 - 50*8 / 2, 180);
			} catch (Exception e) {
				logger.log(Level.INFO, "Cannot load the font!");
				e.printStackTrace();
				gc.setFont(new Font("FreeSans", 60));
			}

			// main screen top scores list
			try {
				gc.setFont(Font.loadFont(getClass().getResource("font.ttf").toExternalForm(), 25));
				gc.setFill(Color.RED);
				gc.fillText(bundle.getString("scores"), breakoutCanvas.getWidth() / 8, 260);
				gc.setFill(Color.GOLD);
				for (int i = 0; i < 3; i++) {
					try {
						gc.fillText(String.valueOf(scores.get(i).getScore()), breakoutCanvas.getWidth() / 8, 310 + i * 60);
					} catch (IndexOutOfBoundsException e){
						gc.fillText(String.valueOf(0), breakoutCanvas.getWidth() / 8, 310 + i * 60);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				gc.setFont(new Font("FreeSans", 60));

			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void start() {
		if (!started) {
			started = true;
			this.timer = new AnimationTimer() {
				private Long previous;

				@Override
				public void handle(long now) {
					if (previous == null) {
						previous = now;
					} else {
						draw((now - previous) / 1e9);
						previous = now;
					}
				}
			};
			this.timer.start();
		}
	}

	private void draw(double deltaT) {
		if (!paused) {
			breakoutCanvas.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
				this.game.setPaddlePosition(new Point2D(event.getX(), this.game.getPaddlePosition().getY()));
			});
			game.draw(breakoutCanvas);
			game.simulate(deltaT);
			this.score = this.game.getScore().getScore();
		}
	}

	private void save() {

		CompletableFuture<Boolean> database = CompletableFuture.supplyAsync(() -> {
			try {
				EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("hon0102");
				EntityManager entitymanager = emfactory.createEntityManager();
				EntityTransaction trx = entitymanager.getTransaction();

				trx.begin();
				entitymanager.persist(new Score(this.score));
				trx.commit();

			} catch (Exception e){
				e.printStackTrace();
				return false;
			}
			return true;
		});

		CompletableFuture<Boolean> file = CompletableFuture.supplyAsync(() -> {
			try (PrintWriter pw = new PrintWriter(new FileWriter("score.txt"))) {
				Consumer<Score> method = (theScore) -> {
					logger.log(Level.INFO,"Saving score {} to file", theScore.getScore());
					pw.printf("%d\n", theScore.getScore());
				};
				scores.forEach( method );

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		});

		CompletableFuture<Boolean> savingComplete = database.thenCombine(file, (dbOk, fileOk) -> dbOk && fileOk);

		try {
			logger.log(Level.INFO, "{} {}", savingComplete.get(), savingComplete.isDone());
			if (savingComplete.isDone() && savingComplete.get()){
				logger.log(Level.INFO,"Scores saved successfuly to database and file");
			} else {
				logger.log(Level.INFO,"Could not save scores!!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void read() {
		try {
			EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("hon0102");
			EntityManager entitymanager = emfactory.createEntityManager();

			CriteriaBuilder cb = entitymanager.getCriteriaBuilder();
			CriteriaQuery<Score> query = entitymanager.getCriteriaBuilder()
					.createQuery(Score.class);
			Root<Score> root = query.from(Score.class);
			int amount = 0;
			for (Score res : entitymanager.createQuery(query.select(root)).getResultList()) {
				scores.add(res);
			}
			emfactory.close();
		} catch (Throwable e){
			// top scores probably not found
			// fill with 0
			scores.add(new Score(0));
			return;
		}

		Collections.sort(scores, new Comparator<Score>() {
			@Override
			public int compare(Score one, Score two) {
				return Integer.compare(two.getScore(), one.getScore());
			}
		});

		/*
		int amount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader("scores.txt"))) {
			String current;
			while ((current = br.readLine()) != null && amount < 3) {
				amount++;
				scores.add(new Score(Integer.parseInt(current)));
			}
		} catch (IOException e) {
			// top scores probably not found
			// fill with 0
			scores.add(new Score(0));
			return;
		}
		if (amount == 0) {
			scores.add(new Score(0));
		}
		*/
	}

	public void stop() {
		save();
		Platform.exit();
	}

	@FXML
	public void pause() {
		if (!paused)
			this.paused = true;
		else
			this.paused = false;
	}

}
