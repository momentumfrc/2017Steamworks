
package org.usfirst.frc.team4999.robot;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.first.wpilibj.ADXL362;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Servo;

import edu.wpi.first.wpilibj.Preferences;

import org.usfirst.frc.analog.adis16448.ADIS16448_IMU;

/**
* The VM is configured to automatically run this class, and to call the
* functions corresponding to each mode, as described in the IterativeRobot
* documentation. If you change the name of this class or the package after
* creating this project, you must also update the manifest file in the resource
* directory.
*/

public class Robot extends IterativeRobot {

	NetworkTable table;

	public static final int IMAGE_WIDTH = 160;
	public static final int IMAGE_HEIGHT = 120;

	Preferences prefs;

	int autoMode;
	private Joystick flightStick;
	private XboxController xboxController = new XboxController(0);
	private VictorSP leftFront, leftBack, rightFront, rightBack, shooter, intake, helix, winch;
	//private CamServer server;
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
	Command autonomusCommand;
	SendableChooser autonomusChooser;
	DigitalInput input;
	DigitalOutput output;
	DoubleSolenoid piston;
	Ultrasonic ultrasonic;
	Distance distance;
	// test
	boolean pid = true;
	long timer;
	Servo servo = new Servo(9);
	boolean foundTarget;
	double x1,x2,y1,y2,cX,cY,wL,hL,wR,hR;
	boolean failSafeAuto = false;
	//final String[] keys = {"HueMin","HueMax","SatMin","SatMax","ValMin","ValMax"};

	/**
	 * This method is run once when the robot is turned on.
	 */
	public void robotInit() {
		NetworkTable.setServerMode();
		NetworkTable.setTeam(4999);
		NetworkTable.initialize();
		prefs = Preferences.getInstance();

		// pixel values
		if (!prefs.containsKey("IMAGE_IDEAL_X"))
			prefs.putInt("IMAGE_IDEAL_X", 82);
		if (!prefs.containsKey("IMAGE_IDEAL_Y"))
			prefs.putInt("IMAGE_IDEAL_Y", 90);

		// inches value
		if (!prefs.containsKey("ENGAGE_DIST"))
			prefs.putInt("ENGAGE_DIST", 10);

		// pixel value
		if (!prefs.containsKey("ENGAGE_XERR"))
			prefs.putInt("ENGAGE_XERR", 5);

		trackDistance = new Distance(builtIn, adis);
		rightFront = new VictorSP(0);
		leftFront = new VictorSP(2);
		rightBack = new VictorSP(1);
		leftBack = new VictorSP(3);
		rightBack.setInverted(true);
		rightFront.setInverted(true);
		//shooter = new VictorSP(4);
		//helix = new VictorSP(5);
		intake = new VictorSP(4);
		winch = new VictorSP(5);
		adis.reset();
		adis.updateTable();
		/**input = new DigitalInput(0);
		output = new DigitalOutput(1);*/
		ultrasonic = new Ultrasonic(0,1);
		ultrasonic.setAutomaticMode(true);
		piston = new DoubleSolenoid(2,0);
		//server = new CamServer(SERVER_IP, SERVER_PORT);
		flightStick = new Joystick(1);
		timer = 0;
		autonomusChooser = new SendableChooser();
		SmartDashboard.putData("Autonomus Mode Selector", autonomusChooser);
		//SmartDashboard.putNumber("Smoothing", trackDistance.ALPHA);
		table = NetworkTable.getTable("visionTable");
		autonomusChooser.addObject("Left", 1);
		autonomusChooser.addObject("Middle", 2);
		autonomusChooser.addObject("Right", 3);
		autonomusChooser.addDefault("Failsafe", 4);
		autonomusChooser.addObject("Debug", 5);
		//SmartDashboard.putBoolean("autoFailSafe", false);
		SmartDashboard.putData("Autonomus Chooser", autonomusChooser);
		/*
		for(String key : keys) {
			SmartDashboard.putNumber(key, table.getNumber(key, -1));
		}
		*/
		CameraServer.getInstance().startAutomaticCapture("DriverView", 0);
	}

	/*void updateFilter() {
		for(String key : keys) {
			table.putNumber(key, SmartDashboard.getNumber(key, table.getNumber(key, -1)));
		}
	}*/

	/**
	 * This method is run once at the beginning of the autonomous period.
	 */
	public void autonomousInit() {
		adis.reset();
		timer = System.currentTimeMillis();
		System.out.println((int)autonomusChooser.getSelected());
	}

	public void disabledInit() {
		//server = new CamServer(SERVER_IP, SERVER_PORT);
		adis.reset();
		//String selected = (String) autonomusChooser.getSelected();
	}

