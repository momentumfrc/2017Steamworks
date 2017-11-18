package org.usfirst.frc.team4999.robot.choosers;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class TestChooser extends SendableChooser<TestChooser.TestMode> {
	
	public static enum TestMode { encoders, auto_turn, rotate, xbox, pdp};
	
	public TestChooser(){
		super();
		addObject("Encoders", TestMode.encoders);
		addObject("Autonomous Turn Pid", TestMode.auto_turn);
		addObject("Rotation Values", TestMode.rotate);
		addObject("Controller", TestMode.xbox);
		addDefault("PDP Status", TestMode.pdp);
		
		SmartDashboard.putData("Test Chooser", this);
	}
}
