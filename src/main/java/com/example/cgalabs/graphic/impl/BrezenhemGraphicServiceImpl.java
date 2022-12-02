package com.example.cgalabs.graphic.impl;

import com.example.cgalabs.graphic.GraphicService;
import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Pixel;
import javafx.geometry.Point3D;

import java.util.List;

import static org.apache.commons.math3.util.FastMath.abs;

public class BrezenhemGraphicServiceImpl implements GraphicService {

	@Override
	public void drawLine(Point3D startPoint, Point3D endPoint, Color color, List<Pixel> sidePixels, int[] pixels) {
		var x1 = (int) startPoint.getX();
		var y1 = (int) startPoint.getY();
		var z1 = startPoint.getZ();

		var x2 = (int) endPoint.getX();
		var y2 = (int) endPoint.getY();
		var z2 = endPoint.getZ();

		var dx = abs(x2 - x1);
		var dy = abs(y2 - y1);
		var dz = abs(z2 - z1);

		var xs = x2 > x1 ? 1 : -1;
		var ys = y2 > y1 ? 1 : -1;
		var zs = z2 > z1 ? 1 : -1;

		if (dx >= dy && dx >= dz) {
			var p1 = 2 * dy - dx;
			var p2 = 2 * dz - dx;

			while (x1 != x2) {
				x1 += xs;

				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dx;
				}

				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dx;
				}

				p1 += 2 * dy;
				p2 += 2 * dz;

				drawPoint(pixels, x1, y1, z1, color, sidePixels);
			}
		} else if (dy >= dx && dy >= dz) {
			var p1 = 2 * dx - dy;
			var p2 = 2 * dz - dy;

			while (y1 != y2) {
				y1 += ys;

				if (p1 >= 0) {
					x1 += xs;
					p1 -= 2 * dy;
				}

				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dy;
				}

				p1 += 2 * dx;
				p2 += 2 * dz;

				drawPoint(pixels, x1, y1, z1, color, sidePixels);
			}
		} else {
			var p1 = 2 * dy - dz;
			var p2 = 2 * dx - dz;

			while (z1 < z2) {
				z1 += zs;

				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dz;
				}

				if (p2 >= 0) {
					x1 += xs;
					p2 -= 2 * dz;
				}

				p1 += 2 * dy;
				p2 += 2 * dx;

				drawPoint(pixels, x1, y1, z1, color, sidePixels);
			}
		}
	}
}
