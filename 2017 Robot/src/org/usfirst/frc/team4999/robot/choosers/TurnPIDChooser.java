package org.usfirst.frc.team4999.robot.choosers;

import org.usfirst.frc.team4999.utils.MoPrefs;
import org.usfirst.frc.team4999.utils.MomentumPIDController;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


public class TurnPIDChooser extends SendableChooser<TurnPIDChooser.TurnPIDMode>{
	
	public static enum TurnPIDMode { carpet, cement, tile, preferences };
	
	private double[] carpetVals = { 0.08, 0.01, 0.08 };
	private double[] cementVals = { 0, 0, 0 };
	private double[] tileVals = { 0, 0, 0 };
	
	private MoPrefs prefs;
	
	private MomentumPIDController controller;
	
	public TurnPIDChooser() {
		super();
		addDefault("Carpet", TurnPIDMode.carpet);
		addObject("Cement", TurnPIDMode.cement);
		addObject("Tile", TurnPIDMode.tile);
		addObject("Preferences Values", TurnPIDMode.preferences);
		SmartDashboard.putData("PIDModeChooser", this);
	}
	
	public TurnPIDChooser(MomentumPIDController controller) {
		this();
		this.controller = controller;
		
		ITable table = getTable();
		
		ITableListener listener = new ITableListener() {
			@Override
			public void valueChanged(ITable source, String key, Object value, boolean isNew) {
				if(key == "selected") {
					System.out.println("Updating PID Values...");
					updatePIDController();
				}
			}
		};
		
		table.addTableListener("selected",listener, true);
		updatePIDController();
	}
	
	public double getP() {
		switch(getSelected()) {
		case carpet:
			return carpetVals[0];
		case cement:
			return cementVals[0];
		case tile:
			return tileVals[0];
		case preferences:
			return prefs.getTurnP();
		default:
			return -1;
		}
	}
	public double getI() {
		switch(getSelected()) {
		case carpet:
			return carpetVals[1];
		case cement:
			return cementVals[1];
		case tile:
			return tileVals[1];
		case preferences:
			return prefs.getTurnI();
		default:
			return -1;
		}
	}
	public double getD() {
		switch(getSelected()) {
		case carpet:
			return carpetVals[2];
		case cement:
			return cementVals[2];
		case tile:
			return tileVals[2];
		case preferences:
			return prefs.getTurnD();
		default:
			return -1;
		}
	}
	
	public void updatePIDController(MomentumPIDController controller) {
		controller.setPID(getP(), getI(), getD());
	}
	public void updatePIDController() {
		if(controller == null) 
			return;
		controller.setPID(getP(), getI(), getD());
	}
}
