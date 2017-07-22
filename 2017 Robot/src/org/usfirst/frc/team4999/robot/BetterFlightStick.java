package org.usfirst.frc.team4999.robot;

import java.util.HashMap;
import java.util.HashSet;

import edu.wpi.first.wpilibj.Joystick;



public class BetterFlightStick extends Joystick {
	HashSet<Integer> held;
	HashMap<Integer, Double> calibrations;
	HashMap<Integer, Double> deadzones;
	
	BetterFlightStick(int dev) {
		super(dev);
		held = new HashSet<Integer>();
		calibrations = new HashMap<Integer, Double>();
		deadzones = new HashMap<Integer, Double>();
	}
	
	public double getDeadAxis(int axis) {
		final double val = getRawAxis(axis);
		if(deadzones.containsKey(axis)) {
			return deadzone(val, deadzones.get(axis));
		} else {
			return val;
		}
	}
	public void setDeadzone(int axis, double deadzone) {
		deadzones.put(axis, deadzone);
	}
	
	public double calibrateAxis(int axis) {
		final double val = getRawAxis(axis);
		calibrations.put(axis, val);
		return val;
	}
	
	public double getCalibratedAxis(int axis) {
		if(calibrations.containsKey(axis)) {
			final double val = getRawAxis(axis) - calibrations.get(axis);
			if(deadzones.containsKey(axis)) {
				return deadzone(val, deadzones.get(axis));
			} else {
				return val;
			}
		} else {
			return getDeadAxis(axis);
		}
	}
	
	public void setDeadzoneX(double deadzone) {
		setDeadzone(0, deadzone);
	}
	public void setDeadzoneY(double deadzone) {
		setDeadzone(1, deadzone);
	}
	public void setDeadzoneZ(double deadzone) {
		setDeadzone(2, deadzone);
	}
	public void setDeadzoneTwist(double deadzone) {
		setDeadzoneZ(deadzone);
	}
	
	public double calibrateX() {
		return calibrateAxis(0);
	}
	public double calibrateY() {
		return calibrateAxis(1);
	}
	public double calibrateZ() {
		return calibrateAxis(2);
	}
	public double calibrateTwist() {
		return calibrateZ();
	}
	
	
	public double getCalibratedX() {
		return getCalibratedAxis(0);
	}
	public double getCalibratedY() {
		return getCalibratedAxis(1);
	}
	public double getCalibratedZ() {
		return getCalibratedAxis(2);
	}
	public double getCalibratedTwist() {
		return getCalibratedZ();
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
	
	private double deadzone(double input, double zone) {
		if(input < zone && input > -zone)
			return 0;
		else
			return input;
	}
}
