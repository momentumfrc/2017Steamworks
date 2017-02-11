package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import org.usfirst.frc.analog.adis16448.*;

public class Distance {
	Accelerometer builtIn = new BuiltInAccelerometer();
	Accelerometer ADXL362 = new ADXL362(Accelerometer.Range.k8G);
	ADIS16448_IMU adis = new ADIS16448_IMU();
}
