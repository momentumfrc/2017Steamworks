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
	
	public void Distance() {
		accelsX = new double[9];
		accelsY = new double[9];
		accelsZ = new double[9];
	}
	
	void updateDistance() {
		for(int i = 0; i < 9; i += 3) {
			accelsX[i] = builtIn.getX();
			accelsX[i+1] = ADXL362.getX();
			accelsX[i+2] = adis.getAccelX();
			accelsY[i] = builtIn.getY();
			accelsY[i+1] = ADXL362.getY();
			accelsY[i+2] = adis.getAccelY();
			accelsZ[i] = builtIn.getZ();
			accelsZ[i+1] = ADXL362.getZ();
			accelsZ[i+2] = adis.getAccelZ();
		}
		
	}
}
