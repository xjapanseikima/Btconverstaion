package com.example.btconverstaion;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/*
 * UDP 對話接收 寫在thread 是為了避免被ANR,戰用主執行緒太久
 */

class udpreceive extends Thread {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			while (true) {
				int UDP_SERVER_PORT = Integer.valueOf(MainActivity.receiveport.getText()
						.toString());
				DatagramSocket clientsocket = new DatagramSocket(
						UDP_SERVER_PORT,
						InetAddress.getByName("192.168.11.2"));

				byte[] receivedata = new byte[1024];

				DatagramPacket recv_packet = new DatagramPacket(
						receivedata, receivedata.length);
				Log.d("UDP", "S: Receiving...");
				clientsocket.receive(recv_packet);
				String rec_str = new String(recv_packet.getData(), 0,
						recv_packet.getLength());
				String source_addr = recv_packet.getAddress().toString();
				String[] source_addrx = source_addr.split("/");// 陣列切割,取第二個陣列
				Log.d(" Received String ", rec_str);
				InetAddress ipaddress = recv_packet.getAddress();
				int port = recv_packet.getPort();
				Log.d("IPAddress : ", ipaddress.toString());
				Log.d(" Port : ", Integer.toString(port));
				Bundle udpservicedata = new Bundle();
				udpservicedata.putString("data", rec_str + "from"
						+ source_addrx[1]);
				Message msg = new Message();
				msg.setData(udpservicedata);
				MainActivity.mHandler.sendMessage(msg);
				clientsocket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}