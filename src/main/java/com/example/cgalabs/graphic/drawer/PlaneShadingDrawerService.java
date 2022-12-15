package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.graphic.lighting.Lighting;
import com.example.cgalabs.graphic.lighting.PhongLighting;
import com.example.cgalabs.model.*;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;
import lombok.val;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Stream;

import static org.apache.commons.math3.util.FastMath.abs;
import static org.apache.commons.math3.util.FastMath.round;

public class PlaneShadingDrawerService extends WireDrawerService {

	protected final ZBuffer zBuffer = new ZBuffer(1280, 720);
//	private final Lighting lighting = new LambertLighting();
	protected final Lighting lighting = new PhongLighting();

	@Override
	public void draw(List<Polygon> polygons, Point3D viewVector, GraphicsContext graphicsContext) {
		zBuffer.update();
		super.draw(polygons, viewVector, graphicsContext);
	}

	@Override
	protected void drawPolygon(Polygon polygon, Point3D viewVector, int[] pixels, Color color, List<Pixel> sidePixels) {
//		ДЛЯ PhongShading НЕ ИСПОЛЬЗОВАТЬ СТРОКУ НИЖЕ
//		var polygonColor = Optional.ofNullable(getPolygonColor(polygon, viewVector)).orElse(DEFAULT_PIXEL_COLOR);
		var sides = new ArrayList<Pixel>();

		super.drawPolygon(polygon, viewVector, pixels, color, sides);
		drawInnerPolygonPixels(pixels, sides, color, viewVector);
	}

	private Color getPolygonColor(Polygon polygon, Point3D viewVector) {
		var firstPointNormalVector = polygon.getFirstPolygon().getNormalVector();
		var secondPointNormalVector = polygon.getSecondPolygon().getNormalVector();
		var thirdPointNormalVector = polygon.getThirdPolygon().getNormalVector();

		if (checkForNull(firstPointNormalVector, secondPointNormalVector, thirdPointNormalVector)) return null;

		var firstPointColor = lighting.getPointColor(firstPointNormalVector, viewVector, DEFAULT_PIXEL_COLOR);
		var secondPointColor = lighting.getPointColor(secondPointNormalVector, viewVector, DEFAULT_PIXEL_COLOR);
		var thirdPointColor = lighting.getPointColor(thirdPointNormalVector, viewVector, DEFAULT_PIXEL_COLOR);

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

	protected void drawInnerPolygonPixels(int[] pixels, List<Pixel> sidePixels, Color color, Point3D viewVector) {
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
				drawPoint(pixels, x, y, z, color, new ArrayList<>(), viewVector, DEFAULT_NORMAL_VECTOR, OF_DEFAULT);
				z += dz;
			}
		}
	}

	@Override
	protected void drawPoint(int[] pixels, int x, int y, double z, Color color, List<Pixel> sidePixels,
							 Point3D viewVector, Point3D normalVector, Point4D currPosition) {
		sidePixels.add(new Pixel(x, y, z, color, normalVector, currPosition));

		if (validateCoordinate(x, y, z)) {
			zBuffer.setValue(x, y, z);
			super.drawPoint(pixels, x, y, z, color, sidePixels, viewVector, normalVector, OF_DEFAULT);
		}
	}

	private boolean validateCoordinate(int x, int y, double z) {
		return (x > 0 && x < zBuffer.getWidth()) &&
				(y > 0 && y < zBuffer.getHeight()) &&
				(z <= zBuffer.getValue(x, y));
	}

	protected Pair<Integer, Integer> getMinMaxY(List<Pixel> sidePixels) {
		var sortedSidesPixels = sidePixels.stream()
				.sorted(Comparator.comparing(Pixel::getY))
				.toList();

		return getPair(sortedSidesPixels);
	}

	protected Pair<Integer, Integer> getPair(List<Pixel> sortedSidesPixels) {
		var size = sortedSidesPixels.size();

		return !sortedSidesPixels.isEmpty()
				? new Pair<>(sortedSidesPixels.get(0).getY(), sortedSidesPixels.get(size - 1).getY())
				: new Pair<>(null, null);
	}

	protected Pair<Pixel, Pixel> getStartEndPixelsByY(List<Pixel> sidesPixels, int y) {
		val filteredSidesPixels = sidesPixels.stream()
				.filter(pixel -> pixel.getY() == y)
				.sorted(Comparator.comparing(Pixel::getX))
				.toList();

		var size = filteredSidesPixels.size();

		return !filteredSidesPixels.isEmpty()
				? new Pair<>(filteredSidesPixels.get(0), filteredSidesPixels.get(size - 1))
				: new Pair<>(null, null);
	}
}
