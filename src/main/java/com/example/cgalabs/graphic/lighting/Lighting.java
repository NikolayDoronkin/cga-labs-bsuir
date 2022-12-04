package com.example.cgalabs.graphic.lighting;

import com.example.cgalabs.model.Color;
import javafx.geometry.Point3D;

public interface Lighting {
	Color getPointColor(Point3D normalVector, Point3D viewVector, Color color);
}
