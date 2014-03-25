import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MulticastServer {
	private static final int PORT = 54321;
	private static final String ADDRESS = "224.2.2.3"; 
	private static Database database;
	private static DatagramSocket socket;
	private static DatagramPacket receivePacket;
	
	public static void main(String args[]) {
		database = new Database();
		
		try {
			socket = new DatagramSocket();
			MulticastThread multicast = new MulticastThread(ADDRESS,PORT,socket);
			multicast.start();
			while(true) {
				clientRequest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void clientRequest() throws Exception {
		byte receiveData[] = new byte[512];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);
		ClientThreadUDP serverResponse = new ClientThreadUDP(database, receivePacket);
		serverResponse.start();
	}
}
