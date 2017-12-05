package org.usfirst.frc.team4999.lights;

import java.nio.ByteBuffer;

import org.usfirst.frc.team4999.lights.Color;

import edu.wpi.first.wpilibj.I2C;

public class NeoPixels implements Display {
	
	private final int START_FRAME = 0xFD;
	private final int SHOW_FRAME = 0xFE;
	
	private I2C strip;
	
	private long millis;
	
	private static NeoPixels instance;
	
	public static NeoPixels getInstance() {
		if(instance == null) {
			instance = new NeoPixels();
		}
		return instance;
	}
	
	private NeoPixels() {
		millis = System.currentTimeMillis();
		strip = new I2C(I2C.Port.kOnboard, 16);
	}
	
	private int safe(int in) {
		int out = in;
		switch(in) {
		case 1:
			out = 0;
			break;
		case START_FRAME:
		case SHOW_FRAME:
			out = 0xFF;
			break;
		}
		return out;
	}
	
	synchronized public long show(Color[] currentState) {
		try {
			/*
			// System.out.println("Sending data");
			strip.write(1, START_FRAME);
			millis = System.currentTimeMillis();
			for(Color color : currentState) {
				strip.write(1, safe(color.getRed()));
				strip.write(1, safe(color.getGreen()));
				strip.write(1, safe(color.getBlue()));
			}
			System.out.println("WriteTime: " + (System.currentTimeMillis() - millis));
			strip.write(1, SHOW_FRAME);
			*/
			
			/*
			byte[] buffer = new byte[2 + (currentState.length * 3)];
			
			buffer[0] = (byte) START_FRAME;
			int j = 1;
			for(int i = 0; i < currentState.length; i++ ) {
				buffer[j++] = (byte) currentState[i].getRed();
				buffer[j++] = (byte) currentState[i].getGreen();
				buffer[j++] = (byte) currentState[i].getBlue();
			}
			buffer[currentState.length - 1] = (byte) SHOW_FRAME;
			strip.writeBulk(buffer); 
			
			*/
			/*
			strip.write(1, START_FRAME);
			byte[] color = new byte[3];
			for(int i = 0; i < currentState.length; i++) {
				color[0] = (byte) safe(currentState[i].getRed());
				color[1] = (byte) safe(currentState[i].getGreen());
				color[2] = (byte) safe(currentState[i].getBlue());
				strip.writeBulk(color);
			}
			strip.write(1, SHOW_FRAME);
			*/
			/*
			ByteBuffer buffer = ByteBuffer.allocateDirect(2 + currentState.length * 3);
			buffer.put((byte) START_FRAME);
			for(int i = 0; i < currentState.length; i++ ) {
				buffer.put((byte) safe(currentState[i].getRed()));
				buffer.put((byte) safe(currentState[i].getGreen()));
				buffer.put((byte) safe(currentState[i].getBlue()));
			}
			buffer.put((byte) SHOW_FRAME);
			
			strip.writeBulk(buffer, buffer.capacity());*/
			millis = System.currentTimeMillis();
			int[] buffer;
			if((currentState.length * 3) % 2 == 0) {
				buffer = new int[(currentState.length * 3)];
			} else {
				buffer = new int[(currentState.length * 3) + 1];
				buffer[(currentState.length * 3)] = 1;
			}
			
			strip.write(1, START_FRAME);
			int j = 0;
			for(int i = 0; i < currentState.length; i++ ) {
				buffer[j++] = safe(currentState[i].getRed());
				buffer[j++] = safe(currentState[i].getGreen());
				buffer[j++] = safe(currentState[i].getBlue());
			}
			
			for(int i = 0; i < buffer.length; ) {
				strip.write(buffer[i++], buffer[i++]);
			}
			
			strip.write(1, SHOW_FRAME);
			
			return(System.currentTimeMillis() - millis);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return -1;
		}
		
	}

}
