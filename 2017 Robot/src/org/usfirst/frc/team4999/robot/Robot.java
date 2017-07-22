
package org.usfirst.frc.team4999.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;

/**
* The VM is configured to automatically run this class, and to call the
* functions corresponding to each mode, as described in the IterativeRobot
* documentation. If you change the name of this class or the package after
* creating this project, you must also update the manifest file in the resource
* directory.
*/

public class Robot extends IterativeRobot {
	
	// Used to store semi-permanent variables that can be easily changed via the smartdashboard. Good for tuning PID loops w/o having to change the code every time.
	Preferences prefs;
	DefaultPreferences dprefs;
	
	// Controllers used to receive input from the driver.
	private BetterFlightStick flightStick;
	private BetterXBoxController xboxController = new BetterXBoxController(0);
	
	// Motors
	private VictorSP intake, winch, shooterLeft, shooterRight;
	
	// Drive System
	DriveSystem drive;
	
	// Boolean used to invert the front/back of the robot.
	boolean isInverted = false; // True if inverted
	
	//Boolean to disable outreach driving
	boolean outreachDisabled = false;
	
	// Piston used to deploy gears.
	DoubleSolenoid piston;
	
	Timer gear, auto, outreach;
	
	// The two cameras connected to the RoboRio.
	UsbCamera cam1;
	Cam2 cam2;
	
	// Values to store user input
	double moveRequest,turnRequest;
	
	// Sendable chooser for test mode
	TestChooser testMode;
	

	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		
		prefs = Preferences.getInstance();
		
		dprefs = new DefaultPreferences();
		// AUTO_LEFT and AUTO_RIGHT are the values given to the tank drive for the left and right sides of the robot during autonomous.
		dprefs.addKey("AUTO_LEFT", 1);
		dprefs.addKey("AUTO_RIGHT", 1);
		// AUTO_MULT is the speed limiter during autonomous.
		dprefs.addKey("AUTO_MULT", 0.25);
		// AUTO_TIME is the amount time the robot will move forward for.
		dprefs.addKey("AUTO_TIME", 5);
		// OUTREACH_TIME is the number of seconds allowed for outreach driving
		dprefs.addKey("OUTREACH_TIME", 30);
		// OUTREACH_SPEED is the max speed of the outreach driving.
		dprefs.addKey("OUTREACH_SPEED", 0.25);
		// OUTREACH_TURN is the max turn speed of the outreach driving.
		dprefs.addKey("OUTREACH_TURN", 0.5);
		// The two pins the motors for the shooter are connected to
		dprefs.addKey("SHOOTER_LEFT", 6);
		dprefs.addKey("SHOOTER_RIGHT", 7);
		
		dprefs.addKey("TEST_TURN_PID_DEG", 45);
		
		// DriveSystem
		drive = new DriveSystem(2,3,0,1);
		// Motors
		shooterRight = new VictorSP(prefs.getInt("RIGHT", 7));
		shooterLeft = new VictorSP(prefs.getInt("LEFT", 6));
		
		shooterLeft.setInverted(true);
		
		intake = new VictorSP(4);
		winch = new VictorSP(5);
		
		// Piston for gear placement
		piston = new DoubleSolenoid(2,0);
		
		// Flight stick for user input
		flightStick = new BetterFlightStick(1);
		flightStick.setDeadzoneY(0.15);
		flightStick.setDeadzoneTwist(0.20);
		
		auto = new Timer();
		gear = new Timer();
		outreach = new Timer();
		auto.start();
		gear.start();
		outreach.start();
		
		// Begin capturing video from the cameras and streaming it back to the smartDashboard
		cam1 = CameraServer.getInstance().startAutomaticCapture("DriverView", 0);
		
		// Put text on the second camera to show if the robot is reversed
		cam2 = new Cam2("ProcessedView",1);
		cam2.start();
		
