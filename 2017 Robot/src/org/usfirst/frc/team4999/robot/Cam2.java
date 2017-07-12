package org.usfirst.frc.team4999.robot;

import org.opencv.core.Scalar;

/**
 * An implementation of the Vision Class designed for camera 2
 * @author Jordan
 *
 */
public class Cam2 extends Vision {
	
	private double[] hsvThresholdHue = {72.84172661870504, 105.12733446519523};
	private double[] hsvThresholdSaturation = {100.89928057553958, 255.0};
	private double[] hsvThresholdValue = {165.10791366906474, 255.0};
	
	Cam2(String name, int device) {
		super(name,device, 640, 480);
	}
	public boolean reversed;
	public boolean testProcess = false;
	public void process() {
		if(!testProcess) {
			if(reversed) {
				drawText("Front",5,475,2,0,255,0);
			} else {
				drawText("Back",5,475,2,255,0,0);
			}
		} else {
		
			blur("BOX",3.6036036036036037);
			
			hsvThreshold(hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue);
			
			findContours(false);
			
			image.setTo(new Scalar(0,0,0));
			drawContours(255,255,255);
		}
		
	}
	protected void cameraInit() {
		System.out.println("The properties on "+ cam.getName() +" are:");
		System.out.println(cam.enumerateProperties());
	}
}
