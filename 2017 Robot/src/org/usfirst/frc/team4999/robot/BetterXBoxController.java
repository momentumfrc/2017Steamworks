package org.usfirst.frc.team4999.robot;

import java.util.HashSet;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

public class BetterXBoxController extends XboxController {
	HashSet<Integer> held;
	public BetterXBoxController(int port) {
		super(port);
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
	
	public Thread pulseRumble(RumbleType type, double value, double sec) {
		Thread rumble = new Thread() {
			Timer len;
			public void run() {
				len = new Timer();
				len.start();
				len.reset();
				setRumble(type, value);
				while(!len.hasPeriodPassed(sec)){
					try {
						Thread.sleep(10);
						if(Thread.interrupted())
							throw new InterruptedException();
					} catch (InterruptedException e) {
						break;
					}
				}
				setRumble(type, 0);
			}
		};
		rumble.start();
		return rumble;
	}

}
