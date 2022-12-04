package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.model.Polygon;
import javafx.geometry.Point3D;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public interface DrawerService {
	void draw(List<Polygon> polygons, Point3D viewVector, GraphicsContext graphicsContext);
}
