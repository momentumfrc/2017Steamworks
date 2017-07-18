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

class DriveSystem: public frc::Subsystem {
public:
	DriveSystem(int leftFront, int leftBack, int rightFront, int rightBack);
private:
	frc::VictorSP leftFront, leftBack, rightFront, rightBack;
	ADIS16448_IMU adis;
	frc::Preferences prefs;
	frc::Encoder leftEnc, rightEnc;
	frc::PIDController move, turn;
};

#endif /* SRC_DRIVESYSTEM_H_ */
