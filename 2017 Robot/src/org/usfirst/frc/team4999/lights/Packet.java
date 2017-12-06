package org.usfirst.frc.team4999.lights;

import java.nio.ByteBuffer;

public class Packet {
	private static final ByteBuffer B_BUFFER = ByteBuffer.allocateDirect(16);
	
	private final byte DISPLAY_FRAME = 0x01;
	private final byte SET_SINGLE = 0x02;
	private final byte SET_AREA = 0x03;
	private final byte SET_STRIDE = 0x04;
	
	private static Packet show;
	
	private byte command;
	private byte[] data;
	
	private Packet() {
		command = DISPLAY_FRAME;
		data = new byte[0];
	}
	
	/**
	 * Sets color of a single pixel
	 * @param address pixel to set
	 * @param color color to set to
	 */
	public Packet(int address, Color color) {
		command = SET_SINGLE;
		data = new byte[] {
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue()
		};
	}
	/**
	 * Sets color of an area of pixels
	 * @param address start of area
	 * @param color color to set to
	 * @param length number of pixels to set, starting at address
	 */
	public Packet(int address, Color color, int length) {
		command = SET_AREA;
		data = new byte[] {
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue(),
				(byte) length
		};
	}
	/**
	 * Sets color of an area of pixels, and repeats this area, skipping stride steps
	 * @param address start of area
	 * @param color color to set
	 * @param length number of pixels to set, starting at address
	 * @param stride number to add to address to get start of next area
	 */
	public Packet(int address, Color color, int length, int stride) {
		command = SET_STRIDE;
		data = new byte[] {
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue(),
				(byte) length,
				(byte) stride
		};
	}
	
	public int getDataSize() {
		return data.length + 1;
	}
	public int getPacketSize() {
		return data.length + 2;
	}
	
	public byte getCommand() {
		return command;
	}
	public byte[] getData() {
		return data;
	}
	
	public ByteBuffer fillBuffer() {
		B_BUFFER.rewind();
		B_BUFFER.put((byte) getDataSize());
		B_BUFFER.put(command);
		if(data.length > 0) B_BUFFER.put(data);
		B_BUFFER.rewind();
		return B_BUFFER;
	}
	
	public static ByteBuffer syncBuffer() {
		B_BUFFER.rewind();
		while(B_BUFFER.hasRemaining()) {
			B_BUFFER.put((byte)0xFF);
		}
		B_BUFFER.rewind();
		return B_BUFFER;
	}
	
	public static Packet showPacket() {
		if(show == null) {
			show = new Packet();
		}
		return show;
	}
	

}
