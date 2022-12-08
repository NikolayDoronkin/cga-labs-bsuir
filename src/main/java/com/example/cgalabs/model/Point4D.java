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

	public Point4D divide(double w) {
		return new Point4D(this.getX() / w, this.getY() / w, this.getZ(), this.getW() / w);
	}
}
