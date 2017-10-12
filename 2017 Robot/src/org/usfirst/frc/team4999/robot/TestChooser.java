package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum TestMode { encoders, auto_turn, rotate, xbox};

public class TestChooser extends SendableChooser<TestMode> {
	TestChooser(){
		super();
		addObject("Encoders", TestMode.encoders);
		addDefault("Autonomous Turn Pid", TestMode.auto_turn);
		addObject("Rotation Values", TestMode.rotate);
		addObject("Controller", TestMode.xbox);
		
		SmartDashboard.putData("Test Chooser", this);
	}
}
