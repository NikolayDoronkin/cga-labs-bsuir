package com.example.cgalabs.parser;

import com.example.cgalabs.model.ObjectData;
import com.example.cgalabs.model.Point4D;
import com.example.cgalabs.model.Polygon;
import com.example.cgalabs.model.PolygonPoint;
import javafx.geometry.Point3D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserService {
	private static final String V = "v";
	private static final String VT = "vt";
	private static final String VN = "vn";
	private static final String F = "f";
	private static final String SPACE_REGEXP = "\\s+";
	private static final String SLASH_REGEXP = "/";

	public ObjectData readFromFile(String filePath) throws IOException {
		var geometricPoints = new ArrayList<Point4D>();
		var texturePoints = new ArrayList<Point3D>();
		var normalVectors = new ArrayList<Point3D>();
		var polygons = new ArrayList<Polygon>();

		Files.readAllLines(Path.of(filePath))
				.stream()
//				.filter(line -> !line.contains(SPACE_REGEXP))
				.map(this::parseLine)
				.forEach(line -> {
					switch (line.get(0)) {
						case V -> geometricPoints.add(getGeometricPoint(line));
						case VT -> texturePoints.add(getTexturePoint(line));
						case VN -> normalVectors.add(getNormalVector(line));
						case F -> polygons.add(getPolygon(line, geometricPoints, texturePoints, normalVectors));
					}
				});

		return ObjectData.builder()
				.geometricPoints(geometricPoints)
				.texturePoints(texturePoints)
				.normalVectors(normalVectors)
				.polygons(polygons)
				.build();
	}

	private Point4D getGeometricPoint(List<String> line) {
		return Point4D.builder()
				.x(Double.valueOf(line.get(1)))
				.y(Double.valueOf(line.get(2)))
				.z(Double.valueOf(line.get(3)))
				.w(line.size() == 5 ? Double.parseDouble(line.get(4)) : 1)
				.build();
	}

	private Point3D getTexturePoint(List<String> line) {
		float x = Float.parseFloat(line.get(1));
		float y = 0;
		float z = 0;

		if (line.size() > 2) y = Float.parseFloat(line.get(2));
		if (line.size() > 3) z = Float.parseFloat(line.get(3));

		return new Point3D(x, y, z);
	}

	private Point3D getNormalVector(List<String> line) {
		return new Point3D(Float.parseFloat(line.get(1)), Float.parseFloat(line.get(2)), Float.parseFloat(line.get(3)));
	}

	private Polygon getPolygon(List<String> line, List<Point4D> geometricPoints, List<Point3D> texturePoints, List<Point3D> normalVectors) {
		/*if (line.size() - 1 != 3) {
			throw new IllegalStateException("Invalid polygon!");
		}*/

		return Polygon.builder()
				.firstPolygon(getPolygonPoint(line.get(1), geometricPoints, texturePoints, normalVectors))
				.secondPolygon(getPolygonPoint(line.get(2), geometricPoints, texturePoints, normalVectors))
				.thirdPolygon(getPolygonPoint(line.get(3), geometricPoints, texturePoints, normalVectors))
				.build();
	}

	private PolygonPoint getPolygonPoint(String point, List<Point4D> geometricPoints, List<Point3D> texturePoints, List<Point3D> normalVectors) {
		var polygonPoint = new PolygonPoint();

		var pointComponents = Arrays.stream(point.split(SLASH_REGEXP)).toList();

		var localSpacePointIndex = Integer.parseInt(pointComponents.get(0));
		polygonPoint.setLocalSpacePoint(localSpacePointIndex == -1
				? geometricPoints.get(geometricPoints.size() - 1)
				: geometricPoints.get(localSpacePointIndex - 1));


		// texturePointIndexStr will be empty if was "//" delimiter
		var texturePointIndexStr = pointComponents.size() > 1 ? pointComponents.get(1) : "";

		if (!texturePointIndexStr.isBlank()){
			polygonPoint.setTexturePoint(texturePoints.get(Integer.parseInt(texturePointIndexStr) - 1));
		}

		var normalVectorIndex = pointComponents.size() > 2 ? pointComponents.get(2) : "";

		if (!normalVectorIndex.isBlank()){
			polygonPoint.setNormalVector(normalVectors.get(Integer.parseInt(normalVectorIndex) - 1));
		}

		return polygonPoint;
	}

	private List<String> parseLine(String unhandledString) {
		return List.of(unhandledString.split(SPACE_REGEXP));
	}
}
