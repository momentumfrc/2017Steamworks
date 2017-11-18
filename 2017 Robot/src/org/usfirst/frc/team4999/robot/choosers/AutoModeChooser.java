package org.usfirst.frc.team4999.robot.choosers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class AutoModeChooser extends SendableChooser<AutoModeChooser.AutoMode> {
	
	public static enum AutoMode { left, center, right, fallbackDistance, fallbackTime };
	
	public AutoModeChooser() {
		super();
		addDefault("Time-Based Fallback", AutoMode.fallbackTime);
		addObject("Distance-Based Fallback", AutoMode.fallbackDistance);
		addObject("Left Auto", AutoMode.left);
		addObject("Center Auto", AutoMode.center);
		addObject("Right Auto", AutoMode.right);
		
		SmartDashboard.putData("Auto Mode Chooser", this);
	}
}
