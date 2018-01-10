package org.usfirst.frc.team4999.robot.choosers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveModeChooser extends SendableChooser<DriveModeChooser.DriveMode> {
	
	public static enum DriveMode { XBOX, FLIGHTSTICK, F310 };
	
	public DriveModeChooser() {
		addObject("Flight Stick", DriveMode.FLIGHTSTICK);
		addObject("Logitech F310", DriveMode.F310);
		addDefault("XBox Controller", DriveMode.XBOX);
		
		SmartDashboard.putData("Drive Controller", this);
	}

}
