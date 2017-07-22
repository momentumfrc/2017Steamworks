package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum TestMode {shooter, auto_turn, auto_move, adis};

public class TestChooser extends SendableChooser<TestMode> {
	TestChooser(){
		super();
		addDefault("Shooter", TestMode.shooter);
		addObject("Autonomous Turn Pid", TestMode.auto_turn);
		addObject("Autonomous Move Pid", TestMode.auto_move);
		addObject("Adis values", TestMode.adis);
		
		SmartDashboard.putData("Test Chooser", this);
	}
}
