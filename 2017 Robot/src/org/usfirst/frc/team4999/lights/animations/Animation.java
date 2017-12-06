package org.usfirst.frc.team4999.lights.animations;

import org.usfirst.frc.team4999.lights.Packet;

public interface Animation {
	/**
	 * Main animation class
	 * @param pixels Current state of pixels
	 * @return The new state of pixels
	 */
	Packet[] animate();
	/**
	 * Gets time to wait before calling animate. Returns -1 to indicate animate() should only be called once
	 * @return The delay to wait for in milliseconds
	 */
	int getDelayUntilNextFrame();
}
