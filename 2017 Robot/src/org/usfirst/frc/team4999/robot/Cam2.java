package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.Timer;

/**
 * An implementation of the Vision Class designed for camera 2
 * @author Jordan
 *
 */
public class Cam2 extends Vision {
	
	private double[] hsvThresholdHue = {72.84172661870504, 105.12733446519523};
	private double[] hsvThresholdSaturation = {100.89928057553958, 255.0};
	private double[] hsvThresholdValue = {165.10791366906474, 255.0};
	
	Timer autoTest;
	Boolean isFirstLoop;
	
	Cam2(String name, int device) {
		super(name,device, 640, 480);
		autoTest = new Timer();
		isFirstLoop = true;
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
			
			isFirstLoop = true;
		} else {
			if(isFirstLoop) {
				isFirstLoop = false;
				autoTest.reset();
			}
			if(autoTest.hasPeriodPassed(2))
				blur("BOX",3.6036036036036037);
			if(autoTest.hasPeriodPassed(4))
				hsvThreshold(hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue);
			if(autoTest.hasPeriodPassed(6)) {
				findContours(false);
				drawContours(255,255,255, 5);
			}
		}
		
	}
	protected void cameraInit() {
		System.out.println("The properties on "+ cam.getName() +" are:");
		System.out.println(cam.enumerateProperties());
	}
}
