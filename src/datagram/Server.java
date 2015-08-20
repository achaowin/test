package datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server {

	static final int PORT = 19981;
	
	private DatagramSocket socket;
	
	public Server() throws SocketException, UnknownHostException {
		InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("192.168.1.199"), PORT);
		socket = new DatagramSocket(addr);
	}
	
	public void start() {
		while (true) {
			byte[] buf = new byte[256];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(dp);
				byte[] tmp = new byte[dp.getLength()];
				System.arraycopy(buf, 0, tmp, 0, tmp.length);
				System.out.println("from " + dp.getSocketAddress() + ": " + new String(tmp));
				
				byte[] buf2 = "You're served.".getBytes();
				DatagramPacket dp2 = new DatagramPacket(buf2, buf2.length, dp.getSocketAddress());
				socket.send(dp2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws SocketException, UnknownHostException {
		new Server().start();
	}
}
