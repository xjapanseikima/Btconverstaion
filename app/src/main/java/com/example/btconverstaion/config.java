package com.example.btconverstaion;
import android.os.StrictMode;

public class config {
	static byte[] picArray;// 照片轉bytearray
	static int totalength;//照片總共要傳幾次
	public static void strictmode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

	}

}
