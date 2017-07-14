package org.usfirst.frc.team4999.robot;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer; 

/**
 * An abstract class that can be implemented to perform vision processing on usb cameras on the RoboRio.
 * @author Jordan
 *
 */
public abstract class Vision extends Thread {
	// Get video from a usb camera
	protected UsbCamera cam;
	// CvSink gets frames from the camera
	protected CvSink imagesink;
	// CvSource provides frames to the cameraserver
	protected CvSource imagesource;
	// image holds the frame currently being processed
	protected Mat image = new Mat();
	// Contours in the image
	protected ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	
	// Mat for holding temporary images
	protected Mat temp = new Mat();
	
	
	/**
	 * Creates a vision object to run vision processing on a usb camera.
	 * @param name The name of the camera
	 * @param device The number of the device
	 */
	Vision(String name, int device, int w, int h) {
		super("Process "+name);
		this.cam = new UsbCamera(name,device);
		cam.setResolution(w, h); // Possible resolutions: 640x480, 320x240, 160x120
		cameraInit();
		imagesink = new CvSink(name+" Sink");
		imagesink.setSource(cam);
		imagesource = CameraServer.getInstance().putVideo(name + " Source", w, h);
		// Prevent robot code from waiting for this thread to finish before exiting
		setDaemon(true);
	}
	Vision(String name, int device) {
		this(name, device, 640, 480);
	}
	
	protected abstract void cameraInit();
	
	/**
	 * Method run in the separate thread when start() is called.
	 */
	public void run() {
		while(!Thread.interrupted()) {
			if(imagesink.grabFrame(image) == 0)
				continue;
			process();
			imagesource.putFrame(image);
		}
	}
	/**
	 * Method in which vision processing occurs. The property image contains the current Mat.
	 */
	public abstract void process();
	
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
	
	/**
	 * Draws a circle around a point
	 * @param center A Point representing the center of the circle
	 * @param radius The radius of the circle to be drawn
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	void drawCircle(Point center, int radius, double r, double g, double b) {
		Imgproc.circle(image, center, radius, new Scalar(r,g,b));
	}
	/**
	 * Draws a circle around a point
	 * @param x X-Coordinate of the center of the circle
	 * @param y Y-Coordinate of the center of the circle
	 * @param radius The radius of the circle to be drawn
	 * @param r red
	 * @param g green 
	 * @param b blue
	 */
	void drawCircle(double x, double y, int radius, double r, double g, double b) {
		Imgproc.circle(image, new Point(x,y), radius, new Scalar(r,g,b));
	}
	
	/**
	 * Draws all contours onto the image
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	void drawContours(double r, double g, double b) {
		Imgproc.drawContours(image, contours, -1, new Scalar(r,g,b));
	}
	/**
	 * Draws a specific contour onto the image
	 * @param index The index of the contour to draw
	 * @param r red
	 * @param g green
	 * @param b blue
	 */
	void drawContour(int index, double r, double g, double b) {
		Imgproc.drawContours(image, contours, index, new Scalar(r,g,b));
	}
	/**
	 * Softens an image using one of several filters.
	 * @param type The blurType to perform. Either BOX, GAUSSIAN, MEDIAN, or BILATERAL
	 * @param doubleRadius The radius for the blur.
	 */
	void blur(String type, double doubleRadius) {
			int radius = (int)(doubleRadius + 0.5);
			int kernelSize;
			switch(type){
				case "BOX":
					kernelSize = 2 * radius + 1;
					Imgproc.blur(image, temp, new Size(kernelSize, kernelSize));
					break;
				case "GAUSSIAN":
					kernelSize = 6 * radius + 1;
					Imgproc.GaussianBlur(image, temp, new Size(kernelSize, kernelSize), radius);
					break;
				case "MEDIAN":
					kernelSize = 2 * radius + 1;
					Imgproc.medianBlur(image, temp, kernelSize);
					break;
				case "BILATERAL":
					Imgproc.bilateralFilter(image, temp, -1, radius, radius);
					break;
			}
			image = temp;
	}
	
	/**
	 * Segment an image based on hue, saturation, and value ranges.
	 *
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param val The min and max value
	 */
	void hsvThreshold(double[] hue, double[] sat, double[] val) {
			Imgproc.cvtColor(image, temp, Imgproc.COLOR_BGR2HSV);
			Core.inRange(temp, new Scalar(hue[0], sat[0], val[0]), new Scalar(hue[1], sat[1], val[1]), image);
	}
	
	/**
	 * Finds contours in a mat.
	 * @param externalOnly Whether or not to retrieve only the extreme outer contours
	 */
	void findContours(boolean externalOnly) {
			contours.clear();
			int mode;
			if (externalOnly) {
				mode = Imgproc.RETR_EXTERNAL;
			} else {
				mode = Imgproc.RETR_LIST;
			}
			int method = Imgproc.CHAIN_APPROX_SIMPLE;
			Imgproc.findContours(image, contours, temp, mode, method);
	}
	
	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximimum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	void filterContours(double minArea, double minPerimeter, double minWidth, double maxWidth, double minHeight, double maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double minRatio, double maxRatio) {
			final MatOfInt hull = new MatOfInt();
			ArrayList<MatOfPoint> output = new ArrayList<MatOfPoint>();
			
			for (int i = 0; i < contours.size(); i++) {
				final MatOfPoint contour = contours.get(i);
				final Rect bb = Imgproc.boundingRect(contour);
				if (bb.width < minWidth || bb.width > maxWidth) continue;
				if (bb.height < minHeight || bb.height > maxHeight) continue;
				final double area = Imgproc.contourArea(contour);
				if (area < minArea) continue;
				if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
				Imgproc.convexHull(contour, hull);
				MatOfPoint mopHull = new MatOfPoint();
				mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
				for (int j = 0; j < hull.size().height; j++) {
					int index = (int)hull.get(j, 0)[0];
					double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
					mopHull.put(j, 0, point);
				}
				final double solid = 100 * area / Imgproc.contourArea(mopHull);
				if (solid < solidity[0] || solid > solidity[1]) continue;
				if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
				final double ratio = bb.width / (double)bb.height;
				if (ratio < minRatio || ratio > maxRatio) continue;
				output.add(contour);
			}
			contours = output;
		}
	/**
	 * Finds the centers of every contour
	 * @return An ArrayList of the points of the center of every contour
	 */
	public ArrayList<Point> centers() {
		int size = contours.size();
		ArrayList<Point> points = new ArrayList<Point>(size);
		for( int i = 0; i < size; i++ ){
			Moments moment = Imgproc.moments(contours.get(i), false);
			points.add(new Point( (moment.get_m10() / moment.get_m00()), (moment.get_m01() / moment.get_m00()) ));
		}
		return points;
	}
	
	/**
	 * Finds the width and height of each contour.
	 * @return A two-dimensional array. The first dimension represents each contour, and the second dimension gives the width and height.
	 */
	public double[][] wH() {
		
		double[][] WHs = new double[(contours.size())][2];
		for( int i = 0; i < WHs.length; i++) {
			final Rect rect = Imgproc.boundingRect(contours.get(i));
			WHs[i] = new double[]{rect.width, rect.height};
		}
		return WHs;
	}
	
}
