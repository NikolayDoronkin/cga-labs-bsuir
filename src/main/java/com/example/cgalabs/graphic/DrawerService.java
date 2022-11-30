package com.example.cgalabs.graphic;

import com.example.cgalabs.graphic.impl.BrezenhemGraphicServiceImpl;
import com.example.cgalabs.graphic.impl.DdaGraphicServiceImpl;
import com.example.cgalabs.model.Polygon;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.util.Callback;
import org.apache.commons.math3.util.Pair;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;

public class DrawerService {
	private final GraphicService graphicService = new BrezenhemGraphicServiceImpl();
	private HashSet<Pair<Point2D, Point2D>> alreadyDrawnPolygons = new HashSet<>();

	public void draw(List<Polygon> polygons, GraphicsContext graphicsContext) {
		alreadyDrawnPolygons.clear();

		var intBuffer = IntBuffer.allocate(1280 * 720);
		var pixels = intBuffer.array();
		var pixelBuffer = new PixelBuffer(1280, 720, intBuffer, PixelFormat.getIntArgbPreInstance());
		var image = new WritableImage(pixelBuffer);

		polygons.forEach(polygon -> draw(polygon, pixels, graphicsContext));

		pixelBuffer.updateBuffer((Callback<PixelBuffer, Rectangle2D>) pixelBuffer1 -> null);
		graphicsContext.drawImage(image, 0.0, 0.0);
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

	private void drawLineIfNotAlreadyDrawn(Point2D firstLinePoint,
										   Point2D secondLinePoint,
										   int[] pixels) {
		var linePointsPair = new Pair<>(firstLinePoint, secondLinePoint);
		var reverseLinePointsPair = new Pair<>(secondLinePoint, firstLinePoint);

		if (alreadyDrawnPolygons.contains(linePointsPair) || alreadyDrawnPolygons.contains(reverseLinePointsPair)) {
			return;
		}

		graphicService.drawLine(firstLinePoint, secondLinePoint, pixels);
		alreadyDrawnPolygons.add(linePointsPair);
		alreadyDrawnPolygons.add(reverseLinePointsPair);
	}
}
