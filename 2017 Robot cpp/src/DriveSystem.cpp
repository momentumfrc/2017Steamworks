/*
 * DriveSystem.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include <DriveSystem.h>

DriveSystem::DriveSystem(int leftFront, int leftBack, int rightFront, int rightBack)
: frc::Subsystem("Drive System"s) {
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

	turn = new frc::PIDController(
			prefs.GetDouble("AUTO_TURN_KP", 0.0),
			prefs.GetDouble("AUTO_TURN_KI", 0.0),
			prefs.GetDouble("AUTO_TURN_KD", 0.0),
			adisInt,
			new PIDTurnInterface(this)
	);
	move = new frc::PIDController(
				prefs.GetDouble("AUTO_MOVE_KP", 0.0),
				prefs.GetDouble("AUTO_MOVE_KI", 0.0),
				prefs.GetDouble("AUTO_MOVE_KD", 0.0),
				leftEnc,
				new PIDMoveInterface(this)
	);

}


