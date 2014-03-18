import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class MulticastClient {
	private static final int PORT = 54321;
	private static final String ADDRESS = "224.2.2.3";
	private static MulticastSocket multiSocket;
	private static DatagramSocket socket;
	private static  InetAddress group;
	private static DatagramPacket sendPacket;
	private static DatagramPacket receivePacket;
	private static DatagramPacket multicastPacket;
	private static String sendData;
	private static byte receiveData[];
	private static Scanner keyboard = new Scanner(System.in);
	
	public static void main(String[] args) {
		try {
			socket = new DatagramSocket();
			multiSocket = new MulticastSocket(PORT);
			group = InetAddress.getByName(ADDRESS);
			multiSocket.joinGroup(group);
			receiveData = new byte[512];
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
	
	private static void clientRequest() throws Exception {
		sendData = userInput();
		String address = new String(multicastPacket.getData());
		sendPacket = new DatagramPacket(sendData.getBytes(),sendData.length(),InetAddress.getByName(address),multicastPacket.getPort());
		socket.send(sendPacket);
	}
	
	private static String userInput() {
		System.out.println("1. Register");
		System.out.println("2. LookUp");
		System.out.println("3. Exit");
		System.out.print("Which action do you want to take: ");
		
		int option;
		try {
			option = Integer.parseInt(keyboard.nextLine());
		}catch(Exception e) {
			option = 3;
		}
		
		String plate;
		
		switch(option) {
		case 1:
			System.out.print("Insert Plate: ");
			plate = keyboard.nextLine();
			System.out.print("Insert owner: ");
			String owner = keyboard.nextLine();
			return "REGISTER " + plate + " " + owner;
		case 2:
			System.out.print("Insert Plate: ");
			plate = keyboard.nextLine();
			return "LOOKUP " + plate;
		case 3:
			System.out.println("\nGoodbye ...");
			keyboard.close();
			socket.close();
			System.exit(0);
		default:
			System.out.println("\nWRONG OPTION !!\nGoodbye ...");
			keyboard.close();
			socket.close();
			System.exit(0);
		}
		return null;
	}

	private static void serverResponse() throws Exception {
		receiveData = new byte[512];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		socket.receive(receivePacket);

		System.out.print("\nResponse: ");
		System.out.println(new String(receivePacket.getData()));
		System.out.println("\n");
	}
}
