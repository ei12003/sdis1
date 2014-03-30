package sdis;

//Import some needed classes
import java.io.Console;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SubscribeChannel implements Runnable {
	private MulticastSocket multiSocket;
	private DatagramPacket receivePacket;
	private DatagramPacket sendPacket;
	private byte receiveData[];
	private String strAdress;
	private int port;
	private Backup backup;
	
	private Restore restore;

	public SubscribeChannel(String strAdress, String strPort)
			throws IOException {

		this.port = Integer.parseInt(strPort);
		this.strAdress = strAdress;
		// Create the socket and bind it to port 'port'.
		multiSocket = new MulticastSocket(port);

		// join the multicast group
		multiSocket.joinGroup(InetAddress.getByName(strAdress));
	}

	public byte[][] receive() throws IOException {
		byte[] newPacket;
		receiveData = new byte[65000];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		 do {

		multiSocket.receive(receivePacket);

		 System.out.println(InetAddress.getLocalHost().getHostAddress().toString()+receivePacket.getAddress().toString());
		 } while (InetAddress.getLocalHost().getHostAddress().toString()
		 .equals(receivePacket.getAddress().toString().split("/")[1]));

		// System.out.write(receivePacket.getData(), 0,
		// receivePacket.getLength());
		// System.out.println();

		// multiSocket.leaveGroup(InetAddress.getByName(strAdress));
		// multiSocket.close();

		newPacket = Arrays.copyOfRange(receivePacket.getData(), 0,
				receivePacket.getLength());
		System.out.println("GET LENGTH"+newPacket.length);

		byte[][] r = { receivePacket.getAddress().toString().split("/")[1].getBytes(),
				newPacket };
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

		byte[][] tmp=null;

		while (true) {
			try {
				tmp = this.receive();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final byte[][] received = tmp;
			Thread t = new Thread(new Runnable() {

				public void run() {
					String peer, message, messageType;
					Message msg;

					peer = new String(received[0]);
					msg = new Message(received[1]);
					messageType = msg.getMessageType();

					if (messageType.equals("STORED")) {
						System.out.println("RECEIVED STORE" + strAdress);
						backup.updateChunkPeers(msg,peer);
						if(backup.currentChunk!=null)
							backup.currentChunk.addStoredPeer(peer);
					}
					else if (messageType.equals("GETCHUNK")) {
						
						ArrayList<Chunk>  allStoredChunks = backup.allStoredChunks;
						System.out.println("GETTIN"+allStoredChunks.size());
						//CHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF> <Body>
						for (int i = 0; i < allStoredChunks.size(); i++) {
							System.out.println("SENDCHUNK");
							// System.out.println("!"+allStoredChunks.size()+"!"+allStoredChunks.get(i).chunkNo+"||"+msg.getChunkNo());
							if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
									&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
								
								
								try {
									System.out.println(allStoredChunks.get(i).data.length);
									backup.sendChunk(allStoredChunks.get(i), allStoredChunks.get(i).data,"MDR");
								} catch (IOException | InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					}
					else if (messageType.equals("DELETE")) {

					}
					else if (messageType.equals("REMOVED")) {

					}
					else if(messageType.equals("CHUNK")){
						if(restore.restoringATM){
						Chunk chunk = new Chunk(msg.getFileId(),msg.getChunkNo(),0);
						chunk.setData(msg.getData());
						restore.fileRestoring.put(msg.getChunkNo(), chunk);
						System.out.println("CHUUUNK");
						}
					}
					else if (messageType.equals("PUTCHUNK")) {
						System.out.println("Received PUTCHUNK from "
								+ strAdress);
						backup.saveChunk(msg);
						if(msg.getData().length<64000){
							try {
								FileOutputStream fout = new FileOutputStream("allStoredChunks.bak");
								ObjectOutputStream oos;
								oos = new ObjectOutputStream(fout);
								   oos.writeObject(backup.allStoredChunks);
							        oos.close();

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			});

			t.start();

		}
	}

	public void setBackup(Backup backup) {
		this.backup = backup;

	}

	public void setRestore(Restore restore) {
		this.restore = restore;
		
	}
}