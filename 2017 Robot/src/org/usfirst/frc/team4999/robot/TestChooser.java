package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum TestMode { encoders, auto_turn, rotate, xbox, pdp};

public class TestChooser extends SendableChooser<TestMode> {
	TestChooser(){
		super();
		addObject("Encoders", TestMode.encoders);
		addObject("Autonomous Turn Pid", TestMode.auto_turn);
		addObject("Rotation Values", TestMode.rotate);
		addObject("Controller", TestMode.xbox);
		addDefault("PDP Status", TestMode.pdp);
		
		SmartDashboard.putData("Test Chooser", this);
	}
}
