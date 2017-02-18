
package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;

import org.usfirst.frc.analog.adis16448.ADIS16448_IMU;


/**
* The VM is configured to automatically run this class, and to call the
* functions corresponding to each mode, as described in the IterativeRobot
* documentation. If you change the name of this class or the package after
* creating this project, you must also update the manifest file in the resource
* directory.
*/

public class Robot extends IterativeRobot {
	private Joystick flightStick;
	private Joystick xboxController;
	private VictorSP leftFront, leftBack, rightFront, rightBack, shooter, intake, helix, winch;
	private CamServer server;
	String autoSelected;
	// <Accelerometers>
	private ADIS16448_IMU adis = new ADIS16448_IMU(ADIS16448_IMU.Axis.kX);
	private Accelerometer builtIn = new BuiltInAccelerometer();
	//private ADXL362 ADXL =  new ADXL362(Accelerometer.Range.k8G); //commented out until we get it working
	//</Accelerometers>
	public static final String SERVER_IP = "10.49.99.12";
	public static  final int SERVER_PORT = 5810;
	boolean isInverted = false;
	Distance trackDistance;
	final String right = "Right Side";
	final String left = "Left Side";
	final String middle = "Middle Side";
	Command autonomusCommand;
	SendableChooser autonomusChooser;
<<<<<<< HEAD
	
=======
	DigitalInput input;
	DigitalOutput output;
	DoubleSolenoid piston;
	Ultrasonic ultrasonic;
	Distance distance;
>>>>>>> branch 'master' of https://github.com/momentumfrc/2017Steamworks.git
	// test
	long timer;
	
	
	
	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		trackDistance = new Distance(builtIn, adis);
		leftFront = new VictorSP(0);
		rightFront = new VictorSP(1);
		leftBack = new VictorSP(2);
		rightBack = new VictorSP(3);
		shooter = new VictorSP(4);
		helix = new VictorSP(5);
		intake = new VictorSP(6);
		winch = new VictorSP(7);
		adis.reset();
		adis.updateTable();
		/**input = new DigitalInput(0);
		output = new DigitalOutput(1);*/
		ultrasonic = new Ultrasonic(0,1);
		//piston = new DoubleSolenoid(0,1);
		server = new CamServer(SERVER_IP, SERVER_PORT);
		flightStick = new Joystick(0);
		timer = 0;
		autonomusChooser = new SendableChooser();
		autonomusChooser.addObject("Right Side Of The Field", right);
		autonomusChooser.addObject("Left Side Of The Field", left);
		autonomusChooser.addObject("Middle Of The Field", middle);
		SmartDashboard.putData("Autonomus Mode Selector", autonomusChooser);

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
		//final double gearForwardErr = map(ultrasonic.getRangeInches(), 1, 4, -1, 1);
		
		System.out.println("blobXError: " + xErr);
		System.out.println("turnRequest: " + turnRequest);
		
		arcadeDrive(1, turnRequest, 0.25);
		
