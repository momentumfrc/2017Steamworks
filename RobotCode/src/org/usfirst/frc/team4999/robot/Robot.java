
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

    public void autonomousInit() {
    	
    }

    public void autonomousPeriodic() {
	 
    }

    public void teleopPeriodic() {
        double moveRequest = deadzone(-flightStick.getY(), 0.15);
        double turnRequest = deadzone(flightStick.getTwist(), 0.20);
        double speedLimiter = (-flightStick.getThrottle() + 1) / 2;
        
        arcadeDrive(moveRequest, turnRequest, speedLimiter);
    }
    
    public void testPeriodic() {
    	
    }
    
    private double map(double input, double maxIn, double minIn, double maxOut, double minOut) {
    		
    }
    
    private double proportionalOutput(double input, double setpoint) {
    	
    }
    
    private void arcadeDrive(double moveRequest, double turnRequest, double speedLimiter) {
    	double leftDrive = speedLimiter * (moveRequest + turnRequest);
    	double rightDrive = speedLimiter * (moveRequest - turnRequest);
    	
    	leftFront.set(leftDrive);
    	leftBack.set(leftDrive);
    	rightFront.set(rightDrive);
    	rightBack.set(rightDrive);
    }
    
    private double deadzone(double input) {
    	double zone = 0.1;
    	if(input < zone && input > -zone)
    		return 0;
    	else
    		return input;	
    }
    
    private double deadzone(double input, double zone) {
    	if(input < zone && input > -zone)
    		return 0;
    	else
    		return input;
    }
    
}
