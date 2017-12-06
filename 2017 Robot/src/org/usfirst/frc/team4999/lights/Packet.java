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
	
	public Packet(int address, Color color) {
		command = SET_SINGLE;
		data = new byte[] {
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue()
		};
	}
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
