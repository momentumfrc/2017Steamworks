package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Preferences;

public class beachBlitzAutoCode {
	double speedLimit = 0.2;
	
	Encoder right,left;
	driveSystem drive;
	Preferences prefs;
	
	beachBlitzAutoCode(driveSystem drive){
		prefs = Preferences.getInstance();
		
		// The max speed the bot can travel in autonomous mode
		if(!prefs.containsKey("AUTO_SPEED_LIMIT")) {
			prefs.putDouble("AUTO_SPEED_LIMIT", 0.2);
		}
		// The distance that corresponds to one pulse of the encoder
		if(!prefs.containsKey("AUTO_DIST_PER_PULSE")) {
			prefs.putDouble("AUTO_DIST_PER_PULSE", -1);
			
		}
		
		// The two encoders
		left = new Encoder(0,1);
		right = new Encoder(2,3);
		
		left.setDistancePerPulse(prefs.getDouble("AUTO_DIST_PER_PULSE", -1));
		right.setDistancePerPulse(prefs.getDouble("AUTO_DIST_PER_PULSE", -1));
		this.speedLimit = prefs.getDouble("AUTO_SPEED_LIMIT", 0.2);
		
		this.drive = drive;
	}
	
	/**
	 * Zeros the encoders
	 */
	public void reset() {
		left.reset();
		right.reset();
	}
	/**
	 *  Zeros a particular encoder
	 *  @param enc - The encoder to be zeroed. Either "left" or "right"
	 */
	public void reset(String enc) {
		switch(enc){
		case "left":
			left.reset();
			break;
		case "right":
			right.reset();
			break;
		default:
			System.err.println(enc + " is not a valid option. Please specify left or right");
		}
	}
	
	/**
	 * Moves the robot forward for a specified number of encoder ticks at full allowed speed
	 * @param ticks - The number of ticks to move
	 */
	public void moveTicks(int ticks){
		int target = ticks + left.get();
		while(left.get() < target) {
			drive.arcadeDrive(1, 0, speedLimit);
		}
		drive.stop();
	}
	/**
	 * Moves the robot forward for a specified number of encoder ticks at a specified speed
	 * @param ticks - The number of ticks to move
	 * @param speed - The speed at which to move
	 */
	public void moveTicks(int ticks, double speed){
		int target = ticks + left.get();
		while(left.get() < target) {
			drive.arcadeDrive(speed, 0, speedLimit);
		}
		drive.stop();
	}
	
	/**
	 * Asynchronously moves the robot forward
	 * @param ticks - The number of ticks to move
	 * @param speed - The speed at which to move
	 * @return A thread object running the move code. Movement may be interrupted by calling the method interrupt() on this object
	 */
	Thread asyncMoveTicks(int ticks, double speed) {
		Thread move = new Thread() {
			public void run() {
				int target = ticks + left.get();
				while(left.get() < target) {
					drive.arcadeDrive(speed, 0, speedLimit);
					if(Thread.interrupted()) {
						drive.stop();
						return;
					}
				}
				drive.stop();
			}
		};
		move.start();
		return move;
	}
	
	/**
	 * Moves the robot forward a specified distance
	 * @param dist - The distance to move
	 */
	void moveDist(double dist) {
		double target = dist + left.getDistance();
		while(left.getDistance() < target) {
			drive.arcadeDrive(1, 0, speedLimit);
		}
		drive.stop();
	}
	
	/**
	 * Moves the robot forward a specified distance at a specified speed
	 * @param dist - The distance to move
	 * @param speed - The speed at which to move
	 */
	public void moveDist(double dist, double speed) {
		double target = dist + left.getDistance();
		while(left.getDistance() < target) {
			drive.arcadeDrive(speed, 0, speedLimit);
		}
		drive.stop();
	}
	/**
	 * Asynchronously moves the robot forward a specified distance
	 * @param dist - The distance to move
	 * @param speed - The speed at which to move
	 * @return A thread object running the move code. Movement may be interrupted by calling the method interrupt() on this object
	 */
	Thread asyncMoveDist(double dist, double speed) {
		Thread move = new Thread() {
			public void run() {
				double target = dist + left.getDistance();
				while(left.getDistance() < target) {
					drive.arcadeDrive(speed, 0, speedLimit);
					if(Thread.interrupted()) {
						drive.stop();
						return;
					}
				}
				drive.stop();
			}
		};
		move.start();
		return move;
	}
	
	
	
}
