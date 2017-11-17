package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;

public class Solid implements Animation {
	
	Color color;
	
	public Solid(Color color) {
		this.color = color;
	}

	@Override
	public Color[] animate(Color[] pixels) {
		Color[] out = new Color[pixels.length];
		for(int i = 0; i < out.length; i++) {
			out[i] = color;
		}
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		return -1;
	}

}
