package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.Color;

import edu.wpi.first.wpilibj.I2C;

public class NeoPixels implements Display {
	
	private final int START_FRAME = 0xFD;
	private final int SHOW_FRAME = 0xFE;
	
	private I2C strip;
	
	private static NeoPixels instance;
	
	public static NeoPixels getInstance() {
		if(instance == null) {
			instance = new NeoPixels();
		}
		return instance;
	}
	
	private NeoPixels() {
		strip = new I2C(I2C.Port.kOnboard, 4);
	}
	
	private int safe(int in) {
		int out = in;
		switch(in) {
		case 1:
			out = 0;
			break;
		case START_FRAME:
		case SHOW_FRAME:
			out = 0xFF;
			break;
		}
		return out;
	}
	
	synchronized public void show(Color[] currentState) {
		strip.write(1, START_FRAME);
		for(Color color : currentState) {
			strip.write(1, safe(color.getRed()));
			strip.write(1, safe(color.getGreen()));
			strip.write(1, safe(color.getBlue()));
		}
		strip.write(1, SHOW_FRAME);
	}

}
