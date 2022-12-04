package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Pixel;
import com.example.cgalabs.model.Polygon;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class WireDrawerService implements DrawerService{
	protected static final Color DEFAULT_PIXEL_COLOR = new Color(128, 128, 128);

	@Override
	public void draw(List<Polygon> polygons, Point3D viewVector, GraphicsContext graphicsContext) {
		var intBuffer = IntBuffer.allocate(1280 * 720);
		var pixels = intBuffer.array();
		var pixelBuffer = new PixelBuffer<>(1280, 720, intBuffer, PixelFormat.getIntArgbPreInstance());
		var image = new WritableImage(pixelBuffer);

		polygons.stream()
				.filter(this::isVisible)
				.forEach(polygon -> drawPolygon(polygon, viewVector, pixels));

		pixelBuffer.updateBuffer(buffer -> null);
		graphicsContext.drawImage(image, 0.0, 0.0);
	}

	private void drawPolygon(Polygon polygon, Point3D viewVector, int[] pixels) {
		drawPolygon(polygon, viewVector, pixels, DEFAULT_PIXEL_COLOR, new ArrayList<>());
	}

	protected void drawPolygon(Polygon polygon, Point3D viewVector, int[] pixels, Color color, List<Pixel> sidePixels) {
		drawLine(
				polygon.getFirstPolygon().getScreenSpacePointVector(),
				polygon.getSecondPolygon().getScreenSpacePointVector(),
				color,
				sidePixels,
				pixels
		);

		drawLine(
				polygon.getSecondPolygon().getScreenSpacePointVector(),
				polygon.getThirdPolygon().getScreenSpacePointVector(),
				color,
				sidePixels,
				pixels
		);

		drawLine(
				polygon.getThirdPolygon().getScreenSpacePointVector(),
				polygon.getFirstPolygon().getScreenSpacePointVector(),
				color,
				sidePixels,
				pixels
		);
	}

	private boolean isVisible(Polygon polygon) {
		return getPolygonNormal(polygon).getZ() < 0;
	}

	private Vector3D getPolygonNormal(Polygon polygon) {
		var firstPointVector = polygon.getFirstPolygon().getScreenSpacePointVector();
		var secondPointVector = polygon.getSecondPolygon().getScreenSpacePointVector();
		var thirdPointVector = polygon.getThirdPolygon().getScreenSpacePointVector();

		var firstVector = new Vector3D(
				secondPointVector.getX() - firstPointVector.getX(),
				secondPointVector.getY() - firstPointVector.getY(),
				secondPointVector.getZ() - firstPointVector.getZ());

		var secondVector = new Vector3D(
				thirdPointVector.getX() - firstPointVector.getX(),
				thirdPointVector.getY() - firstPointVector.getY(),
				thirdPointVector.getZ() - firstPointVector.getZ());

		return firstVector.crossProduct(secondVector).normalize();

	}

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

	protected void drawPoint(int[] arr, int x, int y, double z, Color color, List<Pixel> sidePixels) {
		var pixelIndex = (x % 1280) + (y * 1280);

		if (pixelIndex < 0 || pixelIndex >= arr.length) {
			return;
		}

		arr[(x % 1280) + (y * 1280)] = argbInt(color);
	}

	private int argbInt(Color color) {
		return color.a() << 24 | (color.r() << 16) | (color.g() << 8) | color.b();
	}
}
