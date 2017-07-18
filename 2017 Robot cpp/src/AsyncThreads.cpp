/*
 * AsyncThreads.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include <AsyncThreads.h>

TurnThread::TurnThread(DriveSystem *drive, double degrees, bool debug): std::thread(this->run(drive, degrees, debug)) {}
void TurnThread::Stop() {
	this->stop = true;
}
void TurnThread::run(DriveSystem *drive, double degrees, bool debug) {
	this->stop = false;
	drive->turnCont.SetSetpoint(drive->adis.GetAngleZ() + degrees);
	if(debug) { printf("Beginning turn. Setpoint set to: %.2f\n", drive->turnCont.GetSetpoint()); }
	drive->turnCont.Enable();
	while(!drive->turnCont.OnTarget() && !stop) {
		if(debug) { printf("Turn in progress! Currently at %.2f and set to %.2f\n", drive->adis.GetAngleZ(), drive->turnCont.GetSetpoint()); }
		std::this_thread::sleep_for(std::chrono::milliseconds(10));
	}
	drive->turnCont.Disable();
}

MoveThread::MoveThread(DriveSystem *drive, double dist, bool debug): std::thread(this->run(drive, dist, debug)) {}
void MoveThread::Stop() {
	this->stop = true;
}
void MoveThread::run(DriveSystem *drive, double dist, bool debug) {
	this->stop = false;
	drive->moveCont.SetSetpoint(drive->leftEnc.GetDistance() + dist);
	if(debug) { printf("Beginning turn. Setpoint set to: %.2f\n", drive->moveCont.GetSetpoint()); }
	drive->moveCont.Enable();
	while(!drive->moveCont.OnTarget() && !stop) {
		if(debug) { printf("Turn in progress! Currently at %.2f and set to %.2f\n", drive->leftEnc.GetDistance(), drive->moveCont.GetSetpoint()); }
		std::this_thread::sleep_for(std::chrono::milliseconds(10));
	}
	drive->moveCont.Disable();
}
