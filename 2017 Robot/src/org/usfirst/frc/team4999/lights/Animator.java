package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;

import edu.wpi.first.wpilibj.Timer;

class AnimatorThread extends Thread {
	private Color[] currentState;
	private Display out;
	private Animation current;
	private double brightness;
	
	private final int TIME_TO_SEND_FRAME = 50;
	
	
	public AnimatorThread(Display out, Color[] currentState, Animation current, double brightness) {
		if(brightness < 0 || brightness > 1) throw new IllegalArgumentException("Brightness must be within [0,1]"); 
		this.currentState = currentState;
		this.out = out;
		this.current = current;
		this.brightness = brightness;
		
		this.setDaemon(true);
	}
	
	private Color dim(Color in) {
		return new Color((int)(in.getRed() * brightness), (int)(in.getGreen() * brightness), (int)(in.getBlue() * brightness));
	}
	
	private Color[] setBrightness(Color[] in) {
		Color[] out = in.clone();
		for(int i = 0; i < out.length; i++) {
			out[i] = dim(out[i]);
		}
		return out;
	}
	
	public void run() {
		while(!Thread.interrupted()) {
			currentState = current.animate(currentState);
			out.show(setBrightness(currentState));
			int delay = current.getDelayUntilNextFrame();
			//System.out.println("Expected: " + delay);
			delay -= TIME_TO_SEND_FRAME;
			delay = (delay < 0) ? 0 : delay;
			if (delay > 0) Timer.delay(delay / 1000.0);
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
	
	public static final Color MOMENTUM_BLUE = new Color(6,206,255);
	public static final Color MOMENTUM_PURPLE = new Color(159,1,255);
	

	Color[] currentState;
	Display pixels;
	Animation currentAnimation;
	AnimatorThread animate;
	
	double masterBrightness;
	
	public Animator(int numberOfLights) {
		this(numberOfLights, NeoPixels.getInstance());
	}
	
	public Animator(int numberOfLights, Display pixels) {
		currentState = new Color[numberOfLights];
		this.pixels = pixels;
		masterBrightness = 0.2;
		
		setAnimation(new Solid(Color.BLACK));
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
		if(currentAnimation.getDelayUntilNextFrame() < 0) {
			currentState = currentAnimation.animate(currentState);
			pixels.show(currentState);
		} else {
			animate = new AnimatorThread(pixels, currentState, currentAnimation, masterBrightness);
			animate.start();
		}
	}
	
	public void setBrightness(double brightness) {
		if(brightness < 0 || brightness > 1) throw new IllegalArgumentException("Brightness must be within [0,1]"); 
		masterBrightness = brightness;
	}
	

}