	/**
	 * This method runs in a loop during autonomous mode.
	 */
	public void autonomousPeriodic() {


		//TERRY: Can you check this
		
		
			double distance = ultrasonic.getRangeInches();

			int moveRequest = (distance > 5)? 1 : 0;
			double turnRequest = (0.1);

			
			
			if (System.currentTimeMillis() - timer <= 5000)
				arcadeDrive(moveRequest, turnRequest, 0.25);
			else
				arcadeDrive(0,0,0);


			/*if(System.currentTimeMillis() - timer <= 5000) {
				leftFront.set(0.25);
				leftBack.set(0.25);
				rightFront.set(0.25);
				rightBack.set(0.25);
			} else {
				leftFront.set(0);
				leftBack.set(0);
				rightFront.set(0);
				rightBack.set(0);
			}
		/*
		}else{

			String selected = (String) autonomusChooser.getSelected();
			scan(selected);
		}



		String selected = (String) autonomusChooser.getSelected();

		scan(selected);*/

		/**

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
		boolean searchingForPeg = true;
		boolean aligningPeg = false;
		switch (autoSelected) {
		case left:
			//first stage: turn the camera and move forward until peg is next to camera
			if (searchingForPeg){
				servo.setAngle(180);
				arcadeDrive(1, 0, 0.25);
				if (xErr > -10 && xErr < 10) {
					arcadeDrive(0, 0, 0);
					servo.setAngle(90);
					searchingForPeg = false;
					aligningPeg = true;
				}
			}else{
			//second stage: turn the whole robot around and go forward until peg is directly in front
				if (aligningPeg){
					arcadeDrive(0, 0.5, 0.25);
				}
				if (xErr > -10 && xErr < 10) {
					aligningPeg = false;
					if (ultrasonic.getRangeInches() < 2){
						leftFront.set(0);
						leftBack.set(0);
						rightFront.set(0);
						rightBack.set(0);
						gearPlacement();
					}else{
						arcadeDrive(1, 0, 0.25);
					}
				}
			}
			break;
		case middle:
			// Add the code to make the robot continue on a straight vector then do the things it needs to do
			// Code for the ultrasonic to stop the robot if we are too close.
			servo.setAngle(90);
			if (ultrasonic.getRangeInches() < 2){
				leftFront.set(0);
				leftBack.set(0);
				rightFront.set(0);
				rightBack.set(0);
				gearPlacement();
			}else{
				arcadeDrive(1, 0, 0.25);
			}
			break;
		case right:
			//first stage: turn the camera and move forward until peg is next to camera
			if (searchingForPeg){
				servo.setAngle(0);
				arcadeDrive(1, 0, 0.25);
				if (xErr > -10 && xErr < 10) {
					arcadeDrive(0, 0, 0);
					servo.setAngle(90);
					searchingForPeg = false;
					aligningPeg = true;
				}
			}else{
			//second stage: turn the whole robot around and go forward until peg is directly in front
				if (aligningPeg){
					arcadeDrive(0, -0.5, 0.25);
				}
				if (xErr > -10 && xErr < 10) {
					aligningPeg = false;
					if (ultrasonic.getRangeInches() < 2){
						leftFront.set(0);
						leftBack.set(0);
						rightFront.set(0);
						rightBack.set(0);
						gearPlacement();
					}else{
						arcadeDrive(1, 0, 0.25);
					}
				}
			}
			break;
		}

		int selected = (int) autonomusChooser.getSelected();
		scan(selected);*/
	}
	/**
	 * This method runs in a loop during teleop mode.
	 */
	boolean ignoreInput = false;
	public void teleopPeriodic() {
		udateTable();
		System.out.println("xER:" + (prefs.getInt("IMAGE_IDEAL_X", (IMAGE_WIDTH/2)) - cX));

		System.out.printf("Dist: %.2f\n\n", ultrasonic.getRangeInches());

		//TODO: Did we comment out the PID?
		final double moveRequest = deadzone(-flightStick.getY(), 0.15);
		final double turnRequest;
		if (isInverted)
			turnRequest = -deadzone(flightStick.getTwist(), 0.20);
		else
			turnRequest = deadzone(flightStick.getTwist(), 0.20);
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


		/*trackDistance.updateDistance();

		if(flightStick.getRawButton(2)) {
			trackDistance.velocity = 0.0;
			trackDistance.distance = 0.0;
		}
		if(flightStick.getRawButton(5)) {
			trackDistance.calibrate = true;
		}*/

		/*
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
		*/

		System.out.println("distance travelled: " + trackDistance.distance);
		System.out.println("Left Front: " + leftFront.getInverted());
		System.out.println("Left Back: " + leftBack.getInverted());
		System.out.println("Right Front: " + rightFront.getInverted());
		System.out.println("Right Back: " + rightBack.getInverted());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		
		if(pid){
		arcadeDrive(moveRequest, turnRequest, speedLimiter);
		}else{
			arcadeDrive(moveRequest, map(turnRateRequest - rateX, -45, 45, -1, 1),speedLimiter);
		}

		/**if(xboxController.getRawAxis(3) == 1){
			//shooter.set(.5);
		}*/
		if(xboxController.getRawButton(5)){
			intake.set(1);
		} else if(xboxController.getRawButton(6)){
			intake.set(-1);
		} else {
			intake.set(0);
		}
		if(xboxController.getRawAxis(6) == -1){
			//helix.set(-1);
		}
		if(xboxController.getRawAxis(6) == 1){
			//helix.set(1);
		}


		//winch.set(clip(xboxController.getRawAxis(1), 0, 1));
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


		/**if(flightStick.getRawButton(3)){
			if (!ignoreInput){
				ignoreInput = true;
				isInverted =! isInverted;
				rightFront.setInverted(!rightFront.getInverted());
				rightBack.setInverted(!rightBack.getInverted());
				leftFront.setInverted(!leftFront.getInverted());
				leftBack.setInverted(!leftBack.getInverted());
			}
		}else{
			ignoreInput = false;
		}*/
		if(flightStick.getRawButton(1) || xboxController.getBButton()){
			if(flightStick.getRawButton(7) || flightStick.getRawButton(8) || xboxController.getBButton()){
				timer = System.currentTimeMillis();
				piston.set(DoubleSolenoid.Value.kForward);
			}
		}
		if(System.currentTimeMillis() - timer > 750){
			piston.set(DoubleSolenoid.Value.kReverse);
		}
	}




