package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

/**
 * 组播程序示例
 * @author cfwx-yf1use
 *
 */
public class WorkMember {

	// 多播地址
	private String addr = "228.5.6.7";
	
	private int port = 6789;
	
	private InetAddress mcastaddr;
	
	private MulticastSocket socket;
	
	private Sender sender;
	
	private Receiver receiver;
	
	public WorkMember() throws IOException {
		mcastaddr = InetAddress.getByName(addr);
		InetAddress iaddr = InetAddress.getByName("192.168.23.1");
		SocketAddress sa = new InetSocketAddress(port);
		socket = new MulticastSocket(sa);
		// 在指定的网卡上(192.168.23.1:6789)监听 组播(228.5.6.7)    存在多个网卡时必须要指定(谁知道默认会选哪个网卡)
		socket.joinGroup(new InetSocketAddress(mcastaddr, port), NetworkInterface.getByInetAddress(iaddr));
		
		sender = new Sender();
		receiver = new Receiver();
	}
	
	public void start() {
		sender.start();
		receiver.start();
	}
	
	private void stop() throws IOException {
		sender.stop = true;
		receiver.stop = true;
		socket.leaveGroup(mcastaddr);
		socket.close();
	}
	
	private class Sender extends Thread {
		
		volatile boolean stop;
		
		private String str = "Hello";
		private DatagramPacket dp;
		public Sender() {
			stop = false;
			dp = new DatagramPacket(str.getBytes(), str.length(), WorkMember.this.mcastaddr, WorkMember.this.port);
		}
		
		public void run() {
			while (!stop) {
				try {
					WorkMember.this.socket.send(dp);
					Thread.sleep(1000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Receiver extends Thread {
		
		volatile boolean stop;
		private byte[] buf;
		private DatagramPacket dp;
		
		private Receiver() {
			buf = new byte[256];
			dp = new DatagramPacket(buf, buf.length);
		}
		
		public void run() {
			while (!stop) {
				try {
					WorkMember.this.socket.receive(dp);
					SocketAddress addr = dp.getSocketAddress();
					System.out.println("from " + addr + ": " + new String(buf, 0, dp.getLength()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		WorkMember member = new WorkMember();
		member.start();
		System.in.read();
		member.stop();
	}
}
