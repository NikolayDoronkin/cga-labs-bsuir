package com.example.cgalabs.graphic.lighting;

import com.example.cgalabs.model.Color;
import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface Lighting {
	Color getPointColor(Point3D normalVector, Color color);
}
