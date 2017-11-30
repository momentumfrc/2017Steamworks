package org.usfirst.frc.team4999.robot.choosers;

import org.usfirst.frc.team4999.lights.Color;

import org.usfirst.frc.team4999.lights.Animator;
import org.usfirst.frc.team4999.lights.animations.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

class LightsListener implements ITableListener {
	
	private final int NUM_LIGHTS = 32;
	
	Animator animator;
	SendableChooser<Animation> chooser;
	
	public LightsListener(SendableChooser<Animation> chooser) {
		animator = new Animator(NUM_LIGHTS);
		this.chooser = chooser;
		animator.setAnimation(this.chooser.getSelected());
	}
	
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		if(key.equals("selected")) {
			animator.setAnimation(this.chooser.getSelected());
			System.out.println("Setting animation to " + value);
		}
	}
	
}

public class LightsChooser extends SendableChooser<Animation> {
	
	public LightsChooser() {
		super();
		
		AnimationSequence momentum = new AnimationSequence(new Animation[] {
				Snake.twoColorSnake(Animator.MOMENTUM_PURPLE, Animator.MOMENTUM_BLUE, 1, 5, 2, 50),
				new Fade(new Color[]{Animator.MOMENTUM_BLUE, Animator.MOMENTUM_PURPLE}, 500,0),
				Snake.twoColorSnake(Animator.MOMENTUM_BLUE, Animator.MOMENTUM_PURPLE, 3, 0, 3, 50),
				new Fade(new Color[]{Animator.MOMENTUM_BLUE, Animator.MOMENTUM_PURPLE}, 250,0),
		}, 5000);
		
		AnimationSequence rainbow = new AnimationSequence(new Animation[] {
				Snake.rainbowSnake(50),
				Fade.RainbowFade(500, 250),
				Snake.rainbowSnake(25),
				Fade.RainbowFade(1000, 0)
		}, new int[] {5000, 9000, 1000, 12000});
		
		AnimationSequence christmas = new AnimationSequence(new Animation[] {
				Snake.twoColorSnake(Color.RED, Color.WHITE, 2, 0, 4, 50),
				new Fade(new Color[] {Color.RED, new Color(60,141,13), Color.WHITE}, 0, 250),
				new Snake(new Color[] {new Color(255,223,0), new Color(60,141,13), new Color(45,100,13), new Color(45,100,13), new Color(39,84,14), new Color(39,84,14) }, 50)
		}, 5000);
		
		Animation solid = new Solid(Color.WHITE);
		
		addDefault("Momentum", momentum);
		addObject("Rainbow", rainbow);
		addObject("Christmas",christmas);
		addObject("Solid White", solid);
		
		SmartDashboard.putData("Lights Chooser", this);
		
		this.getTable().addTableListener("selected", new LightsListener(this), true);
		
	}
	
}
