/*
 * PIDInterfaces.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include <PIDInterfaces.h>

PIDTurnInterface::PIDTurnInterface(DriveSystem& drive) {
	this->drive = drive;
}
void PIDTurnInterface::PIDWrite(double output) {

}

PIDMoveInterface::PIDMoveInterface(DriveSystem& drive) {
	this->drive = drive;
}
void PIDMoveInterface::PIDWrite(double output) {

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
