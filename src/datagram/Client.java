package datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Client {

	static final int PORT = 19982;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DatagramSocket socket = new DatagramSocket(PORT);
		InetSocketAddress remoteAddr = new InetSocketAddress(InetAddress.getByName("192.168.1.255"), Server.PORT);
		byte[] buf = "Hello".getBytes();
		DatagramPacket dp = new DatagramPacket(buf, buf.length, remoteAddr);
		
		socket.send(dp);
		
		byte[] buf2 = new byte[256];
		DatagramPacket dp2 = new DatagramPacket(buf2, buf2.length);
		socket.receive(dp2);
		byte[] tmp = new byte[dp2.getLength()];
		System.arraycopy(buf2, 0, tmp, 0, tmp.length);
		System.out.println("Server say: " + new String(tmp));
		
		System.in.read();
		socket.close();
	}

}
