package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoMode { left, center, right, fallbackDistance, fallbackTime };

public class AutoModeChooser extends SendableChooser<autoMode> {
	public AutoModeChooser() {
		super();
		addDefault("Time-Based Fallback", autoMode.fallbackTime);
		addObject("Distance-Based Fallback", autoMode.fallbackDistance);
		addObject("Left Auto", autoMode.left);
		addObject("Center Auto", autoMode.center);
		addObject("Right Auto", autoMode.right);
		
		SmartDashboard.putData("Auto Mode Chooser", this);
	}
}
