package sdis;

//Import some needed classes
import java.io.Console;
import java.io.IOException;
import java.net.*;

public class SubscribeChannel {
	private static MulticastSocket multiSocket;
	private static DatagramPacket receivePacket;
	private static byte receiveData[];
    
	public SubscribeChannel(String strChannel, String strAdress) throws IOException {
		receive(strChannel,strAdress);
	}
	private static void receive(String strChannel, String strAdress)throws IOException{
		int port= Integer.parseInt(strChannel);
		// Create the socket and bind it to port 'port'.
		multiSocket = new MulticastSocket(port);

		// join the multicast group
		multiSocket.joinGroup(InetAddress.getByName(strAdress));
		// Now the socket is set up and we are ready to receive packets

		// Create a DatagramPacket and do a receive
		receiveData = new byte[65000];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		multiSocket.receive(receivePacket);

		// Finally, let us do something useful with the data we just received,
		// like print it on stdout :-)
		System.out.println("Received data from: " + receivePacket.getAddress().toString() +
				    ":" + receivePacket.getPort() + " with length: " +
				    receivePacket.getLength());
		System.out.write(receivePacket.getData(),0,receivePacket.getLength());
		System.out.println();

		// And when we have finished receiving data leave the multicast group and
		// close the socket
		multiSocket.leaveGroup(InetAddress.getByName(strAdress));
		multiSocket.close();


	}
	private static void send(String strChannel, String strAdress, byte buf[])throws IOException{
		int port= Integer.parseInt(strChannel);
		// Which ttl
		int ttl = 1;

		// Create the socket but we don't bind it as we are only going to send data
		MulticastSocket s = new MulticastSocket();
		// Create a DatagramPacket 
		DatagramPacket pack = new DatagramPacket(buf, buf.length,
							 InetAddress.getByName(strAdress), port);
		// Do a send. Note that send takes a byte for the ttl and not an int.
		s.send(pack,(byte)ttl);

		// And when we have finished sending data close the socket
		s.close();
	}
}