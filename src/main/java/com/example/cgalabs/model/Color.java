package com.example.cgalabs.model;

import javafx.geometry.Point3D;

public record Color(int r, int g, int b, int a) {
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public Point3D getVector() {
		return new Point3D(r, g, b);
	}
}
