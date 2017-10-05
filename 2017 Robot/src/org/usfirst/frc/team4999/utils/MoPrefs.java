package org.usfirst.frc.team4999.utils;

import edu.wpi.first.wpilibj.Preferences;

public class MoPrefs {
	
	Preferences prefs;
	DefaultPreferences dprefs;
	
	static private double defaultTicksPerMeter = 187;
	static private double defaultMoveErrGain = 1;
	static private double defaultAutoSpeedLimit = 0.25;
	
	static private double defaultTurnP = 0;
	static private double defaultTurnI = 0;
	static private double defaultTurnD = 0;
	
	static private int defaultTestTurn = 45;
	
	
	public MoPrefs() {
		prefs = Preferences.getInstance();
		dprefs = new DefaultPreferences();
		
		dprefs.addKey("ENC_TICKS_PER_METER", defaultTicksPerMeter);
		dprefs.addKey("MOVE_ERR_GAIN", defaultMoveErrGain);
		dprefs.addKey("AUTO_SPEED_LIMIT", defaultAutoSpeedLimit);
		dprefs.addKey("AUTO_TURN_KP", defaultTurnP);
		dprefs.addKey("AUTO_TURN_KI", defaultTurnI);
		dprefs.addKey("AUTO_TURN_KD", defaultTurnD);
		dprefs.addKey("TEST_TURN_PID_DEG", defaultTestTurn);
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
	
	public double getTurnP() {
		return prefs.getDouble("AUTO_TURN_KP", defaultTurnP);
	}
	public double getTurnI() {
		return prefs.getDouble("AUTO_TURN_KI", defaultTurnI);
	}
	public double getTurnD() {
		return prefs.getDouble("AUTO_TURN_KD", defaultTurnD);
	}
	
	public int getTestTurn() {
		return prefs.getInt("TEST_TURN_PID_DEG", defaultTestTurn);
	}
	
	
}
