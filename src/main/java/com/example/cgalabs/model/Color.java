package com.example.cgalabs.model;

public record Color(int r, int g, int b, int a) {
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}
}
