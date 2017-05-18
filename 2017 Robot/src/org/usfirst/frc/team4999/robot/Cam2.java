package org.usfirst.frc.team4999.robot;

/**
 * An implementation of the Vision Class designed for camera 2
 * @author Jordan
 *
 */
public class Cam2 extends Vision {
	Cam2(String name, int device) {
		super(name,device);
	}
	public boolean reversed;
	public void run() {
		while(!Thread.interrupted()) {
			if(imagesink.grabFrame(image) == 0)
				continue;
			if(reversed) {
				drawText("Front",5,475,2,0,255,0);
			} else {
				drawText("Back",5,475,2,255,0,0);
			}
			
			// If we wanted to do more  vision processing, we could do it here.
			
			imagesource.putFrame(image);
		}
	}
}
