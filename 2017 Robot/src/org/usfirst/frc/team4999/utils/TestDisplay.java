package org.usfirst.frc.team4999.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;

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
		
		frame.pack();
		frame.setVisible(true);
		
		Animator an = new Animator(32, tc);
		
		Color[] testColor = {new Color(0,0,0), new Color(255,0,0), new Color(255,0,255), new Color(255,255,255), new Color(0,0,0)};
		tc.show(testColor);
		
		
	}

}
