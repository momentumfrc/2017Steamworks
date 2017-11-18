package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;

public class Solid implements Animation {
	
	Color[] color;
	
	public static Solid rainbow() {
		Color[] rainbow = {
				Color.RED,
				new Color(255,127,0),
				Color.YELLOW,
				Color.GREEN,
				Color.BLUE,
				new Color(139,0,255)
		};
		return new Solid(rainbow);
	}
	
	public Solid(Color[] colors) {
		this.color = colors;
	}
	
	public Solid(Color color) {
		this.color = new Color[]{color};
	}

	@Override
	public Color[] animate(Color[] pixels) {
		Color[] out = pixels.clone();
		
		for(int i = 0; i < out.length; i++) {
			out[i] = color[i % color.length];
		}
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		return -1;
	}

}
