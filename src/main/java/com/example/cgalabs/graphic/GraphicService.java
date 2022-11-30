package com.example.cgalabs.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public interface GraphicService {
	int PIXEL_COLOR_ARGB = 255 << 24 | (0 << 16) | (0 << 8) | 0;
//	void drawLine(Point2D startPoint, Point2D endPoint, GraphicsContext graphicsContext);
	void drawLine(Point2D startPoint, Point2D endPoint, int[] pixels);

	default void drawPoint(int[] arr, int x, int y) {
		var pixelIndex = (x % 1280) + (y * 1280);

		if (pixelIndex < 0 || pixelIndex >= arr.length) {
			return;
		}

		arr[(x % 1280) + (y * 1280)] = PIXEL_COLOR_ARGB;
	}
}
