package com.example.btconverstaion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	static EditText ipadress, sendmessage, port, receiveport, ippicsend;
	static TextView receive, mtotallength, mheader;
	static Button button_send, button_inform, openpic, button_receive_msg, button_rev_pic, button_send_pic,
			button_manual_send, button_exit;
	ImageView imageView;
	Bitmap pic;// 圖片打開
	static int g = 1;// 先看看看看看ㄎ
	DatagramSocket picclientsocket;// 送出去開的PORT
	DatagramSocket picservertsocket;// 接收到的PORT
	DatagramSocket clientSocket;
	ByteBuffer bb = ByteBuffer.allocate(/* Integer.parseInt(piclength) */5030000);
	private static final int MSG_SUCCESS = 0;
	private static final int MSG_header = 1;
	private static final int MSG_TOTALLENGTH = 2;
	private static final int PIC_DONE=3;
	boolean finished = true;
	String strheader;
	byte[] headData = new byte[100];
	private InetAddress ipaddress;
	int count = 1;
	int x = 135;// 照片大小預設長135
	int y = 180;// 照片大小預設長180
	int o = 1;// 用來判斷重傳次數,如果沒成功就ＤＲＯＰ
	Spinner sp;// 下拉式的選單
	ArrayAdapter<String> adapter;// 下拉式的選單宣告
	int totalength = 0;// 照片總共要傳幾次
	ImageView picimage;
	Bitmap new_img;
	byte[] result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ipadress = (EditText) findViewById(R.id.xml_ipaddress);
		sendmessage = (EditText) findViewById(R.id.xml_sendmessage);
		port = (EditText) findViewById(R.id.xml_port);
		receiveport = (EditText) findViewById(R.id.xml_receiveport);
		button_receive_msg = (Button) findViewById(R.id.xml_receive);
		button_send = (Button) findViewById(R.id.button_send);
		button_inform = (Button) findViewById(R.id.button_inform);
		button_manual_send = (Button) findViewById(R.id.manul);
		openpic = (Button) findViewById(R.id.openimage);
		receive = (TextView) findViewById(R.id.receivelayout);
		button_rev_pic = (Button) findViewById(R.id.receivepic);
		button_exit = (Button) findViewById(R.id.exit);
		button_send_pic = (Button) findViewById(R.id.sendpic);
		ippicsend = (EditText) findViewById(R.id.sen_picip);
		picimage = (ImageView) findViewById(R.id.rec_image);
		mtotallength = (TextView) findViewById(R.id.xml_totallength);
		mheader = (TextView) findViewById(R.id.xml_header);
		String[] sizechoose = new String[] { "180x135", "400x800" };// 下拉式選單大小
		// 程式剛啟始時載入第一個下拉選單
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sizechoose);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp = (Spinner) findViewById(R.id.sizechoose);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(selectListener);
		button_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					sendmsg.send();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});// send meesage event
		button_receive_msg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					receive();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		button_rev_pic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					receivepic();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		button_send_pic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
				//button_send_pic.setEnabled(false);
					sendpic();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		button_inform.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				try {
					ConnectionInformation();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		button_manual_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manulsend();
			}
		});
		openpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					openpic();// 打開照片
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void openpic() {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent, 1);
			}
		});
		button_exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					finish();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	protected void manulsend() {
		
		Thread manulSend = new manulSend();
		manulSend.start();
	}

	@Override
	public void onPause() {
		super.onStop();
		String TAG = "現在型態";
		Log.d(TAG, "-- onPause --");
		g = 1;
	}

	/*
	 * 下拉式選單
	 */
	private OnItemSelectedListener selectListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
			// 讀取第一個下拉選單是選擇第幾個
			int pos = sp.getSelectedItemPosition();//
			System.out.println(pos);
			switch (pos) {
			case 0:
				x = 180;
				y = 135;
				break;
			case 1:
				x = 400;
				y = 800;
				break;
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	protected void sendpic() {
		// TODO Auto-generated method stub 傳送圖片
		Thread picsend = new picsend();
		picsend.start();
	}

	protected void receivepic() {
		// TODO Auto-generated method stub 接收圖片
		Thread picreceive = new picreceive();
		picreceive.start();
	}

	protected void receive() {
		// TODO Auto-generated method stub
		Thread udpreceive = new udpreceive();
		udpreceive.start();
	}

	/*
	 * 把照片存進路徑名稱
	 */
	private void storeImage(Bitmap image) {
		File pictureFile = getOutputMediaFile();
		String TAG = "TAG";
		if (pictureFile == null) {
			Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	/*
	 * 設定路徑名稱
	 */
	private File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Commutronics/data/"
				+ getApplicationContext().getPackageName());
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		File mediaFile;
		String mImageName = "commutronics_" + timeStamp + ".jpg";
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
		return mediaFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.e("uri", uri.toString());
			ContentResolver cr = this.getContentResolver();
			try {
				pic = BitmapFactory.decodeStream(cr.openInputStream(uri));
				imageView = (ImageView) findViewById(R.id.imageView);
				/* 将Bitmap设定到ImageView */
				Bitmap picx = Bitmap.createScaledBitmap(pic, x, y, true);// picx為設定照片顯示大小,大小為400x800
				imageView.setImageBitmap(picx);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				picx.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				config.picArray = stream.toByteArray();
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 獲取IPV4的連線資訊
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	protected void ConnectionInformation() throws UnknownHostException {
		// TODO Auto-generated method stub
		config.strictmode();
		System.out.println(getLocalIpAddress());
		// Log.d("IP位置",serverIP.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.d("msg", msg.getData().getString("data"));
			receive.setText(msg.getData().getString("data"));
		}
	};
	private Handler picHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				byte[] readBuf = (byte[]) msg.obj;
				bb.put(readBuf);
				result = bb.array();
				new_img = BitmapFactory.decodeByteArray(result, 0, result.length);
				picimage.setImageBitmap(new_img);
			case MSG_header:
				mheader.setText("接收到照片分段進度：" + strheader);
				break;
			case MSG_TOTALLENGTH:
				picimage.setImageBitmap(null);
				mheader.setText("接收到照片分段進度：" +"0");
				mtotallength.setText("照片分段總長:" + Integer.toString(totalength));
				break;
			case PIC_DONE:
				bb=ByteBuffer.allocate(5030000);//這裡要稍微改下,因為在改在503000的時候ＯＫ,懷疑因該是給的陣列太小
				bb.clear();
				result =null;
				new_img.recycle();
				new_img=null;
				break;
			}
		}
	};

	class picreceive extends Thread {
		@Override
		public void run() {
			super.run();
			DatagramSocket lengthsocket = null;
			if (lengthsocket == null) {
				try {
					lengthsocket = new DatagramSocket(null);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					lengthsocket.setReuseAddress(true);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					lengthsocket.bind(new InetSocketAddress(9870));
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			byte[] lengthr = new byte[2];
			DatagramPacket recv_length = new DatagramPacket(lengthr, lengthr.length);
			try {
				lengthsocket.receive(recv_length);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String totallength = new String(recv_length.getData(), 0, recv_length.getLength());// 照片接收的次數!!!!!!
			System.out.println("需要接收" + totallength);
			totalength = Integer.valueOf(totallength);
			picHandler.obtainMessage(MSG_TOTALLENGTH, totalength).sendToTarget();
			finished=true;
			
			while (finished) {
				try {
					/*
					 * 接收header
					 */
					if (picservertsocket == null) {
						picservertsocket = new DatagramSocket(null);
						picservertsocket.setReuseAddress(true);
						picservertsocket.bind(new InetSocketAddress(9875));
					}
					byte[] receiveheader = new byte[100];
					DatagramPacket recv_header = new DatagramPacket(receiveheader, receiveheader.length);
					/* header time 過久 */
					if (count != 1) {
						picservertsocket.setSoTimeout(63000);// header偵測只要超過65s,重送header
					}
					try {
						picservertsocket.receive(recv_header);
						strheader = new String(recv_header.getData(), 0, recv_header.getLength());// 照片分段的HEADER!!!!!!
						if (count <= totalength) {
							/*
							 * 用buffer分段存照片 是為了開開關關socket 太多次,而造成錯誤
							 */
							if (picclientsocket == null) {
								picclientsocket = new DatagramSocket(null);
								picclientsocket.setReuseAddress(true);
								picclientsocket.bind(new InetSocketAddress(9876));
							}
							byte[] receivedata = new byte[1000];
							DatagramPacket recv_packet = new DatagramPacket(receivedata, receivedata.length);
							Log.d("UDP", "S: Receiving...");
							/* picdata 逾時 */
							picclientsocket.setSoTimeout(63000);
							try {
								picclientsocket.receive(recv_packet);
								byte[] buff = recv_packet.getData();
								System.out.println(receivedata.length);
								picHandler.obtainMessage(MSG_SUCCESS, buff).sendToTarget();
								picHandler.obtainMessage(MSG_header, strheader).sendToTarget();
								ipaddress = recv_packet.getAddress();
								int port = recv_packet.getPort();
								Log.d("IPAddress : ", ipaddress.toString());
								Log.d(" Port : ", Integer.toString(port));
								System.out.println("以接收到" + strheader);
								count = count + 1;
								Thread.sleep(2500);
								clientSocket = new DatagramSocket(10000);
								if (count <= totalength) {
									headData = (String.valueOf(Integer.valueOf(strheader) + 1)).getBytes();// header要求,接到載回傳
									String y = new String(headData);
									System.out.println("要求" + y);
									DatagramPacket sendPacket = new DatagramPacket(headData, headData.length, ipaddress,
											9872);
									o = 1;// 初始重傳次數
									clientSocket.send(sendPacket);
									clientSocket.close();
									Thread.sleep(3500);
								} else {
									System.out.println("照片傳完");
									/*
									 * int bmpWidth = new_img.getWidth(); int
									 * bmpHeight = new_img.getHeight(); Matrix
									 * matrix = new Matrix(); float scaleWidth =
									 * 5; float scaleHeight = 5;
									 * matrix.postScale(scaleWidth,
									 * scaleHeight); Bitmap resizeBmp =
									 * Bitmap.createBitmap(new_img, 0, 0,
									 * bmpWidth, bmpHeight, matrix, true);
									 * picimage.setImageBitmap(resizeBmp);
									 */
									finished = false;
									count = 1;// 為了讓receive 不再一直丟timeout
									storeImage(new_img);
									clientSocket.close();
									picHandler.obtainMessage(PIC_DONE).sendToTarget();

									
									//break;
								}
							} catch (SocketTimeoutException e) {
								System.out.println("pic data over time");
								clientSocket.close();
							}
						}
					} catch (SocketTimeoutException e) {
						if (o <= 3) {
							System.out.println("header over time");
							clientSocket = new DatagramSocket(10000);
							DatagramPacket sendPacket = new DatagramPacket(headData, headData.length, ipaddress, 9872);
							clientSocket.send(sendPacket);
							clientSocket.close();
							String p = new String(headData);
							System.out.println("重送HEADER:" + p);
							o = o + 1;
						} else {
							System.out.println("ＤＲＯＰ!");
							clientSocket.close();
							break;
						}
					}
				} catch (Exception e) {
					Log.e("UDP", "S: Error", e);
				}
			}
		}
	}

	class manulSend extends Thread {
		@Override
		public void run() {
			try {
				clientSocket = new DatagramSocket(10000);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			headData = (String.valueOf(Integer.valueOf(strheader) + 1)).getBytes();
			DatagramPacket sendPacket = new DatagramPacket(headData, headData.length, ipaddress, 9872);
			try {
				clientSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientSocket.close();
			String p = new String(headData);
			System.out.println("手動重新送ＨＥＡＤＥＲ" + p);
			while (finished) {
				try {
					/*
					 * 接收header
					 */
					if (picservertsocket == null) {
						picservertsocket = new DatagramSocket(null);
						picservertsocket.setReuseAddress(true);
						picservertsocket.bind(new InetSocketAddress(9875));
					}
					byte[] receiveheader = new byte[100];
					DatagramPacket recv_header = new DatagramPacket(receiveheader, receiveheader.length);
					/* header time 過久 */
					if (count != 1) {
						picservertsocket.setSoTimeout(62000);// header偵測只要超過65s,重送header
					}
					try {
						picservertsocket.receive(recv_header);
						strheader = new String(recv_header.getData(), 0, recv_header.getLength());// 照片分段的HEADER!!!!!!
						if (count <= totalength) {
							/*
							 * 用buffer分段存照片 是為了開開關關socket 太多次,而造成錯誤
							 */
							if (picclientsocket == null) {
								picclientsocket = new DatagramSocket(null);
								picclientsocket.setReuseAddress(true);
								picclientsocket.bind(new InetSocketAddress(9876));
							}
							byte[] receivedata = new byte[1000];
							DatagramPacket recv_packet = new DatagramPacket(receivedata, receivedata.length);
							Log.d("UDP", "S: Receiving...");
							/* picdata 逾時 */
							picclientsocket.setSoTimeout(10000);
							try {
								picclientsocket.receive(recv_packet);
								byte[] buff = recv_packet.getData();
								System.out.println(receivedata.length);
								picHandler.obtainMessage(MSG_SUCCESS, buff).sendToTarget();
								ipaddress = recv_packet.getAddress();
								int port = recv_packet.getPort();
								Log.d("IPAddress : ", ipaddress.toString());
								Log.d(" Port : ", Integer.toString(port));
								System.out.println("以接收到" + strheader);
								count = count + 1;
								Thread.sleep(2500);
								clientSocket = new DatagramSocket(10000);
								if (count <= totalength) {
									headData = (String.valueOf(Integer.valueOf(strheader) + 1)).getBytes();// header要求,接到載回傳
									System.out.println("要求" + String.valueOf(Integer.valueOf(strheader) + 1));
									sendPacket = new DatagramPacket(headData, headData.length, ipaddress, 9872);
									o = 1;// 初始重傳次數
									clientSocket.send(sendPacket);
									clientSocket.close();
									Thread.sleep(3500);
								} else {
									System.out.println("照片傳完");
									/*
									 * int bmpWidth = new_img.getWidth(); int
									 * bmpHeight = new_img.getHeight(); Matrix
									 * matrix = new Matrix(); float scaleWidth =
									 * 5; float scaleHeight = 5;
									 * matrix.postScale(scaleWidth,
									 * scaleHeight); Bitmap resizeBmp =
									 * Bitmap.createBitmap(new_img, 0, 0,
									 * bmpWidth, bmpHeight, matrix, true);
									 * picimage.setImageBitmap(resizeBmp);
									 */
									finished = false;
									count = 1;// 為了讓receive 不再一直丟timeout
									storeImage(new_img);
									clientSocket.close();
									break;
								}
							} catch (SocketTimeoutException e) {
								System.out.println("pic data over time");
								clientSocket.close();
							}
						}
					} catch (SocketTimeoutException e) {
						if (o <= 3) {
							System.out.println("header over time");
							clientSocket = new DatagramSocket(10000);
							sendPacket = new DatagramPacket(headData, headData.length, ipaddress, 9872);
							clientSocket.send(sendPacket);
							clientSocket.close();
							String x = new String(headData);
							System.out.println("重送HEADER:" + x);
							o = o + 1;
						} else {
							System.out.println("ＤＲＯＰ!");
							clientSocket.close();
							break;
						}
					}
				} catch (Exception e) {
					Log.e("UDP", "S: Error", e);
				}
			}
		}
	}
}