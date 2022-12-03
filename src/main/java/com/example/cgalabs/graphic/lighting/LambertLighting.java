package com.example.cgalabs.graphic.lighting;

import com.example.cgalabs.model.Color;
import javafx.geometry.Point3D;

import static org.apache.commons.math3.util.FastMath.max;

public class LambertLighting implements Lighting {
	private final Point3D lightVector = new Point3D(0f, 3f, 1f);

	@Override
	public Color getPointColor(Point3D normalVector, Color color) {
		var dotProduct = normalVector.normalize().dotProduct(lightVector.normalize());
		var coefficient = max(dotProduct, 0f);

		var red = (int) (color.r() * coefficient);
		var green = (int) (color.g() * coefficient);
		var blue = (int) (color.b() * coefficient);

		return new Color(red, green, blue);
	}
}
