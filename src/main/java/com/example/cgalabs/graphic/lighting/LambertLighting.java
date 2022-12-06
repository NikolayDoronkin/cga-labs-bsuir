package com.example.cgalabs.graphic.lighting;

import com.example.cgalabs.model.Color;
import javafx.geometry.Point3D;

import static org.apache.commons.math3.util.FastMath.max;

public class LambertLighting implements Lighting {

	@Override
	public Color getPointColor(Point3D normalVector, Point3D viewVector, Color color) {
		var lightVector = viewVector.multiply(-1);
		var dotProduct = normalVector.normalize().dotProduct(lightVector.normalize());
		var coefficient = max(dotProduct, 0f);

		var red = (int) (color.r() * coefficient);
		var green = (int) (color.g() * coefficient);
		var blue = (int) (color.b() * coefficient);

		return new Color(red, green, blue);
	}

	@Override
	public Color getTexturedPointColor(Point3D texel, Point3D viewVector) {
		throw new RuntimeException();
	}
}
