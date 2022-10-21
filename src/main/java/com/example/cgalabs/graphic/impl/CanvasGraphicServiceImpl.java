package com.example.cgalabs.graphic.impl;

import com.example.cgalabs.graphic.GraphicService;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class CanvasGraphicServiceImpl implements GraphicService {

	@Override
	public void drawLine(Point2D startPoint, Point2D endPoint, GraphicsContext graphicsContext) {
		graphicsContext.setLineWidth(0.25);

		graphicsContext.beginPath();

		graphicsContext.moveTo(startPoint.getX() + 650, startPoint.getY() + 350);
		graphicsContext.lineTo(endPoint.getX() + 650, endPoint.getY() + 350);

		graphicsContext.stroke();
	}
}
