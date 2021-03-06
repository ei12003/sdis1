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

		// System.out.println(InetAddress.getLocalHost().getHostAddress().toString()+receivePacket.getAddress().toString());
		 } while (InetAddress.getLocalHost().getHostAddress().toString()
		 .equals(receivePacket.getAddress().toString().split("/")[1]));


		newPacket = Arrays.copyOfRange(receivePacket.getData(), 0,
				receivePacket.getLength());
		

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
						System.out.println("RECEIVED STORE from " + peer);
						backup.updateChunkPeers(msg,peer);
						if(backup.currentChunk!=null)
							backup.currentChunk.addStoredPeer(peer);
					}
					else if (messageType.equals("DELETE")) {
						int removed;
						do{
							removed=0;
						for(int i=0;i<backup.allStoredChunks.size();i++){
							if(backup.allStoredChunks.get(i).fileId.equals(msg.getFileId())){
								removed++;
								backup.allStoredChunks.remove(i);
							}
						}
						}while(removed>0);
						
						try {
							backup.updateAllStoredChunksFile();
						} catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					else if (messageType.equals("GETCHUNK")) {
						
						ArrayList<Chunk>  allStoredChunks = backup.allStoredChunks;
						
						//CHUNK <Version> <FileId> <ChunkNo> <CRLF> <CRLF> <Body>
						for (int i = 0; i < allStoredChunks.size(); i++) {
							
							
							if (msg.getFileId().equals(allStoredChunks.get(i).fileId)
									&& (msg.getChunkNo() == allStoredChunks.get(i).chunkNo)) {
								
								
								try {
									
									backup.sendChunk(allStoredChunks.get(i), allStoredChunks.get(i).data,"MDR",false);
								} catch (IOException | InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
					}

					else if (messageType.equals("REMOVED")) {
						try {
							if(!backup.removedSent(msg,peer))
								System.out.println("Couldn't removed as wished.");
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(messageType.equals("CHUNK")){
						if(restore.restoringATM){
						Chunk chunk = new Chunk(msg.getFileId(),msg.getChunkNo(),0);
						chunk.setData(msg.getData());
						restore.fileRestoring.put(msg.getChunkNo(), chunk);
						
						}
					}
					else if (messageType.equals("PUTCHUNK")) {
						
						
						
						if(!backup.isFileBacked(msg.getFileId()) && !backup.isChunkRemoved(msg.getFileId())){
						
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