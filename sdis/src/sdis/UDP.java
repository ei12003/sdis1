package sdis;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDP {
	public static final int MAX_TRIES = 5;
	private DatagramSocket clientSocket;
	private DatagramSocket serverSocket;
	private InetAddress IPAddress;
	private byte[] sendData;
	private byte[] receiveData;
	public UDP(int port) throws UnknownHostException, SocketException{
		clientSocket = new DatagramSocket();
		serverSocket = new DatagramSocket(port);
		IPAddress = InetAddress.getByName("localhost");
		sendData = new byte[65000];
		receiveData = new byte[65000];
	}
	public void send() throws Exception {
			Scanner scanner = new Scanner( System.in );
			System.out.print( "Type some data for the program: " );
			String input = scanner.nextLine();
			
			String sentence = input;
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, 9876);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
			//clientSocket.close();
	}
	public void receive() throws Exception {

			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData());
			System.out.println("RECEIVED: " + sentence);
			IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, port);
			//serverSocket.send(sendPacket);
	}
	public boolean sendandreceive() throws Exception{
		int tries = 0;

		while (tries < MAX_TRIES) {
			receive();
			send();
			tries++;
		}
		if (tries == MAX_TRIES)
			return false;

		else
			return true;

	}
}