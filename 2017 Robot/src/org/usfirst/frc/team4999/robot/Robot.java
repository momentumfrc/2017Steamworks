
package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.analog.adis16448.ADIS16448_IMU;

/**
* The VM is configured to automatically run this class, and to call the
* functions corresponding to each mode, as described in the IterativeRobot
* documentation. If you change the name of this class or the package after
* creating this project, you must also update the manifest file in the resource
* directory.
*/

/**This is a test comment*/
/** TEST THIS IS A TEST OF THE GIT */

public class Robot extends IterativeRobot {
	private Joystick flightStick;
	private VictorSP leftFront, leftBack, rightFront, rightBack;
	private CamServer server;
	private ADIS16448_IMU adis;
	public static final String SERVER_IP = "10.49.99.12";
	public static  final int SERVER_PORT = 5810;
	
	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		
		leftFront = new VictorSP(0);
		rightFront = new VictorSP(1);
		leftBack = new VictorSP(2);
		rightBack = new VictorSP(3);
		
		adis = new ADIS16448_IMU(ADIS16448_IMU.Axis.kX);
		adis.reset();
		adis.updateTable();
		
		
		server = new CamServer(SERVER_IP, SERVER_PORT);
		flightStick = new Joystick(0);
	}
	
	/**
	 * This method is run once at the beginning of the autonomous period.
	 */
	public void autonomousInit() {
		server = new CamServer(SERVER_IP, SERVER_PORT);
	}
	
	public void disabledInit() {
		server = new CamServer(SERVER_IP, SERVER_PORT);
		adis.reset();
	}
	
	/**
	 * This method runs in a loop during autonomous mode.
	 */
	public void autonomousPeriodic() {
		server.refresh();
		final int xErr = server.getXError();
		final int yErr = server.getYError();
		
		// Our image width is 160, so the error must be within -80 and 80 pixels.
		final double turnRequest = map(xErr, -80, 80, -1, 1);
		
		System.out.println("blobXError: " + xErr);
		System.out.println("turnRequest: " + turnRequest);
		
		arcadeDrive(0, turnRequest, 0.25);
	}
	
	/**
	 * This method runs in a loop during teleop mode.
	 */
	public void teleopPeriodic() {		
		final double moveRequest = deadzone(-flightStick.getY(), 0.15);
		final double turnRequest = deadzone(flightStick.getTwist(), 0.20);
		final double turnRateRequest = turnRequest * 45;
		final double speedLimiter = (-flightStick.getThrottle() + 1) / 2;
		final double rateX = -adis.getRateX();
		final double angleY = -adis.getAngleY();
		final double angleZ = -adis.getAngleZ();
		final double xRotationError = map(turnRateRequest - rateX, -45, 45, -1, 1);
		final double getYaw = adis.getYaw();
		final double getRoll = adis.getRoll();
		final double xAcceleration = adis.getAccelX();
		//final double antiTipError = map(angleY,)

		System.out.println("moveRequest: " + moveRequest);
		System.out.println("turnRequest: " + turnRequest);
		System.out.println("speedLimiter: " + speedLimiter);
		System.out.println("rateX: " + rateX);
		System.out.println("turnRateRequest: " + turnRateRequest);
		System.out.println("xRotationError: " + xRotationError);
		System.out.println("===================================");
		System.out.println("Y: " + angleY);
		System.out.println("------");
		System.out.println("Yaw: " + angleY);
		System.out.println("Y accel:" + adis.getAccelY());
		System.out.println(xAcceleration);

		arcadeDrive(moveRequest, xRotationError, speedLimiter);
		
	}
	
	/**
	 * This method runs in a loop during test mode.
	 */
	public void testPeriodic() {
		 // Code to write to the smart dashboard
		SmartDashboard.putNumber("X Acceleration", adis.getAccelX());
		SmartDashboard.putNumber("Y Acceleration", adis.getAccelY());
		SmartDashboard.putNumber("Z Acceleration", adis.getAccelZ());
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
	 * Maps a number from one range to another range.
	 *
	 * @param input The incoming value to convert into the proper range.
	 * @param minIn The lower bound of the input value's range.
	 * @param maxIn The upper bound of the input value's range.
	 * @param minOut The lower bound of the output's range.
	 * @param maxOut The upper bound of the output's range.
	 * @return The input value mapped to the given range.
	 */
	public static double map(double input, double minIn, double maxIn, double minOut, double maxOut) {
		return minOut + (maxOut - minOut) * ((input - minIn) / (maxIn - minIn));
	}
	
	/**
	 * Clips a value to be within a given range.
	 *
	 * @param input The value to clip.
	 * @param max The maximum possible value for the output.
	 * @param min The minimum possible value for the output.
	 * @return The input value, clipped to be within the given max and min range.
	 */
	public static double clip(double input, double max, double min) {
		if(input > max)
			return max;
		else if(input < min)
			return min;
		else
			return input;	
	}
	
	/**
	 * Takes the output value of a joystick axis, and applies a deadzone if it is within -0.1 and 0.1
	 *
	 * @param input The output value of the joystick axis.
	 * @return The joystick axis value, or 0 if the input value is within the deadzone.
	 */
	public static double deadzone(double input) {
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
	public static double deadzone(double input, double zone) {
		if(input < zone && input > -zone)
			return 0;
		else
			return input;
	}

}
