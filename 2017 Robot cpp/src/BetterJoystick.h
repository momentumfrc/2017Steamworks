/*
 * BetterJoystick.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_BETTERJOYSTICK_H_
#define SRC_BETTERJOYSTICK_H_

#include <Joystick.h>

class BetterJoystick : public Joystick {
public:
	BetterJoystick(int dev);
	bool IsFirstPush(int button);
private:
std::set held;
};


#endif /* SRC_BETTERJOYSTICK_H_ */
