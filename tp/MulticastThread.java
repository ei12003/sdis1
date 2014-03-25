import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MulticastThread extends Thread {
	private int port;
	private InetAddress address;
	private String sendData;
	private DatagramPacket sendPacket;
	private DatagramSocket socket;
	
	public MulticastThread(String address, int port, DatagramSocket socket) {
		try {
			this.address = InetAddress.getByName(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.port = port;
		this.socket = socket;
	}
	
	public void run() {
		sendData = "localhost";
		try {
			sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(),address,port);
			while(true) {
				socket.send(sendPacket);
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
