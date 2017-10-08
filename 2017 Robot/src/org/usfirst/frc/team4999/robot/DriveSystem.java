package org.usfirst.frc.team4999.robot;

import org.usfirst.frc.analog.adis16448.ADIS16448_IMU;
import org.usfirst.frc.team4999.utils.DefaultPreferences;
import org.usfirst.frc.team4999.utils.MoPrefs;
import org.usfirst.frc.team4999.utils.MomentumPIDController;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Implements the PIDOutput interface to turn the robot
 * @author jordan
 *
 */
class turnInterface implements PIDOutput {
	DriveSystem drive;
	MoPrefs moprefs;
	
	turnInterface(DriveSystem drive) {
		this.drive = drive;
		moprefs = new MoPrefs();
	}
	@Override
	public void pidWrite(double output) {
		drive.arcadeDrive(0, output, moprefs.getDefaultAutoSpeedLimit());
	}
}

/**
 * Implements the PIDSource interface to return the appropriate values from the ADIS.
 * Can't use the ADIS's default implementation because it uses the method getAngle() which returns an inaccurate value
 * @author jordan
 *
 */
class adisPIDInterface implements PIDSource {
	private PIDSourceType sourceType = PIDSourceType.kDisplacement;
	ADIS16448_IMU adis;
	
	adisPIDInterface(ADIS16448_IMU adis) {
		this.adis = adis;
	}
	
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		this.sourceType = pidSource;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return this.sourceType;
	}

	@Override
	public double pidGet() {
		switch (sourceType) {
	      case kRate:
	        return adis.getRateZ();
	      case kDisplacement:
	        return adis.getAngleZ();
	      default:
	        return 0.0;
	    }
	}
	
}


/**
 * The drive system of the robot. Has pid controller to turn a specified number of degrees
 * @author jordan
 *
 */
public class DriveSystem extends Subsystem {
	VictorSP leftFront, leftBack, rightFront, rightBack;
	
	Preferences prefs;
	DefaultPreferences dprefs;
	MoPrefs moprefs;
	
	public ADIS16448_IMU adis;
	Encoder right, left;
	
	MomentumPIDController turnCont;
	
	double currentMovePower = 0;
	
	/**
	 * Initiates the robot drive system object
	 */
	DriveSystem(int leftFront, int leftBack, int rightFront, int rightBack) {
		this(new VictorSP(leftFront), new VictorSP(leftBack), new VictorSP(rightFront), new VictorSP(rightBack));
	}
	/**
	 * Initiates the robot drive system object
	 */
	DriveSystem(VictorSP leftFront, VictorSP leftBack, VictorSP rightFront, VictorSP rightBack) {
		super("Drive System");
		prefs = Preferences.getInstance();
		dprefs = new DefaultPreferences();
		moprefs = new MoPrefs();
		
		dprefs.addKey("AUTO_TURN_TARGET_TIME", 1.0);
		dprefs.addKey("DIST_BTW_WHEELS", 20);
		
		this.leftFront = leftFront;
		this.leftBack = leftBack;
		this.rightFront = rightFront;
		this.rightBack = rightBack;
		
		this.rightBack.setInverted(true);
		this.rightFront.setInverted(true);
		
		adis = new ADIS16448_IMU();
		adis.setTiltCompYaw(false);
		adisPIDInterface adisInt = new adisPIDInterface(adis);
		adisInt.setPIDSourceType(PIDSourceType.kDisplacement);
		
		left = new Encoder(2,3);
		right = new Encoder(4,5);
		left.setDistancePerPulse(1/moprefs.getTicksPerMeter());
		right.setDistancePerPulse(1/moprefs.getTicksPerMeter());
		left.setPIDSourceType(PIDSourceType.kDisplacement);
		right.setPIDSourceType(PIDSourceType.kDisplacement);
		
		turnCont = new MomentumPIDController(
				moprefs.getTurnP(),
				moprefs.getTurnI(),
				moprefs.getTurnD(),
				adisInt,
				new turnInterface(this)
		);
		
		initLiveWindow();
		
		turnCont.setAbsoluteTolerance(moprefs.getTurnPIDTolerance());
		turnCont.setOutputRange(-1,1);
	}
	
	private void initLiveWindow() {
		LiveWindow.addActuator("Drive System", "LeftFront Motor", leftFront);
		LiveWindow.addActuator("Drive System", "RightFront Motor", rightFront);
		LiveWindow.addActuator("Drive System", "LeftBack Motor", leftBack);
		LiveWindow.addActuator("Drive System", "RightBack Motor", rightBack);
		LiveWindow.addActuator("Drive System", "Turn PID", turnCont);
		LiveWindow.addSensor("Drive System", "ADIS", adis);
		LiveWindow.addSensor("Drive System", "Left Encoder", left);
		LiveWindow.addSensor("Drive System", "Right Encoder", right);
	}

