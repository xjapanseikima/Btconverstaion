package com.example.btconverstaion;
import android.os.StrictMode;

public class config {
	static byte[] picArray;// �Ӥ���bytearray
	static int totalength;//�Ӥ��`�@�n�ǴX��
	public static void strictmode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

	}

}
