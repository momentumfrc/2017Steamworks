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
	
	static private int defaultFPS = 8;
	
	static private double defaultRightMoveBeforeTurn = 2.15;
	static private double defaultLeftMoveBeforeTurn = 2.15;
	static private int turnDegs = -60;
	static private double defaultMoveTime = 3.0;
	
	static private double turnPIDTolerance = 2;
	
	static private double pidTargetTime = 1;
	
	static private double defaultXboxCurve = 2.5;
	static private double defaultXboxDeadzone = 0.1;
	
	static private double defaultThrottle = 1;
	
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
		dprefs.addKey("CAM1_FPS", defaultFPS);
		dprefs.addKey("L_MOVE_BEFORE_TURN", defaultLeftMoveBeforeTurn);
		dprefs.addKey("R_MOVE_BEFORE_TURN", defaultRightMoveBeforeTurn);
		dprefs.addKey("MOVE_FOR_TIME", defaultMoveTime);
		dprefs.addKey("TURN", turnDegs);
		dprefs.addKey("TURN_TOL", turnPIDTolerance);
		dprefs.addKey("PID_ONTARGET_TIME", pidTargetTime);
		dprefs.addKey("XBOX_CURVE_EXP", defaultXboxCurve);
		dprefs.addKey("XBOX_DEADZONE", defaultXboxDeadzone);
		dprefs.addKey("THROTTLE", defaultThrottle);
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
	
	public int getFPS() {
		return prefs.getInt("CAM1_FPS", defaultFPS);
	}
	
	public double getLMoveBeforeTurn() {
		return prefs.getDouble("L_MOVE_BEFORE_TURN", defaultLeftMoveBeforeTurn);
	}
	public double getRMoveBeforeTurn() {
		return prefs.getDouble("R_MOVE_BEFORE_TURN", defaultRightMoveBeforeTurn);
	}
	public double getMoveForTime() {
		return prefs.getDouble("MOVE_FOR_TIME", defaultMoveTime);
	}
	public int getTurn() {
		return prefs.getInt("TURN", turnDegs);
	}
	public double getTurnPIDTolerance() {
		return prefs.getDouble("TURN_TOL", turnPIDTolerance);
	}
	public double getPIDTargetTime() {
		return prefs.getDouble("PID_ONTARGET_TIME", pidTargetTime);
	}
	
	public double getXboxCurve() {
		return prefs.getDouble("XBOX_CURVE_EXP", defaultXboxCurve);
	}
	public double getXboxDeadzone() {
		return prefs.getDouble("XBOX_DEADZONE", defaultXboxDeadzone);
	}
	
	public double getThrottle() {
		return prefs.getDouble("THROTTLE", defaultThrottle);
	}
}
