package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.CameraServer;

/**
 * An implementation of the Vision Class designed for camera 2
 * @author Jordan
 *
 */
public class Cam2 extends Vision {
	Cam2(CameraServer server,String name, int device) {
		super(server,name,device);
	}
	public boolean reversed;
	public void run() {
		while(!Thread.interrupted()) {
			if(imagesink.grabFrame(image) == 0)
				continue;
			if(reversed) {
				drawText("Back",2,158,2,255,0,0);
			} else {
				drawText("Front",2,158,2,0,255,0);
			}
			
			// If we wanted to do more  vision processing, we could do it here.
			
			imagesource.putFrame(image);
		}
	}
}
