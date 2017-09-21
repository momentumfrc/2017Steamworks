package org.usfirst.frc.team4999.utils;

import edu.wpi.first.wpilibj.Preferences;

public class MoPrefs {
	
	Preferences prefs;
	DefaultPreferences dprefs;
	
	static private double defaultTicksPerMeter = 187;
	static private double defaultMoveErrGain = 1;
	static private double defaultAutoSpeedLimit = 0.25;

	public MoPrefs() {
		prefs = Preferences.getInstance();
		dprefs = new DefaultPreferences();
		
		dprefs.addKey("ENC_TICKS_PER_METER", defaultTicksPerMeter);
		dprefs.addKey("MOVE_ERR_GAIN", defaultMoveErrGain);
		dprefs.addKey("AUTO_SPEED_LIMIT", defaultAutoSpeedLimit);
	}
	
	public double getTicksPerMeter() {
		return prefs.getDouble("ENC_TICKS_PER_METER", defaultTicksPerMeter);
	}
	
	public double getMoveErrGain() {
		return prefs.getDouble("MOVE_ERR_GAIN", defaultMoveErrGain);
	}
	
	public double getDefaultAutoSpeedLimit() {
		return prefs.getDouble("AUTO_SPEED_LIMIT", defaultAutoSpeedLimit);
	}
	
}
