package com.example.btconverstaion;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class sendmsg {
	protected static void send() throws Exception {
		config.strictmode();
		DatagramSocket clientSocket = new DatagramSocket();
		String fromIp = MainActivity.ipadress.getText().toString();
		InetAddress IPAddress = InetAddress.getByName(fromIp);
		byte[] sendData = new byte[1024];
		String sentence = MainActivity.sendmessage.getText().toString();
		String msgIP = sentence;
		sendData = msgIP.getBytes();
		int sendport = Integer.valueOf(MainActivity.port.getText().toString());
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, IPAddress, sendport);
		clientSocket.send(sendPacket);
		clientSocket.close();
	}
}
