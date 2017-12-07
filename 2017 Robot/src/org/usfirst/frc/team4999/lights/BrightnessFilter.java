package org.usfirst.frc.team4999.lights;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


public class BrightnessFilter implements ITableListener {
	Preferences prefs;
	
	public BrightnessFilter() {
		prefs = Preferences.getInstance();
		
		NetworkTable.getTable("Preferences").addTableListener(this);
		
	}
	
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		// TODO Auto-generated method stub
		
	}

}
