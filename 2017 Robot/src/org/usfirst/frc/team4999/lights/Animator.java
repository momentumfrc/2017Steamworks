package org.usfirst.frc.team4999.lights;

import org.usfirst.frc.team4999.lights.Color;
import org.usfirst.frc.team4999.lights.animations.Animation;
import org.usfirst.frc.team4999.lights.animations.Solid;

import edu.wpi.first.wpilibj.Timer;

/**
 * Runs in an infinite loop. Displays a frame of {@link Animation}, then waits the duration specified by the animation
 * @author jordan
 *
 */
class AnimatorThread extends Thread {
	private Display out;
	private Animation current;
	
	// Generic object. The wait() method is used to pause the run thread until the notifyAll() method is called
	// If an animation returns -1 for the time to wait, the run thread pauses execution until a new animation is set
	private final Object pauseLock = new Object();
	
	private long timeToSend = 50;
	
	
	public AnimatorThread(Display out, Animation current) {
		this.out = out;
		this.current = current;
		
		// Needs to be daemon so that it doesn't block the exit of the JVM
		this.setDaemon(true);
	}
	
	public void setAnimation(Animation newAnimation) {
		synchronized (current) {
			this.current = newAnimation;
		}
		// Tells the thread to continue. 
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public void run() {
		while(!Thread.interrupted()){
			
			int delay;
			synchronized (current) {
				// show current frame. keep track of how long it took
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
/**
 * Holds a runnable {@link AnimationThread}
 * @author jordan
 *
 */
public class Animator {
	
	private AnimatorThread animate;
	
	/**
	 * Creates an animator using the {@link NeoPixels} as the default display
	 */
	public Animator() {
		this(NeoPixels.getInstance());
	}
	
	/**
	 * Creates an animator using the specified {@link Display} 
	 * @param pixels Display to output to
	 */
	public Animator(Display pixels) {
		animate = new AnimatorThread(pixels, new Solid(Color.BLACK));
	}
	
	/**
	 * Set the animation run on the AnimationThread
	 * @param newAnimation
	 */
	public void setAnimation(Animation newAnimation) {
		if(newAnimation == null) {
			System.out.println("Can't set a null animation!!");
			return;
		}
		animate.setAnimation(newAnimation);
	}
	

}
