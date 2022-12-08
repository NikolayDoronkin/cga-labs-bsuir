package com.example.cgalabs.engine;

import com.example.cgalabs.model.*;
import javafx.geometry.Point3D;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EngineBuilder {

	static {
		WindowConstants.WINDOW_HEIGHT = 720.0;
		WindowConstants.WINDOW_WIDTH = 1280.0;
		WindowConstants.WINDOW_ASPECT = WindowConstants.WINDOW_WIDTH / WindowConstants.WINDOW_HEIGHT;
		DISTANCE_TO_NEAR = 10f;
		DISTANCE_TO_FAR = 1000f;
	}

	public static Vector3D UP_CAMERA_VECTOR = new Vector3D(0f, 1f, 0f);
	public static Vector3D INIT_MODEL_POSITION = new Vector3D(0f, 0f, 0f);
	public static Vector3D INIT_CAMERA_POSITION = new Vector3D(0.1f, 0.1f, 3f);

	public static RealMatrix toViewSpaceMatrix = buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);
	private static RealMatrix toClipSpaceMatrix = buildToClipSpaceMatrix(45.0);
	private static RealMatrix toScreenSpaceMatrix = buildToScreenSpaceMatrix();

	private static final Float DISTANCE_TO_NEAR;
	private static final Float DISTANCE_TO_FAR;

	public List<Polygon> fillAllSpaces(ObjectData data) {
		var polygons = new ArrayList<>(data.getPolygons());

		polygons.forEach(polygon -> {
			fillSpaces(polygon.getFirstPolygon());
			fillSpaces(polygon.getSecondPolygon());
			fillSpaces(polygon.getThirdPolygon());
		});

		return polygons;
	}

	public static RealMatrix buildToViewSpaceMatrix(Vector3D cameraPosition,
													Vector3D modelPosition,
													Vector3D upCameraVector) {
		var zAxisVector = normalizeToVector3D(cameraPosition.subtract(modelPosition));
		var xAxisVector = normalizeToVector3D(upCameraVector.crossProduct(zAxisVector));
		var yAxisVector = normalizeToVector3D(zAxisVector.crossProduct(xAxisVector));

		var firstColumn = Point4D.of(xAxisVector.getX(), yAxisVector.getX(), zAxisVector.getX(), 0.0);
		var secondColumn = Point4D.of(xAxisVector.getY(), yAxisVector.getY(), zAxisVector.getY(), 0.0);
		var thirdColumn = Point4D.of(xAxisVector.getZ(), yAxisVector.getZ(), zAxisVector.getZ(), 0.0);
		var fourthColumn = Point4D.of(
				-xAxisVector.dotProduct(cameraPosition),
				-yAxisVector.dotProduct(cameraPosition),
				-zAxisVector.dotProduct(cameraPosition),
				1.0);

		toViewSpaceMatrix = buildMatrix(firstColumn, secondColumn, thirdColumn, fourthColumn);

		return toViewSpaceMatrix;
	}

	public static Vector3D normalizeToVector3D(Vector3D subtract) {
		double s = subtract.getNorm();
		if (s == 0.0D) {
			return new Vector3D(Double.NaN, Double.NaN, Double.NaN);
		} else {
			return subtract.normalize();
		}
	}

	public static Point3D normalizeToPoint3D(Point3D subtract) {
		double s = FastMath.sqrt(
				subtract.getX() * subtract.getX() +
				subtract.getY() * subtract.getY() +
						subtract.getZ() * subtract.getZ());
		if (s == 0.0D) {
			return new Point3D(Double.NaN, Double.NaN, Double.NaN);
		} else {
			return subtract.normalize();
		}
	}

	public static RealMatrix buildToClipSpaceMatrix(Double fov) {
		var halfOfFovTangent = Math.tan(Math.toRadians(fov / 2));

		var firstColumnComponent = 1 / (WindowConstants.WINDOW_ASPECT * halfOfFovTangent);
		var secondColumnComponent = 1 / halfOfFovTangent;
		var thirdColumnComponent = DISTANCE_TO_FAR / (DISTANCE_TO_NEAR - DISTANCE_TO_FAR);
		var fourthColumnComponent = (DISTANCE_TO_NEAR * DISTANCE_TO_FAR) / (DISTANCE_TO_NEAR - DISTANCE_TO_FAR);

		var firstColumn = Point4D.of(firstColumnComponent, 0.0, 0.0, 0.0);
		var secondColumn = Point4D.of(0.0, secondColumnComponent, 0.0, 0.0);
		var thirdColumn = Point4D.of(0.0, 0.0, (double) thirdColumnComponent, -1.0);
		var fourthColumn = Point4D.of(0.0, 0.0, (double) fourthColumnComponent, 0.0);

		toClipSpaceMatrix = buildMatrix(firstColumn, secondColumn, thirdColumn, fourthColumn);

		return toClipSpaceMatrix;
	}

	private static RealMatrix buildToScreenSpaceMatrix() {
		var firstColumn = Point4D.of((WindowConstants.WINDOW_WIDTH / 2), 0.0, 0.0, 0.0);
		var secondColumn = Point4D.of(0.0, -(WindowConstants.WINDOW_HEIGHT / 2), 0.0, 0.0);
		var thirdColumn = Point4D.of(0.0, 0.0, 1.0, 0.0);
		var fourthColumn = Point4D.of((WindowConstants.WINDOW_WIDTH / 2), (WindowConstants.WINDOW_HEIGHT / 2), 0.0, 1.0);

		return buildMatrix(firstColumn, secondColumn, thirdColumn, fourthColumn);
	}

	private void fillSpaces(PolygonPoint polygonPoint) {
		calcViewSpacePointVector(polygonPoint);
		calcClipSpacePointVector(polygonPoint);
		calcScreenSpacePointVector(polygonPoint);
	}

	private void calcViewSpacePointVector(PolygonPoint polygonPoint) {
		polygonPoint.setViewSpacePointVector(
				buildPoint4DTest(
						toViewSpaceMatrix.multiply(
								buildMatrixTest(polygonPoint.getLocalSpacePoint()))));
	}

	private void calcClipSpacePointVector(PolygonPoint polygonPoint) {
		var column = toClipSpaceMatrix.multiply(buildMatrixTest(polygonPoint.getViewSpacePointVector())).getColumn(0);
		var calculatedVector = Point4D.of(column[0], column[1], column[2], column[3]);
		var dividedVector = calculatedVector.divide(calculatedVector.getW());

		polygonPoint.setClipSpacePointVector(dividedVector);
	}

	private void calcScreenSpacePointVector(PolygonPoint polygonPoint) {
		polygonPoint.setScreenSpacePointVector(
				buildPoint4DTest(
						toScreenSpaceMatrix.multiply(
								buildMatrixTest(polygonPoint.getClipSpacePointVector()))));
	}

	private static RealMatrix buildMatrix(Point4D firstColumn, Point4D secondColumn, Point4D thirdColumn, Point4D fourthColumn) {
		return MatrixUtils.createRealMatrix(new double[][]{
				{firstColumn.getX(), secondColumn.getX(), thirdColumn.getX(), fourthColumn.getX()},
				{firstColumn.getY(), secondColumn.getY(), thirdColumn.getY(), fourthColumn.getY()},
				{firstColumn.getZ(), secondColumn.getZ(), thirdColumn.getZ(), fourthColumn.getZ()},
				{firstColumn.getW(), secondColumn.getW(), thirdColumn.getW(), fourthColumn.getW()}});
	}

	public static RealMatrix buildMatrixTest(Point4D point4D) {
		return MatrixUtils.createRealMatrix(new double[][]
				{
						{point4D.getX()},
						{point4D.getY()},
						{point4D.getZ()},
						{point4D.getW() == null ? 1 : point4D.getW()}
				});
	}

	private Point4D buildPoint4DTest(RealMatrix matrix) {
		var column = matrix.getColumn(0);

		return Point4D.of(column[0], column[1], column[2], column[3]);
	}

	public static Point3D buildPoint3DTest(RealMatrix matrix) {
		var column = matrix.getColumn(0);

		return new Point3D(column[0], column[1], column[2]);
	}
}
