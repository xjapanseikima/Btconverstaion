package com.example.btconverstaion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

class picsend extends Thread {
	// 待修改
	int i = 1;// header初始值
	int w;// header轉INT
	int x = 0;
	int y = 1000;
	int s = (config.picArray.length / 1000) + 1;// 照片總共傳送次數

	public void run() {
		DatagramPacket packet = null;
		InetAddress IPAddress = null;
		DatagramSocket clientSocket = null;
		DatagramSocket headersocket = null;
		boolean onoff = true;// while一直送,當送到<總長度,break;
		try {
			clientSocket = new DatagramSocket(9874);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String fromIp = MainActivity.ippicsend.getText().toString();
			IPAddress = InetAddress.getByName(fromIp);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buffer2;
		byte[] length = new byte[100];
		String lengthx = String.valueOf(s);
		length = lengthx.getBytes();
		DatagramPacket lengthpacket = new DatagramPacket(length, length.length, IPAddress, 9870);
		try {
			clientSocket.send(lengthpacket);
			System.out.println(s);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (onoff) {
			if (i == 1) {
				byte[] header = new byte[100];
				String headerx = String.valueOf(i);
				header = headerx.getBytes();
				DatagramPacket headerpacket = new DatagramPacket(header, header.length, IPAddress, 9875);
				try {
					clientSocket.send(headerpacket);// 送header的byte
					System.out.println("送headerpacket");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(2500);// 原本5秒,縮短為2.5秒
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				buffer2 = Arrays.copyOfRange(config.picArray, x, y);
				packet = new DatagramPacket(buffer2, buffer2.length, IPAddress, 9876);
				try {
					clientSocket.send(packet);// 送圖片的byte
					System.out.println("送圖片packet");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buffer2 = null;
				x = x + 1000;
				y = y + 1000;
			} else// header超過1 以後都是由這方法去做
			{
				byte[] header = new byte[100];
				String headerx = String.valueOf(w);
				header = headerx.getBytes();
				DatagramPacket headerpacket = new DatagramPacket(header, header.length, IPAddress, 9875);
				try {
					clientSocket.send(headerpacket);// 送header的byte
					System.out.println("送圖片headerpacket");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				x = (w - 1) * 1000;
				y = (w) * 1000;
				System.out.println("圖片的" + x + "段到" + y + "段");
				buffer2 = Arrays.copyOfRange(config.picArray, x, y);
				packet = new DatagramPacket(buffer2, buffer2.length, IPAddress, 9876);
				try {
					clientSocket.send(packet);// 送圖片的byte
					System.out.println("x送圖片packet");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buffer2 = null;
			}
			try {
				try {
					headersocket = new DatagramSocket(null);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					headersocket.setReuseAddress(true);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					headersocket.bind(new InetSocketAddress(9872));
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] receiveheader = new byte[100];
				DatagramPacket recv_header = new DatagramPacket(receiveheader, receiveheader.length);
				try {
					headersocket.receive(recv_header);
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				String strheader = new String(recv_header.getData(), 0, recv_header.getLength());
				System.out.println("對方要求" + strheader);
				w = Integer.parseInt(strheader);
				i = i + 1;
				Thread.sleep(2500);// 原本為5秒,現在縮短為1秒
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		clientSocket.close();
	}

}