package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;

import edu.wpi.first.wpilibj.Timer;

class AnimatorThread extends Thread {
	private Display out;
	private Animation current;
	
	private long timeToSend = 50;
	
	
	public AnimatorThread(Display out, Animation current) {
		this.out = out;
		this.current = current;
		
		this.setDaemon(true);
	}
	
	public void run() {
		while(!Thread.interrupted()) {
			timeToSend = out.show(current.animate());
			if(timeToSend < 0) {
				System.out.println("Failed to write to neopixels, exiting animation thread");
				break;
			}
			int delay = current.getDelayUntilNextFrame();
			//System.out.println("Expected: " + delay);
			delay -= timeToSend;
			delay = (delay < 0) ? 0 : delay;
			if (delay > 0) Timer.delay(delay / 1000.0);
		}
	}
	
}

public class Animator {
	
	public static final Color MOMENTUM_BLUE = new Color(6,206,255);
	public static final Color MOMENTUM_PURPLE = new Color(159,1,255);
	

	Display pixels;
	Animation currentAnimation;
	AnimatorThread animate;
	
	public Animator() {
		this(NeoPixels.getInstance());
	}
	
	public Animator(Display pixels) {
		this.pixels = pixels;
		
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
		}
		this.currentAnimation = newAnimation;
		if(currentAnimation.getDelayUntilNextFrame() < 0) {
			pixels.show(currentAnimation.animate());
		} else {
			animate = new AnimatorThread(pixels, currentAnimation);
			animate.start();
		}
	}
	

}
