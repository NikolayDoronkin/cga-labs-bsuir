package com.example.cgalabs.model;

import javafx.geometry.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pixel {
	private int x;
	private int y;
	private double z;
	private Color color;
	private double nw;
	private Point3D normalVector;
	private Point3D texel;
}
