package com.example.cgalabs.graphic;

import com.example.cgalabs.graphic.impl.BrezenhemGraphicServiceImpl;
import com.example.cgalabs.model.Polygon;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.Pair;
import com.example.cgalabs.model.Color;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DrawerService {
	private static final Color DEFAULT_PIXEL_COLOR = new Color(0, 0, 0, 255);

	private final GraphicService graphicService = new BrezenhemGraphicServiceImpl();
	private HashSet<Pair<Point3D, Point3D>> alreadyDrawnPolygons = new HashSet<>();

	public void draw(List<Polygon> polygons, GraphicsContext graphicsContext) {
		alreadyDrawnPolygons.clear();

		var intBuffer = IntBuffer.allocate(1280 * 720);
		var pixels = intBuffer.array();
		var pixelBuffer = new PixelBuffer<>(1280, 720, intBuffer, PixelFormat.getIntArgbPreInstance());
		var image = new WritableImage(pixelBuffer);

		polygons.stream()
				.filter(this::isVisible)
				.forEach(polygon -> draw(polygon, pixels, graphicsContext));

		pixelBuffer.updateBuffer(buffer -> null);
		graphicsContext.drawImage(image, 0.0, 0.0);
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

		//возможно придется использовать самописный normalize
		return firstVector.crossProduct(secondVector).normalize();
	}

	public void draw(Polygon polygon, int[] pixels, GraphicsContext graphicsContext) {
		drawLineIfNotAlreadyDrawn(
				polygon.getFirstPolygon().getScreenSpacePointVector(),
				polygon.getSecondPolygon().getScreenSpacePointVector(),
				pixels);

		drawLineIfNotAlreadyDrawn(
				polygon.getSecondPolygon().getScreenSpacePointVector(),
				polygon.getThirdPolygon().getScreenSpacePointVector(),
				pixels);

		drawLineIfNotAlreadyDrawn(
				polygon.getThirdPolygon().getScreenSpacePointVector(),
				polygon.getFirstPolygon().getScreenSpacePointVector(),
				pixels);
	}

	private void drawLineIfNotAlreadyDrawn(Point3D firstLinePoint,
										   Point3D secondLinePoint,
										   int[] pixels) {
		var linePointsPair = new Pair<>(firstLinePoint, secondLinePoint);
		var reverseLinePointsPair = new Pair<>(secondLinePoint, firstLinePoint);

		if (alreadyDrawnPolygons.contains(linePointsPair) || alreadyDrawnPolygons.contains(reverseLinePointsPair)) {
			return;
		}

		graphicService.drawLine(firstLinePoint, secondLinePoint, DEFAULT_PIXEL_COLOR, new ArrayList<>(), pixels);
		alreadyDrawnPolygons.add(linePointsPair);
		alreadyDrawnPolygons.add(reverseLinePointsPair);
	}
}