		//Initialize the chooser
		testMode = new TestChooser();
		
	}

	public void disabledInit() {
		cam2.testProcess = false;
		drive.STOP();
	}
	
	public void autonomousInit() {
		// Set the timer to the current time. We will use the difference between this time and the current time to calculate time elapsed.
		auto.reset();
		cam2.testProcess = true;
	}

	/**
	 * This method runs in a loop during autonomous mode.
	 */
	public void autonomousPeriodic() {
		// We use the smartDashboard to fine-tune the values so that the robot drives in a straight line, for just the right amount of time, at just the right speed.
		
		// For AUTO_TIME seconds...
		if (auto.hasPeriodPassed(prefs.getDouble("AUTO_TIME", 5))) {
			// Drive forward using the tank drive. Drive with AUTO_LEFT on the left and AUTO_RIGHT on the right. Speed limiter is AUTO_MULT.
			drive.tankDrive(prefs.getDouble("AUTO_LEFT",1),prefs.getDouble("AUTO_RIGHT", 1),prefs.getDouble("AUTO_MULT", 0.25));
		} else {
			// After the time has elapsed, don't move
			drive.stop();
		}
			
	}
	
	public void teleopPeriodic() {
		

		// The input from the driver. Deadzones are used to make the robot less twitchy.
		moveRequest = -flightStick.getCalibratedY();
		turnRequest = flightStick.getCalibratedTwist();
		
		// Allow the driver to switch back and front.
		moveRequest = (isInverted)? -moveRequest: moveRequest;
		
		// Throttle
		double speedLimiter = (-flightStick.getThrottle() + 1) / 2;
		
		drive.arcadeDrive(moveRequest, turnRequest, speedLimiter);
		
		// Drive the intake
		if(xboxController.getBumper(Hand.kLeft)){
			xboxController.setRumble(RumbleType.kRightRumble, 0);
			xboxController.setRumble(RumbleType.kLeftRumble, 0.5);
			
			intake.set(1);
		} else if(xboxController.getBumper(Hand.kRight)){
			xboxController.setRumble(RumbleType.kLeftRumble, 0);
			xboxController.setRumble(RumbleType.kRightRumble, 0.5);
			
			intake.set(-1);
		} else {
			xboxController.setRumble(RumbleType.kLeftRumble, 0);
			xboxController.setRumble(RumbleType.kRightRumble, 0);
			
			intake.set(0);
		}
		
		// Switch front and back on the push of button 2.
		if(flightStick.isFirstPush(2)){
			isInverted = !isInverted;
			cam2.reversed = isInverted;
		}
		
		// Drive the winch.
		if(flightStick.getRawButton(5)){
			winch.set(1);
		} else if(flightStick.getRawButton(3)) {
			winch.set(.50);
		} else if(flightStick.getRawButton(6)) {
			winch.set(-.25);
		}else {
			winch.set(0);
		}
		
		if(flightStick.getRawButton(7) && flightStick.getRawButton(8)) {
			flightStick.calibrateY();
			flightStick.calibrateTwist();
		}
		
		// Drive the gear.
		if( (flightStick.getRawButton(1) && (flightStick.getRawButton(7) || flightStick.getRawButton(8)) ) || xboxController.getBButton()){
			xboxController.setRumble(RumbleType.kRightRumble, 0.9);
			gear.reset();
			piston.set(DoubleSolenoid.Value.kForward);
		} else if(piston.get() == DoubleSolenoid.Value.kForward) {
			xboxController.setRumble(RumbleType.kRightRumble, 0.6);
		}
		if(gear.hasPeriodPassed(0.75)){
			xboxController.setRumble(RumbleType.kRightRumble, 0);
			piston.set(DoubleSolenoid.Value.kReverse);
		}
	}
	
	public void practiceInit() {
		outreachInit();
	}
	public void practicePeriodic() {
		outreachPeriodic();
	}
	
	public void testInit(){
		switch(testMode.getSelected()) {
		case shooter:
			break;
		case auto_turn:
			break;
		case auto_move:
			break;
		case adis:
			break;
		default:
			break;
		}
	}
	
	public void testPeriodic() {
		switch(testMode.getSelected()) {
		case shooter:
			shooterPeriodic();
			break;
		case auto_turn:
			if(flightStick.isFirstPush(1)) {
				drive.turn(prefs.getInt("TEST_TURN_PID_DEG", 45), true);
			}
			if(flightStick.isFirstPush(8)) {
				drive.writeTurnPIDValues();
			}
			if(flightStick.isFirstPush(7)) {
				System.out.println("Trying to stop pid");
				drive.maintainCurrentHeading(false);
			}
			break;
		case auto_move:
			if(flightStick.isFirstPush(1)) {
				drive.move(10, true);
			}
			if(flightStick.isFirstPush(8)) {
				drive.writeMovePIDValues();
			}
			if(flightStick.isFirstPush(7)) {
				System.out.println("Trying to stop pid");
				drive.maintainCurrentPosition(false);
			}
			break;
		case adis:
			// USE getAngleZ IN PID
			System.out.format("Angle X: %.2f  AngleY: %.2f  AngleZ: %.2f\n",drive.adis.getAngleX(),drive.adis.getAngleY(),drive.adis.getAngleZ());
			System.out.format("Complementary angle: %.2f\n", drive.adis.getAngle());
			System.out.println("---------------------------------------");
			if(flightStick.isFirstPush(12)) {
				drive.adis.calibrate();
			}
			teleopPeriodic();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Runs driving code that is modified to be safer for novices to drive. This could potentially be used for outreach by selling time driving the robot.
	 */
	void outreachInit() {
		outreach.reset();
	}
	void outreachPeriodic() {
		if(!outreachDisabled) {
			moveRequest = flightStick.getCalibratedY();
			turnRequest = map(flightStick.getCalibratedTwist(),0,1,0,prefs.getDouble("OUTREACH_TURN", .5));
			
			// Allow the driver to switch back and front.
			moveRequest = (isInverted)? -moveRequest: moveRequest;
			
			// Throttle
			double speedLimiter = map((-flightStick.getThrottle() + 1) / 2,0,1,0,prefs.getDouble("OUTREACH_SPEED",.25));
			
			drive.arcadeDrive(moveRequest, turnRequest, speedLimiter);
			
			// Drive the intake
			if(xboxController.getRawButton(5)){
				intake.set(1);
			} else if(xboxController.getRawButton(6)){
				intake.set(-1);
			} else {
				intake.set(0);
			}
			
			// Switch front and back on the push of button 2.
			if(flightStick.isFirstPush(2)){
				isInverted = !isInverted;
				cam2.reversed = isInverted;
			}
			
			// Drive the winch.
			if(flightStick.getRawButton(5)){
				winch.set(1);
			} else {
				winch.set(0);
			}

			if(flightStick.getRawButton(3)){
				winch.set(.50);
			}
			if(flightStick.getRawButton(6)){
				winch.set(-.25);
			}
			
			
			// Drive the gear.
			if( (flightStick.getRawButton(1) && (flightStick.getRawButton(7) || flightStick.getRawButton(8)) ) || xboxController.getBButton()){
				gear.reset();
				piston.set(DoubleSolenoid.Value.kForward);
			}
			if(gear.hasPeriodPassed(0.5)){
				piston.set(DoubleSolenoid.Value.kReverse);
			}
		} else {
			drive.stop();
		}
		if(outreach.hasPeriodPassed(prefs.getDouble("OUTREACH_TIME",30))) {
			outreachDisabled = true;
		}
		
		// Disable by pushing X
		if(xboxController.isFirstPushX()) {
			outreachDisabled = !outreachDisabled;
		}
		
		// Reset time by pushing Y
		if(xboxController.isFirstPushY()) {
			outreach.reset();
		}
	}
	
	/**
	 * Test the shooter
	 */
	void shooterPeriodic() {
		double throttle = 1-(flightStick.getThrottle()+1)/2;
		if(flightStick.getRawButton(12)){
			System.out.println("Throttle: " + throttle);
			shooterLeft.set(throttle);
			shooterRight.set(throttle);
		} else {
			shooterLeft.set(0);
			shooterRight.set(0);
		}
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
