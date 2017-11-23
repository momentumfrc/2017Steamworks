package org.usfirst.frc.team4999.lights;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.usfirst.frc.team4999.lights.animations.*;

/**
 * Makes a JFrame displays lights on it, to test animations
 * @author jordan
 *
 */

class TestComponent extends JComponent implements Display {

	private final int PIXEL_SIZE = 30;
	private final int FRAME_BORDER = 10;
	
	Color[] pixels;
	
	Dimension pixsize;
	
	public TestComponent() {
		super();
		pixels = new Color[0];
		resize();
	}
	
	@Override
	public void paintComponent(Graphics gd) {
		Graphics2D g = (Graphics2D) gd;
		
		for(int y = 0; y < pixsize.height; y++) {
			for(int x = 0; x < pixsize.width; x++) {
				Rectangle rect = new Rectangle(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
				int i = (y * pixsize.width) + x;
				if(i >= pixels.length) {
					return;
				}
				g.setPaint(pixels[i]);
				g.fill(rect);
			}
		}
	}
	
	public void resize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int maxrows = (screenSize.width - FRAME_BORDER) / PIXEL_SIZE;
		
		int x = (pixels.length % maxrows) * PIXEL_SIZE, y = PIXEL_SIZE;
		for(int i = 0; i < ((pixels.length * PIXEL_SIZE) / maxrows) / PIXEL_SIZE; i++) {
			y += PIXEL_SIZE;
			x = (screenSize.width - FRAME_BORDER);
		}
		
		pixsize = new Dimension(x / PIXEL_SIZE, y/PIXEL_SIZE);
		
		setPreferredSize(new Dimension(x,y));
		
	}
	
	@Override
	public void show(Color[] pixels) {
		this.pixels = pixels;
		
		resize();
		
		revalidate();
		repaint();
	}
	
}

public class TestDisplay {
	
	public static void main(String[] args) {
		TestComponent tc = new TestComponent();
		JFrame frame = new JFrame();
		frame.add(tc);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Animator an = new Animator(32, tc);
		

		frame.pack();
		frame.setVisible(true);
		
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
		
		an.setAnimation(christmas);
	}

}
