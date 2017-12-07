package org.usfirst.frc.team4999.lights;


import edu.wpi.first.wpilibj.I2C;

/**
 * Class to communicate with an arduino driving a strip of NeoPixel LEDs over I2C
 * @author jordan
 *
 */
public class NeoPixels implements Display {
	
	private I2C strip;
	
	private static NeoPixels instance;
	
	private final int SYNC_FREQ = 1000;
	private int syncidx = 1000;
	
	/**
	 * Gets an instance of NeoPixels
	 * @return an instance of NeoPixels
	 */
	public static NeoPixels getInstance() {
		if(instance == null) {
			instance = new NeoPixels();
		}
		return instance;
	}
	
	private NeoPixels() {
		strip = new I2C(I2C.Port.kOnboard, 16);
	}
	
	synchronized public void show(Packet[] packets) {
		try {
			// Send a sync packet every SYNC_FREQ frames
			if(syncidx == SYNC_FREQ) 
				strip.writeBulk(Packet.syncPacket(), 16); // Send synchronize packet
			syncidx = (++syncidx > SYNC_FREQ) ? 0 : syncidx;
			
			// Send each packet
			for(Packet packet : packets) {
				strip.writeBulk(packet.fillBuffer(), packet.getPacketSize());
			}
			// Show the sent packets
			strip.writeBulk(Packet.showPacket().fillBuffer(), Packet.showPacket().getPacketSize());
			
		} catch (Exception e) {
			// The generic try-catch prevents an error in the purely cosmetic neopixels from killing the whole robot
			System.err.println(e.getMessage());
			System.err.println(e.getStackTrace());
		}
		
	}

}
