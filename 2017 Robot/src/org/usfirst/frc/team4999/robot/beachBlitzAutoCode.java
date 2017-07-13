package org.usfirst.frc.team4999.robot;

import org.usfirst.frc.analog.adis16448.ADIS16448_IMU;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Preferences;

public class beachBlitzAutoCode {
	double speedLimit = 0.2;
	
	Encoder right,left;
	driveSystem drive;
	Preferences prefs;
	ADIS16448_IMU adis;
	PIDController turnCont;
	
	beachBlitzAutoCode(driveSystem drive){
		prefs = Preferences.getInstance();
		DefaultPreferences dprefs = new DefaultPreferences();
		
		dprefs.addKeys(new Object[][] {
			// The max speed the bot can travel in autonomous mode
			{"AUTO_SPEED_LIMIT", 0.2},
			// The distance that corresponds to one pulse of the encoder
			{"AUTO_DIST_PER_PULSE", -1},
			// Kp, Ki, and Kd for turn PID
			{"AUTO_TURN_KP", 0},
			{"AUTO_TURN_KI", 0},
			{"AUTO_TURN_KD", 0},
			// Percent tolerance. If the turn PID is within this range, the pid will turn off
			{"AUTO_TURN_TOLERANCE", 10.0}
		});
		
		//Instantiate the adis
		adis = new ADIS16448_IMU();
		adis.setTiltCompYaw(false);
		adis.setPIDSourceType(PIDSourceType.kDisplacement);
		
		// The two encoders
		left = new Encoder(0,1);
		right = new Encoder(2,3);
		
		left.setDistancePerPulse(prefs.getDouble("AUTO_DIST_PER_PULSE", -1));
		right.setDistancePerPulse(prefs.getDouble("AUTO_DIST_PER_PULSE", -1));
		this.speedLimit = prefs.getDouble("AUTO_SPEED_LIMIT", 0.2);
		
		this.drive = drive;
		
		// Instantiate the turn PID
		this.turnCont = new PIDController(prefs.getDouble("AUTO_TURN_KP", 0), prefs.getDouble("AUTO_TURN_KI", 0), prefs.getDouble("AUTO_TURN_KD", 0),adis,drive);
		this.turnCont.setOutputRange(-1,1);
		this.turnCont.setPercentTolerance(prefs.getDouble("AUTO_TURN_TOLERANCE", 10.0));
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
		this.moveTicks(ticks, 1);
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
	public Thread asyncMoveTicks(int ticks, double speed) {
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
		this.moveDist(dist,1);
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
	public Thread asyncMoveDist(double dist, double speed) {
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
	
	/**
	 * Turns the robot the specified number of degrees
	 * @param deg - Number of degrees to turn
	 * @param debug - Print debug messages
	 */
	public void turn(double deg, boolean debug) {
		drive.setPIDMode(driveSystem.PIDTurn);
		turnCont.setSetpoint(adis.getAngle() + deg);
		if(debug)
			System.out.format("Beginning turn. Setpoint set to: %.2f\n", turnCont.getSetpoint());
		turnCont.enable();
		while(!turnCont.onTarget()){
			try {
				if(debug)
					System.out.format("Turn in progress! Current angle: %.2f. Using P:%.2f, I:%.2f, D:%.2f\n", adis.getAngle(), turnCont.getP(), turnCont.getI(), turnCont.getD());
				Thread.sleep(10);
			} catch (InterruptedException e) {
				break;
			}
		}
		turnCont.disable();
	}
	public void turn(double deg) {
		turn(deg, false);
	}
	
	/**
	 * Asynchronously turns the robot the specified number of degrees
	 * @param deg - Number of degrees to turn
	 * @param debug - Print debug messages
	 * @return A thread object running the turn code. Rotation may be interrupted by calling the method interrupt() on this object
	 */
	public Thread asyncTurn(double deg, boolean debug) {
		Thread turn = new Thread() {
			public void run() {
				drive.setPIDMode(driveSystem.PIDTurn);
				turnCont.setSetpoint(adis.getAngle() + deg);
				if(debug)
					System.out.format("Beginning turn. Setpoint set to: %.2f\n", turnCont.getSetpoint());
				turnCont.enable();
				while(!turnCont.onTarget()){
					try {
						Thread.sleep(10);
						if(debug)
							System.out.format("Turn in progress! Using P:%.2f, I:%.2f, D:%.2f\n", turnCont.getP(), turnCont.getI(), turnCont.getD());
						if(Thread.interrupted()) {
							throw new InterruptedException();
						}
					} catch (InterruptedException e) {
						break;
					}
					
				}
				turnCont.disable();
			}
		};
		turn.start();
		return turn;
	}
	public Thread asyncTurn(double deg) {
		return asyncTurn(deg, false);
	}
	
	public void writePIDValues() {
		prefs.putDouble("AUTO_TURN_KP", turnCont.getP());
		prefs.putDouble("AUTO_TURN_KI", turnCont.getI());
		prefs.putDouble("AUTO_TURN_KD", turnCont.getD());
	}
	
	
	
}
