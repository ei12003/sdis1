package sdis;

//Import some needed classes
import java.io.Console;
import java.io.IOException;
import java.net.*;

public class SubscribeChannel {
	private static MulticastSocket multiSocket;
	private static DatagramSocket socket;
	private static InetAddress group;
	private static DatagramPacket sendPacket;
	private static DatagramPacket receivePacket;
	private static DatagramPacket multicastPacket;
	private static byte receiveData[];
	private static byte sendData[];
    
	public SubscribeChannel(String strChannel, String strAdress) throws IOException {
		int port= Integer.parseInt(strChannel);
		try {
			socket = new DatagramSocket();
			multiSocket = new MulticastSocket(port);
			group = InetAddress.getByName(strAdress);
			multiSocket.joinGroup(group);
			sendData = new byte[65000];
			receiveData = new byte[65000];
			multicastPacket = new DatagramPacket(receiveData, receiveData.length);
			multicastPacket = new DatagramPacket(sendData, sendData.length);
			multiSocket.receive(multicastPacket);
			multiSocket.leaveGroup(group);
			while(true) {
				clientRequest();
				serverResponse();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
    
	
	private static void clientRequest() throws Exception {
		int ttl = 1;
		sendData = new byte[65000];
		sendPacket = new DatagramPacket(sendData, sendData.length);
		multiSocket.send(sendPacket,(byte)ttl);
	}
	
	private static void serverResponse() throws Exception {
		receiveData = new byte[65000];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
	}
    
}
