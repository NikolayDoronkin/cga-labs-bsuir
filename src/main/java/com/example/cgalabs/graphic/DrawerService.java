package com.example.cgalabs.graphic;

import com.example.cgalabs.graphic.impl.CanvasGraphicServiceImpl;
import com.example.cgalabs.model.Polygon;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.List;

public class DrawerService {
	private final GraphicService graphicService = new CanvasGraphicServiceImpl();
	private HashSet<Pair<Point2D, Point2D>> alreadyDrawnPolygons = new HashSet<>();

	public void draw(List<Polygon> polygons, GraphicsContext graphicsContext) {
		alreadyDrawnPolygons.clear();
		polygons.forEach(polygon -> draw(polygon, graphicsContext));
	}

	public void draw(Polygon polygon, GraphicsContext graphicsContext) {
		drawLineIfNotAlreadyDrawn(
				polygon.getFirstPolygon().getScreenSpacePointVector(),
				polygon.getSecondPolygon().getScreenSpacePointVector(),
				graphicsContext
		);

		drawLineIfNotAlreadyDrawn(
				polygon.getSecondPolygon().getScreenSpacePointVector(),
				polygon.getThirdPolygon().getScreenSpacePointVector(),
				graphicsContext
		);

		drawLineIfNotAlreadyDrawn(
				polygon.getThirdPolygon().getScreenSpacePointVector(),
				polygon.getFirstPolygon().getScreenSpacePointVector(),
				graphicsContext
		);
	}

	private void drawLineIfNotAlreadyDrawn(Point2D firstLinePoint,
										   Point2D secondLinePoint,
										   GraphicsContext graphicsContext) {
		var linePointsPair = new Pair<>(firstLinePoint, secondLinePoint);
		var reverseLinePointsPair = new Pair<>(secondLinePoint, firstLinePoint);

		if (alreadyDrawnPolygons.contains(linePointsPair) || alreadyDrawnPolygons.contains(reverseLinePointsPair)) {
			return;
		}

		graphicService.drawLine(firstLinePoint, secondLinePoint, graphicsContext);
		alreadyDrawnPolygons.add(linePointsPair);
		alreadyDrawnPolygons.add(reverseLinePointsPair);
	}
}
