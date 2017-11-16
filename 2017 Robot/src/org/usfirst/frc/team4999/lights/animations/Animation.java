package org.usfirst.frc.team4999.lights.animations;

import java.awt.Color;

public interface Animation {
	/**
	 * Main animation class
	 * @param pixels Current state of pixels
	 * @return The new state of pixels
	 */
	Color[] animate(Color[] pixels);
	/**
	 * Gets time to wait before calling animate. Returns -1 to indicate animate() should only be called once
	 * @return The delay to wait for in milliseconds
	 */
	int getDelayBetweenFrames();
}
