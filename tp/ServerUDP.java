import java.net.DatagramPacket;
import java.net.DatagramSocket;

class ServerUDP {
	private static final int PORT = 54321;
	private static Database database;
	private static DatagramSocket socket;
	private static DatagramPacket receivePacket;
	
	public static void main(String args[]) {
		database = new Database();
		
		try {
			socket = new DatagramSocket(PORT);
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