/*
 * BetterJoystick.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include "BetterJoystick.h"

BetterJoystick::BetterJoystick(int dev) : Joystick(dev) {
	this->held = new std::set<int>;
}
bool BetterJoystick::IsFirstPush(int button) {
	if(this->GetRawButton(button)) {
		if(held.count(button)) {
			return false;
		} else {
			held.insert(button);
			return true;
		}
	} else {
		held.erase(button);
		return false;
	}
}

