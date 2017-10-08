package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum DriveMode { tankDrive, arcadeDrive };

public class DriveModeChooser extends SendableChooser<DriveMode> {
	public DriveModeChooser() {
		addDefault("Arcade Drive", DriveMode.arcadeDrive);
		addObject("Tank Drive", DriveMode.tankDrive);
		
		SmartDashboard.putData("Drive Controller", this);
	}

}
