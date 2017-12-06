package org.usfirst.frc.team4999.robot.choosers;

import org.usfirst.frc.team4999.lights.Color;

import java.util.HashMap;
import java.util.Vector;

import org.usfirst.frc.team4999.lights.Animator;
import org.usfirst.frc.team4999.lights.animations.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

class LightsListener implements ITableListener {
	
	private Animator animator;
	private SendableChooser<Animation> chooser;
	
	HashMap<String, Animation> animationTable;
	Vector<String> animations;
	
	public LightsListener(SendableChooser<Animation> chooser) {
		animator = new Animator();
		this.chooser = chooser;
		animator.setAnimation(this.chooser.getSelected());
	}
	
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		if(key.equals("selected") && animations.isEmpty()) {
			animator.setAnimation(this.chooser.getSelected());
			System.out.println("Setting animation to " + value);
		}
	}
	
	public void pushAnimation(String key, Animation a) {
		animationTable.put(key, a);
		animations.add(key);
		animator.setAnimation(animationTable.get(animations.lastElement()));
	}
	
	public void popAnimation(String key) {
		animations.remove(key);
		animationTable.remove(key);
		if(animations.isEmpty()) {
			animator.setAnimation(chooser.getSelected());
		} else {
			animator.setAnimation(animationTable.get(animations.lastElement()));
		}
	}
	
}

public class LightsChooser extends SendableChooser<Animation> {
	
	private LightsListener list;
	
	public final Animation blinkRed = new Blink(new Color[] {Color.RED, Color.BLACK}, 50);
	public final Animation whiteSnake = new Snake(new Color[] {Color.WHITE, Color.BLACK}, 50);
	public final Animation reverseWhiteSnake = new Snake(new Color[] {Color.WHITE, Color.BLACK}, 50, true);
	
	public LightsChooser() {
		super();
		
		AnimationSequence momentum = new AnimationSequence(new Animation[] {
				Snake.twoColorSnake(Animator.MOMENTUM_PURPLE, Animator.MOMENTUM_BLUE, 1, 5, 2, 5),
				new Fade(new Color[]{Animator.MOMENTUM_BLUE, Animator.MOMENTUM_PURPLE}, 200, 0),
				Snake.twoColorSnake(Animator.MOMENTUM_BLUE, Animator.MOMENTUM_PURPLE, 3, 0, 3, 50),
				new Fade(new Color[]{Animator.MOMENTUM_BLUE, Animator.MOMENTUM_PURPLE}, 250,0),
		}, 5000);
		
		AnimationSequence rainbow = new AnimationSequence(new Animation[] {
				Snake.rainbowSnake(10),
				Fade.RainbowFade(50, 20),
				Snake.rainbowSnake(10),
				Fade.RainbowFade(500, 0)
		}, new int[] {5000, 9000, 1000, 12000});
		
		AnimationSequence christmas = new AnimationSequence(new Animation[] {
				Snake.twoColorSnake(Color.RED, Color.WHITE, 2, 0, 4, 5),
				new Fade(new Color[] {Color.RED, new Color(60,141,13), Color.WHITE}, 0, 250),
				new Snake(new Color[] {new Color(255,223,0), new Color(60,141,13), new Color(45,100,13), new Color(45,100,13), new Color(39,84,14), new Color(39,84,14) }, 50)
		}, 5000);
		
		Animation solid = new Solid(Color.WHITE);
		
		Animation random = new RandomColors(500, 120);
		
		addDefault("Momentum", momentum);
		addObject("Rainbow", rainbow);
		addObject("Christmas",christmas);
		addObject("Solid White", solid);
		addObject("Random", random);
		
		SmartDashboard.putData("Lights Chooser", this);
		
		list = new LightsListener(this);
		this.getTable().addTableListener("selected", list, true);
		
	}
	
	public void pushAnimation(String key, Animation a) {
		list.pushAnimation(key, a);
	}
	public void popAnimation(String key) {
		list.popAnimation(key);
	}
	
}
