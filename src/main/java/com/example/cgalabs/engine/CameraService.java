package com.example.cgalabs.engine;

import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import static com.example.cgalabs.engine.EngineBuilder.*;
import static java.lang.Math.signum;

public class CameraService {

	public static Point3D CAMERA_POSITION;

	private static final Double MOUSE_SENSITIVITY = 0.2;

	static {
		CameraSphere.setCoordinates(0.1, 0.1, 3);
		CAMERA_POSITION = buildPosition(INIT_CAMERA_POSITION);
	}

	public void move(Double xOffset, Double yOffset) {
		var previousRadius = CameraSphere.radius;
		var x = CameraSphere.getCoordinateX() - xOffset * MOUSE_SENSITIVITY;
		var y = CameraSphere.getCoordinateY() + yOffset * MOUSE_SENSITIVITY;

		CameraSphere.setCoordinates(x, y, CameraSphere.getCoordinateZ());

		CameraSphere.radius = previousRadius;
		var coordinateZ = CameraSphere.getCoordinateZ();

		INIT_CAMERA_POSITION = new Vector3D(
				CameraSphere.getCoordinateX(),
				CameraSphere.getCoordinateY(),
				coordinateZ);

		EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);

		CAMERA_POSITION = buildPosition(INIT_CAMERA_POSITION);
	}

	public void zoom(Double offset) {
		CameraSphere.setRadius(CameraSphere.getCoordinateX(), CameraSphere.getCoordinateY(),
				CameraSphere.getCoordinateZ() - signum(offset) * MOUSE_SENSITIVITY);

		INIT_CAMERA_POSITION = new Vector3D(
				CameraSphere.getCoordinateX(),
				CameraSphere.getCoordinateY(),
				CameraSphere.getCoordinateZ()
		);

		EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);

		CAMERA_POSITION = buildPosition(INIT_CAMERA_POSITION);
	}

	private static Point3D buildPosition(Vector3D source) {
		return new Point3D(source.getX(), source.getY(), source.getZ());
	}
}
