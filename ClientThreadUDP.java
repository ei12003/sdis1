import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class ClientThreadUDP extends Thread {
	Database database;
	int port; InetAddress address;
	String message;
	DatagramSocket socket;
	
	public ClientThreadUDP(Database database, DatagramPacket receivePacket) {
		this.database = database;
		address = receivePacket.getAddress();
		port = receivePacket.getPort();
		message = new String(receivePacket.getData());
		try {
			socket = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		String sendData = createResponse();
		DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), address, port);
		try {
			socket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String createResponse() {
		String messageArray[] = message.split("\\s");
		String op = messageArray[0].trim();
		String plate = messageArray[1].trim();
		
		if(op.equals("REGISTER")) {
			String owner = parseOwner(messageArray);
			System.out.println("REGISTER: " + plate + " " + owner);
			int res = database.register(plate, owner);
			return Integer.toString(res);
		}
		else if(op.equals("LOOKUP")) {
			System.out.println("LOOKUP: " + plate);
			String res = database.lookUp(plate);
			return res;
		}
		return "ERROR";
	}
	
	private String parseOwner(String messageArray[]) {
		messageArray = Arrays.copyOfRange(messageArray, 2, messageArray.length);
		String result = "";
		for(int i=0; i<messageArray.length; i++) {
			result += messageArray[i] + " ";
		}
		return result.trim();
	}
}
