package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;

public class Blink implements Animation {
	
	private int delay;
	private Color color;
	
	private boolean on = false;
	
	public Blink(int delay, Color color) {
		this.delay = delay;
		this.color = color;
	}

	@Override
	public Color[] animate(Color[] pixels) {
		Color[] out = new Color[pixels.length];
		on = !on;
		for(int i = 0; i < out.length; i++) {
			out[i] = (on) ? color : Color.BLACK;
		}
		return out;
	}

	@Override
	public int getDelayBetweenFrames() {
		return delay;
	}

}
