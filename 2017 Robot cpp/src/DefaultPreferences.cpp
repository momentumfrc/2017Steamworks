/*
 * DefaultPreferences.cpp
 *
 *  Created on: Jul 17, 2017
 *      Author: jordan
 */

#include <DefaultPreferences.h>
DefaultPreferences::DefaultPreferences() {
	this->prefs = frc::Preferences::GetInstance();
}
void DefaultPreferences::addDefault(llvm::StringRef key, llvm::StringRef value) {
	if(!prefs->ContainsKey(key)) {
		prefs->PutString(key, value);
	}
}
void DefaultPreferences::addDefault(llvm::StringRef key, int value) {
	if(!prefs->ContainsKey(key)) {
		prefs->PutInt(key, value);
	}
}
void DefaultPreferences::addDefault(llvm::StringRef key, double value) {
	if(!prefs->ContainsKey(key)) {
		prefs->PutDouble(key, value);
	}
}
void DefaultPreferences::addDefault(llvm::StringRef key, float value) {
	if(!prefs->ContainsKey(key)) {
		prefs->PutFloat(key, value);
	}
}
void DefaultPreferences::addDefault(llvm::StringRef key, bool value) {
	if(!prefs->ContainsKey(key)) {
		prefs->PutBoolean(key, value);
	}
}
void DefaultPreferences::addDefault(llvm::StringRef key, long value) {
	if(!prefs->ContainsKey(key)) {
		prefs->PutLong(key, value);
	}
}
