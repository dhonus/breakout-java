package com.java2.hon0102;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle implements DrawableElement {

	private int width;
	private int height;
	private Point2D position;
	private Game game;

	public Paddle(Game game, int width) {
		this.width = width;
		this.height = width / 4;
		this.game = game;
	}

	@Override
	public void draw(GraphicsContext gc) {
		gc.save();
		gc.setFill(Color.WHITE);
		gc.fillRect(position.getX(), position.getY(), width, height);
		gc.restore();
	}

	@Override
	public void simulate(double time) {

	}

	@Override
	public void setColor(int color) {
		// TODO Auto-generated method stub
	}

	@Override
	public int setTransparent(boolean transparent) {
		return 0;
	}

	@Override
	public void setPosition(Point2D position) {
		if (position.getX() + width > game.getWidth())
			this.position = new Point2D(game.getWidth() - width, position.getY());
		else
			this.position = position;
	}

	public Point2D getPosition() {
		return this.position;
	}

	@Override
	public Rectangle2D getBoundingBox() {
		return new Rectangle2D(this.position.getX(), this.position.getY(), this.width, this.height);
	}

	@Override
	public void hit(Rectangle2D boundingBox) {
		System.out.println("No hit for paddle");

	}

	@Override
	public boolean getHidden() {
		// TODO Auto-generated method stub
		return false;
	}

}
