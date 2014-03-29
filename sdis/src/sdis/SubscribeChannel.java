package sdis;

//Import some needed classes
import java.io.Console;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class SubscribeChannel implements Runnable {
	private MulticastSocket multiSocket;
	private DatagramPacket receivePacket;
	private DatagramPacket sendPacket;
	private byte receiveData[];
	private String strAdress;
	private int port;
	private Backup backup;

	public SubscribeChannel(String strAdress, String strPort)
			throws IOException {

		this.port = Integer.parseInt(strPort);
		this.strAdress = strAdress;
		// Create the socket and bind it to port 'port'.
		multiSocket = new MulticastSocket(port);

		// join the multicast group
		multiSocket.joinGroup(InetAddress.getByName(strAdress));
	}

	public String[] receive() throws IOException {
		byte[] newPacket;
		receiveData = new byte[65000];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		// do {

		multiSocket.receive(receivePacket);

		// System.out.println(InetAddress.getLocalHost().getHostAddress().toString()+receivePacket.getAddress().toString());
		// } while (InetAddress.getLocalHost().getHostAddress().toString()
		// .equals(receivePacket.getAddress().toString().split("/")[1]));

		// System.out.write(receivePacket.getData(), 0,
		// receivePacket.getLength());
		// System.out.println();

		// multiSocket.leaveGroup(InetAddress.getByName(strAdress));
		// multiSocket.close();

		newPacket = Arrays.copyOfRange(receivePacket.getData(), 0,
				receivePacket.getLength());

		String[] r = { receivePacket.getAddress().toString().split("/")[1],
				new String(newPacket) };
		return r;

	}

	public void send(byte buf[]) throws IOException {

		int ttl = 1;

		// multiSocket = new MulticastSocket();
		sendPacket = new DatagramPacket(buf, buf.length,
				InetAddress.getByName(strAdress), port);
		multiSocket.send(sendPacket, (byte) ttl);

	}

	@Override
	public void run() {

		String[] tmp = new String[2];

		while (true) {
			try {
				tmp = this.receive();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String[] received = tmp;
			Thread t = new Thread(new Runnable() {

				public void run() {
					String peer, message, messageType;
					Message msg;

					peer = received[0];
					msg = new Message(received[1]);
					messageType = msg.getMessageType();

					if (messageType.equals("STORED")) {
						System.out.println("RECEIVED STORE" + strAdress);
						backup.updateChunkPeers(msg,peer);
						backup.currentChunk.addStoredPeer(peer);
					}
					if (messageType.equals("GETCHUNK")) {

					}
					if (messageType.equals("DELETE")) {

					}
					if (messageType.equals("REMOVED")) {

					}
					if (messageType.equals("PUTCHUNK")) {
						System.out.println("Received PUTCHUNK from "
								+ strAdress);
						backup.saveChunk(msg);
						

					}
				}
			});

			t.start();

		}
	}

	public void setBackup(Backup backup) {
		this.backup = backup;

	}
}