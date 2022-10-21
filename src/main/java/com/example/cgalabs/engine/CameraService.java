package com.example.cgalabs.engine;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealMatrix;

public class CameraService {
	private Vector3D position = new Vector3D(0.0, 0.0, 1.0);
	private Vector3D focus = new Vector3D(0.0, 0.0, 0.0);
	private Vector3D worldUp = new Vector3D(0.0, 1.0, 0.0);

	private Double yaw = -90.0;
	private Double pitch = 0.0;
	private Double zoom = 45.0;

	private final Double mouseSensitivity = 0.5;
	private final Double zoomSensitivity = 0.1;

	private Vector3D front;
	private Vector3D up;
	private Vector3D right;

	private static final Double MAX_ZOOM = 180.0;

	{
		updateCameraVectors();
	}

	public RealMatrix move(Double xOffset, Double yOffset) {
		return move(xOffset, yOffset, true);
	}

	public RealMatrix move(Double xOffset, Double yOffset, boolean constrainPitch) {
		yaw += xOffset * mouseSensitivity;
		pitch -= yOffset * mouseSensitivity;

		if (constrainPitch) {
			if (pitch > 89.0) pitch = 89.0;
			if (pitch < -89.0) pitch = -89.0;
		}
		return updateCameraVectors();
	}

	public RealMatrix zoom(Double offset) {
		zoom -= offset * zoomSensitivity;

		if (zoom < 1f) zoom = 1.0;
		if (zoom > MAX_ZOOM) zoom = MAX_ZOOM;

		return EngineBuilder.buildToClipSpaceMatrix(zoom);
	}

	private RealMatrix updateCameraVectors() {
		front = new Vector3D(
				Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
				Math.sin(Math.toRadians(pitch)),
				Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
		).normalize();

		position = focus.subtract(front).scalarMultiply(position.getNorm());
		right = front.crossProduct(worldUp).normalize();
		up = right.crossProduct(front).normalize();

		return EngineBuilder.buildToViewSpaceMatrix(position, focus, up);
	}
}
