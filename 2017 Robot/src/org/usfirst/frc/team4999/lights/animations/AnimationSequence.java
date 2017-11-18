package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;

public class AnimationSequence implements Animation {
	
	private Animation[] animations;
	private int[] times;
	
	private int timepassed = 0, currentidx = 0;
	
	public AnimationSequence(Animation[] animations, int time) {
		int[] times = new int[animations.length];
		for(int i = 0; i < times.length; i++) {
			times[i] = time; 
		}
		
		this.animations = animations;
		this.times = times;
	}
	
	public AnimationSequence(Animation[] animations, int[] times) {
		assert animations.length == times.length;
		
		this.animations = animations;
		this.times = times;
	}

	@Override
	public Color[] animate(Color[] pixels) {
		if(timepassed >= times[currentidx]) {
			timepassed = 0;
			currentidx++;
			currentidx = (currentidx < times.length) ? currentidx : 0;
		}
		return animations[currentidx].animate(pixels);
	}

	@Override
	public int getDelayUntilNextFrame() {
		
		int delay = animations[currentidx].getDelayUntilNextFrame();
		delay = (delay < 0) ? times[currentidx] : delay;
		timepassed += delay;
		
		return delay;
	}

}
