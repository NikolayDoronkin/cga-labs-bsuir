package com.example.cgalabs.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Point4D {
	private Double x;
	private Double y;
	private Double z;
	private Double w;

	public Point4D divide(Double w) {
		return new Point4D(this.getX() / w, this.getY() / w, this.getZ(), this.getW() / w);
	}

	public Point4D subtract(Point4D source) {
		return Point4D.of(
				this.x - source.getX(),
				this.y - source.getY(),
				this.z - source.getZ(),
				this.w - source.getW());
	}

	public Point4D multiply(double value) {
		return Point4D.of(
				this.x * value,
				this.y * value,
				this.z * value,
				this.w * value);
	}

	public Point4D add(Point4D source) {
		return Point4D.of(
				this.x + source.getX(),
				this.y + source.getY(),
				this.z + source.getZ(),
				this.w + source.getW());
	}
}
