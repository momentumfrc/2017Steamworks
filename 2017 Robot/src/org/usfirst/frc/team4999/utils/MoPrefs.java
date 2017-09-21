package org.usfirst.frc.team4999.utils;

import edu.wpi.first.wpilibj.Preferences;

public class MoPrefs {
	
	Preferences prefs;
	DefaultPreferences dprefs;
	
	static private double defaultTicksPerMeter = 187;

	public MoPrefs() {
		prefs = Preferences.getInstance();
		dprefs = new DefaultPreferences();
		
		dprefs.addKey("ENC_TICKS_PER_METER", defaultTicksPerMeter);
	}
	
	public double getTicksPerMeter() {
		return prefs.getDouble("ENC_TICKS_PER_METER", defaultTicksPerMeter);
	}
	
}
