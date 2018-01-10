
package org.usfirst.frc.team4999.robot;

import org.usfirst.frc.team4999.lights.BrightnessFilter;
import org.usfirst.frc.team4999.robot.choosers.*;
import org.usfirst.frc.team4999.utils.MoPrefs;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
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
	MoPrefs moprefs; // Used to retrieve values from the preferences. Preferred over direct access to the preferences object
	
	// Controllers used to receive input from the driver.
	private BetterFlightStick flightStick;
	private BetterXBoxController xboxController;
	
	// Motors
	private VictorSP winch;
	
	// Drive System
	DriveSystem drive;
	
	// Boolean used to invert the front/back of the robot.
	boolean isInverted = false; // True if inverted
	
	
	// Piston used to deploy gears.
	DoubleSolenoid piston;
	
	// Timer to track time, so that the gear is held out for half a second
	Timer gear;
	
	// The two cameras connected to the RoboRio.
	UsbCamera cam1;
	
	// Values to store user input
	double moveRequest,turnRequest;
	
	// Sendable choosers for test mode, pid values, auto mode, and drive controller
	TestChooser testMode;
	TurnPIDChooser turnPIDChooser;
	AutoModeChooser autoMode;
	DriveModeChooser driveMode;
	
	//PowerDistributionPanel pdp;
	
	LightsChooser lightchooser;
	

	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		
		prefs = Preferences.getInstance();
		
		// Preferences
		moprefs = new MoPrefs();
		
		// DriveSystem
		drive = new DriveSystem(2,3,0,1);
		
		// Motor
		winch = new VictorSP(5);
		
		// Piston for gear placement
		piston = new DoubleSolenoid(2,0);
		
		// xboxcontroller for other controller
		 xboxController = new BetterXBoxController(0);
		
		// Flight stick for user input
		flightStick = new BetterFlightStick(1);
		flightStick.setDeadzoneY(0.15);
		flightStick.setDeadzoneTwist(0.20);
		
		// Start the timer so that it's ready to count
		gear = new Timer();
		gear.start();
		
		// Begin capturing video from the cameras and streaming it back to the smartDashboard
		cam1 = CameraServer.getInstance().startAutomaticCapture("DriverView", 0);
		cam1.setFPS(moprefs.getFPS());
		
		//Initialize the choosers
		testMode = new TestChooser();
		turnPIDChooser = new TurnPIDChooser(drive.turnCont);
		autoMode = new AutoModeChooser();
		driveMode = new DriveModeChooser();
		lightchooser = new LightsChooser();
		
		//pdp = new PowerDistributionPanel();
		//LiveWindow.add(pdp);
		
		BrightnessFilter.register();
		
		
	}

	// If we get disabled, stop all the motors and pid controllers
	public void disabledInit() {
		drive.STOP();
	}
	
	boolean doOnce = true; // It would be smarter to just have all the auto code in the init method, but I'm not sure if you're allowed to write to the motors in the init
	public void autonomousInit() {
		System.out.println("Beginning auto");
		doOnce = true;
	}

	/**
	 * This method runs in a loop during autonomous mode.
	 */
	public void autonomousPeriodic() {
		if(doOnce) {
			System.out.println("Switching automode");
			doOnce = false;
			switch(autoMode.getSelected()) { // get the selected autonomous mode
			case left: // Left and Right both move forward an amount specified in the preferences, turn the number of degrees specified in the preferences (usually around 60), then move for the time specified in the preferences
				drive.blockingMoveDistance(moprefs.getLMoveBeforeTurn(), 1, 0.1);
				drive.blockingTurn(-1 * moprefs.getTurn(), true);
				drive.blockingMoveTime(moprefs.getMoveForTime(), 1, 0.1);
				break;
			case center: // center just moves for the time specified in the preferences
				drive.blockingMoveTime(moprefs.getMoveForTime(), 1, 0.05);
				break;
			case right: // Same as left
				drive.blockingMoveDistance(moprefs.getRMoveBeforeTurn(), 1, 0.1);
				drive.blockingTurn(moprefs.getTurn(), true);
				drive.blockingMoveTime(moprefs.getMoveForTime(), 1, 0.1);
				break;
			case fallbackDistance:
				drive.blockingMoveDistance(3, 1, 0.1);
				break;
			case fallbackTime:
				drive.blockingMoveTime(5, 1, 0.1);
				break;
			default:
				break;
			}
		}
		
	}
	
	public void teleopInit() {
		// Get the deadzone and curves set in the robot preferences 
		// This allows us to fine tune the feel of the robot according to the driver's preferences
		xboxController.setDeadzoneX(Hand.kRight, moprefs.getXboxDeadzone());
		xboxController.setCurveX(Hand.kRight, moprefs.getXboxCurve());
		xboxController.setDeadzoneY(Hand.kLeft, moprefs.getXboxDeadzone());
		xboxController.setCurveY(Hand.kLeft, moprefs.getXboxCurve());
	}
	
	public void teleopPeriodic() {
		double speedLimiter;
		switch(driveMode.getSelected()) { // get the selected controlelr
			case tankDrive:
				// get the position of the sticks on the xbox controller
				moveRequest = xboxController.getY(BetterXBoxController.Hand.kLeft);
				turnRequest = xboxController.getX(BetterXBoxController.Hand.kRight);
				
				break;
			case arcadeDrive:
			default:
				
				// The input from the driver. Deadzones are used to make the robot less twitchy.
				moveRequest = -flightStick.getCalibratedY();
				turnRequest = flightStick.getCalibratedTwist();
		}
		
		// Allow the driver to switch back and front.
		moveRequest = (isInverted)? -moveRequest: moveRequest;
		
		// Throttle
		// speedLimiter = (-flightStick.getThrottle() + 1) / 2;
		speedLimiter = moprefs.getThrottle();
		
		// Write the move and turn request calculated to the drive system
		drive.arcadeDrive(moveRequest, turnRequest, speedLimiter);	
		
		// Drive the winch
		if(xboxController.getBumper(Hand.kLeft) || flightStick.getRawButton(5)) {
			xboxController.removeRumble("Winch Right");
			xboxController.addRumble("Winch Left", RumbleType.kLeftRumble, 0.5);
			
			lightchooser.pushAnimation("winch F", lightchooser.whiteSnake);
			
			winch.set(1);
		} else if(xboxController.getBumper(Hand.kRight) || flightStick.getRawButton(3)){
			xboxController.removeRumble("Winch Left");
			xboxController.addRumble("Winch Right", RumbleType.kRightRumble, 0.5);
			
			lightchooser.pushAnimation("winch F", lightchooser.whiteSnake);
			
			winch.set(0.5);
		} else if(xboxController.getYButton() || flightStick.getRawButton(6)) {
			xboxController.addRumble("Winch Left", RumbleType.kLeftRumble, 0.5);
			xboxController.addRumble("Winch Right", RumbleType.kRightRumble, 0.5);
			
			lightchooser.pushAnimation("winch R", lightchooser.reverseWhiteSnake);
			
			winch.set(-0.25);
		} else {
			xboxController.removeRumble("Winch Right");
			xboxController.removeRumble("Winch Left");
			
			lightchooser.popAnimation("winch F");
			lightchooser.popAnimation("winch R");
			
			winch.set(0);
		}
		
		// Switch front and back on the push of button 2 or X.
		if(flightStick.isFirstPush(2) || xboxController.isFirstPushX()){
			isInverted = !isInverted;
		}
		
		
		// Drive the gear.
		if( (flightStick.getRawButton(1) && (flightStick.getRawButton(7) || flightStick.getRawButton(8)) ) || xboxController.getBButton()){
			xboxController.addRumble("Gear", RumbleType.kRightRumble, 0.9);
			gear.reset();
			lightchooser.pushAnimation("Gear", lightchooser.blinkRed);
			piston.set(DoubleSolenoid.Value.kForward);
		} else if(piston.get() == DoubleSolenoid.Value.kForward) {
			xboxController.setRumble(RumbleType.kRightRumble, 0.6);
			xboxController.addRumble("Gear", RumbleType.kRightRumble, 0.6);
		}
		if(gear.hasPeriodPassed(0.75)){
			xboxController.removeRumble("Gear");
			lightchooser.popAnimation("Gear");
			piston.set(DoubleSolenoid.Value.kReverse);
		}
	}
	
	public void testInit(){
		switch(testMode.getSelected()) {
		case encoders:
			break;
		case auto_turn:
			break;
		case rotate:
			break;
		case xbox:
			xboxController.setDeadzoneX(Hand.kRight, moprefs.getXboxDeadzone());
			xboxController.setCurveX(Hand.kRight, moprefs.getXboxCurve());
			xboxController.setDeadzoneY(Hand.kLeft, moprefs.getXboxDeadzone());
			xboxController.setCurveY(Hand.kLeft, moprefs.getXboxCurve());
			break;
		case pdp:
			teleopInit();
			break;
		default:
			break;
		}
	}
	
	public void testPeriodic() {
		switch(testMode.getSelected()) {
		case encoders:
			System.out.format("Left count: %d, dist: %.2f    Right count: %d, dist: %.2f\n", drive.left.get(), drive.left.getDistance(), drive.right.get(), drive.right.getDistance());
			if(flightStick.isFirstPush(8)) {
				drive.left.reset();
				drive.right.reset();
			}
			teleopPeriodic();
			break;
		case auto_turn:
			// Turn 45 degs
			if(flightStick.isFirstPush(1)) {
				drive.turn(moprefs.getTestTurn(), true);
			}
			// Write the set pid values
			if(flightStick.isFirstPush(8)) {
				drive.writeTurnPIDValues();
			}
			// Stop pid
			if(flightStick.isFirstPush(7)) {
				System.out.println("Trying to stop pid");
				drive.maintainCurrentHeading(false);
			}
			break;
		case rotate:
			// USE getAngleZ IN PID
			System.out.format("Encoder angle: %.2f\n", drive.getEncAngle());
			System.out.format("Angle X: %.2f  AngleY: %.2f  AngleZ: %.2f\n",drive.adis.getAngleX(),drive.adis.getAngleY(),drive.adis.getAngleZ());
			System.out.format("Complementary angle: %.2f\n", drive.adis.getAngle());
			System.out.println("---------------------------------------");
			if(flightStick.isFirstPush(12)) {
				drive.adis.calibrate();
			}
			if(flightStick.isFirstPush(8)) {
				drive.left.reset();
				drive.right.reset();
			}
			teleopPeriodic();
			break;
		case xbox:
			moveRequest = xboxController.getY(BetterXBoxController.Hand.kRight);
			turnRequest = xboxController.getX(BetterXBoxController.Hand.kLeft);
			
			System.out.format("Move: %.2f   Turn: %.2f\n", moveRequest, turnRequest);
			break;
		case pdp:
			teleopPeriodic();
			break;
		default:
			break;
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
