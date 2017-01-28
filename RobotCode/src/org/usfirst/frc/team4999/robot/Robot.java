
package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    VictorSP left1, left2, right1, right2;
	
    public void robotInit() {
        right1 = new VictorSP(0);
        right2 = new VictorSP(1);
        left1 = new VictorSP(2);
        left2 = new VictorSP(3);
    }
    

    public void autonomousInit() {
    	
    }

    public void autonomousPeriodic() {

    }

    public void teleopPeriodic() {
        left1.set(0.25);
        left2.set(0.25);
        right1.set(0.25);
        right2.set(0.25);
    }
    
    public void testPeriodic() {
    
    }
    
}
