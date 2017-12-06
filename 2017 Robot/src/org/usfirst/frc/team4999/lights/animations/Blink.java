package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.Packet;

public class Blink implements Animation {
	
	private Color[] colors;
	private int[] waittimes;
	
	private int idx;
	
	public Blink(Color[] colors, int waittime) {
		int[] waittimes = new int[colors.length];
		for(int i = 0; i < waittimes.length; i++) {
			waittimes[i] = waittime;
		}
		this.colors = colors;
		this.waittimes = waittimes;
		idx = 0;
	}
	
	public Blink(Color[] colors, int[] waittimes) {
		if(colors.length != waittimes.length) throw new IllegalArgumentException("Need exactly one waittime for every color");
		this.colors = colors;
		this.waittimes = waittimes;
		idx = 0;
	}

	@Override
	public Packet[] animate() {
		Packet out[] = {new Packet(0, colors[idx], 1, 1)};
		idx = (idx++ > colors.length) ? 0 : idx;
		return out;
	}

	@Override
	public int getDelayUntilNextFrame() {
		return waittimes[idx];
	}

}
