package com.example.cgalabs.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Polygon {
	private PolygonPoint firstPolygon;
	private PolygonPoint secondPolygon;
	private PolygonPoint thirdPolygon;
}
