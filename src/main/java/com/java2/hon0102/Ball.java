package com.java2.hon0102;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Math.abs;

@Getter
@Setter
public class Ball implements DrawableElement {
	private Point2D position;
	private int topOffset = 70;
	private int size; // size of ball
	private int paddleWidth;
	private double speedUp = 1.2;

	private short lives = 3;

	private Image ballImage;
	private Game game;
	private double dirX = 3;
	private double dirY = -2;

	private static final Logger logger = LogManager.getLogger(Ball.class);

	public Ball(Game game, int size, double speed, int paddleWidth) {
		this.size = size;
		this.game = game;
		this.position = new Point2D(game.getWidth() / 2, game.getHeight() - 70 - this.size);
		ballImage = new Image(getClass().getResourceAsStream("ball.png"), size, size, true, true);
		this.paddleWidth = paddleWidth;
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.save();
		gc.setFill(Color.TRANSPARENT);
		gc.drawImage(ballImage, position.getX(), position.getY());
		gc.restore();
	}

	@Override
	public void simulate(double time) {
		double timeStep = time * 100;
		this.position = this.position.add(dirX * timeStep * speedUp, dirY * timeStep * speedUp);

		// check wall collision
		if (this.position.getX() <= 0) {
			this.dirX = this.dirX * -1;
			this.position = new Point2D(0, position.getY());
		}
		if (this.position.getX() >= game.getWidth() - this.size) {
			this.dirX = this.dirX * -1;
			this.position = new Point2D(game.getWidth() - this.size, position.getY());
		}
		if (this.position.getY() <= topOffset) {
			this.dirY = this.dirY * -1;
			this.position = new Point2D(position.getX(), topOffset);
		}
		if (this.position.getY() >= game.getHeight() - this.size) {
			if (!game.haveWeWon()) {
				this.dirY = this.dirY * -1;
				this.position = new Point2D(game.getPaddle().getPosition().getX() + paddleWidth / 2,
						game.getPaddle().getPosition().getY() - size);
				lives--;
				System.out.println(speedUp);
				speedUp = 1.2;
			} else {
				this.dirY = this.dirY * -1;
				this.position = new Point2D(position.getX(), game.getHeight() - size);
			}
		}

		// check paddle collision
		if (position.getY() >= game.getPaddle().getPosition().getY()
				&& position.getX() + size > game.getPaddle().getPosition().getX()
				&& position.getX() < game.getPaddle().getPosition().getX() + paddleWidth) {
			double paddleX = game.getPaddle().getPosition().getX();
			int posX = (int) ((this.position.getX() + size / 2) - (paddleX + paddleWidth / 2));
			double speedXY = Math.sqrt(dirX * dirX + dirY * dirY);

			dirY = Math.sqrt(speedXY * speedXY - dirX * dirX) * (dirY > 0 ? -1 : 1);
			dirX = posX * 0.2;
			if (dirX > 3)
				dirX = 3;
			if (dirX < -3)
				dirX = -3;

			position = new Point2D(position.getX(), game.getPaddle().getBoundingBox().getMinY() - size);
		}

	}

	@Override
	public void setColor(int color) {
		// TODO Auto-generated method stub

	}

	@Override
	public int setTransparent(boolean transparent) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Rectangle2D getBoundingBox() {
		return new Rectangle2D(this.position.getX(), this.position.getY(), this.size, this.size);
	}

	@Override
	public void hit(Rectangle2D BrickBoundingBox) {

		logger.log(Level.INFO,"hit brick at "+ BrickBoundingBox.getMinX() +  " " + BrickBoundingBox.getMinY());
		if (speedUp < 2.7)
			speedUp += 0.05;

		// bottom
		if (this.dirY < 0) {
			if (this.position.getY() - BrickBoundingBox.getHeight() >= BrickBoundingBox.getMinY()
					- abs(this.position.getY() - BrickBoundingBox.getMinY())) {
				this.dirY *= -1;
				this.position = new Point2D(this.position.getX(),
						this.position.getY() + abs(this.position.getY() - BrickBoundingBox.getMinY()));
				return;
			}
		}

		// top
		if (this.dirY > 0) {
			if (this.position.getY() + size <= BrickBoundingBox.getMaxY()) {
				this.dirY *= -1;
				this.position = new Point2D(this.position.getX(),
						this.position.getY() - abs(this.position.getY() + size - BrickBoundingBox.getMinY()));
				return;
			}
		}

		// right
		if (this.dirX < 0) {
			this.dirX *= -1;
			this.position = new Point2D(this.position.getX() + abs(this.position.getX() - BrickBoundingBox.getMaxX()),
					this.position.getY());
			return;
		}

		// left
		if (this.dirX > 0) {
			this.dirX *= -1;
			this.position = new Point2D(
					this.position.getX() - abs(this.position.getX() + size - BrickBoundingBox.getMinX()),
					this.position.getY());
		}
	}

	@Override
	public boolean getHidden() {
		// TODO Auto-generated method stub
		return false;
	}
}
