package org.usfirst.frc.team4999.robot.choosers;

import org.usfirst.frc.team4999.utils.MoPrefs;
import org.usfirst.frc.team4999.utils.MomentumPIDController;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class TurnPIDChooser extends SendableChooser<TurnPIDChooser.TurnPIDMode>{
	
	public static enum TurnPIDMode { carpet, cement, tile, preferences };
	
	private String NAME = "PIDModeChooser";
	
	private double[] carpetVals = { 0.08, 0.01, 0.08 };
	private double[] cementVals = { 0, 0, 0 };
	private double[] tileVals = { 0, 0, 0 };
	
	private MoPrefs prefs;
	
	private MomentumPIDController controller;
	
	public TurnPIDChooser(MomentumPIDController controller) {
		
		addDefault("Carpet", TurnPIDMode.carpet);
		addObject("Cement", TurnPIDMode.cement);
		addObject("Tile", TurnPIDMode.tile);
		addObject("Preferences Values", TurnPIDMode.preferences);
		
		this.controller = controller;
		
		SmartDashboard.putData(NAME, this);
		NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(NAME).getEntry("selected").addListener((notification) -> {
			System.out.println("Updating PID Values...");
			updatePIDController();
		},TableEntryListener.kUpdate|TableEntryListener.kImmediate);
		
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
	
	public void updatePIDController() {
		controller.setPID(getP(), getI(), getD());
	}
}
