package com.example.cgalabs.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public interface GraphicService {
	void drawLine(Point2D startPoint, Point2D endPoint, GraphicsContext graphicsContext);
}
