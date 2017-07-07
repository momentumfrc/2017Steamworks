package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class TestChooser extends SendableChooser<String> {
	TestChooser(){
		super();
		addDefault("Shooter", "shooter");
		addObject("Autonomous","auto");
	}
}
