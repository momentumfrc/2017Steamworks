package org.usfirst.frc.team4999.robot;

import edu.wpi.first.wpilibj.Preferences;

public class DefaultPreferences {
	private Preferences instance;
	DefaultPreferences() {
		instance = Preferences.getInstance();
	}
	
	public void addKey(String key, String value) {
		if(!instance.containsKey(key))
			instance.putString(key, value);
	}
	
	public void addKey(String key, int value) {
		if(!instance.containsKey(key))
			instance.putInt(key, value);
	}
	
	public void addKey(String key, double value) {
		if(!instance.containsKey(key))
			instance.putDouble(key, value);
	}
	
	public void addKey(String key, float value) {
		if(!instance.containsKey(key))
			instance.putFloat(key, value);
	}
	
	public void addKey(String key, boolean value) {
		if(!instance.containsKey(key))
			instance.putBoolean(key, value);
	}
	
	public void addKey(String key, long value) {
		if(!instance.containsKey(key))
			instance.putLong(key, value);
	}
	
	public void addKey(String key, Object value) {
		if(value instanceof String) {
			addKey(key, (String) value);
		} else if (value instanceof Integer) {
			addKey(key, (int) value);
		} else if (value instanceof Double) {
			addKey(key, (double) value);
		} else if (value instanceof Float) {
			addKey(key, (float) value);
		} else if (value instanceof Boolean) {
			addKey(key, (boolean) value);
		} else if (value instanceof Long) {
			addKey(key, (long) value);
		}
			
	}
	
	public void addKeys(Object[][] keys){
		for (int i = 0; i < keys.length; i++) {
			if(keys[i][1] instanceof String) {
				this.addKey((String) keys[i][0], keys[i][1]);
			}
		}
	}
	
}
