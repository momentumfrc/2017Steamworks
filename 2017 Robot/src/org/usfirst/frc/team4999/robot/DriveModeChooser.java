package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

enum DriveMode { tankDrive, arcadeDrive };

public class DriveModeChooser extends SendableChooser<DriveMode> {
	public DriveModeChooser() {
		addDefault("Arcade Drive", DriveMode.arcadeDrive);
		addObject("Tank Drive", DriveMode.tankDrive);
	}

}
