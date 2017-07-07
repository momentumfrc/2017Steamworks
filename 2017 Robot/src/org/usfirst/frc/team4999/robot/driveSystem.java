package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.VictorSP;

public class driveSystem {
	VictorSP leftFront, leftBack, rightFront, rightBack;
	
	/**
	 * Initiates the robot drive system object
	 */
	driveSystem(int leftFront, int leftBack, int rightFront, int rightBack) {
		this.leftFront = new VictorSP(leftFront);
		this.leftBack = new VictorSP(leftBack);
		this.rightFront = new VictorSP(rightFront);
		this.rightBack = new VictorSP(rightBack);
		
		this.rightBack.setInverted(true);
		this.rightFront.setInverted(true);
	}
	/**
	 * Initiates the robot drive system object
	 */
	driveSystem(VictorSP leftFront, VictorSP leftBack, VictorSP rightFront, VictorSP rightBack) {
		this.leftFront = leftFront;
		this.leftBack = leftBack;
		this.rightFront = rightFront;
		this.rightBack = rightBack;
	}
	/**
	 * Moves the robot in arcade-drive fashion with given joystick input. Input values are expected to be
	 * within the range of -1 and 1.
	 *
	 * @param moveRequest The value used to drive the robot forwards and backwards (usually y-axis of joystick).
	 * @param turnRequest The value used to turn the robot (usually x-axis or z-axis of the joystick). Positive is left.
	 * @param speedLimiter A multiplier used to slow down the robot. Set this to 1 for no limitation.
	 */
	public void arcadeDrive(double moveRequest, double turnRequest, double speedLimiter) {
		double leftDrive = speedLimiter * (moveRequest + turnRequest);
		double rightDrive = speedLimiter * (moveRequest - turnRequest);

		leftFront.set(leftDrive);
		leftBack.set(leftDrive);
		rightFront.set(rightDrive);
		rightBack.set(rightDrive);
	}
	
	/**
	 * Moves the robot in a tank-drive fashion according to given inputs. All values are within the range of [-1,1].
	 * @param left The value to drive the left side
	 * @param right The value to drive the right side
	 * @param multiplier The speed multiplier
	 */
	
	public void tankDrive(double left, double right, double multiplier){
		leftFront.set(left * multiplier);
		leftBack.set(left * multiplier);
		rightFront.set(right * multiplier);
		rightBack.set(right * multiplier);
	}
	
	/**
	 * Stops the robot
	 */
	public void stop() {
		leftFront.set(0);
		leftBack.set(0);
		rightFront.set(0);
		rightBack.set(0);
	}
}
