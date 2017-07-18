/*
 * PIDInterfaces.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include <PIDInterfaces.h>

PIDTurnInterface::PIDTurnInterface(DriveSystem& drive) {
	this->drive = drive;
	this->prefs = frc::Preferences::GetInstance;
}
void PIDTurnInterface::PIDWrite(double output) {
	drive->ArcadeDrive(0,output,prefs->GetDouble("AUTO_SPEED_LIMIT", 0.2));
}

PIDMoveInterface::PIDMoveInterface(DriveSystem& drive) {
	this->drive = drive;
	this->prefs = frc::Preferences::GetInstance;
}
void PIDMoveInterface::PIDWrite(double output) {
	drive->ArcadeDrive(output,0,prefs->GetDouble("AUTO_SPEED_LIMIT", 0.2));
}

PIDADISInterface::PIDADISInterface(ADIS16448_IMU &adis) {
	this->adis = adis;
}
void PIDADISInterface::SetPIDSourceType(PIDSourceType pidSource) {
	this->m_pidSource = pidSource;
}
PIDSourceType PIDADISInterface::GetPIDSourceType() const {
	return this->m_pidSource;
}
double PIDADISInterface::PIDGet() {
	switch(this->m_pidSource) {
	case PIDSourceType::kDisplacement:
		return adis->GetAngleZ();
		break;
	case PIDSourceType::kRate:
		return adis->GetRateZ();
		break;
	default:
		return 0.0;
	}
}
