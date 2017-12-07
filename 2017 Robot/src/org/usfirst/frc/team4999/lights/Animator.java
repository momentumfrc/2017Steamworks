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
	
	private final long WAIT_TIMEOUT = 500;
	
	// Generic object. The wait() method is used to pause the run thread until the notifyAll() method is called
	// If an animation returns -1 for the time to wait, the run thread pauses execution until a new animation is set
	private final Object pauseLock = new Object();
	
	public AnimatorThread(Display out, Animation current) {
		this.out = out;
		this.current = current;
	}
	
	public void setAnimation(Animation newAnimation) {
		this.current = newAnimation;
		
		// Tells the thread to continue. 
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public void run() {
		while(!Thread.interrupted()){
			// Note how long the send takes
			long millis = System.currentTimeMillis();
			Animation animation = current;
			// show current frame
			out.show(animation.animate());
			// get how long to delay for
			int delay = animation.getDelayUntilNextFrame();
			
			synchronized (pauseLock) {
				if(delay < 0) {
					try {
						pauseLock.wait(WAIT_TIMEOUT);
					} catch (InterruptedException e) {
						break;
					}
					continue;
				}
			}
			
			delay -= (System.currentTimeMillis() - millis);
			if (delay > 0) Timer.delay(delay / 1000.0);
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
			System.out.println("Recieved null animation! Defaulting to solid black");
			animate.setAnimation(new Solid(Color.BLACK));
			return;
		}
		animate.setAnimation(newAnimation);
	}
	

}
