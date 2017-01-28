package org.usfirst.frc.team4999.robot;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.UnknownHostException;
import java.net.Socket;

public class CamServer {

	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private byte[] buffer;

	public CamServer(String ip, int port) {
			buffer = new byte[2];
			try {
				socket = new Socket(ip, port);
				inputStream = new DataInputStream(socket.getInputStream());
				outputStream = new DataOutputStream(socket.getOutputStream());
			} catch (UnknownHostException e) {
				e.printStackTrace();	
			} catch (IOException e) {
				e.printStackTrace();	
			}
	}
	
	public void refresh() {
		try {
			inputStream.read(buffer);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public int getXError() {
		return (int) buffer[0];
	}
	
	public int getYError() {
		return (int) buffer[1];
	}

}