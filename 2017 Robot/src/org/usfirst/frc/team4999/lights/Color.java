package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.utils.MoPrefs;

public class Color {
	private int r,g,b;
	
	private static double brightness = MoPrefs.getInstance().getBrightness();
	
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	private int dim(int in) {
		return (int) (in * brightness);
	}
	
	public int getRed() {
		return r;
	}
	public int getGreen() {
		return g;
	}
	public int getBlue() {
		return b;
	}
	
	public int getDimRed() {
		return dim(r);
	}
	public int getDimGreen() {
		return dim(g);
	}
	public int getDimBlue() {
		return dim(b);
	}

	public static Color RED = new Color(255,0,0);
	public static Color YELLOW = new Color(255,255,0);
	public static Color GREEN = new Color(0,255,0);
	public static Color BLUE = new Color(0,0,255);
	public static Color BLACK = new Color(0,0,0);
	public static Color WHITE = new Color(255,255,255);
	
	public static void setBrightness(double newBrightness) {
		brightness = newBrightness;
	}
	
}
