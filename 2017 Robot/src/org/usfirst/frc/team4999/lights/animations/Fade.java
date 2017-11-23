package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;

class MutableColor {
	double[] color = new double[3];
	double[] diffs = new double[3];
	double[] to = new double[3];
	
	private final double CLOSE_ENOUGH = 0.005;
	
	public MutableColor(Color from) {
		color[0] = from.getRed();
		color[1] = from.getGreen();
		color[2] = from.getBlue();
	}
	
	public void calculateDiffs(Color to, int steps) {
		this.to[0] = to.getRed();
		this.to[1] = to.getGreen();
		this.to[2] = to.getBlue();
		diffs[0] = (this.to[0] - color[0]) / steps;
		diffs[1] = (this.to[1] - color[1]) / steps;
		diffs[2] = (this.to[2] - color[2]) / steps;
	}
	
	public boolean applyDiffs() {
		color[0] += diffs[0];
		color[1] += diffs[1];
		color[2] += diffs[2];
		
		return ((Math.abs(color[0] - to[0]) < CLOSE_ENOUGH) && (Math.abs(color[1] - to[1]) < CLOSE_ENOUGH) && (Math.abs(color[2] - to[2]) < CLOSE_ENOUGH));
	}
	
	public Color toColor() {
		int[] out = new int[3];
		
		out[0] = (int) color[0];
		out[1] = (int) color[1];
		out[2] = (int) color[2];
		
		out[0] = (out[0] > 255) ? 255 : out[0];
		out[1] = (out[1] > 255) ? 255 : out[1];
		out[2] = (out[2] > 255) ? 255 : out[2];
		
		out[0] = (out[0] < 0) ? 0 : out[0];
		out[1] = (out[1] < 0) ? 0 : out[1];
		out[2] = (out[2] < 0) ? 0 : out[2];
		
		return new Color(out[0], out[1], out[2]);
	}
	
	
}

public class Fade implements Animation {
	private Color[] colors;
	private MutableColor current;
	private int fadeTime, holdTime;
	
	private int idx = 0;
	private boolean hold = false;
	
	private final int STEPS = 100;
	
	public static Fade RainbowFade(int fadeTime, int holdTime) {
		return new Fade(new Color[]{
				Color.RED,
				new Color(255,127,0),
				Color.YELLOW,
				Color.GREEN,
				Color.BLUE,
				new Color(139,0,255)
		}, fadeTime, holdTime);
	}
	
	public Fade(Color[] colors, int fadeTime, int holdTime) {
		this.colors = colors;
		current = new MutableColor(colors[0]);
		current.calculateDiffs(colors[getNextIndex()], STEPS);
		this.fadeTime = fadeTime;
		this.holdTime = holdTime;
		
	}
	
	@Override
	public Color[] animate(Color[] pixels) {
		Color[] out = pixels.clone();
		if(current.applyDiffs()) {
			idx = getNextIndex();
			hold = true;
			current.calculateDiffs(colors[idx], STEPS);
		}
		Color paint = current.toColor();
		for(int i = 0; i < out.length; i++) {
			out[i] = paint;
		}
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		if(hold) {
			hold = false;
			return holdTime;
		} else {
			return fadeTime/STEPS;
		}
	}
	
	private int getNextIndex() {
		return (idx + 1 ) % colors.length;
	}
	
}
