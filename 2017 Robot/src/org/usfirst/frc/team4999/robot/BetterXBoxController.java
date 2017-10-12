package org.usfirst.frc.team4999.robot;

import java.util.HashSet;
import java.util.HashMap;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

class RumbleValue {
	public XboxController.RumbleType hand;
	public double value;
	public RumbleValue(XboxController.RumbleType hand, double value) {
		this.hand = hand;
		this.value = value;
	}
}

public class BetterXBoxController extends XboxController {
	HashSet<Integer> held;
	HashMap<String, RumbleValue> rumbles;
	HashMap<Integer, Double> deadzones;
	HashMap<Integer, Double> curves;
	
	public BetterXBoxController(int port) {
		super(port);
		held = new HashSet<Integer>();
		rumbles = new HashMap<String, RumbleValue>();
		deadzones = new HashMap<Integer, Double>();
		curves = new HashMap<Integer, Double>();
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
	
	public boolean isFirstPushA() {
		return isFirstPush(1);
	}
	public boolean isFirstPushB() {
		return isFirstPush(2);
	}
	public boolean isFirstPushX() {
		return isFirstPush(3);
	}
	public boolean isFirstPushY() {
		return isFirstPush(4);
	}
	public boolean isFirstPushStick(Hand hand) {
		if (hand.equals(Hand.kLeft)) {
	      return isFirstPush(9);
	    } else {
	      return isFirstPush(10);
	    }
	}
	public boolean isFirstPushBack() {
		return isFirstPush(7);
	}
	public boolean isFirstPushStart() {
		return isFirstPush(8);
	}
	
	@Override
	public double getRawAxis(int axis) {
		double value = super.getRawAxis(axis);
		if(deadzones.containsKey(axis) && Math.abs(value) < deadzones.get(axis))
			value = 0;
		if(curves.containsKey(axis))
			value = expCurve(value, curves.get(axis));
		return value;
	}
	
	public void setDeadzone(int axis, double value) {
		deadzones.put(axis, value);
	}
	public void setDeadzoneX(Hand hand, double value) {
		switch(hand) {
		case kRight:
			deadzones.put(4, value);
			break;
		case kLeft:
			deadzones.put(0, value);
			break;
		}
	}
	public void setDeadzoneY(Hand hand, double value) {
		switch(hand) {
		case kRight:
			deadzones.put(5, value);
			break;
		case kLeft:
			deadzones.put(1, value);
			break;
		}
	}
	
	public void setCurve(int axis, double value) {
		curves.put(axis,  value);
	}
	
	public void setCurveX(Hand hand, double value) {
		switch(hand) {
		case kRight:
			curves.put(4, value);
			break;
		case kLeft:
			curves.put(0, value);
			break;
		}
	}
	public void setCurveY(Hand hand, double value) {
		switch(hand) {
		case kRight:
			curves.put(5, value);
			break;
		case kLeft:
			curves.put(1, value);
			break;
		}
	}
	
	public void refreshRumbles() {
		double lHandRumble = 0;
		double rHandRumble = 0;
		for(String key: rumbles.keySet()) {
			RumbleValue val = rumbles.get(key);
			switch(val.hand) {
			case kRightRumble:
				lHandRumble = (val.value > lHandRumble)? val.value: lHandRumble;
				break;
			case kLeftRumble:
				rHandRumble = (val.value > rHandRumble)? val.value: rHandRumble;
				break;
			}
		}
		setRumble(RumbleType.kLeftRumble, lHandRumble);
		setRumble(RumbleType.kRightRumble, rHandRumble);
	}
	public void addRumble(String name, RumbleType hand, double value) {
		rumbles.put(name, new RumbleValue(hand, value));
		refreshRumbles();
	}
	public void removeRumble(String name) {
		rumbles.remove(name);
		refreshRumbles();
	}
	
	private double expCurve(double input, double pow) {
		if(input == 0)
			return input;
		if(pow % 2 == 0)
			return (input / Math.abs(input)) * Math.pow(input, pow);
		else
			return Math.pow(input, pow);
	}

}
