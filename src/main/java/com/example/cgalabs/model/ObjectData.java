package com.example.cgalabs.model;

import javafx.geometry.Point3D;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ObjectData {

	@Builder.Default
	private List<Point4D> geometricPoints = new ArrayList<>();

	@Builder.Default
	private List<Point3D> texturePoints = new ArrayList<>();

	@Builder.Default
	private List<Point3D> normalVectors = new ArrayList<>();

	@Builder.Default
	private List<Polygon> polygons = new ArrayList<>();
}