	/**
	 * This method runs in a loop during test mode.
	 */

	public void gearPlacement(){

		/*
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

		arcadeDrive(1, xRotationError, .25);
		if(distance < 2){
			final double xErr = 160 - cX;
			final double yErr = 120 - cY;
			map(xErr, -80, 80, -1, 1);
			arcadeDrive(1, xErr, .25);
			if(xErr == 0){
				if(distance < 2){
					timer = System.currentTimeMillis();
					piston.set(DoubleSolenoid.Value.kForward);
					if(System.currentTimeMillis() - timer > 750){
						piston.set(DoubleSolenoid.Value.kReverse);
					}
				}
			}
		}
		*/

		double distance = ultrasonic.getRangeInches();
		final double xErr = prefs.getInt("IMAGE_IDEAL_X", (IMAGE_WIDTH/2)) - cX;


		if(foundTarget){
			if(Math.abs(xErr) > prefs.getInt("ENGAGE_XERR", 5)){
				arcadeDrive(0,map(xErr, -IMAGE_WIDTH/2, IMAGE_WIDTH/2, -1, 1),0.25);
			}else{
				arcadeDrive(1, 0, .25);
			}

		}


		/*if (distance < prefs.getInt("ENGAGE_DIST", 10) && Math.abs(xErr) > prefs.getInt("ENGAGE_XERR", 5)) {

			arcadeDrive(0,map(xErr, -IMAGE_WIDTH/2, IMAGE_WIDTH/2, -1, 1),0.25);

			// engage piston
			timer = System.currentTimeMillis();
			piston.set(DoubleSolenoid.Value.kForward);
			if(System.currentTimeMillis() - timer > 750){
				piston.set(DoubleSolenoid.Value.kReverse);
			}

		} else {
			arcadeDrive(1,map(xErr, -IMAGE_WIDTH/2, IMAGE_WIDTH/2, -1, 1),0.25);
		}*/
	}

	public void scan(int  pos){
		// Pos 1 = Left, 2 = Middle, 3 = Right

		// scan left and right should move forward a certain
		// amount first.
		switch (pos) {
			case 3:
				scanRight();
				break;

			case 1:
				scanLeft();
				break;

			case 2:
				scanMain();
				break;

			case 6:


				break;
			case 5:
				if(table.getBoolean("foundTarget", false)){
					gearPlacement();
				} else {
					leftFront.set(0);
					leftBack.set(0);
					rightFront.set(0);
					rightBack.set(0);
				}

			default:
				double distance = ultrasonic.getRangeInches();
				if(System.currentTimeMillis() - timer <= 5000 && distance > 10) {
					leftFront.set(0.25);
					leftBack.set(0.25);
					rightFront.set(0.25);
					rightBack.set(0.25);
				} else {
					leftFront.set(0);
					leftBack.set(0);
					rightFront.set(0);
					rightBack.set(0);
				}
		}
	}



	public int getData(){
		switch(autoMode){
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		}

		return autoMode;
	}

