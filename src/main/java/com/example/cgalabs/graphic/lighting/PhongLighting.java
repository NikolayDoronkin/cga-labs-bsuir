package com.example.cgalabs.graphic.lighting;

import com.example.cgalabs.model.Color;
import javafx.geometry.Point3D;

import static org.apache.commons.math3.util.FastMath.*;

public class PhongLighting implements Lighting{

	private final Point3D lightVector = new Point3D(0f, 0f, 1f);
	private final Point3D ambientLightCoefficient = new Point3D(0.2f, 0.2f, 0.2f);
	private final Point3D diffuseLightCoefficient = new Point3D(1f, 1f, 1f);
	private final Point3D specularLightCoefficient = new Point3D(0.7f, 0.7f, 0.7f);
	private final Point3D ambientColor = new Point3D(255f, 255f, 255f);
	private final Point3D reflectionColor = new Point3D(255f, 255f, 255f);
	private double shininessCoefficient = 30.0;

	@Override
	public Color getPointColor(Point3D normalVector, Point3D viewVector, Color color) {
		var ambientLight = calcAmbientLight();
		var diffuseLight = calcDiffuseLight(normalVector, color);
		var specularLight = calcSpecularLight(normalVector, viewVector);

		var resultLightVector = ambientLight
				.add(diffuseLight)
				.add(specularLight);

		return new Color(
				(int) min(resultLightVector.getX(), 255f),
				(int) min(resultLightVector.getY(), 255f),
				(int) min(resultLightVector.getZ(), 255f)
		);
	}

	private Point3D calcAmbientLight() {
		return multiplyByCoordinates(ambientLightCoefficient, ambientColor);
	}

	private Point3D calcDiffuseLight(Point3D normalVector, Color color) {
		var dotProduct = normalVector.normalize().dotProduct(lightVector.normalize());
		var lightValue = max(dotProduct, 0f);

		return multiplyByCoordinates(diffuseLightCoefficient.multiply(lightValue), color.getVector());
	}

	private Point3D calcSpecularLight(Point3D normalVector, Point3D viewVector) {
		var reflectionVector = reflect(lightVector, normalVector);
		var dotProduct = reflectionVector.normalize().dotProduct(viewVector.normalize());
		var lightValue = pow(max(dotProduct, 0f), shininessCoefficient);

		return multiplyByCoordinates(specularLightCoefficient.multiply(lightValue), reflectionColor);
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
