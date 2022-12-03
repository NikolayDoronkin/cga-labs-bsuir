package com.example.cgalabs.model;

import lombok.Getter;

@Getter
public class ZBuffer {

	private final int width;
	private final int height;

	private double[][] buffer;

	public ZBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.buffer = new double[width][height];
	}

	public double getValue(int x, int y) {
		return buffer[x][y];
	}

	public void setValue(int x, int y, double value) {
		buffer[x][y] = value;
	}

	public void update() {
		buffer = new double[width][height];
	}
}
