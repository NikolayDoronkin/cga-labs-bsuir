package com.example.cgalabs.model;

import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Texture {

	private static final int RED_PIXEL_PART_SHIFT = 16;
	private static final int GREEN_PIXEL_PART_SHIFT = 8;
	private static final int AND_PIXEL_PART_MULTIPLIER = 0xFF;

	private Image image;

	private double width;

	private double height;

	public Texture(Image image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public Point3D get(int x, int y) {
		var pixel = image.getPixelReader().getArgb(x, y);

		var r = pixel >> RED_PIXEL_PART_SHIFT & AND_PIXEL_PART_MULTIPLIER;
		var g = pixel >> GREEN_PIXEL_PART_SHIFT & AND_PIXEL_PART_MULTIPLIER;
		var b = pixel & AND_PIXEL_PART_MULTIPLIER;

		return new Point3D(r, g, b);
	}
}
