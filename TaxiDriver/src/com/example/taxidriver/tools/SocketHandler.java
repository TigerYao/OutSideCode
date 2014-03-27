package com.example.taxidriver.tools;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.location.Location;

import com.example.taxidriver.bean.DriverInfo;

public class SocketHandler {

	private BufferedInputStream br;
	private OutputStream pw;
	boolean isOpean = false;
	private Socket socket;

	private static SocketHandler instance = null;
	SocketAddress remAddress;
	boolean isOk = false;
	SocketTools tools;

	public static SocketHandler getInstance() {
		if (instance == null) {
			instance = new SocketHandler();

		}
		return instance;
	}

	public void open() {
		// TODO Auto-generated method stub
		if (openSocket()) {
			if (DriverInfo.getInstance().getStrMobile() != null
					&& DriverInfo.getInstance().getStrMobile().length() > 0) {
				String send = tools.createPower(DriverInfo.getInstance()
						.getStrMobile());
				System.out.println(send);
				synchronized (this) {
					if (send != null)
						send(tools.toByteArray(send));
				}
			}
		}
		// timer.schedule(this, 1000);
	}

	Timer getTimer;
	TimerTask task;

	// private void openGetMessage() {
	// getTimer = new Timer();
	// // setId(1);
	// task = new TimerTask() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// if (socket.isConnected()) {
	//
	// getMessage();
	// }
	// }
	// };
	// getTimer.schedule(task, 100, 1000);
	// }

	private SocketHandler() {
		// TODO Auto-generated constructor stub
		socket = new Socket();
		remAddress = new InetSocketAddress("210.51.1.22", 8800);
		tools = new SocketTools();
	}

	public void sendMessage(Location location, SharedPreferences pre) {
		synchronized (this) {
			if (location != null) {
				if (socket.isClosed() || !socket.isConnected()) {
					open();
				}
				String mobile = DriverInfo.getInstance().getStrMobile();
				if (mobile == null) {
					TextUitls.getDriverInfo(pre);
					mobile = DriverInfo.getInstance().getStrMobile();
				}
				if (mobile != null && mobile.length() > 10) {
					String sendss = tools.createSend(location, mobile);
					byte[] b = new byte[sendss.split(" ").length];
					System.out.println(sendss);
					String ss[] = sendss.split(" ");
					for (int i = 0; i < b.length; i++) {
						b[i] = (byte) (0xff & Integer.parseInt(ss[i], 16));
					}
					if (b != null)
						send(b);
				}
			}
		}
	}

	private void send(byte[] b) {
		try {
			if (pw == null) {
				pw = socket.getOutputStream();
			}
			pw.write(b);
			pw.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			getTimer.cancel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			task.cancel();
			pw.close();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.shutdownInput();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			socket.shutdownOutput();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean openSocket() {
		// TODO Auto-generated method stub
		synchronized (this) {
			socket = new Socket();
			remAddress = new InetSocketAddress("210.51.1.22", 8800);
			tools = new SocketTools();
			try {
				socket.connect(remAddress);
				br = new BufferedInputStream(socket.getInputStream());
				pw = socket.getOutputStream();
				return socket.isConnected();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	// private void send(byte[] buffer) {
	// // if (isOk)
	// if (buffer != null && buffer.length > 0 && socket.isConnected()) {
	// try {
	// pw.write(buffer);
	// pw.flush();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	private void getMessage() {
		if (socket.isConnected() && br != null) {
			try {
				int len = -1;
				byte[] buff = new byte[1024];
				String sb = "";
				while ((len = br.read(buff)) != -1) {
					sb += new String(buff, 0, len);
					// System.out.println(sb);
					// System.out.println(br.available());
				}
				if (sb.length() > 0) {
					sb = "";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
