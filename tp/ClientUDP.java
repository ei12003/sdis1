import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientUDP{
	private static final int PORT = 54321;
	private static DatagramSocket socket;
	private static DatagramPacket sendPacket;
	private static DatagramPacket receivePacket;
	private static String sendData;
	private static byte receiveData[];
	private static Scanner keyboard = new Scanner(System.in);

	public static void main(String[] args) {
		try {
			socket = new DatagramSocket();
			
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
		sendPacket = new DatagramPacket(sendData.getBytes(),sendData.length(),InetAddress.getByName("localhost"),PORT);
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

