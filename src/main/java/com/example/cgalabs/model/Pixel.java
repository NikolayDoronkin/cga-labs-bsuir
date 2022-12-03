package com.example.cgalabs.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pixel {
	private int x;
	private int y;
	private double z;
	private Color color;
}
