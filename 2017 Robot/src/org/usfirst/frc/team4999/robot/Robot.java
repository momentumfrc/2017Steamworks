
package org.usfirst.frc.team4999.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;

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
	DefaultPreferences defaults;
	
	// Controllers used to receive input from the driver.
	private Joystick flightStick;
	private XboxController xboxController = new XboxController(0);
	
	// Motors
	private VictorSP intake, winch, shooterLeft, shooterRight;
	
	// Drive System
	driveSystem drive;
	
	// Booleans used to invert the front/back of the robot.
	boolean isInverted = false; // True if inverted
	boolean triggered2 = false; // True while the button is held down. Prevents rapid oscillation from back to front while the button is held down.
	
	//Boolean to disable outreach driving
	boolean outreachDisabled = false;
	boolean triggeredX = false;
	boolean triggeredY = false;
	
	// Piston used to deploy gears.
	DoubleSolenoid piston;
	
	// Ultrasonic sensor used to detect the distance to the closest object in front of the robot.
	Ultrasonic ultrasonic;
	
	// Timers. timer_gear ensures the piston is deployed for at least 0.75 seconds, and timer_auto ensures the robot drives for 5 seconds before stopping.
	long timer_gear, timer_auto, timer_outreach;
	
	// The two cameras connected to the RoboRio.
	UsbCamera cam1;
	Cam2 cam2;
	
	// Values to store user input
	double moveRequest,turnRequest;
	
	// Sendable chooser for test mode
	TestChooser testMode;
	
	// Autonomous code controller
	beachBlitzAutoCode autoCont;

	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		
		prefs = Preferences.getInstance();
		
		defaults.addKeys(new Object[][]{
			// AUTO_LEFT and AUTO_RIGHT are the values given to the tank drive for the left and right sides of the robot during autonomous.
			{"AUTO_LEFT", 1},
			{"AUTO_RIGHT", 1},
			// AUTO_MULT is the speed limiter during autonomous.
			{"AUTO_MULT", 0.25},
			// AUTO_TIME is the amount time the robot will move forward for.
			{"AUTO_TIME", 5000},
			// OUTREACH_TIME is the number of seconds allowed for outreach driving
			{"OUTREACH_TIME", 30},
			// OUTREACH_SPEED is the max speed of the outreach driving.
			{"OUTREACH_SPEED", 0.25},
			// OUTREACH_TURN is the max turn speed of the outreach driving.
			{"OUTREACH_TURN", 0.5},
			// The two pins the motors for the shooter are connected to
			{"SHOOTER_LEFT", 6},
			{"SHOOTER_RIGHT", 7}
		});
		
		// DriveSystem
		drive = new driveSystem(2,3,0,1);
		// Motors
		shooterRight = new VictorSP((int)prefs.getDouble("RIGHT", 7));
		shooterLeft = new VictorSP((int)prefs.getDouble("LEFT", 6));
		
		shooterLeft.setInverted(true);
		
		intake = new VictorSP(4);
		winch = new VictorSP(5);
		
		// The ultrasonic sensor, used to measure distance
		ultrasonic = new Ultrasonic(0,1);
		// Have the ultrasonic continuously measure data
		ultrasonic.setAutomaticMode(true);
		
		
		// Piston for gear placement
		piston = new DoubleSolenoid(2,0);
		
		// Flight stick for user input
		flightStick = new Joystick(1);
		
		// Initialize the timers to zero. This is probably not necessary.
		timer_gear = 0;
		timer_auto = 0;
		
		// Begin capturing video from the cameras and streaming it back to the smartDashboard
		cam1 = CameraServer.getInstance().startAutomaticCapture("DriverView", 0);
		
		// Put text on the second camera to show if the robot is reversed
		cam2 = new Cam2("ProcessedView",1);
		cam2.start();
		
		//Initialize the chooser
		testMode = new TestChooser();
		
		SmartDashboard.putData("Test Chooser", testMode);
		
		// Initialize the autonomous code controller
		autoCont = new beachBlitzAutoCode(drive);
	}

	public void disabledInit() {
		cam2.testProcess = false;
	}
	
	public void autonomousInit() {
		// Set the timer to the current time. We will use the difference between this time and the current time to calculate time elapsed.
		timer_auto = System.currentTimeMillis();
		cam2.testProcess = true;
	}

	/**
	 * This method runs in a loop during autonomous mode.
	 */
	public void autonomousPeriodic() {
		// We use the smartDashboard to fine-tune the values so that the robot drives in a straight line, for just the right amount of time, at just the right speed.
		
		// For AUTO_TIME milliseconds...
		if (System.currentTimeMillis() - timer_auto <= prefs.getDouble("AUTO_TIME", 5000)) {
			// Drive forward using the tank drive. Drive with AUTO_LEFT on the left and AUTO_RIGHT on the right. Speed limiter is AUTO_MULT.
			drive.tankDrive(prefs.getDouble("AUTO_LEFT",1),prefs.getDouble("AUTO_RIGHT", 1),prefs.getDouble("AUTO_MULT", 0.25));
		} else {
			// After the time has elapsed, don't move
			drive.tankDrive (0,0,0);
		}
			
	}
	
	public void teleopPeriodic() {
		

		// The input from the driver. Deadzones are used to make the robot less twitchy.
		moveRequest = deadzone(-flightStick.getY(), 0.15);
		turnRequest = deadzone(flightStick.getTwist(), 0.20);
		
		// Allow the driver to switch back and front.
		moveRequest = (isInverted)? -moveRequest: moveRequest;
		
		// Throttle
		double speedLimiter = (-flightStick.getThrottle() + 1) / 2;
		
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
		if(flightStick.getRawButton(2)){
			if(!triggered2){
				triggered2 = true;
				isInverted = !isInverted;
				cam2.reversed = isInverted;
			}
		} else {
			triggered2 = false;
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
			timer_gear = System.currentTimeMillis();
			piston.set(DoubleSolenoid.Value.kForward);
		}
		if(System.currentTimeMillis() - timer_gear > 750){
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
		case "shooter":
			break;
		case "auto":
			break;
		default:
			break;
		}
	}
	
	public void testPeriodic() {
		switch(testMode.getSelected()) {
		case "shooter":
			shooterPeriodic();
			break;
		case "auto":
			autoTestPeriodic();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Runs driving code that is modified to be safer for novices to drive. This could potentially be used for outreach by selling time driving the robot.
	 */
	void outreachInit() {
		timer_outreach = System.currentTimeMillis();
	}
	void outreachPeriodic() {
		if(!outreachDisabled) {
			moveRequest = deadzone(-flightStick.getY(), 0.15);
			turnRequest = map(deadzone(flightStick.getTwist(), 0.20),0,1,0,prefs.getDouble("OUTREACH_TURN", .5));
			
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
			if(flightStick.getRawButton(2)){
				if(!triggered2){
					triggered2 = true;
					isInverted = !isInverted;
					cam2.reversed = isInverted;
				}
			} else {
				triggered2 = false;
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
				timer_gear = System.currentTimeMillis();
				piston.set(DoubleSolenoid.Value.kForward);
			}
			if(System.currentTimeMillis() - timer_gear > 750){
				piston.set(DoubleSolenoid.Value.kReverse);
			}
		}
		if(System.currentTimeMillis() - timer_outreach > prefs.getDouble("OUTREACH_TIME",30) * 1000) {
			outreachDisabled = true;
			
		}
		
		// Disable by pushing X
		if(xboxController.getXButton()){
			if(!triggeredX){
				triggeredX = true;
				outreachDisabled = !outreachDisabled;
			}
		} else {
			triggeredX = false;
		}
		
		// Reset time by pushing Y
		if(xboxController.getYButton()) {
			if(!triggeredY){
				triggeredY = true;
				timer_outreach = System.currentTimeMillis();
			}
		} else {
			triggeredY = false;
		}
		if(outreachDisabled) {
			drive.stop();
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
	 * Test various autonomous methods
	 */
	void autoTestPeriodic() {
		autoCont.turn(45);
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
