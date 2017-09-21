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
	turnInterface(DriveSystem drive) {
		this.drive = drive;
	}
	@Override
	public void pidWrite(double output) {
		drive.arcadeDrive(0, output, Preferences.getInstance().getDouble("AUTO_SPEED_LIMIT", 0.2));
		drive.pidArcadeTurnDrive(output);
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
 * The drive system of the robot. Has pid controllers to move a specified distance or turn a specified number of degrees
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
		
		dprefs.addKey("AUTO_TURN_KP", 0);
		dprefs.addKey("AUTO_TURN_KI", 0);
		dprefs.addKey("AUTO_TURN_KD", 0);
		dprefs.addKey("AUTO_PID_TURN_TOLERANCE", 5.0);
		dprefs.addKey("AUTO_TURN_TARGET_TIME", 1.0);
		dprefs.addKey("AUTO_SPEED_LIMIT", 0.2);
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
				prefs.getDouble("AUTO_TURN_KP", 0),
				prefs.getDouble("AUTO_TURN_KI", 0),
				prefs.getDouble("AUTO_TURN_KD", 0),
				adisInt,
				new turnInterface(this)
		);
		
		initLiveWindow();
		
		turnCont.setAbsoluteTolerance(prefs.getDouble("AUTO_PID_TURN_TOLERANCE", 5.0));
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
	
	
	/**
	 * Moves the robot in arcade-drive fashion with given joystick input. Input values are expected to be
	 * within the range of -1 and 1.
	 *
	 * @param moveRequest The value used to drive the robot forwards and backwards (usually y-axis of joystick).
	 * @param turnRequest The value used to turn the robot (usually x-axis or z-axis of the joystick). Positive is left.
	 * @param speedLimiter A multiplier used to slow down the robot. Set this to 1 for no limitation.
	 */
	public synchronized void arcadeDrive(double moveRequest, double turnRequest, double speedLimiter) {
		double leftDrive = speedLimiter * (moveRequest + turnRequest);
		double rightDrive = speedLimiter * (moveRequest - turnRequest);

		leftFront.set(leftDrive);
		leftBack.set(leftDrive);
		rightFront.set(rightDrive);
		rightBack.set(rightDrive);
	}
	
	
	private double PIDmove, PIDturn;
	public synchronized void pidArcadeMoveDrive(double moveRequest) {
		PIDmove= moveRequest;
		arcadeDrive(PIDmove,PIDturn,prefs.getDouble("AUTO_SPEED_LIMIT", 0.25));
	}
	public synchronized void pidArcadeTurnDrive(double turnRequest) {
		PIDturn = turnRequest;
		arcadeDrive(PIDmove,PIDturn,prefs.getDouble("AUTO_SPEED_LIMIT", 0.25));
	}
	
	
	/**
	 * Moves the robot in a tank-drive fashion according to given inputs. All values are within the range of [-1,1].
	 * @param left The value to drive the left side
	 * @param right The value to drive the right side
	 * @param multiplier The speed multiplier
	 */
	
	public synchronized void tankDrive(double left, double right, double multiplier){
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
		PIDmove= 0;
		PIDturn = 0;
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
	
	/**
	 * Asynchronously turns the robot the specified number of degrees
	 * @param deg - Number of degrees to turn
	 * @param debug - Print debug messages
	 * @return A thread object running the turn code. Rotation may be interrupted by calling the method interrupt() on this object
	 */
	public Thread asyncTurn(double deg, boolean debug) {
		Thread turn = new Thread() {
			public void run() {
				turnCont.setSetpoint(adis.getAngleZ() + deg);
				if(debug)
					System.out.format("Beginning turn. Setpoint set to: %.2f\n", turnCont.getSetpoint());
				turnCont.enable();
				DriverStation driver = DriverStation.getInstance();
				Timer onTarget = new Timer();
				onTarget.start();
				boolean firstOnTarget = true;
				while(!driver.isDisabled()){
					try {
						Thread.sleep(10);
						if(debug) {
							System.out.format("Turn in progress! Currently at %.2f and set to %.2f\n", adis.getAngleZ(), turnCont.getSetpoint());
							SmartDashboard.putNumber("Adis Reading", adis.getAngleZ());
						}
						if(turnCont.onTarget()) {
							if(firstOnTarget){
								onTarget.reset();
								firstOnTarget = false;
							}
							if(onTarget.hasPeriodPassed(prefs.getFloat("AUTO_TURN_TARGET_TIME", 1))) {
								break;
							}
						} else {
							firstOnTarget = true;
						}
						if(Thread.interrupted()) {
							throw new InterruptedException();
						}
					} catch (InterruptedException e) {
						break;
					}
					
				}
				turnCont.disable();
				PIDmove = 0;
				PIDturn = 0;
			}
		};
		turn.start();
		return turn;
	}
	public Thread asyncTurn(double deg) {
		return asyncTurn(deg, false);
	}
	
	public void maintainCurrentHeading(boolean value) {
		if(turnCont.isEnabled() != value) {
			if(value) {
				turnCont.setSetpoint(adis.getAngleZ());
				turnCont.enable();
			} else {
				turnCont.disable();
				PIDmove = 0;
				PIDturn = 0;
			}
		}
	}
	
	
}
