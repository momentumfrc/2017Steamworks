package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;


public class Fade implements Animation {
	private final double CLOSE_ENOUGH = 0.005;
	
	public static enum colorspace { RGB, HSV };
	
	private colorspace space;
	private float[] color1, color2, current, diffs;
	
	private boolean forward = true, hold = false;
	
	private int fadeTime, holdTime, steps;
	
	/**
	 * Creates an animation object that is a fade between two colors
	 * @param space The colorspace to fade across. Either colorspace.RGB or colorspace.HSV
	 * @param color1 One of the colors
	 * @param color2 The other color
	 * @param fadeTime How long a fade should last
	 * @param holdTime How long a color should be held before beginning the fade
	 * @param steps How many times to change the color during a fade
	 */
	public Fade(colorspace space, Color color1, Color color2, int fadeTime, int holdTime, int steps) {
		switch(space) {
		case RGB:
			this.color1 = color1.getRGBColorComponents(null);
			this.color2 = color2.getRGBColorComponents(null);
			break;
		case HSV:
			this.color1 = toHSV(color1);
			this.color2 = toHSV(color2);
			break;
		}
		this.space = space;
		current = this.color1.clone();
		diffs = new float[]{(this.color2[0] - this.color1[0]) / steps, (this.color2[1] - this.color1[1]) / steps, (this.color2[2] - this.color1[2]) / steps};
		this.fadeTime = fadeTime;
		this.holdTime = holdTime;
		this.steps = steps;
	}

	@Override
	public Color[] animate(Color[] pixels) {
		
		Color[] out = pixels.clone();
		
		if(forward) {
			current[0] += diffs[0];
			current[1] += diffs[1];
			current[2] += diffs[2];
			
			if(isCloseTo(current,color2)) {
				forward = false;
				hold = true;
			}
			
		} else {
			current[0] -= diffs[0];
			current[1] -= diffs[1];
			current[2] -= diffs[2];
			if(isCloseTo(current,color1)) {
				forward = true;
				hold = true;
			}
		}
		current = limit(current);
		Color currentc = Color.BLACK;
		switch(space) {
		case RGB:
			currentc = new Color(current[0], current[1], current[2]);
			break;
		case HSV:
			currentc = Color.getHSBColor(current[0], current[1], current[2]);
			break;
		}
		
		
		for(int i = 0; i < out.length; i++) {
			out[i] = currentc;
		}
		
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		if(hold) {
			hold = false;
			return holdTime;
		} else {
			return fadeTime / steps;
		}
	}
	
	private float[] toHSV(Color rgbColor) {
		return Color.RGBtoHSB(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue(), null);
	}
	
	private float[] toRGB(Color rgbColor) {
		return new float[] {rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue()};
	}
	
	private boolean isCloseTo(float[] color1, float[] color2) {
		return Math.abs(color1[0] - color2[0]) < CLOSE_ENOUGH && Math.abs(color1[1] - color2[1] ) < CLOSE_ENOUGH && Math.abs(color1[2] - color2[2]) < CLOSE_ENOUGH;
	}
	
	private float[] limit(float[] in) {
		float[] out = in.clone();
		for(int i = 0; i < out.length; i++ ) {
			out[i] = (out[i] < 0) ? 0 : out[i];
			out[i] = (out[i] > 1) ? 1: out[i];
		}
		return out;
	}
	
	private String cToString(float[] c) {
		return String.format("[%f, %f, %f]", c[0], c[1], c[2]);
	}
	
	
}
