package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Pixel;
import com.example.cgalabs.model.PolygonPoint;
import javafx.geometry.Point3D;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.math3.util.FastMath.abs;

public class PhongShadingDrawer extends PlaneShadingDrawerService {

	@Override
	protected void drawSide(PolygonPoint firstSidePoint, PolygonPoint secondSidePoint, Color color, int[] pixels,
							List<Pixel> sidesPixels, Point3D viewVector) {
		var firstPointVector = firstSidePoint.getScreenSpacePointVector();
		var firstPointNormalVector = firstSidePoint.getNormalVector();

		if (Objects.isNull(firstPointNormalVector)) return;

		var firstPointPixel = getSidePixel(firstPointVector, color, firstPointNormalVector);

		var secondPointVector = secondSidePoint.getScreenSpacePointVector();
		var secondPointNormalVector = secondSidePoint.getNormalVector();

		if (Objects.isNull(secondPointNormalVector)) return;

		var secondPointPixel = getSidePixel(secondPointVector, color, secondPointNormalVector);

		drawLine(firstPointPixel, secondPointPixel, color, sidesPixels, pixels, viewVector);
	}

	private Pixel getSidePixel(Point3D pointVector, Color color, Point3D normalVector) {
		return new Pixel(
				(int) pointVector.getX(),
				(int) pointVector.getY(),
				pointVector.getZ(), color, normalVector);
	}

	@Override
	public void drawLine(Pixel startPoint, Pixel endPoint, Color color, List<Pixel> sidePixels, int[] pixels, Point3D viewVector) {
		var x1 = startPoint.getX();
		var y1 = startPoint.getY();
		var z1 = startPoint.getZ();

		var x2 = endPoint.getX();
		var y2 = endPoint.getY();
		var z2 = endPoint.getZ();

		var dx = abs(x2 - x1);
		var dy = abs(y2 - y1);
		var dz = abs(z2 - z1);

		var xs = x2 > x1 ? 1 : -1;
		var ys = y2 > y1 ? 1 : -1;
		var zs = z2 > z1 ? 1 : -1;

		var currNormal = startPoint.getNormalVector();
		var sameX = abs(startPoint.getX() - endPoint.getX()) < abs(endPoint.getY() - startPoint.getY());

		var deltaNormal = sameX
				? endPoint.getNormalVector().subtract(startPoint.getNormalVector()).multiply((double) 1 / dy)
				: endPoint.getNormalVector().subtract(startPoint.getNormalVector()).multiply((double) 1 / dx);

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

				if (!sameX) {
					currNormal = add(currNormal, deltaNormal);
				}

				drawPoint(pixels, x1, y1, z1, color, sidePixels, viewVector, currNormal);
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

				if (sameX) {
					currNormal = add(currNormal, deltaNormal);
				}

				drawPoint(pixels, x1, y1, z1, color, sidePixels, viewVector, currNormal);
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

				drawPoint(pixels, x1, y1, z1, color, sidePixels, viewVector, currNormal);
			}
		}
	}

	@Override
	protected void drawInnerPolygonPixels(int[] pixels, List<Pixel> sidePixels, Color color, Point3D viewVector) {
		var minAndMaxY = super.getMinMaxY(sidePixels);
		var minY = minAndMaxY.getKey();
		var maxY = minAndMaxY.getValue();

		if (minY == null || maxY == null) return;

		for (int y = minY; y < maxY; y++) {
			var startAndEndPixel = getStartEndPixelsByY(sidePixels, y);
			var startPixel = startAndEndPixel.getKey();
			var endPixel = startAndEndPixel.getValue();

			if (startPixel == null || endPixel == null) continue;

			var z = startPixel.getZ();
			var dz = (endPixel.getZ() - startPixel.getZ()) - abs(endPixel.getX() - startPixel.getX());

			var deltaNormal = endPixel.getNormalVector().subtract(startPixel.getNormalVector())
					.multiply((double) 1 / (endPixel.getX() - startPixel.getX()));

			var currNormal = startPixel.getNormalVector();

			for (int x = startPixel.getX(); x < endPixel.getX(); x++) {
				currNormal = add(currNormal, deltaNormal);
				drawPoint(pixels, x, y, z, color, sidePixels, viewVector, currNormal);
				z += dz;
			}
		}
	}


	@Override
	protected void drawPoint(int[] pixels, int x, int y, double z, Color color, List<Pixel> sidePixels,
							 Point3D viewVector, Point3D normalVector) {
		var calculatedPixelColor = lighting.getPointColor(normalVector, viewVector, color);

		super.drawPoint(pixels, x, y, z, calculatedPixelColor, sidePixels, viewVector, normalVector);
	}

	private Point3D add(Point3D first, Point3D second) {
		return new Point3D(
				first.getX() + second.getX(),
				first.getY() + second.getY(),
				first.getZ() + second.getZ());
	}
}
