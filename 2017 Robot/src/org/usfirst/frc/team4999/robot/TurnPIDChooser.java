package org.usfirst.frc.team4999.robot;

import org.usfirst.frc.team4999.utils.MomentumPIDController;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum TurnPIDMode { carpet, cement, tile };

public class TurnPIDChooser extends SendableChooser<TurnPIDMode>{
	
	private double[] carpetVals = { 0.08, 0.01, 0.08 };
	private double[] cementVals = { 0, 0, 0 };
	private double[] tileVals = { 0, 0, 0 };
	
	public TurnPIDChooser() {
		super();
		addDefault("Carpet", TurnPIDMode.carpet);
		addObject("Cement", TurnPIDMode.cement);
		addObject("Tile", TurnPIDMode.tile);
		
		SmartDashboard.putData("PIDModeChooser", this);
	}
	
	public double getP() {
		switch(getSelected()) {
		case carpet:
			return carpetVals[0];
		case cement:
			return cementVals[0];
		case tile:
			return tileVals[0];
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
		default:
			return -1;
		}
	}
	
	public void updatePIDController(MomentumPIDController controller) {
		controller.setPID(getP(), getI(), getD());
	}
}
