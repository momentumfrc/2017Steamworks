#include <iostream>
#include <memory>
#include <string>

#include <IterativeRobot.h>
#include <LiveWindow/LiveWindow.h>
#include <SmartDashboard/SendableChooser.h>
#include <SmartDashboard/SmartDashboard.h>

#include <Preferences.h>

class Robot: public frc::IterativeRobot {
public:
	void RobotInit() {

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
	frc::Preferences *prefs = Preferences::GetInstance();
	frc::LiveWindow* lw = LiveWindow::GetInstance();
};

START_ROBOT_CLASS(Robot)