		Scheduler.getInstance().run();
		switch (autoSelected) {
		case left:
			// Add the code to turn the robot to the right then keep on going!
			// Code for the ultrasonic to stop the robot if we are too close.
			 /*if(ultrasonic.getRangeInches() > 4){
				timer = System.currentTimeMillis();
				leftFront.set(0);
				leftBack.set(0);
				rightFront.set(0);
				rightBack.set(0);
				if(timer > 250){
					if(ultrasonic.getRangeInches() < 4){
						arcadeDrive(1, 0, 0.25);
						timer = 0;
					}else{
						timer = System.currentTimeMillis();
						leftFront.set(0);
						leftBack.set(0);
						rightFront.set(0);
						rightBack.set(0);
					}
				}
			}
			*/
			break;
		case middle:
			// Add the code to make the robot continue on a straight vector then do the things it needs to do
			// Code for the ultrasonic to stop the robot if we are too close.
			/*if(ultrasonic.getRangeInches() > 4){
							timer = System.currentTimeMillis();
							leftFront.set(0);
							leftBack.set(0);
							rightFront.set(0);
							rightBack.set(0);
				if(timer > 250){
					if(ultrasonic.getRangeInches() < 4){
						timer = 0;
						arcadeDrive(1, 0, 0.25);
						}else{
							timer = System.currentTimeMillis();
							leftFront.set(0);
							leftBack.set(0);
							rightFront.set(0);
							rightBack.set(0);
						}
					}
				}
				*/
			break;
		case right:
			// Add the code to make the robot turn to the left then do the things it needs to do.
			// Code for the ultrasonic to stop the robot if we are too close.
				/*if(ultrasonic.getRangeInches() > 4){
							timer = System.currentTimeMillis();
							leftFront.set(0);
							leftBack.set(0);
							rightFront.set(0);
							rightBack.set(0);
					if(timer > 250){
						if(ultrasonic.getRangeInches() < 4){
							timer = 0;
							arcadeDrive(1, 0, 0.25);
						}else{
							timer = System.currentTimeMillis();
							leftFront.set(0);
							leftBack.set(0);
							rightFront.set(0);
							rightBack.set(0);
						}
					}
				}
				*/
			break;
		}
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
		
		if(xboxController.getRawAxis(3) == 1){
			shooter.set(.5);
		}
		if(xboxController.getRawAxis(2) == 1){
			intake.set(1);
		}
		if(xboxController.getRawButton(4)){
			intake.set(-1);
		}
		if(xboxController.getRawAxis(6) == -1){
			helix.set(-1);
		}
		if(xboxController.getRawAxis(6) == 1){
			helix.set(1);
		}
		if(xboxController.getRawAxis(1) == 1){
			winch.set(1);
		}
		if(flightStick.getRawButton(3)){
			isInverted =! isInverted;
		}
		if(isInverted){
			rightFront.setInverted(true);
			rightBack.setInverted(true);
			leftFront.setInverted(true);
			leftBack.setInverted(true);
		}
		/**if(flightStick.getRawButton(1)){
			if(flightStick.getRawButton(7) || flightStick.getRawButton(8)){
				piston.set(DoubleSolenoid.Value.kForward);
			}else{
				piston.set(DoubleSolenoid.Value.kReverse);
			}
		}*/
	}
	
	/**
	 * This method runs in a loop during test mode.
	 */
	
	public void gearPlacement(){
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
		double distance = ultrasonic.getRangeInches();
		arcadeDrive(moveRequest, xRotationError, speedLimiter);
		if(distance < 2){
			final int xErr = server.getXError();
			map(xErr, -80, 80, -1, 1);
			arcadeDrive(1, xErr, .25);
			if(xErr == 0){
				if(distance < 2){
					timer = System.currentTimeMillis();
					piston.set(DoubleSolenoid.Value.kForward);
					if(timer > .25){
						piston.set(DoubleSolenoid.Value.kReverse);
					}
				}
			}
		}
	}
	
	
	public void testPeriodic() {
		trackDistance.updateDistance();
		System.out.println("Get Distance: X:" + trackDistance.getDist().getX() + " Z: " + trackDistance.getDist().getY() /** Y is being misused on the vector class to hold Z*/ );
		System.out.println("");
		//System.out.println(ADXL362.getX());
		// Piston Code
		/**if(flightStick.getRawButton(1)){
			piston.set(DoubleSolenoid.Value.kForward);		
		}else{
			piston.set(DoubleSolenoid.Value.kOff);
		}*/
		
		
		
		
		
		/*trackDistance.updateDistance();
		 // Code to write to the smart dashboard
		SmartDashboard.putNumber("X Acceleration", adis.getAccelX());
		SmartDashboard.putNumber("Y Acceleration", adis.getAccelY());
		SmartDashboard.putNumber("Z Acceleration", adis.getAccelZ());*/
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
