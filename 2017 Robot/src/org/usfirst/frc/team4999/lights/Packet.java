package org.usfirst.frc.team4999.lights;

import java.nio.ByteBuffer;

/**
 * A command sent to the arduino to light up a pixel or area of pixels with a certain color.
 * Follows <a href="https://github.com/momentumfrc/2017Steamworks/wiki/Light-strings-on-the-Robot">this</a> structure.
 * @author jordan
 *
 */
public class Packet {
	
	// All according to specification
	private final byte DISPLAY_FRAME = 0x01;
	private final byte SET_SINGLE = 0x02;
	private final byte SET_RUN = 0x03;
	private final byte SET_STRIDE = 0x04;
	
	
	private byte command;
	private byte[] data;
	
	/**
	 * Creats a DISPLAY_FRAME packet
	 */
	public Packet() {
		command = DISPLAY_FRAME;
		data = new byte[0];
	}
	
	/**
	 * Sets color of a single pixel
	 * @param address pixel to set
	 * @param color color to set to
	 * @see #Packet(int, Color, int) 
	 * @see #Packet(int, Color, int, int)
	 */
	public Packet(int address, Color color) {
		command = SET_SINGLE;
		Color dimColor = BrightnessFilter.dimColor(color);
		data = new byte[] {
				(byte) address, 
				(byte) dimColor.getRed(),
				(byte) dimColor.getGreen(),
				(byte) dimColor.getBlue(),
		};
	}
	/**
	 * Sets color of an area of pixels
	 * @param address start of area
	 * @param color color to set to
	 * @param length number of pixels to set, starting at address
	 * @see #Packet(int, Color)
	 * @see #Packet(int, Color, int, int)
	 */
	public Packet(int address, Color color, int length) {
		command = SET_RUN;
		Color dimColor = BrightnessFilter.dimColor(color);
		data = new byte[] {
				(byte) address, 
				(byte) dimColor.getRed(),
				(byte) dimColor.getGreen(),
				(byte) dimColor.getBlue(),
				(byte) length
		};
	}
	/**
	 * Sets color of an area of pixels, and repeats this area, skipping stride steps
	 * @param address start of area
	 * @param color color to set
	 * @param length number of pixels to set, starting at address
	 * @param stride number to add to address to get start of next area
	 * @see #Packet(int, Color)
	 * @see #Packet(int, Color, int)
	 */
	public Packet(int address, Color color, int length, int stride) {
		command = SET_STRIDE;
		Color dimColor = BrightnessFilter.dimColor(color);
		data = new byte[] {
				(byte) address, 
				(byte) dimColor.getRed(),
				(byte) dimColor.getGreen(),
				(byte) dimColor.getBlue(),
				(byte) length,
				(byte) stride
		};
	}
	
	/**
	 * Get the size of the data, excluding the wrapper byte
	 * @return the size of the data
	 * @see #getPacketSize()
	 */
	public int getDataSize() {
		return data.length + 1;
	}
	/**
	 * Gets the size of the packet, incuding the wrapper byte
	 * @return the size of the packet
	 * @see #getDataSize()
	 */
	public int getPacketSize() {
		return data.length + 2;
	}
	/**
	 * Gets the command to be sent
	 * @return Either: DISPLAY_FRAME, SET_SINGLE, SET_AREA, or SET_STRIDE
	 * @see #getData()
	 */
	public byte getCommand() {
		return command;
	}
	/**
	 * Gets the data to be sent, excluding the wrapper byte and the command byte
	 * @return the data
	 * @see #getCommand()
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Fills the ByteBuffer with the packet data
	 * @param buffer The buffer to fill with packet data
	 */
	public void fillBuffer(ByteBuffer buffer) {
		buffer.rewind();
		buffer.put((byte) getDataSize());
		buffer.put(command);
		if(data.length > 0) buffer.put(data);
		buffer.rewind();
	}
	
	/**
	 * Fills the ByteBuffer with a synchronization packet
	 * @param buffer The buffer to fill with the synchronization packet
	 */
	public static void syncPacket(ByteBuffer buffer) {
		buffer.rewind();
		while(buffer.hasRemaining()) {
			buffer.put((byte)0xFF);
		}
		buffer.rewind();
	}
	
	

}
