package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.model.Polygon;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public interface DrawerService {
	void draw(List<Polygon> polygons, GraphicsContext graphicsContext);
}
