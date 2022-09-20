package com.java2.hon0102;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Brick implements DrawableElement {
	Color color;
	private int scoreMultiplier = 0;
	private int topOffset = 70;
	boolean broken = false;
	double width;
	double height;
	Point2D position;

	public Brick(double gameWidth, Point2D position) {
		this.width = gameWidth / 14;
		this.height = this.width / 2;
		this.position = position;
	}

	@Override
	public void setColor(int color) {
		switch (color) {
		case 0:
			this.color = Color.RED;
			this.scoreMultiplier = 7;
			break;
		case 1:
			this.color = Color.ORANGE;
			this.scoreMultiplier = 7;
			break;
		case 2:
			this.color = Color.GREEN;
			this.scoreMultiplier = 4;
			break;
		case 3:
			this.color = Color.GREENYELLOW;
			this.scoreMultiplier = 4;
			break;
		case 4:
			this.color = Color.YELLOW;
			this.scoreMultiplier = 1;
			break;
		case 5:
			this.color = Color.DODGERBLUE;
			this.scoreMultiplier = 1;
			break;
		default:
			break;
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		if (!this.broken) {
			gc.save();
			gc.setFill(this.color);
			gc.fillRect(position.getX() * this.width + 2, position.getY() * this.height + topOffset - 2, this.width - 2,
					this.height - 2);
			gc.restore();
		}
	}

	@Override
	public void simulate(double time) {
		// TODO Auto-generated method stub

	}

	// sets transparency and returns multiplier, ie break the brick
	@Override
	public int setTransparent(boolean broken) {
		this.broken = broken;
		return scoreMultiplier;
	}

	@Override
	public Rectangle2D getBoundingBox() {
		return new Rectangle2D(this.position.getX() * width, this.position.getY() * height + topOffset, this.width,
				this.height);
	}

	@Override
	public void hit(Rectangle2D boundingBox) {
		System.out.println("No hit for brick");

	}

	@Override
	public boolean intersects(DrawableElement element) {
		if (this.broken)
			return false;

		return element.getBoundingBox().intersects(getBoundingBox());
	}

	@Override
	public boolean getHidden() {
		return this.broken;

	}
}
