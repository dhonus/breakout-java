package com.java2.hon0102;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Getter
@Setter
public class Game {

	private static final Logger logger = LogManager.getLogger(Game.class);

	private Score score = new Score(0);
	private DrawableElement bricks[][];

	private DrawableElement gameParts[];
	private double width, height;

	private boolean won = false;

	private short lives = 3;
	private boolean gameOver = false;

	Game(double width, double height) {
		this.width = width;
		this.height = height;
		int paddleWidth = 70;
		bricks = new Brick[14][14];
		gameParts = new DrawableElement[2];
		gameParts[1] = new Paddle(this, paddleWidth);
		gameParts[1].setPosition(new Point2D(this.width / 2, this.height - 50));
		gameParts[0] = new Ball(this, 17, 1, paddleWidth);

		// initialize brick array
		Scanner scanner;

		//scanner = new Scanner(new File("src/main/resources/com/java2/hon0102/goodlevel"));
		InputStream stream = getClass().getResourceAsStream("goodlevel");
		scanner = new Scanner(stream);
		//scanner = new Scanner(new File(Game.class.getResource("goodlevel").getFile()));
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 14; j++) {
				this.bricks[i][j] = new Brick(width, new Point2D(i, j));
				bricks[i][j].setColor(scanner.nextInt());
				int brokenBrick = scanner.nextInt();
				if (brokenBrick == 0)
					bricks[i][j].setTransparent(false);
				else {
					bricks[i][j].setTransparent(true);
				}
			}
		}


	}

	public double getWidth() {
		return this.width;
	}

	public double getHeight() {
		return this.height;
	}

	public void draw(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		if (!gameOver) {
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			gc.setFill(Color.rgb(15, 15, 15));
			gc.fillRect(0, 0, width, height);

			gc.setFill(Color.rgb(25, 25, 25));
			gc.fillRect(0, 0, width, 70);
			gc.setStroke(Color.DARKRED);
			gc.strokeLine(0, 70, this.width, 70);

			gc.setLineWidth(1);


			try {
				gc.setFont(Font.loadFont(getClass().getResource("font.ttf").toExternalForm(), 40));
			} catch (Exception e) {
				logger.log(Level.INFO, "Cannot load the font!");
				gc.setFont(new Font("FreeSans", 60));
				e.printStackTrace();
			}

			// score
			gc.setStroke(Color.WHITE);
			gc.setFill(Color.WHITE);
			gc.fillText(String.valueOf(this.score.getScore()), 10, 55);

			gc.fillText(String.valueOf(this.lives), width - 50, 55);

			int bric = 0;
			int checkBrickBreakage = 0;
			for (int i = 0; i < 14; i++) {
				for (int j = 0; j < 14; j++) {
					bricks[i][j].draw(gc);
					if (bricks[i][j].getHidden()) {
						checkBrickBreakage++;
					}
					bric++;
				}
			}

			won = (bric == checkBrickBreakage);

			Stream.Builder<DrawableElement> drawableElementBuilder = Stream.builder();
			drawableElementBuilder.accept(gameParts[0]);
			drawableElementBuilder.accept(gameParts[1]);
			Stream<DrawableElement> empStream = drawableElementBuilder.build();

			Consumer<DrawableElement> gameControl = (e) -> {
				if (e instanceof Ball) {
					e.draw(gc);
					lives = ((Ball) e).getLives();
					if (lives < 1)
						gameOver = true;
				}
				// this means the game has been won
				if (e instanceof Paddle && !won) {
					e.draw(gc);
				}
			};

			empStream.forEach( gameControl );

		} else {
			gameOver(gc);
		}
	}

	public void gameOver(GraphicsContext gc) {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width, height);
		gc.setStroke(Color.WHITE);
		gc.setFill(Color.WHITE);
		gc.fillText("GAME OVER", width / 5 - 10, 80);
	}

	public void simulate(double timeDelta) {
		if (won) {
			for (DrawableElement e : gameParts) {
				if (e instanceof Ball) {
					e.simulate(timeDelta);
					return;
				}
			}
		}
		if (!gameOver) {
			for (DrawableElement e : gameParts) {
				e.simulate(timeDelta);
			}
			for (DrawableElement[] b : bricks) {
				for (DrawableElement brick : b) {
					if (brick.intersects(gameParts[0])) {
						addScore(brick.setTransparent(true));
						gameParts[0].hit(brick.getBoundingBox());
					}
				}
			}
		}
	}

	public void setPaddlePosition(Point2D position) {
		for (DrawableElement e : gameParts) {
			if (e instanceof Paddle) {
				e.setPosition(position);
			}
		}
	}

	public Point2D getPaddlePosition() {
		for (DrawableElement e : gameParts) {
			if (e instanceof Paddle) {
				return e.getPosition();
			}
		}
		return null;
	}

	public DrawableElement getPaddle() {
		for (DrawableElement e : gameParts) {
			if (e instanceof Paddle) {
				return e;
			}
		}
		return null;
	}

	public void addScore(int amount) {
		score.add(amount);
	}

	public Score getScore() {
		return this.score;
	}

	public int save() {
		logger.log(Level.INFO, "Saving game!");
		return this.score.getScore();
	}

	public boolean haveWeWon() {
		return this.won;
	}

}
