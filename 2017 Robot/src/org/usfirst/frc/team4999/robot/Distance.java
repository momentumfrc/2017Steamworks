package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import org.usfirst.frc.analog.adis16448.*;

import Vector.Vector2D;

public class Distance {
	double[] accelsX;
	double[] accelsY;
	double[] accelsZ;
	Accelerometer builtIn; // = new BuiltInAccelerometer();
	ADXL362 ADXL; // = new ADXL362(Accelerometer.Range.k8G);
	ADIS16448_IMU adis; // = new ADIS16448_IMU();
	long time = System.currentTimeMillis();
	private double[] offsetX = {0,0}; // ADIS, BuiltIn
	private double[] offsetZ = {0,0};
	private boolean calibrate = true;
	
	private Vector2D accel = new Vector2D(0,0);
	private Vector2D vel = new Vector2D(0,0);
	private Vector2D dist = new Vector2D(0,0);
	
	/**
	 * Creates a new Distance object.
	 */
	public Distance(Accelerometer builtIn, ADXL362 ADXL, ADIS16448_IMU adis) {
		accelsX = new double[9];
		accelsY = new double[9];
		this.builtIn = builtIn;
		this.ADXL = ADXL;
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
		if(calibrate) {
			offsetX[0] = adis.getAccelX();
			offsetX[1] = builtIn.getX();
			offsetZ[0] = adis.getAccelZ();
			offsetZ[1] = builtIn.getZ();
			calibrate = false;
		}
			
		System.out.println("Builtin: X: " + builtIn.getX() + " Y: "+ builtIn.getY() + " Z: " + builtIn.getZ());
		//System.out.println("ADXL362: X: " + ADXL.getAcceleration(ADXL362.Axes.kX) + " Y: " + ADXL.getAcceleration(ADXL362.Axes.kY) + " Z: " + ADXL.getAcceleration(ADXL362.Axes.kZ));
		System.out.println("ADIS: X: " + adis.getAccelX() + " Y: " + adis.getAccelY() + " Z: "+ adis.getAccelZ());
		for(int i = 0; i < 6; i += 2) {
			accelsX[i] = builtIn.getX() - offsetX[1];
			accelsX[i+1] = -(adis.getAccelX() - offsetX[0]);
			accelsY[i] = builtIn.getZ() - offsetZ[1];
			accelsY[i+1] = -(adis.getAccelZ() - offsetZ[0]);
		}
		double[] averageAccels = new double[2];
		for(int i=0; i < 6; i++) {
			averageAccels[0] += accelsX[i];
			averageAccels[1] += accelsY[i];
		}
		averageAccels[0] = averageAccels[0] / 6;
		averageAccels[1] = averageAccels[1] / 6;
		System.out.println("Acceleration: X: " + averageAccels[0] + " Y: " + averageAccels[1] );
		for(int i = 0; i < 2; i++ ) {
			if(Math.abs(averageAccels[i]) < .15 ) {
				averageAccels[i] = 0;
			}
			// averageAccels[i] = averageAccels[i] * 9.81; // meters
			averageAccels[i] = averageAccels[i] * 32.174; // feet
		}
		accel.addToXY(averageAccels[0], averageAccels[1]);
		long timeChange = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		vel.addVectorWithTime(accel, timeChange);
		dist.addVectorWithTime(vel, timeChange);
	}
}
