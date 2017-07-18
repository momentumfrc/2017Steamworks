/*
 * DefaultPreferences.h
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#ifndef SRC_DEFAULTPREFERENCES_H_
#define SRC_DEFAULTPREFERENCES_H_

#include <Preferences.h>

class DefaultPreferences {
public:
	DefaultPreferences();
	void addDefault(llvm::StringRef key, llvm::StringRef value);
	void addDefault(llvm::StringRef key, int value);
	void addDefault(llvm::StringRef key, double value);
	void addDefault(llvm::StringRef key, float value);
	void addDefault(llvm::StringRef key, bool value);
	void addDefault(llvm::StringRef key, long value);
private:
	frc::Preferences *prefs;
};




#endif /* SRC_DEFAULTPREFERENCES_H_ */
