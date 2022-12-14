package com.example.cgalabs.model;

import javafx.geometry.Point3D;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolygonPoint {
	private Point4D localSpacePoint;
	private Point4D viewSpacePointVector = Point4D.of(0.0, 0.0, 0.0, 0.0);
	private Point4D clipSpacePointVector = Point4D.of(0.0, 0.0, 0.0, 0.0);
	private Point4D screenSpacePointVector = Point4D.of(0.0, 0.0, 0.0, 0.0);
	private Point3D texturePoint = null;
	private Point3D normalVector = null;
}
