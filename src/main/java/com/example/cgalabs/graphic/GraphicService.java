package com.example.cgalabs.graphic;

import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Pixel;
import javafx.geometry.Point3D;

import java.util.List;

public interface GraphicService {
	void drawLine(Point3D startPoint, Point3D endPoint, Color color, List<Pixel> sidePixels, int[] pixels);

	default void drawPoint(int[] arr, int x, int y, double z, Color color, List<Pixel> sidePixels) {
		var pixelIndex = (x % 1280) + (y * 1280);

		if (pixelIndex < 0 || pixelIndex >= arr.length) {
			return;
		}

		arr[(x % 1280) + (y * 1280)] = argbInt(color);
	}

	private int argbInt(Color color) {
		return color.a() << 24 | (color.r() << 16) | (color.g() << 8) | color.b();
	}
}
