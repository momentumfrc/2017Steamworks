package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.Color;

import org.usfirst.frc.team4999.lights.animations.Animation;

public class RandomColors implements Animation {
	
	private int delay;
	
	public RandomColors(int delay) {
		this.delay = delay;
	}
	
	
	private int randomRGB() {
		return (int)(Math.random() * 256);
	}
	
	@Override
	public Color[] animate(Color[] pixels) {
		Color[] out = pixels.clone();
		for(int i = 0; i < out.length; i++) {
			out[i] = new Color(randomRGB(), randomRGB(), randomRGB());
		}
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		return delay;
	}

}
