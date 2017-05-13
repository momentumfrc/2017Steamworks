package org.usfirst.frc.team4999.robot;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.CameraServer; 

public class Vision extends Thread {
	// Get video from a usb camera
	protected UsbCamera cam;
	// CvSink gets frames from the camera
	protected CvSink imagesink;
	// CvSource provides frames to the cameraserver
	protected CvSource imagesource;
	// image holds the frame currently being processed
	protected Mat image = new Mat();
	
	/**
	 * Creates a vision object to run vision processing on a usb camera.
	 * @param name The name of the camera
	 * @param device The number of the device
	 */
	Vision(String name, int device) {
		super("Process "+name);
		this.cam = new UsbCamera(name,device);
		cam.setResolution(640, 480);
		imagesink = new CvSink(name+" Sink");
		imagesink.setSource(cam);
		imagesource = new CvSource(name+" Source",VideoMode.PixelFormat.kMJPEG, 640, 480, 30);
		CameraServer.getInstance().startAutomaticCapture(imagesource);
	}
	public void run() {
		while(!Thread.interrupted()) {
			if(imagesink.grabFrame(image) == 0)
				continue;
			imagesource.putFrame(image);
		}		
	}
	/**
	 * Draws a line of text on the image
	 * @param text
	 * @param x coordinate of the bottom left of the text
	 * @param y coordinate of the bottom left of the text
	 * @param size multiplier
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	void drawText(String text, double x, double y, double size, double r, double g, double b) {
		Imgproc.putText(image, text, new Point(x,y), Core.FONT_HERSHEY_SIMPLEX, size, new Scalar(r,g,b));
	}
}
