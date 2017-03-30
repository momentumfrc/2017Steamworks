package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import org.usfirst.frc.analog.adis16448.*;

import Vector.Vector2D;

/**
 * ADIS: Positive Y is accelerate right
 * 		Positive Z is accelerate down
 * 		Positive X is accelerate forward
 * 
 * BuiltIn: Positive X is accelerate right
 * 		Positive Z is accelerate up
 * 		Positive Y is accelerate forward
 * 
 */
public class Distance {
	double accelX;
	double accelZ;
	double lastaccelX;
	double lastaccelZ;
	double accelOutX;
	double accelOutZ;
	Accelerometer builtIn; 
	ADIS16448_IMU adis;
	long time = System.currentTimeMillis();
	private double[] offsetX = {0,0}; // ADIS, BuiltIn
	private double[] offsetZ = {0,0};
	boolean calibrate = true; // Set calibrate to true to set the offset
	
	private Vector2D accel = new Vector2D(0/** ForwardBack is X */,0 /** LeftRight is Z */);
	private Vector2D vel = new Vector2D(0,0);
	private Vector2D dist = new Vector2D(0,0);
	public double distance = 0.0;
	public double velocity = 0.0;
	
	public double ALPHA = 0.8;
	
	/**
	 * Creates a new Distance object.
	 */
	public Distance(Accelerometer builtIn, ADIS16448_IMU adis) {
		this.builtIn = builtIn;
		this.adis = adis;
	}
	/**
	 * Returns the acceleration in a Vector2D object
	 */
	public Vector2D getAccel(){
		return accel;
	}
	/**
	 * Returns the velocity in a Vector2D object
	 */
	public Vector2D getVel(){
		return vel;
	}
	/**
	 * Returns the distance in a Vector2D object
	 */
	public Vector2D getDist(){
		return dist;
	}
	/**
	 * Resets acceleration, velocity, and distance to (0,0).
	 */
	public void zero(){
		accel = new Vector2D(0,0);
		vel = new Vector2D(0,0);
		dist = new Vector2D(0,0);
	}
	/**
	 * Updates acceleration, velocity, and distance properties.
	 */
	public void updateDistance() {
		//Set the offset if the calibrate boolean is true. 
		if(calibrate) {
			offsetX[0] = adis.getAccelX();
			offsetX[1] = builtIn.getY();
			offsetZ[0] = adis.getAccelY();
			offsetZ[1] = builtIn.getX();
			calibrate = false;
		}
		
		//System.out.println("ADXL362: X: " + ADXL.getAcceleration(ADXL362.Axes.kX) + " Y: " + ADXL.getAcceleration(ADXL362.Axes.kY) + " Z: " + ADXL.getAcceleration(ADXL362.Axes.kZ));
		/**System.out.println("Builtin: X: " + builtIn.getX() + " Y: "+ builtIn.getY() + " Z: " + builtIn.getZ());
		System.out.println("ADIS: X: " + adis.getAccelX() + " Y: " + adis.getAccelY() + " Z: "+ adis.getAccelZ());*/
		
		accelX = ((adis.getAccelX() - offsetX[0]) + (builtIn.getY() - offsetX[1])) / 2;
	    accelOutX = lastaccelX + ALPHA * (accelX - lastaccelX);
	    lastaccelX = accelX;
	    accelZ = ((adis.getAccelY() - offsetZ[0]) + (builtIn.getX() - offsetZ[1])) / 2;
	    accelOutZ = lastaccelZ + ALPHA * (accelZ - lastaccelZ);
	    lastaccelZ = accelZ;
	    
	    if(accelOutX < .05) {
	    	accelOutX = 0;
	    }
	    if(accelOutZ < .05) {
	    	accelOutZ = 0;
	    }
	    
	    accelOutX = accelOutX * 32.174;
	    accelOutZ = accelOutZ * 32.174;
	    
		/**
		// Sum the accelerometer values over 3 samples
		for(int i = 0; i < samplesize; i += 2) {
			accelsX[i] = builtIn.getY() - offsetX[1];
			accelsX[i+1] = adis.getAccelX() - offsetX[0];
			accelsZ[i] = builtIn.getX() - offsetZ[1];
			accelsZ[i+1] = adis.getAccelY() - offsetZ[0];
		}
		
		double[] averageAccels = new double[2]; // 0 : X, 1 : Z
		for(int i=0; i < samplesize; i++) {
			averageAccels[0] += accelsX[i];
			averageAccels[1] += accelsZ[i];
		}
		averageAccels[0] = averageAccels[0] / samplesize;
		averageAccels[1] = averageAccels[1] / samplesize;
		for(int i = 0; i < 2; i++ ) {
			if(Math.abs(averageAccels[i]) < .05 ) { // ignore noise below a certain threshold
				averageAccels[i] = 0;
			}
			// averageAccels[i] = averageAccels[i] * 9.81; // meters
			averageAccels[i] = averageAccels[i] * 32.174; // feet
		}*/
		System.out.println("Acceleration: X: " + accelOutX + " Z: " + accelOutZ );
		//get Time in seconds
		float timeChange = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		timeChange = timeChange / 1000;
		System.out.println("Time: " + timeChange);
		//getVelocity
		
		velocity += Math.copySign(Math.sqrt(Math.pow(accelOutX, 2) + Math.pow(accelOutZ,2)),accelOutX) * timeChange;
		/**if(Math.abs(velocity) < .1) {
			velocity = 0;
		}*/
		distance += velocity * timeChange;
		/**vel.addVectorWithTime(accel, timeChange);
		if(Math.abs(vel.getX()) < .5 ) {
			vel.setX(0.0);
		}
		if(Math.abs(vel.getY()) < .5 ){
			vel.setY(0.0);
		}
		System.out.println("Velocity: X:" + vel.getX() + " Z: " + vel.getY());
		dist.addVectorWithTime(vel, timeChange);*/
	}
}
