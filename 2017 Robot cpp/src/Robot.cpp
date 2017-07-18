#include <iostream>
#include <memory>
#include <string>

#include <IterativeRobot.h>
#include <LiveWindow/LiveWindow.h>
#include <SmartDashboard/SendableChooser.h>
#include <SmartDashboard/SmartDashboard.h>

#include <Preferences.h>
#include <VictorSP.h>
#include <DoubleSolenoid.h>
#include <Timer.h>
#include <CameraServer.h>
#include <cscore.h>

#include "DriveSystem.h"
#include "DefaultPreferences.h"
#include "BetterJoystick.h"

class Robot: public frc::IterativeRobot {
public:
	DriveSystem drive;
	DefaultPreferences dprefs;

	VictorSP shooterRight, shooterLeft, intake, winch;

	DoubleSolenoid piston;

	BetterJoystick flightStick;

	Timer gear, autonomous, outreach;

	cs::UsbCamera cam1;

	void RobotInit() {
		dprefs = new DefaultPreferences();

		dprefs.addDefault("AUTO_LEFT", 1.0);
		dprefs.addDefault("AUTO_RIGHT", 1.0);
		dprefs.addDefault("AUTO_MULT", 1.0);
		dprefs.addDefault("AUTO_TIME", 5);
		dprefs.addDefault("OUTREACH_TIME", 30);
		dprefs.addDefault("OUTREACH_SPEED", 0.25);
		dprefs.addDefault("OUTREACH_TURN", 0.5);
		dprefs.addDefault("SHOOTER_LEFT", 6);
		dprefs.addDefault("SHOOTER_RIGHT", 7);

		drive = new DriveSystem(2,3,0,1);

		shooterRight = new VictorSP(prefs->GetInt("SHOOTER_RIGHT", 7));
		shooterLeft = new VictorSP(prefs->GetInt("SHOOTER_LEFT", 6));
		shooterLeft.SetInverted(true);

		intake = new VictorSP(4);
		winch = new VictorSP(5);

		piston = new DoubleSolenoid(2,0);

		flightStick = new BetterJoystick(1);

		gear = new Timer();
		autonomous = new Timer();
		outreach = new Timer();

		cam1 = CamServ->StartAutomaticCapture("DriverView", 1);

	}
	void AutonomousInit() override {

	}

	void AutonomousPeriodic() {

	}

	void TeleopInit() {

	}

	void TeleopPeriodic() {

	}

	void TestPeriodic() {
		lw->Run();
	}
private:
	Preferences *prefs = Preferences::GetInstance();
	LiveWindow *lw = LiveWindow::GetInstance();
	CameraServer *CamServ = CameraServer::GetInstance();
};

START_ROBOT_CLASS(Robot)
