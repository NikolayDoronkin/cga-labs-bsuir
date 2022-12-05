package com.example.cgalabs;

import com.example.cgalabs.engine.CameraService;
import com.example.cgalabs.engine.EngineBuilder;
import com.example.cgalabs.graphic.drawer.DrawerService;
import com.example.cgalabs.graphic.drawer.PhongShadingDrawer;
import com.example.cgalabs.graphic.drawer.PlaneShadingDrawerService;
import com.example.cgalabs.model.ObjectData;
import com.example.cgalabs.parser.ParserService;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.io.IOException;

import static com.example.cgalabs.engine.CameraService.*;

public class HelloController {

	@FXML
	private Canvas canvas;
	private GraphicsContext graphicsContext;

	private static ObjectData objectData;

	private static final ParserService parserService = new ParserService();
	private static final EngineBuilder engineBuilder = new EngineBuilder();
	private static final CameraService cameraService = new CameraService();
//	private static final DrawerService drawerService = new PlaneShadingDrawerService();
	private static final DrawerService drawerService = new PhongShadingDrawer();

	private Double lastX = 0.0;
	private Double lastY = 0.0;

	private static final String PATH1 = "src/main/resources/african_head.obj";
	private static final String PATH2 = "src/main/resources/moon.obj";
	private static final String PATH3 = "src/main/resources/cube.obj";
	private static final String PATH4 = "src/main/resources/uploads_files_3862208_Cube.obj";
	private static final String PATH5 = "src/main/resources/test.obj";
	private static final String PATH6 = "src/main/resources/head.obj";

	static {
		try {
			objectData = parserService.readFromFile(PATH1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handle() {
		graphicsContext = canvas.getGraphicsContext2D();
		graphicsContext.clearRect(0.0, 0.0, 1280, 720);
		var polygons = engineBuilder.fillAllSpaces(objectData);
		drawerService.draw(polygons, CAMERA_POSITION.multiply(-1),  graphicsContext);
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