package com.java2.hon0102;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface DrawableElement {
	void draw(GraphicsContext gc);

	void simulate(double time);

	void setColor(int color);

	int setTransparent(boolean transparent);

	void setPosition(Point2D position);

	Point2D getPosition();

	Rectangle2D getBoundingBox();

	default boolean intersects(DrawableElement element) {
		return element.getBoundingBox().intersects(getBoundingBox());
	}

	void hit(Rectangle2D boundingBox);

	boolean getHidden();
}
