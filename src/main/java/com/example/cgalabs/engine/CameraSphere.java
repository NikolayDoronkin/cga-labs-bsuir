package com.example.cgalabs.engine;

import static org.apache.commons.math3.util.FastMath.*;

public class CameraSphere {
	public static double radius;
	public static double tau;
	public static double fi;
	public static double sinFi;
	public static double cosFi;

	public static double getCoordinateX() {
		return radius * cosFi * sin(tau);
	}

	public static double getCoordinateY() {
		return radius * sinFi * sin(tau);
	}

	public static double getCoordinateZ() {
		return radius * cos(tau);
	}

	public static void setRadius(double x, double y, double z) {
		var b = sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
		radius = max(b, 0.1);
	}

	public static void setCoordinates(double x, double y, double z) {
		radius = sqrt(pow(x, 2) + pow(y, 2) + pow(z, 2));
		sinFi = y / sqrt(x * x + y * y);
		cosFi = x / sqrt(x * x + y * y);

		fi = acos(cosFi);
		tau = acos(z / radius);
	}
}
