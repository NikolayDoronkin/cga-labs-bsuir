package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.graphic.lighting.LambertLighting;
import com.example.cgalabs.graphic.lighting.Lighting;
import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Pixel;
import com.example.cgalabs.model.Polygon;
import com.example.cgalabs.model.ZBuffer;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;
import lombok.val;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Stream;

import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.round;

public class PlaneShadingDrawerService extends WireDrawerService {

	private ZBuffer zBuffer = new ZBuffer(3000, 3000);
	private Lighting lighting = new LambertLighting();

	@Override
	public void draw(List<Polygon> polygons, GraphicsContext graphicsContext) {
		super.draw(polygons, graphicsContext);
	}

	@Override
	protected void drawPolygon(Polygon polygon, int[] pixels, Color color, List<Pixel> sidePixels) {
		var polygonColor = Optional.ofNullable(getPolygonColor(polygon)).orElse(DEFAULT_PIXEL_COLOR);
		var sides = new ArrayList<Pixel>();
		super.drawPolygon(polygon, pixels, polygonColor, sides);
		drawInnerPolygonPixels(pixels, sides, polygonColor);
	}

	private Color getPolygonColor(Polygon polygon) {
		var firstPointNormalVector = polygon.getFirstPolygon().getNormalVector();
		var secondPointNormalVector = polygon.getSecondPolygon().getNormalVector();
		var thirdPointNormalVector = polygon.getThirdPolygon().getNormalVector();

		if (checkForNull(firstPointNormalVector, secondPointNormalVector, thirdPointNormalVector)) return null;

		var firstPointColor = lighting.getPointColor(firstPointNormalVector, DEFAULT_PIXEL_COLOR);
		var secondPointColor = lighting.getPointColor(secondPointNormalVector, DEFAULT_PIXEL_COLOR);
		var thirdPointColor = lighting.getPointColor(thirdPointNormalVector, DEFAULT_PIXEL_COLOR);

		return calcAverageColor(firstPointColor, secondPointColor, thirdPointColor);
	}

	private Color calcAverageColor(Color firstPointColor, Color secondPointColor, Color thirdPointColor) {
		var sumR = firstPointColor.r() + secondPointColor.r() + thirdPointColor.r();
		var sumG = firstPointColor.g() + secondPointColor.g() + thirdPointColor.g();
		var sumB = firstPointColor.b() + secondPointColor.b() + thirdPointColor.b();
		var sumA = firstPointColor.a() + secondPointColor.a() + thirdPointColor.a();

		var r = (int) round(sumR / 3.0);
		var g = (int) round(sumG / 3.0);
		var b = (int) round(sumB / 3.0);
		var a = (int) round(sumA / 3.0);

		return new Color(r, g, b, a);
	}

	private boolean checkForNull(Point3D first, Point3D second, Point3D third) {
		return Stream.of(first, second, third).anyMatch(Objects::isNull);
	}

	private void drawInnerPolygonPixels(int[] pixels, List<Pixel> sidePixels, Color color) {
		var minAndMaxY = getMinMaxY(sidePixels);
		var minY = minAndMaxY.getKey();
		var maxY = minAndMaxY.getValue();
		if (minY == null || maxY == null) return;

		for (int y = minY; y < maxY; y++) {
			var startAndEndPixel = getStartEndPixelsByY(sidePixels, y);
			var startPixel = startAndEndPixel.getKey();
			var endPixel = startAndEndPixel.getValue();

			if (startPixel == null || endPixel == null) continue;

			var z = startPixel.getZ();
			var dz = (endPixel.getZ() - startPixel.getZ()) / abs(endPixel.getX() - startPixel.getX());

			for (int x = startPixel.getX(); x < endPixel.getX(); x++) {
				drawPoint(pixels, x, y, z, color, new ArrayList<>());
				z += dz;
			}
		}
	}

	@Override
	protected void drawPoint(int[] pixels, int x, int y, double z, Color color, List<Pixel> sidePixels) {
		sidePixels.add(new Pixel(x, y, z, color));

		if (validateCoordinate(x, y, z)) {
			zBuffer.setValue(x, y, z);
			super.drawPoint(pixels, x, y, z, color, sidePixels);
		}
	}

	private boolean validateCoordinate(int x, int y, double z) {
		return (x >= 1 && x <= zBuffer.getWidth()) &&
				(y >= 1 && y <= zBuffer.getHeight()) &&
				(z <= zBuffer.getValue(x, y) + 5);
	}

	private Pair<Integer, Integer> getMinMaxY(List<Pixel> sidePixels) {
		var sortedSidesPixels = sidePixels.stream()
				.sorted(Comparator.comparing(Pixel::getY))
				.toList();

		return getPair(sortedSidesPixels);
	}

	private Pair<Integer, Integer> getPair(List<Pixel> sortedSidesPixels) {
		int size = sortedSidesPixels.size();

		return !sortedSidesPixels.isEmpty()
				? new Pair<>(sortedSidesPixels.get(0).getY(), sortedSidesPixels.get(size - 1).getY())
				: new Pair<>(null, null);
	}

	private Pair<Pixel, Pixel> getStartEndPixelsByY(List<Pixel> sidesPixels, int y) {
		val filteredSidesPixels = sidesPixels.stream()
				.filter(pixel -> pixel.getY() == y)
				.sorted(Comparator.comparing(Pixel::getX))
				.toList();

		int size = filteredSidesPixels.size();

		return !filteredSidesPixels.isEmpty()
				? new Pair<>(filteredSidesPixels.get(0), filteredSidesPixels.get(size - 1))
				: new Pair<>(null, null);
	}
}
