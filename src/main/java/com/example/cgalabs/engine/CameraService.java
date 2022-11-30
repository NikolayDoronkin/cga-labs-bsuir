package com.example.cgalabs.engine;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;

import static com.example.cgalabs.engine.EngineBuilder.*;

public class CameraService {
	private Vector3D position = new Vector3D(0.0, 0.0, 1.0);
	private Vector3D focus = new Vector3D(0.0, 0.0, 0.0);
	private Vector3D worldUp = new Vector3D(0.0, 1.0, 1000.0);

	private Double yaw = -90.0;
	private Double pitch = 0.0;

	private final Double mouseSensitivity = 0.5;
	private final Double zoomSensitivity = 0.1;

	private Vector3D front;
	private Vector3D up;
	private Vector3D right;

	private static final Double MAX_ZOOM = 1000.0;

	{
		updateCameraVectors(false);
	}

	public RealMatrix move(Double xOffset, Double yOffset) {
		return move(xOffset, yOffset, true);
	}

	public RealMatrix move(Double xOffset, Double yOffset, boolean constrainPitch) {
		yaw += xOffset * mouseSensitivity;
		pitch -= yOffset * mouseSensitivity;

		if (constrainPitch) {
			if (pitch > 89.9) pitch = 89.9;
			if (pitch < -89.9) pitch = -89.9;
		}
		return updateCameraVectors(true);
	}

	public void zoom(Double offset) {
//		zoom -= offset * zoomSensitivity;

//		if (zoom < 1f) zoom = 1.0;
//		if (zoom > MAX_ZOOM) zoom = MAX_ZOOM;

		EngineBuilder.INIT_CAMERA_POSITION = new Vector3D(
				INIT_CAMERA_POSITION.getX(),
				INIT_CAMERA_POSITION.getY(),
				INIT_CAMERA_POSITION.getZ() + Math.signum(offset)
		);
		EngineBuilder.buildToViewSpaceMatrix(INIT_CAMERA_POSITION, INIT_MODEL_POSITION, UP_CAMERA_VECTOR);

//		return EngineBuilder.buildToClipSpaceMatrix(zoom);
//		return updateCameraVectors(true);
	}

	private RealMatrix updateCameraVectors(boolean flag) {
		front = new Vector3D(
				Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
				Math.sin(Math.toRadians(pitch)),
				Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
		).normalize();

		position = focus.subtract(front).scalarMultiply(position.getNorm());
		right = front.crossProduct(worldUp).normalize();
		up = right.crossProduct(front).normalize();

		if (flag) {
			return EngineBuilder.buildToViewSpaceMatrix(position, focus, up);
		}
		else return EngineBuilder.toViewSpaceMatrix;

//		return EngineBuilder.buildToViewSpaceMatrix(position, focus, up);
	}
}
