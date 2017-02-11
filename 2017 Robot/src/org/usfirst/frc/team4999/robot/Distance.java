package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import org.usfirst.frc.analog.adis16448.*;

public class Distance {
	double[] accelsX;
	double[] accelsY;
	double[] accelsZ;
	Accelerometer builtIn = new BuiltInAccelerometer();
	Accelerometer ADXL362 = new ADXL362(Accelerometer.Range.k8G);
	ADIS16448_IMU adis = new ADIS16448_IMU();
	int count = 0;
	long time = System.currentTimeMillis();
	
	public Vector2D accel = new Vector2D(0,0);
	public Vector2D vel = new Vector2D(0,0);
	public Vector2D dist = new Vector2D(0,0);
	
	public void Distance() {
		accelsX = new double[9];
		accelsY = new double[9];
	}
	
	public void updateDistance() {
		for(int i = 0; i < 9; i += 3) {
			accelsX[i] = builtIn.getX();
			accelsX[i+1] = ADXL362.getX();
			accelsX[i+2] = adis.getAccelX();
			accelsY[i] = builtIn.getY();
			accelsY[i+1] = ADXL362.getY();
			accelsY[i+2] = adis.getAccelY();
		}
		double[] averageAccels = new double[2];
		for(int i=0; i < 9; i++) {
			averageAccels[0] += accelsX[i];
			averageAccels[1] += accelsY[i];
		}
		averageAccels[0] = averageAccels[0] / 9;
		averageAccels[1] = averageAccels[1] / 9;
		accel.addToXY(averageAccels[0], averageAccels[1]);
		long timeChange = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		vel.addVectorWithTime(accel, timeChange);
		dist.addVectorWithTime(vel, timeChange);
	}
}
