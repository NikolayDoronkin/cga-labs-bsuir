package com.example.cgalabs.graphic.impl;

import com.example.cgalabs.graphic.GraphicService;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DdaGraphicServiceImpl implements GraphicService {

	@Override
	public void drawLine(Point2D startPoint, Point2D endPoint, GraphicsContext graphicsContext) {
		var roundedStartX = (int) Math.round(startPoint.getX());
		var roundedStartY = (int) Math.round(startPoint.getY());
		var roundedEndX = (int) Math.round(endPoint.getX());
		var roundedEndY = (int) Math.round(endPoint.getY());

		var deltaX = Math.abs(roundedStartX - roundedEndX);
		var deltaY = Math.abs(roundedStartY - roundedEndY);

		var length = Math.max(deltaX, deltaY);
		if (length == 0) {
			graphicsContext.getPixelWriter().setColor(roundedStartX + 650, roundedStartY + 350, Color.BLACK);
			return;
		}

		var dX = (endPoint.getX() - startPoint.getX()) / length;
		var dY = (endPoint.getY() - startPoint.getY()) / length;

		var x = startPoint.getX();
		var y = startPoint.getY();
		for (int i = 0; i <= length; i++) {
			graphicsContext.getPixelWriter().setColor((int) Math.round(x + 650), (int) Math.round(y + 350), Color.BLACK);
			x += dX;
			y += dY;
		}
	}
}
