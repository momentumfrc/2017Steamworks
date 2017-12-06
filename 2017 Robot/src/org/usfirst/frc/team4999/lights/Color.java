package org.usfirst.frc.team4999.lights;

public class Color {
	private int r,g,b;
	
	private static double brightness = 0.2;
	
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	private int dim(int in) {
		return (int) (in * brightness);
	}
	
	public int getRed() {
		return dim(r);
	}
	public int getGreen() {
		return dim(g);
	}
	public int getBlue() {
		return dim(b);
	}
	
	public int getFullRed() {
		return r;
	}
	public int getFullGreen() {
		return g;
	}
	public int getFullBlue() {
		return b;
	}

	public static Color RED = new Color(255,0,0);
	public static Color YELLOW = new Color(255,255,0);
	public static Color GREEN = new Color(0,255,0);
	public static Color BLUE = new Color(0,0,255);
	public static Color BLACK = new Color(0,0,0);
	public static Color WHITE = new Color(255,255,255);
	
}
