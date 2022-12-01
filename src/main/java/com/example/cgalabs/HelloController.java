package com.example.cgalabs;

import com.example.cgalabs.engine.CameraService;
import com.example.cgalabs.engine.EngineBuilder;
import com.example.cgalabs.graphic.DrawerService;
import com.example.cgalabs.model.ObjectData;
import com.example.cgalabs.parser.ParserService;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.io.IOException;

public class HelloController {

	@FXML
	private Canvas canvas;
	private GraphicsContext graphicsContext;

	private static ObjectData objectData;

	private static final ParserService parserService = new ParserService();
	private static final EngineBuilder engineBuilder = new EngineBuilder();
	private static final CameraService cameraService = new CameraService();
	private static final DrawerService drawerService = new DrawerService();

	private Double lastX = 0.0;
	private Double lastY = 0.0;

	private static final String PATH1 = "C:\\Users\\nicol\\OneDrive\\AKG\\cga-labs\\skull.obj";
	private static final String PATH2 = "C:\\Users\\nicol\\OneDrive\\AKG\\CGA\\src\\main\\resources\\com\\example\\cga\\models\\Skull_OBJ.OBJ";
	private static final String PATH3 = "C:\\Users\\nicol\\OneDrive\\AKG\\1\\src\\main\\resources\\african_head.obj";
	private static final String PATH4 = "C:\\Users\\nicol\\OneDrive\\AKG\\1\\src\\main\\resources\\moon.obj";
	private static final String PATH5 = "C:\\Users\\nicol\\OneDrive\\AKG\\1\\src\\main\\resources\\cube.obj";
	private static final String PATH6 = "C:\\Users\\nicol\\OneDrive\\AKG\\1\\src\\main\\resources\\uploads_files_3862208_Cube.obj";
	private static final String PATH7 = "C:\\Users\\nicol\\OneDrive\\AKG\\1\\src\\main\\resources\\test.obj";

	static {
		try {
			objectData = parserService.readFromFile(PATH2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handle() {
		graphicsContext = canvas.getGraphicsContext2D();
		graphicsContext.clearRect(0.0, 0.0, 1280, 720);
		var polygons = engineBuilder.fillAllSpaces(objectData);
		drawerService.draw(polygons, graphicsContext);
	}

	@FXML
	void itPressed(MouseEvent event) {
		lastX = event.getX();
		lastY = event.getY();
		event.setDragDetect(true);
		handle();
	}

	@FXML
	void itDragged(MouseEvent event) {
		var xOffset = event.getX() - lastX;
		var yOffset = event.getY() - lastY;

		lastX = event.getX();
		lastY = event.getY();

		cameraService.move(xOffset, yOffset);

		handle();
	}

	@FXML
	void itScrolled(ScrollEvent event) {
		cameraService.zoom(event.getDeltaY());

		handle();
	}
}