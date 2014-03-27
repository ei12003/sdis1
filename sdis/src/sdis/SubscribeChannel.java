package sdis;

//Import some needed classes
import java.io.Console;
import java.io.IOException;
import java.net.*;

public class SubscribeChannel implements Runnable {
	private static MulticastSocket multiSocket;
	private static DatagramPacket receivePacket;
	private static DatagramPacket sendPacket;
	private static byte receiveData[];
	private static String strAdress;
    private static int port;
	private Backup backup;
	
	public SubscribeChannel(String strAdress, String strPort) throws IOException {
		
		this.port= Integer.parseInt(strPort);
		this.strAdress=strAdress;
		// Create the socket and bind it to port 'port'.
		multiSocket = new MulticastSocket(port);

		// join the multicast group
		multiSocket.joinGroup(InetAddress.getByName(strAdress));
	}
	public static String[] receive()throws IOException{
		
		receiveData = new byte[65000];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		do{
		multiSocket.receive(receivePacket);
		}while(InetAddress.getLocalHost().getHostAddress().toString().equals(receivePacket.getAddress().toString().split("/")[1]));
		
		System.out.println("Received data from: " + receivePacket.getAddress().toString() +
				    ":" + receivePacket.getPort() + " with length: " +
				    receivePacket.getLength());
		System.out.write(receivePacket.getData(),0,receivePacket.getLength());
		System.out.println();

		multiSocket.leaveGroup(InetAddress.getByName(strAdress));
		multiSocket.close();
		String[] r= {receivePacket.getAddress().toString(), receivePacket.getData().toString()};
		return r;
		
	}
	public static void send(byte buf[])throws IOException{
		
		int ttl = 1;

		multiSocket = new MulticastSocket();
		sendPacket = new DatagramPacket(buf, buf.length,
							 InetAddress.getByName(strAdress), port);
		multiSocket.send(sendPacket,(byte)ttl);

		multiSocket.close();
	}
	@Override
	public void run() {
		String ip;
		String mess;
		String[] receive= new String[2];
		try {
			System.out.println("receiving");
			receive=this.receive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("received:"+receive[1]);
		ip=receive[0];
		mess=receive[1];
			String[] parts = mess.split(" ");
String part1 = parts[0];
if(part1=="STORED"){
            
}
if(part1=="GETCHUNK"){
            
}
if(part1=="DELETE"){
            
}
if(part1=="REMOVED"){
            
}
	}
	public void setBackup(Backup backup) {
		this.backup=backup;
		
	}
}