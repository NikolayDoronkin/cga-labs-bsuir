package com.example.cgalabs.graphic.lighting;

import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Point4D;
import com.example.cgalabs.model.Texture;
import javafx.geometry.Point3D;

import java.util.Optional;

import static com.example.cgalabs.engine.EngineBuilder.*;
import static org.apache.commons.math3.util.FastMath.*;

public class PhongLighting implements Lighting {

	private final Texture diffuseTexture;
	private final Texture normalsTexture;
	private final Texture specularTexture;

	public PhongLighting(Texture diffuseTexture, Texture normalsTexture, Texture specularTexture) {
		this.diffuseTexture = diffuseTexture;
		this.normalsTexture = normalsTexture;
		this.specularTexture = specularTexture;
	}

	private final Point3D lightVector = new Point3D(0f, 0f, 1f);
	private final Point3D ambientLightCoefficient = new Point3D(0.2f, 0.2f, 0.2f);
	private final Point3D diffuseLightCoefficient = new Point3D(1f, 1f, 1f);
	private final Point3D specularLightCoefficient = new Point3D(0.7f, 0.7f, 0.7f);
	private final Point3D ambientColor = new Point3D(255f, 255f, 255f);
	private final Point3D reflectionColor = new Point3D(255f, 255f, 255f);
	private double shininessCoefficient = 30.0;

	@Override
	public Color getPointColor(Point3D normalVector, Point3D viewVector, Color color) {
		var ambientLight = calcAmbientLight(ambientColor);
		var diffuseLight = calcDiffuseLight(normalVector, color.getVector());
		var specularLight = calcSpecularLight(normalVector, viewVector, reflectionColor);

		var resultLightVector = ambientLight.add(diffuseLight).add(specularLight);

		return new Color(
				(int) min(resultLightVector.getX(), 255),
				(int) min(resultLightVector.getY(), 255),
				(int) min(resultLightVector.getZ(), 255)
		);
	}

	public Color getTexturedPointColor(Point3D texel, Point3D viewVector) {
		var diffuse = Optional.ofNullable(diffuseTexture).orElseThrow(RuntimeException::new);
		var normals = Optional.ofNullable(normalsTexture).orElseThrow(RuntimeException::new);
		var specular = Optional.ofNullable(specularTexture).orElseThrow(RuntimeException::new);

		var x = (texel.getX() * diffuse.getWidth()) % diffuse.getWidth();
		var y = ((1 - texel.getY()) * diffuse.getHeight()) % diffuse.getHeight();

		if (x < 0 || y < 0) return new Color(0, 0, 0);

		var normalVector = normals.get((int) (x), (int) (y));
		normalVector = normalVector.subtract(new Point3D(127.5f, 127.5f, 127.5f));

		var normalize = normalizeToPoint3D(normalVector);
		normalVector = buildPoint3DTest(toViewSpaceMatrix
				.multiply(buildMatrixTest(
						Point4D.of(
								normalize.getX(),
								normalize.getY(),
								normalize.getZ(), 0D))))
				.normalize();

		var colorFromDiffuseTexture = diffuse.get((int) (x), (int) (y));
		var reflectionColor = specular.get((int) (x), (int) (y));

		var ambientLight = calcAmbientLight(colorFromDiffuseTexture);
		var diffuseLight = calcDiffuseLight(normalVector, colorFromDiffuseTexture);
		var specularLight = calcSpecularLight(normalVector, viewVector, reflectionColor);

		var resultLightVector = ambientLight.add(diffuseLight).add(specularLight);

		return new Color(
				(int) min(resultLightVector.getX(), 255f),
				(int) min(resultLightVector.getY(), 255f),
				(int) min(resultLightVector.getZ(), 255f));
	}

	private Point3D calcAmbientLight(Point3D color) {
		return multiplyByCoordinates(ambientLightCoefficient, color);
	}

	private Point3D calcDiffuseLight(Point3D normalVector, Point3D color) {
		var dotProduct = normalVector.normalize().dotProduct(lightVector.normalize());
		var lightValue = max(dotProduct, 0f);

		return multiplyByCoordinates(diffuseLightCoefficient.multiply(lightValue), color);
	}

	private Point3D calcSpecularLight(Point3D normalVector, Point3D viewVector, Point3D color) {
		var reflectionVector = reflect(lightVector, normalVector);
		var dotProduct = reflectionVector.normalize().dotProduct(viewVector.normalize());
		var lightValue = pow(max(dotProduct, 0f), shininessCoefficient);

		return multiplyByCoordinates(specularLightCoefficient.multiply(lightValue), color);
	}

	private Point3D multiplyByCoordinates(Point3D first, Point3D second) {
		return new Point3D(
				first.getX() * second.getX(),
				first.getY() * second.getY(),
				first.getZ() * second.getZ());
	}

	private Point3D reflect(Point3D first, Point3D second) {
		var $this$times$iv = 2.0F * (second.getX() * first.getX() + second.getY() * first.getY() + second.getZ() * first.getZ());

		var v$iv = new Point3D(
				$this$times$iv * second.getX(),
				$this$times$iv * second.getY(),
				$this$times$iv * second.getZ());

		return new Point3D(
				first.getX() - v$iv.getX(),
				first.getY() - v$iv.getY(),
				first.getZ() - v$iv.getZ());
	}
}
