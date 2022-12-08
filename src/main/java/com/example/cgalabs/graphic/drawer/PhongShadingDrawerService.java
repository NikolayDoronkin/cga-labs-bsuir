package com.example.cgalabs.graphic.drawer;

import com.example.cgalabs.HelloController;
import com.example.cgalabs.model.Color;
import com.example.cgalabs.model.Pixel;
import com.example.cgalabs.model.PolygonPoint;
import com.example.cgalabs.model.Texture;
import javafx.geometry.Point3D;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.math3.util.FastMath.abs;

@NoArgsConstructor
public class PhongShadingDrawerService extends PlaneShadingDrawerService {

	public PhongShadingDrawerService(Texture diffuseTexture, Texture normalsTexture, Texture specularTexture) {
		super(diffuseTexture, normalsTexture, specularTexture);
	}

	@Override
	protected void drawSide(PolygonPoint firstSidePoint, PolygonPoint secondSidePoint, Color color, int[] pixels,
							List<Pixel> sidesPixels, Point3D viewVector) {
		var firstPointPixel = getSidePixel(firstSidePoint, color);
		var secondPointPixel = getSidePixel(secondSidePoint, color);

		if (Objects.isNull(firstPointPixel) || Objects.isNull(secondPointPixel)) return;

		drawLine(firstPointPixel, secondPointPixel, color, sidesPixels, pixels, viewVector);
	}

	private Pixel getSidePixel(PolygonPoint sidePoint, Color color) {
		var pointVector = sidePoint.getScreenSpacePointVector();
		var normalVector = sidePoint.getNormalVector();
		var texel = sidePoint.getTexturePoint();

		if (Objects.isNull(normalVector) || Objects.isNull(texel)) return null;

		if (!HelloController.TEXTURES_ENABLED) {
			pointVector.setW(1D);
		}

		return new Pixel(
				pointVector.getX().intValue(),
				pointVector.getY().intValue(),
				pointVector.getZ(),
				color,
				1 / pointVector.getW(),
				normalVector.multiply(1 / pointVector.getW()),
				texel.multiply(1 / pointVector.getW()));
	}

