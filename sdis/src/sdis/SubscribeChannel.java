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
	private static String sendData;
	private static byte receiveData[];
    
	public SubscribeChannel(String strChannel, String strAdress) throws IOException {
		int port= Integer.parseInt(strChannel);
		try {
			socket = new DatagramSocket();
			multiSocket = new MulticastSocket(port);
			group = InetAddress.getByName(strAdress);
			multiSocket.joinGroup(group);
			receiveData = new byte[256];
			multicastPacket = new DatagramPacket(receiveData, receiveData.length);
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
	
	private static String userInput() {
		Console console = System.console();
		String input = console.readLine("Enter input:");
		return input;
	}
    
	
	private static void clientRequest() throws Exception {
		sendData = userInput();
		String address = new String(multicastPacket.getData());
		sendPacket = new DatagramPacket(sendData.getBytes(),sendData.length(),InetAddress.getByName(address),multicastPacket.getPort());
		socket.send(sendPacket);
	}
	
	private static void serverResponse() throws Exception {
		receiveData = new byte[256];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
        
		System.out.print("\nResponse: ");
		System.out.println(new String(receivePacket.getData()));
		System.out.println("\n");
	}
    
}
