/*
 * PIDInterfaces.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_PIDINTERFACES_H_
#define SRC_PIDINTERFACES_H_

#include <DriveSystem.h>
#include <ADIS16448_IMU.h>

class PIDTurnInterface: public frc::PIDOutput {
public:
	PIDTurnInterface(DriveSystem& drive);
	void PIDWrite(double output) override;
	virtual ~PIDTurnInterface();
private:
	DriveSystem *drive;
	frc::Preferences *prefs;
};
class PIDMoveInterface: public frc::PIDOutput {
public:
	PIDMoveInterface(DriveSystem& drive);
	void PIDWrite(double output) override;
	virtual ~PIDMoveInterface();
private:
	DriveSystem *drive;
	frc::Preferences *prefs;
};

class PIDADISInterface: protected frc::PIDSource {
public:
	PIDADISInterface(ADIS16448_IMU& adis);
	void SetPIDSourceType(PIDSourceType pidSource);
	PIDSourceType GetPIDSourceType() const;
	double PIDGet() override;
	virtual ~PIDADISInterface();
private:
	ADIS16448_IMU *adis;
};

#endif /* SRC_PIDINTERFACES_H_ */
