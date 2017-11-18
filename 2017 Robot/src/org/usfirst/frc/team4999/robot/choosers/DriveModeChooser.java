package org.usfirst.frc.team4999.robot.choosers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveModeChooser extends SendableChooser<DriveModeChooser.DriveMode> {
	
	public static enum DriveMode { tankDrive, arcadeDrive };
	
	public DriveModeChooser() {
		addObject("Flight Stick", DriveMode.arcadeDrive);
		addDefault("XBox Controller", DriveMode.tankDrive);
		
		SmartDashboard.putData("Drive Controller", this);
	}

}
