package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;

import edu.wpi.first.wpilibj.Timer;

class AnimatorThread extends Thread {
	private Display out;
	private Animation current;
	
	private final Object pauseLock = new Object();
	
	private long timeToSend = 50;
	
	
	public AnimatorThread(Display out, Animation current) {
		this.out = out;
		this.current = current;
		
		this.setDaemon(true);
	}
	
	public void setAnimation(Animation newAnimation) {
		synchronized (current) {
			this.current = newAnimation;
		}
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public void run() {
		while(!Thread.interrupted()){
			
			int delay;
			synchronized (current) {
				timeToSend = out.show(current.animate());
				if(timeToSend < 0) {
					System.out.println("Failed to write to neopixels, suspending animation thread");
					try {
						pauseLock.wait();
					} catch (InterruptedException e) {
						break;
					}
					continue;
				}
				delay = current.getDelayUntilNextFrame();
			}
			synchronized (pauseLock) {
				if(delay < 0) {
					try {
						pauseLock.wait();
					} catch (InterruptedException e) {
						break;
					}
				} else {
					//System.out.format("Send took %dms, delay requested is %dms\n", timeToSend, delay);
					delay -= timeToSend;
					delay = (delay < 0) ? 0 : delay;
					if (delay > 0) Timer.delay(delay / 1000.0);
				}
			}
		}
	}
	
}

public class Animator {
	
	public static final Color MOMENTUM_BLUE = new Color(6,206,255);
	public static final Color MOMENTUM_PURPLE = new Color(159,1,255);
	
	AnimatorThread animate;
	
	public Animator() {
		this(NeoPixels.getInstance());
	}
	
	public Animator(Display pixels) {
		animate = new AnimatorThread(pixels, new Solid(Color.BLACK));
	}
	
	public void setAnimation(Animation newAnimation) {
		if(newAnimation == null) {
			System.out.println("Can't set a null animation!!");
			return;
		}
		animate.setAnimation(newAnimation);
	}
	

}
