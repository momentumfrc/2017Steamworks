package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum TestMode {outreach, shooter, encoders, auto_turn, auto_move, rotate};

public class TestChooser extends SendableChooser<TestMode> {
	TestChooser(){
		super();
		addObject("Shooter", TestMode.shooter);
		addObject("Encoders", TestMode.encoders);
		addDefault("Autonomous Turn Pid", TestMode.auto_turn);
		addObject("Autonomous Move Pid", TestMode.auto_move);
		addObject("Rotation Values", TestMode.rotate);
		addObject("Outreach", TestMode.outreach);
		
		SmartDashboard.putData("Test Chooser", this);
	}
}
