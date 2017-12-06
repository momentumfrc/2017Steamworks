package org.usfirst.frc.team4999.lights;

import java.nio.ByteBuffer;

import org.usfirst.frc.team4999.lights.Color;

import edu.wpi.first.wpilibj.I2C;

public class NeoPixels implements Display {
	
	
	private I2C strip;
	
	private static NeoPixels instance;
	
	
	public static NeoPixels getInstance() {
		if(instance == null) {
			instance = new NeoPixels();
		}
		return instance;
	}
	
	private NeoPixels() {
		strip = new I2C(I2C.Port.kOnboard, 16);
	}
	
	synchronized public long show(Packet[] packets) {
		try {
			long millis = System.currentTimeMillis();
			
			strip.writeBulk(Packet.syncBuffer(), 16); // Send synchronize packet
			for(Packet packet : packets) {
				strip.writeBulk(packet.fillBuffer(), packet.getPacketSize());
			}
			strip.writeBulk(Packet.showPacket().fillBuffer(), Packet.showPacket().getPacketSize());
			
			return (System.currentTimeMillis() - millis);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return -1;
		}
		
	}

}
