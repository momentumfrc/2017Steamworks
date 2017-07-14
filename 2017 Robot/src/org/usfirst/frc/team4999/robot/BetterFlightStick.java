package org.usfirst.frc.team4999.robot;

import java.util.HashSet;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

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
	
	public Thread pulseRumble(RumbleType type, double value, double sec) {
		Thread rumble = new Thread() {
			Timer len;
			public void run() {
				len = new Timer();
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
