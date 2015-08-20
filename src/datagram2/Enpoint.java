package datagram2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * 说明: UDP示例
 * 运行方式(两台机器): host1(192.168.23.22), host2(192.168.23.23)
 * host1: java datagram2.Endpoint 192.168.23.22 192.168.23.23
 * host2: java datagram2.Endpoint 192.168.23.23 192.168.23.22
 */
public class Enpoint {

	public static final char[] CHARS = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	};
	
	private static final int PORT = 10000;
	
	DatagramSocket socket;
	
	private String sourceIp;
	
	private String destIp;
	
	private Sender sender;
	
	private Receiver receiver;
	
	public Enpoint(String sourceIp, String destIp) throws UnknownHostException, SocketException {
		this.sourceIp = sourceIp;
		this.destIp = destIp;
		// 选定一个监听的IP地址(多网卡时有用)
		SocketAddress addr = new InetSocketAddress(InetAddress.getByName(this.sourceIp), PORT);
		socket = new DatagramSocket(addr);
		socket.setSoTimeout(2000);
		sender = new Sender();
		receiver = new Receiver();
	}
	
	public void start() {
		sender.start();
		receiver.start();
	}
	
	public void stop() {
		sender.stop = true;
		receiver.stop = true;
		socket.close();
	}
	
	public static void main(String[] args) throws IOException {
		Enpoint ep = new Enpoint(args[0], args[1]);
		ep.start();
		System.in.read();
		ep.stop();
	}
	
	private class Sender extends Thread {
		
		volatile boolean stop;
		
		private InetAddress address;
		
		private byte[] buf;
		
		private Random rand;
		
		public Sender() throws UnknownHostException {
			buf = new byte[6];
			rand = new Random();
			address = InetAddress.getByName(destIp);
			stop = false;
		}
		
		public void run() {
			
			while (!stop) {
				generate();
				DatagramPacket dp = new DatagramPacket(buf, buf.length, address, PORT);
				try {
					socket.send(dp);
					Thread.sleep(1000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void generate() {
			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte)CHARS[rand.nextInt(CHARS.length)];
			}
		}
	}
	
	private class Receiver extends Thread {
		
		volatile boolean stop;
		private byte[] buf;
		
		public Receiver() {
			buf = new byte[6];
			stop = false;
		}
		
		public void run() {
			
			while (!stop) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(dp);
					System.out.println("from " + dp.getSocketAddress() + ": " + new String(buf, 0, dp.getLength()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
