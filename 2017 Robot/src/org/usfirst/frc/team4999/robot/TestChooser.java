package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TestChooser extends SendableChooser<String> {
	TestChooser(){
		super();
		addDefault("Shooter", "shooter");
		addObject("Autonomous Turn Pid","auto_turn");
		addObject("Autonomous Move Pid","auto_move");
		addObject("Adis values", "adis");
		
		SmartDashboard.putData("Test Chooser", this);
	}
}
