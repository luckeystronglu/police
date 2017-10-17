/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("JniMissingFunction")
public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags)
			throws SecurityException, IOException {
		/* Check access permission */
		// �޸Ķ�дȨ��:
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		//mFd = OpenPortNew(device.getAbsolutePath(), baudrate, 8, 1, parity);
		// (device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}
	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}
	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}
	// JNI
	@SuppressWarnings("JniMissingFunction")
	public native static int SetPower(Boolean power);
	/*
	 * ���豸 path:�豸���� baudrate:������ flags:��Ȩ�޵ĸ������ã�����ɶ���д
	 */
	@SuppressWarnings("JniMissingFunction")
	private native static FileDescriptor open(String path, int baudrate,
											  int flags);

	/**
	 * 1. even ÿ���ֽڴ�������������bitΪ1�ĸ�����ż������У��λ���������� 2.
	 * oddÿ���ֽڴ�������������bitΪ1�ĸ�������������У��λ���������� 3. noparity û��У��λ 4. space У��λ��Ϊ0 5.
	 * mark У��λ��Ϊ1 �´򿪴��ں��������У��λ��
	 *
	 * @param path
	 * @param baudrate
	 * @param databits
	 * @param stopbits
	 * @param parity
	 *            n(NULL),o(Odd),e(EVEN),s(Space)
	 * @return
	 */
	@SuppressWarnings("JniMissingFunction")
	private native static FileDescriptor OpenPortNew(String path, int baudrate,
													 int databits, int stopbits, char parity);
	/*
	 * �ر��豸
	 */
	public native void close();

	static public native int readdata(byte[] buf);

	static public native int writedata(byte[] buf);

	static {
		System.loadLibrary("tiny-tools");
	}
}
