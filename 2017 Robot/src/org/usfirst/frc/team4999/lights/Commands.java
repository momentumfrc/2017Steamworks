package org.usfirst.frc.team4999.lights;

public class Commands {
	
	// All according to specification
	private static final byte DISPLAY_FRAME = 0x01;
	private static final byte SET_SINGLE = 0x02;
	private static final byte SET_RUN = 0x03;
	private static final byte SET_STRIDE = 0x04;
	
	private static byte[] setSizeByte(byte[] data) {
		byte[] out = new byte[data.length + 1];
		out[0] = (byte) (data.length);
		for(int i = 0; i < data.length; i++) {
			out[i+1] = data[i];
		}
		return out;
	}
	
	
	public static Packet setSingle(int address, Color color) {
		Color dimColor = BrightnessFilter.dimColor(color);
		return setSingle(address, dimColor);
	}
	
	public static Packet setSingleNoDim(int address, Color color) {
		byte[] data = {
				SET_SINGLE,
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue(),
		};
		return new Packet(setSizeByte(data));
	}
	
	public static Packet setRun(int address, Color color, int length) {
		Color dimColor = BrightnessFilter.dimColor(color);
		return setRunNoDim(address, dimColor, length);
	}
	
	public static Packet setRunNoDim(int address, Color color, int length) {
		byte[] data = {
				SET_RUN,
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue(),
				(byte) length
		};
		return new Packet(setSizeByte(data));
	}
	
	public static Packet setStride(int address, Color color, int length, int stride) {
		Color dimColor = BrightnessFilter.dimColor(color);
		return setStrideNoDim(address, dimColor, length, stride);
	}
	
	public static Packet setStrideNoDim(int address, Color color, int length, int stride) {
		byte[] data = {
				SET_STRIDE,
				(byte) address, 
				(byte) color.getRed(),
				(byte) color.getGreen(),
				(byte) color.getBlue(),
				(byte) length,
				(byte) stride
		};
		return new Packet(setSizeByte(data));
	}
	
	public static Packet syncPacket() {
		byte[] data = new byte[16];
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte) (0xFF);
		}
		return new Packet(data);
	}
	
	public static Packet showPacket() {
		byte[] data = {DISPLAY_FRAME};
		return new Packet(setSizeByte(data));
	}
	
}