	public void writeTurnPIDValues() {
		prefs.putDouble("AUTO_TURN_KP", turnCont.getP());
		prefs.putDouble("AUTO_TURN_KI", turnCont.getI());
		prefs.putDouble("AUTO_TURN_KD", turnCont.getD());
	}
	
	
	boolean recentlyTurned = true;
	
	/**
	 * Moves the robot in arcade-drive fashion with given joystick input. Input values are expected to be
	 * within the range of -1 and 1.
	 *
	 * @param moveRequest The value used to drive the robot forwards and backwards (usually y-axis of joystick).
	 * @param turnRequest The value used to turn the robot (usually x-axis or z-axis of the joystick). Positive is left.
	 * @param speedLimiter A multiplier used to slow down the robot. Set this to 1 for no limitation.
	 */
	public synchronized void arcadeDrive(double moveRequest, double turnRequest, double speedLimiter) {
		if(turnRequest == 0) {
			if(recentlyTurned) {
				recentlyTurned = false;
				lEncStart = left.get();
				rEncStart = right.get();
				System.out.println("Resetting Encoder Start positions");
				System.out.format("R: %d, L:%d\n",right.get(),left.get());
			}
			move(moveRequest * speedLimiter);
		} else {
			recentlyTurned = true;
		}
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
	
	public synchronized void tankDrive(double left, double right, double multiplier){
		if(Math.abs(left) > 1 ) {
			left = Math.abs(left) / left;
		}
		if(Math.abs(right) > 1 ) {
			right = Math.abs(right)/right;
		}
		leftFront.set(left * multiplier);
		leftBack.set(left * multiplier);
		rightFront.set(right * multiplier);
		rightBack.set(right * multiplier);
	}
	
	/**
	 * Stops the robot
	 */
	public synchronized void stop() {
		leftFront.set(0);
		leftBack.set(0);
		rightFront.set(0);
		rightBack.set(0);
	}
	
	public synchronized void STOP() {
		stop();
		turnCont.disable();
	}
	
	public double getEncAngle() {
		return Math.toDegrees((left.getDistance() - right.getDistance()) / prefs.getDouble("DIST_BTW_WHEELS", 20));
	}
	
	@Override
	protected void initDefaultCommand() {
	}
	
	/**
	 * Turns the robot the specified number of degrees
	 * @param deg - Number of degrees to turn
	 * @param debug - Print debug messages
	 */
	public void turn(double deg, boolean debug) {
		turnCont.setSetpoint(adis.getAngleZ() + deg);
		if(debug)
			System.out.format("Beginning turn. Setpoint set to: %.2f\n", turnCont.getSetpoint());
		turnCont.enable();
	}
	public void turn(double deg) {
		turn(deg, false);
	}
	
	public void blockingTurn(double deg, boolean debug) {
		turn(deg, debug);
		Timer timer = new Timer();
		timer.start();
		while(!RobotState.isDisabled()) {
			if(turnCont.onTarget()) {
				if(timer.hasPeriodPassed(moprefs.getPIDTargetTime())) {
					break;
				}
			} else {
				timer.reset();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				break;
			}
		}
		turnCont.disable();
	}
	
	public void maintainCurrentHeading(boolean value) {
		if(turnCont.isEnabled() != value) {
			if(value) {
				turnCont.setSetpoint(adis.getAngleZ());
				turnCont.enable();
			} else {
				turnCont.disable();
			}
		}
	}
	
	
	int lEncStart = 0;
	int rEncStart = 0;
	
	/**
	 * Called in a loop, drives the robot forward. Checks for robot drift and corrects.
	 */
	private void move(double speed) {
		double lEnc = left.get();
		double rEnc = right.get();
		double lChange = lEnc - lEncStart;
		double rChange = rEnc - rEncStart;
		double moveError = lChange - rChange;
		
		double lMovePower = 0;
		double rMovePower = 0;
		
		// How to resolve error:
		// - Set one motor to full movePower and reduce the other one.
		// - Select motor to reduce using this table:
		// error+ error-
		// power+ left right
		// power- right left
		//
		// which reduces to:
		// - if the sign of (p*e) is positive, reduce the left
		// - if the sign of (p*e) is negative, reduce the right
		 if(currentMovePower * moveError > 0) {
			// Left is getting ahead:
			// subtract positive error from positive power
			// or negative error from negative power
			lMovePower = currentMovePower - (moveError * moprefs.getMoveErrGain());
			rMovePower = currentMovePower;
			
		 } else {
			// Right is getting ahead:
			// add negative error to positive power
			// or positive error to negative power
			lMovePower = currentMovePower;
			rMovePower = currentMovePower + (moveError * moprefs.getMoveErrGain());
			
			}
		 //System.out.format("LeftMove: %.2f, RightMove: %.2f\n ", lMovePower, rMovePower);
		 tankDrive(lMovePower, rMovePower, speed);
	}
	
	private void move() {
		move(moprefs.getDefaultAutoSpeedLimit());
	}
	
	/**
	 * Gets the average of the distances reported by the left and right encoders
	 * @return The average distance the robot has traveled
	 */
	private double averageDistance(){
		return (left.getDistance() + right.getDistance()) / 2;
	}
	
	/**
	 * Moves the robot the specified distance at the specified power, ramping up by the rampPerTick every 50ms until the power is at the limit specified. Distance is in meters	
	 * @param dist The distance to travel in meters
	 * @param power The power level at which to drive. Between 0 and 1
	 * @param rampPerTick The amount to increase the power by every 50ms
	 * @return The thread checking if the robot has traveled the specified distance. Call interrupt() on this object to stop the robot movement
	 */
	public Thread moveDistance(double dist, double power, double rampPerTick) {
		if (power <= 0 || power > 1) {
			throw new IllegalArgumentException(power + " is not in the range (0, 1]");
		}
		lEncStart = left.get();
		rEncStart = right.get();
		currentMovePower = rampPerTick;
		Thread checkerThread = new Thread() {
			@Override
			public void run() {
				double startDist = averageDistance();
				while(Math.abs(averageDistance() - startDist) < dist && !Thread.interrupted() && !RobotState.isDisabled()){
					//System.out.format("Left dist: %.2f, Right dist: %.2f\n", left.getDistance(), right.getDistance());
					System.out.format("Left: %d, Right: %d, Difference: %d\n", Math.abs(left.get() - lEncStart), Math.abs(right.get() - rEncStart), Math.abs(left.get() - lEncStart) - Math.abs(right.get() - rEncStart));
					move();
					if(currentMovePower < power) {
						currentMovePower += rampPerTick;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					}
				}
				DriveSystem.this.stop();
			}
		};
		checkerThread.start();
		return checkerThread;
		
	}
	
	/**
	 * Moves the robot the specified distance at the specified power. Distance is in meters	
	 * @param dist The distance to travel in meters
	 * @param power The power level at which to drive. Between 0 and 1
	 * @return The thread checking if the robot has traveled the specified distance. Call interrupt() on this object to stop the robot movement
	 */
	public Thread moveDistance(double dist, double power) {
		return moveDistance(dist, power, power);
	}
	
	/**
	 * Moves the robot the specified distance at the specified power, ramping up by the rampPerTick every 50ms until the power is at the limit specified. Distance is in meters	
	 * @param dist The distance to travel in meters
	 * @param power The power level at which to drive. Between 0 and 1
	 * @param rampPerTick The amount to increase the power by every 50ms
	 */
	public void blockingMoveDistance(double dist, double power, double rampPerTick) {
		lEncStart = left.get();
		rEncStart = right.get();
		currentMovePower = rampPerTick;
		double startDist = averageDistance();
		while(Math.abs(averageDistance() - startDist) < dist && !Thread.interrupted() && !RobotState.isDisabled()){
			move();
			if(currentMovePower < power) {
				currentMovePower += rampPerTick;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		stop();
	}
	/**
	 * Moves the robot the specified distance at the specified power. Distance is in meters	
	 * @param dist The distance to travel in meters
	 * @param power The power level at which to drive. Between 0 and 1
	 */
	public void blockingMoveDistance(double dist, double power) {
		blockingMoveDistance(dist, power, power);
	}
	
	/**
	 * Moves the robot at the specified power for the specified time, ramping up by the rampPerTick every 50ms until the power is at the limit specified
	 * @param time The time to travel for
	 * @param power The power level at which to drive. Between 0 and 1
	 * @param rampPerTick The amount to increase the power by every 50ms
	 */
	public void blockingMoveTime(double time, double power, double rampPerTick) {
		//System.out.format("Moving for %.2f at %.2f speed with %.2f rampup\n", time,power,rampPerTick);
		lEncStart = left.get();
		rEncStart = right.get();
		currentMovePower = rampPerTick;
		Timer timer = new Timer();
		timer.start();
		while(!timer.hasPeriodPassed(time) && !Thread.interrupted() && !RobotState.isDisabled()) {
			//System.out.format("Moving at %.2f power\n", currentMovePower);
			move();
			if(currentMovePower < power) {
				currentMovePower += rampPerTick;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		stop();
	}
	
	/**
	 * Moves the robot at the specified power for the specified time
	 * @param time The time to travel for
	 * @param power The power level at which to drive. Between 0 and 1
	 * @param rampPerTick The amount to increase the power by every 50ms
	 */
	public void blockingMoveTime(double time, double power) {
		blockingMoveTime(time, power, power);
	}
	
	public void moveUltrasonic(double untilDist) {
		// Unimplemented
	}
	
	
	
}
 