package com.example.cgalabs.engine;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import static com.example.cgalabs.engine.EngineBuilder.*;
import static java.lang.Math.*;

public class CameraService {

	private static final Double MOUSE_SENSITIVITY = 0.2;

	static {
		CameraSphere.setCoordinates(0.1, 0.1, 3);
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
//				CameraSphere.tau > 1  || CameraSphere.fi > 1 ? (-1) *  coordinateZ : coordinateZ);
				coordinateZ);

		EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);
	}

	public void zoom(Double offset) {
		CameraSphere.setRadius(CameraSphere.getCoordinateX(), CameraSphere.getCoordinateY(),
				CameraSphere.getCoordinateZ() + signum(offset) * MOUSE_SENSITIVITY);

		INIT_CAMERA_POSITION = new Vector3D(
				CameraSphere.getCoordinateX(),
				CameraSphere.getCoordinateY(),
				CameraSphere.getCoordinateZ()
		);

		EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);
	}
}
