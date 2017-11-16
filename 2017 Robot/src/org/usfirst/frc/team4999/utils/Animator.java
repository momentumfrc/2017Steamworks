package org.usfirst.frc.team4999.utils;

import java.awt.Color;

public class Animator {

	Color[] currentState;
	Display pixels;
	
	public Animator(int numberOfLights) {
		currentState = new Color[numberOfLights];
		//pixels = new NeoPixels();
		pixels = new TestDisplay();
	}
	
	

}
