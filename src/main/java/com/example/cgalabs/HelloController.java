package com.example.cgalabs;

import com.example.cgalabs.engine.CameraService;
import com.example.cgalabs.engine.EngineBuilder;
import com.example.cgalabs.graphic.drawer.DrawerService;
import com.example.cgalabs.graphic.drawer.PhongShadingDrawerService;
import com.example.cgalabs.graphic.drawer.PlaneShadingDrawerService;
import com.example.cgalabs.model.ObjectData;
import com.example.cgalabs.model.Texture;
import com.example.cgalabs.parser.ParserService;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.example.cgalabs.engine.CameraService.*;

public class HelloController {

	@FXML
	private Canvas canvas;
	private GraphicsContext graphicsContext;

	private Double lastX = 0.0;
	private Double lastY = 0.0;

	public static final boolean PHONG_ENABLED = true;
	public static final boolean TEXTURES_ENABLED = true;

	private static ObjectData objectData;

	private static final String PATH1_OBJ = "src/main/resources/head/model.obj";
	private static final String PATH2_OBJ = "src/main/resources/moon.obj";
	private static final String PATH3_OBJ = "src/main/resources/cube.obj";
	private static final String PATH4_OBJ = "src/main/resources/uploads_files_3862208_Cube.obj";
	private static final String PATH5_OBJ = "src/main/resources/test.obj";
	private static final String PATH6_OBJ = "src/main/resources/head.obj";

	private static final String NORMALS_TEXTURE_FILE_PATH = "/head/normal.png";
	private static final String DIFFUSE_TEXTURE_FILE_PATH = "/head/diffuse.png";
	private static final String SPECULAR_TEXTURE_FILE_PATH = "/head/specular.png";

	private static final DrawerService drawerService;
	private static final ParserService parserService = new ParserService();
	private static final EngineBuilder engineBuilder = new EngineBuilder();
	private static final CameraService cameraService = new CameraService();

	static {
		try {
			objectData = parserService.readFromFile(PATH1_OBJ);
		} catch (IOException e) {
			e.printStackTrace();
		}
		drawerService = getDrawerService();
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

	private static DrawerService getDrawerService() {
		if (PHONG_ENABLED) {
			if (TEXTURES_ENABLED) {
				var textures = readTextures(
						DIFFUSE_TEXTURE_FILE_PATH, NORMALS_TEXTURE_FILE_PATH, SPECULAR_TEXTURE_FILE_PATH);

				return new PhongShadingDrawerService(textures.getLeft(), textures.getMiddle(), textures.getRight());
			} else {
				return new PhongShadingDrawerService();
			}

		} else {
			return new PlaneShadingDrawerService();
		}
	}

	private static Triple<Texture, Texture, Texture> readTextures(String diffuseTextureFilePath,
																  String normalsTextureFilePath,
																  String specularTextureFilePath) {
		var diffuseTexture = readTexture(diffuseTextureFilePath);
		var normalsTexture = readTexture(normalsTextureFilePath);
		var specularTexture = readTexture(specularTextureFilePath);

		return new MutableTriple<>(diffuseTexture, normalsTexture, specularTexture);
	}

	private static Texture readTexture(String filePath) {
		var url = HelloController.class.getResource(filePath);
		assert url != null;
		return new Texture(new Image(url.toString()));
	}
}