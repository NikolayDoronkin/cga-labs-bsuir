package com.example.cgalabs.engine;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;

import static com.example.cgalabs.engine.EngineBuilder.*;
import static java.lang.Math.*;

public class CameraService {

	static {
		CameraSphere.setCoordinates(0.1, 0.1, 30);
	}

	public RealMatrix move(Double xOffset, Double yOffset) {
		var previousRadius = CameraSphere.radius;
		Double mouseSensitivity = 0.25;
		var x = CameraSphere.getCoordinateX() - xOffset * mouseSensitivity;
		var y = CameraSphere.getCoordinateY() + yOffset * mouseSensitivity;

		CameraSphere.setCoordinates(x, y, CameraSphere.getCoordinateZ());

		CameraSphere.radius = previousRadius;
		double coordinateZ = CameraSphere.getCoordinateZ();

		INIT_CAMERA_POSITION = new Vector3D(
				CameraSphere.getCoordinateX(),
				CameraSphere.getCoordinateY(),
				CameraSphere.tau > 1  || CameraSphere.fi > 1 ? (-1) *  coordinateZ : coordinateZ);

		return EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);
	}

	public RealMatrix zoom(Double offset) {
		CameraSphere.setRadius(CameraSphere.getCoordinateX(), CameraSphere.getCoordinateY(),
				CameraSphere.getCoordinateZ() + signum(offset));

		INIT_CAMERA_POSITION = new Vector3D(
				CameraSphere.getCoordinateX(),
				CameraSphere.getCoordinateY(),
				CameraSphere.getCoordinateZ()
		);

		return EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);
	}
}
