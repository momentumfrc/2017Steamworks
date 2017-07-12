package org.usfirst.frc.team4999.robot;

import java.util.HashSet;

import edu.wpi.first.wpilibj.Joystick;

public class BetterFlightStick extends Joystick {
	HashSet<Integer> held;
	BetterFlightStick(int dev) {
		super(dev);
		held = new HashSet<Integer>();
	}
	
	public boolean isFirstPush(int button) {
		if(this.getRawButton(button)) {
			if(held.contains(button)) {
				return false;
			} else {
				held.add(button);
				return true;
			}
		} else {
			held.remove(button);
			return false;
		}
	}
	
}
