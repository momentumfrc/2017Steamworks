
package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;

/**
* The VM is configured to automatically run this class, and to call the
* functions corresponding to each mode, as described in the IterativeRobot
* documentation. If you change the name of this class or the package after
* creating this project, you must also update the manifest file in the resource
* directory.
*/
public class Robot extends IterativeRobot {
	Joystick flightStick;
	VictorSP leftFront, leftBack, rightFront, rightBack;
	//CamServer server;
	
	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		rightFront = new VictorSP(0);
		rightBack = new VictorSP(1);
		leftFront = new VictorSP(2);
		leftBack = new VictorSP(3);
		rightFront.setInverted(true);
		rightBack.setInverted(true);
		//server = new CamServer("10.49.99.12", 5810);
		flightStick = new Joystick(0);
	}
	
	/**
	 * This method is run once at the beginning of the autonomous period.
	 */
	public void autonomousInit() {
	
	}
	
	/**
	 * This method runs in a loop during autonomous mode.
	 */
	public void autonomousPeriodic() {
	
	}
	
	/**
	 * This method runs in a loop during teleop mode.
	 */
	public void teleopPeriodic() {
		double moveRequest = deadzone(-flightStick.getY(), 0.15);
		double turnRequest = deadzone(flightStick.getTwist(), 0.20);
		double speedLimiter = (-flightStick.getThrottle() + 1) / 2;
		
		arcadeDrive(moveRequest, turnRequest, speedLimiter);
	}
	
	/**
	 * This method runs in a loop during test mode.
	 */
	public void testPeriodic() {
	
	}
	
	/**
	 * Maps a number from one range to another range.
	 *
	 * @param input The incoming value to convert into the proper range.
	 * @param minIn The lower bound of the input value's range.
	 * @param maxIn The upper bound of the input value's range.
	 * @param minOut The lower bound of the output's range.
	 * @param maxOut The upper bound of the output's range.
	 * @return The input value mapped to the given range.
	 */
	private static double map(double input, double minIn, double maxIn, double minOut double maxOut) {
		return minOut + (maxOut - minOut) * ((input - minIn) / (maxIn - minIn));
	}
	
	/**
	 * Moves the robot in arcade-drive fashion with given joystick input. Input values are expected to be
	 * within the range of -1 and 1.
	 *
	 * @param moveRequest The value used to drive the robot forwards and backwards (usually y-axis of joystick).
	 * @param turnRequest The value used to turn the robot (usually x-axis or z-axis of the joystick).
	 * @param speedLimiter A multiplier used to slow down the robot. Set this to 1 for no limitation.
	 */
	private void arcadeDrive(double moveRequest, double turnRequest, double speedLimiter) {
		double leftDrive = speedLimiter * (moveRequest + turnRequest);
		double rightDrive = speedLimiter * (moveRequest - turnRequest);
		
		leftFront.set(leftDrive);
		leftBack.set(leftDrive);
		rightFront.set(rightDrive);
		rightBack.set(rightDrive);
	}
	
	/**
	 * Takes the output value of a joystick axis, and applies a deadzone if it is within -0.1 and 0.1
	 *
	 * @param input The output value of the joystick axis.
	 * @return The joystick axis value, or 0 if the input value is within the deadzone.
	 */
	private double deadzone(double input) {
		double zone = 0.1;
		if(input < zone && input > -zone)
			return 0;
		else
			return input;	
	}
	
	/**
	 * Takes the output value of a joystick axis, and applies a deadzone to it.
	 *
	 * @param input The output value of the joystick axis.
	 * @param zone The size of the deadzone.
	 * @return The joystick axis value, or 0 if the input value is within the deadzone.
	 */
	private double deadzone(double input, double zone) {
		if(input < zone && input > -zone)
			return 0;
		else
			return input;
	}

}
