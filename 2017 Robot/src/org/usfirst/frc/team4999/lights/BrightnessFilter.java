package org.usfirst.frc.team4999.lights;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


public class BrightnessFilter implements ITableListener {
	
	private static double brightness = 0.2;
	
	private static BrightnessFilter instance;
	
	private final String key = "Brightness";
	private final Preferences prefs;
	
	private BrightnessFilter() {
		prefs = Preferences.getInstance();
		
		if(!prefs.containsKey(key))
			prefs.putDouble(key, brightness);
		NetworkTable.getTable("Preferences").addTableListener(key, this, true);
		
	}
	
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		if(key == this.key && value instanceof Double ) {
			brightness = truncate((double) value);
		}
	}
	
	private double truncate(double in) {
		if(in > 1) return 1;
		if(in < 0) return 0;
		return in;
	}
	
	/**
	 * Registers the brightness class to listen to changes in the robot preferences
	 */
	public static void register() {
		if(instance == null)
			instance = new BrightnessFilter();
	}
	
	public static int dimValue(int value) {
		return (int)(value * brightness);
	}
	
	public static Color dimColor(Color in) {
		return new Color(dimValue(in.getRed()), dimValue(in.getGreen()), dimValue(in.getBlue()));
	}

}
