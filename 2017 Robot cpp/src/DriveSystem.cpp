/*
 * DriveSystem.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include <DriveSystem.h>

DriveSystem::DriveSystem(int leftFront, int leftBack, int rightFront, int rightBack) : frc::Subsystem("Drive System") {
	this->leftFront = new frc::VictorSP(leftFront);
	this->leftBack = new frc::VictorSP(leftBack);
	this->rightFront = new frc::VictorSP(rightFront);
	this->rightBack = new frc::VictorSP(rightBack);

	this->rightFront.SetInverted(true);
	this->rightBack.SetInverted(true);

	prefs = frc::Preferences::GetInstance();
	DefaultPreferences dprefs = new DefaultPreferences();
	dprefs.addDefault("AUTO_TURN_KP", 0.0);
	dprefs.addDefault("AUTO_TURN_KI", 0.0);
	dprefs.addDefault("AUTO_TURN_KD", 0.0);
	dprefs.addDefault("AUTO_PID_TURN_TOLERANCE", 5.0);
	dprefs.addDefault("AUTO_MOVE_KP", 0.0);
	dprefs.addDefault("AUTO_MOVE_KI", 0.0);
	dprefs.addDefault("AUTO_MOVE_KD", 0.0);
	dprefs.addDefault("AUTO_PID_MOVE_TOLERANCE", 2);
	dprefs.addDefault("ENC_DIST_PER_PULSE", -1.0);
	dprefs.addDefault("AUTO_SPEED_LIMIT", 0.2);

	adis = new ADIS16448_IMU();
	adis.SetTiltCompYaw(false);
	PIDADISInterface adisInt = new PIDADISInterface(this);
	adisInt.SetPIDSourceType(PIDSourceType::kDisplacement);

	leftEnc = new frc::Encoder(2,3);
	rightEnc = new frc::Encoder(4,5);
	leftEnc.SetDistancePerPulse(prefs.GetDouble("ENC_DIST_PER_PULSE", -1.0));
	rightEnc.SetDistancePerPulse(prefs.GetDouble("ENC_DIST_PER_PULSE", -1.0));
	leftEnc.SetPIDSourceType(PIDSourceType::kDisplacement);
	rightEnc.SetPIDSourceType(PIDSourceType::kDisplacement);

	turnCont = new frc::PIDController(
			prefs.GetDouble("AUTO_TURN_KP", 0.0),
			prefs.GetDouble("AUTO_TURN_KI", 0.0),
			prefs.GetDouble("AUTO_TURN_KD", 0.0),
			adisInt,
			new PIDTurnInterface(this)
	);
	moveCont = new frc::PIDController(
				prefs.GetDouble("AUTO_MOVE_KP", 0.0),
				prefs.GetDouble("AUTO_MOVE_KI", 0.0),
				prefs.GetDouble("AUTO_MOVE_KD", 0.0),
				leftEnc,
				new PIDMoveInterface(this)
	);
	turnCont.SetAbsoluteTolerance(prefs.GetDouble("AUTO_PID_TURN_TOLERANCE", 5.0));
	moveCont.SetAbsoluteTolerance(prefs.GetDouble("AUTO_PID_MOVE_TOLERANCE", 2));
	turnCont.SetOutputRange(-1,1);
	moveCont.SetOutputRange(-1,1);

	InitLiveWindow();


}
void DriveSystem::InitLiveWindow() {
	frc::LiveWindow live = frc::LiveWindow::GetInstance();

	live.AddActuator("Drive System", "LeftFront Motor", leftFront);
	live.AddActuator("Drive System", "RightFront Motor", rightFront);
	live.AddActuator("Drive System", "LeftBack Motor", leftBack);
	live.AddActuator("Drive System", "RightBack Motor", rightBack);
	live.AddActuator("Drive System", "Turn PID", turnCont);
	live.AddActuator("Drive System", "Move PID", moveCont);
	live.AddSensor("Drive System", "ADIS", adis);
	live.AddSensor("Drive System", "Left Encoder", leftEnc);
	live.AddSensor("Drive System", "Right Encoder", rightEnc);
}

void DriveSystem::WriteMovePIDValues() {
	prefs.PutDouble("AUTO_MOVE_KP", moveCont.GetP());
	prefs.PutDouble("AUTO_MOVE_KI", moveCont.GetI());
	prefs.PutDouble("AUTO_MOVE_KD", moveCont.GetD());
}

void DriveSystem::WriteTurnPIDValues() {
	prefs.PutDouble("AUTO_TURN_KP", turnCont.GetP());
	prefs.PutDouble("AUTO_TURN_KI", turnCont.GetI());
	prefs.PutDouble("AUTO_TURN_KD", turnCont.GetD());
}

void DriveSystem::ArcadeDrive(double moveRequest, double turnRequest, double speedLimiter) {
	double leftDrive = speedLimiter * (moveRequest + turnRequest);
	double rightDrive = speedLimiter * (moveRequest - turnRequest);

	leftFront.Set(leftDrive);
	leftBack.Set(leftDrive);
	rightFront.Set(rightDrive);
	rightBack.Set(rightDrive);
}

void DriveSystem::TankDrive(double leftDrive, double rightDrive, double speedLimiter) {
	leftFront.Set(leftDrive * speedLimiter);
	leftBack.Set(leftDrive * speedLimiter);
	rightFront.Set(rightDrive * speedLimiter);
	rightBack.Set(rightDrive * speedLimiter);
}

void DriveSystem::Stop() {
	leftFront.Set(0);
	leftBack.Set(0);
	rightFront.Set(0);
	rightBack.Set(0);
}

void DriveSystem::Turn(double degrees, bool debug) {
	turnCont.SetSetpoint(adis.GetAngleZ() + degrees);
	if(debug) { printf("Beginning turn. Setpoint set to: %.2f\n", turnCont.GetSetpoint()); }
	turnCont.Enable();
	while(!turnCont.OnTarget()) {
		if(debug) { printf("Turn in progress! Currently at %.2f and set to %.2f\n", adis.GetAngleZ(), turnCont.GetSetpoint()); }
		std::this_thread::sleep_for(std::chrono::milliseconds(10));
	}
	turnCont.Disable();
}


void DriveSystem::Turn(double degrees) {
	this->Turn(degrees, false);
}

TurnThread DriveSystem::AsyncTurn(double degrees, bool debug) {
	return new TurnThread(this, degrees, debug);
}
TurnThread DriveSystem::AsyncTurn(double degrees){
	return this->AsyncTurn(degrees, false);
}

void DriveSystem::Move(double dist, bool debug) {
	moveCont.SetSetpoint(leftEnc.GetDistance() + dist);
	if(debug) { printf("Beginning turn. Setpoint set to: %.2f\n", moveCont.GetSetpoint()); }
	moveCont.Enable();
	while(!moveCont.OnTarget()) {
		if(debug) { printf("Turn in progress! Currently at %.2f and set to %.2f\n", leftEnc.GetDistance(), moveCont.GetSetpoint()); }
		std::this_thread::sleep_for(std::chrono::milliseconds(10));
	}
	moveCont.Disable();
}

void DriveSystem::Move(double dist) {
	this->Move(dist, false);
}

MoveThread DriveSystem::AsyncMove(double dist, bool debug) {
	return new MoveThread(this, dist, debug);
}

MoveThread DriveSystem::AsyncMove(double dist) {
	return this->Move(dist, false);
}

