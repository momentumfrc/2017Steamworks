package org.usfirst.frc.team4999.utils;

import java.awt.Color;

public class Animator {

	Color[] currentState;
	Display pixels;
	
	public Animator(int numberOfLights) {
		this(numberOfLights, new NeoPixels());
	}
	
	public Animator(int numberOfLights, Display pixels) {
		currentState = new Color[numberOfLights];
		this.pixels = pixels;
	}
	
	

}