	@Override
	public void drawLine(Pixel startPoint, Pixel endPoint, Color color, List<Pixel> sidePixels, int[] pixels, Point3D viewVector) {
		var x1 = startPoint.getX();
		var y1 = startPoint.getY();
		var z1 = startPoint.getZ();

		var x2 = endPoint.getX();
		var y2 = endPoint.getY();
		var z2 = endPoint.getZ();

		var dx = abs(x2 - x1);
		var dy = abs(y2 - y1);
		var dz = abs(z2 - z1);

		var xs = x2 > x1 ? 1 : -1;
		var ys = y2 > y1 ? 1 : -1;
		var zs = z2 > z1 ? 1 : -1;

		var currNormal = startPoint.getNormalVector();
		var currTexel = startPoint.getTexel();
		var currNW = startPoint.getNw();
		var sameX = abs(startPoint.getX() - endPoint.getX()) < abs(endPoint.getY() - startPoint.getY());

		var deltaNormal = sameX
				? endPoint.getNormalVector().subtract(startPoint.getNormalVector()).multiply((double) 1 / dy)
				: endPoint.getNormalVector().subtract(startPoint.getNormalVector()).multiply((double) 1 / dx);

		var deltaTexel = sameX
				? endPoint.getTexel().subtract(startPoint.getTexel()).multiply((double) 1 / dy)
				: endPoint.getTexel().subtract(startPoint.getTexel()).multiply((double) 1 / dx);

		var deltaNW = sameX
				? (endPoint.getNw() - startPoint.getNw()) / dy
				: (endPoint.getNw() - startPoint.getNw()) / dx;

		if (dx >= dy && dx >= dz) {
			var p1 = 2 * dy - dx;
			var p2 = 2 * dz - dx;

			while (x1 != x2) {
				x1 += xs;

				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dx;
				}

				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dx;
				}

				p1 += 2 * dy;
				p2 += 2 * dz;

				if (!sameX) {
					currNormal = currNormal.add(deltaNormal);
					currTexel = currTexel.add(deltaTexel);
					currNW += deltaNW;
				}

				drawPoint(pixels, x1, y1, z1, color, sidePixels, viewVector, currNormal, currTexel, currNW);
			}
		} else if (dy >= dx && dy >= dz) {
			var p1 = 2 * dx - dy;
			var p2 = 2 * dz - dy;

			while (y1 != y2) {
				y1 += ys;

				if (p1 >= 0) {
					x1 += xs;
					p1 -= 2 * dy;
				}

				if (p2 >= 0) {
					z1 += zs;
					p2 -= 2 * dy;
				}

				p1 += 2 * dx;
				p2 += 2 * dz;

				if (sameX) {
					currNormal = currNormal.add(deltaNormal);
					currTexel = currTexel.add(deltaTexel);
					currNW += deltaNW;
				}

				drawPoint(pixels, x1, y1, z1, color, sidePixels, viewVector, currNormal, currTexel, currNW);
			}
		} else {
			var p1 = 2 * dy - dz;
			var p2 = 2 * dx - dz;

			while (z1 < z2) {
				z1 += zs;

				if (p1 >= 0) {
					y1 += ys;
					p1 -= 2 * dz;
				}

				if (p2 >= 0) {
					x1 += xs;
					p2 -= 2 * dz;
				}

				p1 += 2 * dy;
				p2 += 2 * dx;

				drawPoint(pixels, x1, y1, z1, color, sidePixels, viewVector, currNormal, currTexel, currNW);
			}
		}
	}

	@Override
	protected void drawInnerPolygonPixels(int[] pixels, List<Pixel> sidePixels, Color color, Point3D viewVector) {
		var minAndMaxY = super.getMinMaxY(sidePixels);
		var minY = minAndMaxY.getKey();
		var maxY = minAndMaxY.getValue();

		if (minY == null || maxY == null) return;

		for (int y = minY; y < maxY; y++) {
			var startAndEndPixel = getStartEndPixelsByY(sidePixels, y);
			var startPixel = startAndEndPixel.getKey();
			var endPixel = startAndEndPixel.getValue();

			if (startPixel == null || endPixel == null) continue;

			var z = startPixel.getZ();
			var dz = (endPixel.getZ() - startPixel.getZ()) / abs(endPixel.getX() - startPixel.getX());

			var deltaNormal = endPixel.getNormalVector().subtract(startPixel.getNormalVector())
					.multiply((double) 1 / abs(endPixel.getX() - startPixel.getX()));
			var currNormal = startPixel.getNormalVector();

			var deltaTexel = (endPixel.getTexel().subtract(startPixel.getTexel()))
					.multiply((double) 1 / abs(endPixel.getX() - startPixel.getX()));
			var currTexel = startPixel.getTexel();

			var deltaNW = (endPixel.getNw() - startPixel.getNw()) / (double) abs(endPixel.getX() - startPixel.getX());
			var currNW = startPixel.getNw();

			for (int x = startPixel.getX(); x < endPixel.getX(); x++) {
				currNormal = currNormal.add(deltaNormal);
				currTexel = currTexel.add(deltaTexel);
				currNW += deltaNW;

				drawPoint(pixels, x, y, z, color, sidePixels, viewVector, currNormal, currTexel, currNW);

				z += dz;
			}
		}
	}

	@Override
	protected void drawPoint(int[] pixels, int x, int y, double z, Color color, List<Pixel> sidePixels,
							 Point3D viewVector, Point3D normalVector, Point3D texel, double nw) {
		var calculatedPixelColor = HelloController.TEXTURES_ENABLED
				? lighting.getTexturedPointColor(texel.multiply(1 / nw), viewVector)
				: lighting.getPointColor(normalVector, viewVector, color);

		super.drawPoint(pixels, x, y, z, calculatedPixelColor, sidePixels, viewVector, normalVector, texel, nw);
	}
}
