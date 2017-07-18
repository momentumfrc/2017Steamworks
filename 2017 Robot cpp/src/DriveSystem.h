/*
 * DriveSystem.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_DRIVESYSTEM_H_
#define SRC_DRIVESYSTEM_H_

#include <VictorSP.h>
#include <ADIS16448_IMU.h>
#include <DefaultPreferences.h>
#include <PIDInterfaces.h>
#include <Encoder.h>
#include <PIDController.h>
#include <AsyncThreads.h>
#include <Commands/Subsystem.h>

class DriveSystem: public frc::Subsystem {
public:
	DriveSystem(int leftFront, int leftBack, int rightFront, int rightBack);
	void WriteTurnPIDValues();
	void WriteMovePIDValues();
	void ArcadeDrive(double moveRequest, double turnRequest, double speedLimiter);
	void TankDrive(double leftDrive, double rightDrive, double speedLimiter);
	void Stop();
	void Turn(double degrees, bool debug);
	void Turn(double degrees);
	TurnThread AsyncTurn(double degrees, bool debug);
	TurnThread AsyncTurn(double degrees);
	void Move(double dist, bool debug);
	void Move(double dist);
	MoveThread AsyncMove(double dist, bool debug);
	MoveThread AsyncMove(double dist);

	frc::PIDController moveCont, turnCont;
	ADIS16448_IMU adis;
	frc::Encoder leftEnc, rightEnc;
private:
	void InitLiveWindow();
	frc::VictorSP leftFront, leftBack, rightFront, rightBack;
	frc::Preferences prefs;
};

#endif /* SRC_DRIVESYSTEM_H_ */
