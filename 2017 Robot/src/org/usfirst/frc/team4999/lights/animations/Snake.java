package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.Color;


public class Snake implements Animation {
	
	public static Snake twoColorSnake(Color primary, Color background, int head, int tail, int spaceBetween, int msBetweenFrames) {
		return twoColorSnake(primary, background, head, tail, spaceBetween, msBetweenFrames, false);
	}
	
	public static Snake twoColorSnake(Color primary, Color background, int head, int tail, int spaceBetween, int msBetweenFrames, boolean reversed) {
		Color[] snake = new Color[head+tail+spaceBetween];
		for(int i = 0; i < head; i++) {
			snake[i] = primary;
		}
		for(int i = head; i < head+tail; i++) {
			snake[i] = blendInto(background, primary, (i+1 - head)/(double)(tail+1));
		}
		for(int i = head+tail; i < snake.length; i++) {
			snake[i] = background;
		}
		Snake s = new Snake(snake, msBetweenFrames);
		s.setReverse(reversed);
		return s;
	}
	
	public static Snake rainbowSnake(int msBetweenFrames) {
		Color[] snake = {
				Color.RED,
				new Color(255,127,0),
				Color.YELLOW,
				Color.GREEN,
				Color.BLUE,
				new Color(139,0,255)
		};
		return new Snake(snake, msBetweenFrames);
	}
	
	private Color[] snakes;
	private int offset;
	private int speed;
	
	private boolean reverse = false;
	
	public Snake(Color[] snakes, int msBetweenFrames, boolean reversed) {
		this(snakes, msBetweenFrames);
		this.reverse = reversed;
	}
	
	public Snake(Color[] snakes, int msBetweenFrames) {
		this.snakes = snakes;
		offset = 0;
		speed = msBetweenFrames;
	}

	@Override
	public Color[] animate(Color[] pixels) {
		Color[] out = snakes.clone();
		
		for(int i = 0; i < out.length; i++) {
			out[i] = snakes[(i + offset) % snakes.length];
		}
		offset = (reverse) ? offset-1 : offset+1;
		offset = (offset >= snakes.length) ? 0 : offset;
		offset = (offset < 0) ? 0 : snakes.length - 1;
		
		return out;
		
	}

	@Override
	public int getDelayUntilNextFrame() {
		return speed;
	}
	
	private static Color blendInto(Color bg, Color fg, double percentbg) {
		int r = fg.getRed() + (int)((bg.getRed() - fg.getRed()) * percentbg);
		int g = fg.getGreen() + (int)((bg.getGreen() - fg.getGreen()) * percentbg);
		int b = fg.getBlue() + (int)((bg.getBlue() - fg.getBlue()) * percentbg);
		return new Color(r,g,b);
	}
	
	public void reverse() {
		reverse = !reverse;
	}
	public void setReverse(boolean b) {
		reverse = b;
	}

}
