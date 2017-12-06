package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Packet;

public class RandomColors implements Animation {
	
	private int delay, repeat;
	
	public RandomColors(int delay, int repeat) {
		this.delay = delay;
		this.repeat = repeat;
	}
	
	
	private int randomRGB() {
		return (int)(Math.random() * 256);
	}
	
	@Override
	public Packet[] animate() {
		Packet[] out = new Packet[repeat];
		for(int i = 0; i < out.length; i++) {
			Color paint = new Color(randomRGB(), randomRGB(), randomRGB());
			out[i] = new Packet(i, paint, 1, repeat );
		}
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		return delay;
	}

}
