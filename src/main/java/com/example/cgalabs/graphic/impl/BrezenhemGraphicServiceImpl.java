package com.example.cgalabs.graphic.impl;

import com.example.cgalabs.graphic.GraphicService;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BrezenhemGraphicServiceImpl implements GraphicService {
	@Override
	public void drawLine(Point2D startPoint, Point2D endPoint, GraphicsContext graphicsContext) {
		var dx = (int) (endPoint.getX() - startPoint.getX());
		var dy = (int) (endPoint.getY() - startPoint.getY());

		var incx = sign(dx);
		var incy = sign(dy);

		dx = Math.abs(dx);
		dy = Math.abs(dy);

		int pdx, pdy, es, el;

		if (dx > dy) {
			pdx = incx;
			pdy = 0;
			es = dy;
			el = dx;
		} else {
			pdx = 0;
			pdy = incy;
			es = dx;
			el = dy;
		}

		int x = (int) startPoint.getX();
		int y = (int) startPoint.getY();
		int err = el / 2;
		graphicsContext.getPixelWriter().setColor(x, y, Color.BLACK);

		int t = 0;
		while (t < el) {
			err -= es;
			if (err < 0) {
				err += el;
				x += incx;
				y += incy;
			} else {
				x += pdx;
				y += pdy;
			}
			graphicsContext.getPixelWriter().setColor(x, y, Color.BLACK);
		}
	}

	private int sign(int x) {
//		if (x > 0) return  1 ; else if (x < 0) return  -1 ; else return 0;
		return Integer.compare(x, 0);
	}
}