	public void getCenter(){
		double centerX = (x1 + x2) / 2;
		double centerY = (y1 + y2) / 2;
		cX = centerX;
		cY = centerY;
	}

	public void parallax(double x1, double x2, double y1, double y2){
		//make a parallax thing
	}


	public void scanRight(){
		boolean found = table.getBoolean("foundTarget", true);
		if(found){
			gearPlacement();
		}else{

			rightFront.set(.25);
			rightBack.set(.25);
			leftFront.set(0);
			leftBack.set(0);
		}
	}
	public void scanLeft(){
		boolean found = table.getBoolean("foundTarget", true);
		if(found){
			gearPlacement();
		}else{
			leftFront.set(.25);
			leftBack.set(.25);
			rightFront.set(0);
			rightBack.set(0);
		}
	}

	public void scanMain(){
		boolean found = table.getBoolean("foundTarget", false);
		if(found){
			gearPlacement();
		}
		else{
			leftFront.set(.25);
			leftBack.set(.25);
			rightFront.set(.25);
			rightBack.set(.25);
		}
	}

	public void udateTable(){
		x1 = table.getNumber("x1", -1);
		x2 = table.getNumber("x2", -1);
		y1 = table.getNumber("y1", -1);
		y2 = table.getNumber("y2", -1);
		cX = table.getNumber("cX", -1);
		cY = table.getNumber("cY", -1);
		wR = table.getNumber("wR", -1);
		wL = table.getNumber("wL", -1);
		hR = table.getNumber("hR", -1);
		hL = table.getNumber("hW", -1);
	}

	String coord(double x, double y) {
		return("( " + x + ", " + y + ")");
	}
	public void testInit() {
		//MjpegServer pi = new MjpegServer("piServer", "10.49.99.12", 1186);
		//CameraServer.getInstance().startAutomaticCapture(pi.getSource());
	}
	public void testPeriodic() {

		//updateFilter();

		udateTable();
		getCenter();


		final double rateX = adis.getRateX();
		final double rateY = adis.getRateY();
		final double rateZ = adis.getRateZ();
		final double angleX = adis.getAngleX();
		final double angleY = adis.getAngleY();
		final double angleZ = adis.getAngleZ();
		final double pitch = adis.getPitch();
		final double yaw = adis.getYaw();
		final double roll = adis.getRoll();
		final double yAcceleration = adis.getAccelY();
		final double xAcceleration = adis.getAccelX();
		final double zAcceleration = adis.getAccelZ();

		System.out.println ("==== Gyro Data ====");
		System.out.printf ("rateX: %.2d rateY: %.2d rateZ: %.2d\n", rateX, rateY, rateZ);
		System.out.printf ("angleX: %.2d angleY: %.2d angleZ: %.2d\n", angleX, angleY, angleZ);
		System.out.printf ("pitch: %.2d yaw: .2d roll: %.2d\n", pitch, yaw, roll);
		System.out.printf ("xAcceleration: %.2d yAcceleration: %.2d zAcceleration: %.2d\n", xAcceleration, yAcceleration, zAcceleration);
		System.out.println ("====");
		/*
		System.out.println("Pt1: " + coord(x1,y1));
		System.out.println("Pt2: " + coord(x2, y2));
		System.out.println("Ctr: " + coord(cX, cY));
		System.out.println("WHR: " + coord(wR, hR));
		System.out.println("WHL: " + coord(wL, hL));

		System.out.println("xER:" + (prefs.getInt("IMAGE_IDEAL_X", (IMAGE_WIDTH/2)) - cX));

		System.out.printf("Dist: %.2f\n\n", ultrasonic.getRangeInches());


		double xErr = prefs.getInt("IMAGE_IDEAL_X", IMAGE_WIDTH/2) - cX;


		if(foundTarget){
			if(Math.abs(xErr) > prefs.getInt("ENGAGE_XERR", 5)){
				arcadeDrive(0,map(xErr, -IMAGE_WIDTH/2, IMAGE_WIDTH/2, -1, 1),0.25);
			}
		}
		/*trackDistance.ALPHA = SmartDashboard.getNumber("Smoothing", .8);
		if(flightStick.getRawButton(6)){
			servo.setAngle(0);
		} else if (flightStick.getRawButton(4)) {
			servo.setAngle(180);
		}
		else {
			servo.setAngle(90);
		}
		if(flightStick.getRawButton(2)) {
			trackDistance.velocity = 0.0;
			trackDistance.distance = 0.0;
		}
		if(flightStick.getRawButton(5)) {
			trackDistance.calibrate = true;
		}
		trackDistance.updateDistance();
		System.out.println("Velocity: " + trackDistance.velocity);
		System.out.println("Distance: " + trackDistance.distance + " ft");
		System.out.println("");*/
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
