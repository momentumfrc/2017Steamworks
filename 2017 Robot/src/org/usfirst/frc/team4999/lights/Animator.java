package org.usfirst.frc.team4999.lights;

import java.awt.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;

class AnimatorThread extends Thread {
	private Color[] currentState;
	private Display out;
	private Animation current;
	
	public AnimatorThread(Display out, Color[] currentState, Animation current) {
		this.currentState = currentState;
		this.out = out;
		this.current = current;
		
		this.setDaemon(true);
	}
	
	public void run() {
		while(!Thread.interrupted()) {
			currentState = current.animate(currentState);
			out.show(currentState);
			try {
				Thread.sleep(current.getDelayBetweenFrames());
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public Color[] getColorState() {
		if(!this.isAlive()) {
			return currentState;
		}
		return null;
	}
}

public class Animator {

	Color[] currentState;
	Display pixels;
	Animation currentAnimation;
	AnimatorThread animate;
	
	public Animator(int numberOfLights) {
		this(numberOfLights, new NeoPixels());
	}
	
	public Animator(int numberOfLights, Display pixels) {
		currentState = new Color[numberOfLights];
		this.pixels = pixels;
	}
	
	public void setAnimation(Animation newAnimation) {
		if(animate != null) {
			if(animate.isAlive()) {
				animate.interrupt();
				try {
					animate.join();
				} catch (InterruptedException e) {
				}
			}
			this.currentState = animate.getColorState();
		}
		this.currentAnimation = newAnimation;
		if(currentAnimation.getDelayBetweenFrames() < 0) {
			currentState = currentAnimation.animate(currentState);
		} else {
			animate = new AnimatorThread(pixels, currentState, currentAnimation);
			animate.start();
		}
	}
	
	

}
